# Generated by Django 3.1.7 on 2021-04-07 15:04

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0006_auto_20210407_1932'),
    ]

    operations = [
        migrations.RenameModel(
            old_name='Admin_var',
            new_name='AdminVariable',
        ),
    ]