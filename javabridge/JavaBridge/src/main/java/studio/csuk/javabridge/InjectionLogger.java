package studio.csuk.javabridge;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;

public class InjectionLogger {

    /** The path to the methods needed to run */
    private static String PATH = "studio.csuk.javabridge.InjectionLogger.get().";

    /** Map of serialName to list of lines executed */
    private Map<String, List<Integer>> lines;

    /** The number of variables created */
    private int variableCounter = 0;

    /**
     * In the implementation of variable watch, a program's execution time is measured
     * in "lines" aka the total number of lines that have been executed so far.
     */

    /** Map of serialName to time */
    private Map<String, Integer> currentRunTimeMap;
    /** The history of a variable's value */
    /** Map of variableID to line + value */
    private Map<Integer, Map<Integer,Object>> assignmentMap;
    /** Map of variableID to variable */
    private Map<Integer,Variable> variableMap;
    /** All the variables of a certain program */
    /** Map of serialName to variableIDs */
    private Map<String, List<Integer>> serialVariableMap;

    public InjectionLogger(){
        this.lines = new HashMap<>();
        this.currentRunTimeMap = new HashMap<>();
        this.assignmentMap = new HashMap<>();
        this.serialVariableMap = new HashMap<>();
    }

    /**
     * Creates the required data structures for the class specified by the serial name
     * @param serialName the name of the class file in the working directory
     */
    public void register(String serialName) {
        lines.put(serialName, new LinkedList<>());
        currentRunTimeMap.put(serialName, 0);
        serialVariableMap.put(serialName, new LinkedList<>());
    }

    /**
     * Removes all the data of a class file from the program
     * @param serialName the name of the class file in the working directory
     */
    public void remove(String serialName){

        lines.remove(serialName);
        currentRunTimeMap.remove(serialName);

        serialVariableMap.get(serialName).stream().forEach(id -> assignmentMap.remove(id));
        serialVariableMap.remove(serialName);
    }

    /**
     * Allocates the next variable id to be used
     * @return the next variable id
     */
    private synchronized int getNextVariableID(){
        return variableCounter++;
    }

    /**
     * Used to log the current line of a program
     * @param serialName the name of the program
     * @param line the line that program is currently on
     */
    @SuppressWarnings("unused")
    public void onLine(String serialName, int line){
        // add this line to the history of all lines
        lines.get(serialName).add(line);
        // increment the current line by one
        currentRunTimeMap.compute(serialName, (s, i) -> i+1);
    }

    /**
     * Called when a new variable is created.
     * @param serialName the name of the program
     * @param variableName the name of the variable within the program
     * @param scope the scope of the variable
     * @param value the value it is initialised to
     */
    @SuppressWarnings("unused")
    public void onVariableInit(String serialName, String variableName, int scope, Object value){

        // create a new variable instance
        var variableID = getNextVariableID();
        var variable = Variable.of(variableID, serialName, variableName, scope);

        // register the variable with the system
        variableMap.put(variable.getVariableID(), variable);
        assignmentMap.put(variable.getVariableID(), new HashMap<>());
    }

    @SuppressWarnings("unused")
    public void onVariableAssign(String serialName, int variableID, Object value){

        // add an entry for the variable at this time with this value
        // get the assignments map
        var map = assignmentMap.get(variableID);
        // get the current time (in lines) of the program
        var currentTime = currentRunTimeMap.get(serialName);
        // add the new value at the specified time
        map.put(currentTime, value);
    }

    public String getLineCode(String serialName, int line, boolean offset){

        if(offset) line -= 1;

        return format("onLine(\"" + serialName + "\", " + line + ");", null);
    }

    public String getVariableInitCode(String serialName, int line, String variableName, int scope, Object value, boolean offset) {

        if(offset) line -= 1;

        return format("onVariableInit(%s,%s,%s,%s)", serialName, variableName, scope, value);
    }

    public String getVariableAssignCode(String serialName, int line, int variableID, Object value, boolean offset) {

        if(offset) line -= 1;

        return format("onVariableAssign(%s,%s,%s)", serialName, variableID, value);
    }

    public List<Integer> getLines(String serialName){
        return lines.get(serialName);
    }

    public Map<Integer, Map<Integer,Object>> getAssignments(String serialName){
        return this.assignmentMap;
    }

    private static String format(String text, Object... values){
        return String.format(PATH + text, values);
    }

    @Value(staticConstructor="of")
    class Variable{

        private int variableID;
        private String serialName;
        private String variableName;
        private int scope;
    }

    private static final InjectionLogger instance = new InjectionLogger();

    public static InjectionLogger get(){
        return instance;
    }
}
