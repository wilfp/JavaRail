from django.http import HttpResponse
from django.template import loader
from learn.models import Challenge
from learn.models import FileUpload
from learn import preprocessor
import json
import time


def index(request):
    return HttpResponse("Hello, world. This is the learn app index.")


def profile(request):

    context = {
        "challenge_list": Challenge.objects.all(),
    }

    template = loader.get_template("learn/profile.html")

    return HttpResponse(template.render(context, request))


def challenge(request, challenge_id):

    challenge_obj = Challenge.objects.get(id=challenge_id)

    context = {"challenge": challenge_obj}
    template = loader.get_template("learn/challenge.html")

    return HttpResponse(template.render(context, request))


def runJavaCode(request):

    if request.method != "POST":
        return HttpResponse("ERROR")

    jsonData = json.loads(request.body)

    challenge_id = jsonData['challenge_id']
    challenge_obj = Challenge.objects.get(id=challenge_id)

    code_submitted = jsonData['code']

    mappings_file = FileUpload.objects.get(name="mappings.json").file
    mappings_text = mappings_file.read()
    mappings = json.loads(mappings_text)

    context_file = FileUpload.objects.get(name="MainContext.java").file
    main_context = context_file.read()

    # TODO: compile and call Java
    ready_code = preprocessor.get_code(code_submitted, challenge_obj, mappings, main_context)

    text = "Code run successfully!"
    status = "success"

    response = "{ \"text\": \"" + text + "\", \"status\": \"" + status + "\" }"

    return HttpResponse(response)
