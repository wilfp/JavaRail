# Generated by Django 2.1.3 on 2019-03-09 13:08

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('learn', '0012_challenge_has_indent'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='challenge',
            name='has_indent',
        ),
    ]
