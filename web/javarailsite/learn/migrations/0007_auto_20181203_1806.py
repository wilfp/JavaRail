# Generated by Django 2.1.3 on 2018-12-03 18:06

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('learn', '0006_auto_20181203_1802'),
    ]

    operations = [
        migrations.AlterField(
            model_name='challenge',
            name='id',
            field=models.IntegerField(default=0, primary_key=True, serialize=False),
        ),
    ]
