# Generated by Django 3.1.7 on 2021-04-07 06:53

import django.core.validators
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0002_auto_20210406_2218'),
    ]

    operations = [
        migrations.AlterField(
            model_name='data',
            name='option1',
            field=models.IntegerField(blank=True, validators=[django.core.validators.MaxValueValidator(1), django.core.validators.MinValueValidator(0)]),
        ),
        migrations.AlterField(
            model_name='data',
            name='option2',
            field=models.IntegerField(blank=True, validators=[django.core.validators.MaxValueValidator(1), django.core.validators.MinValueValidator(0)]),
        ),
        migrations.AlterField(
            model_name='data',
            name='option3',
            field=models.IntegerField(blank=True, validators=[django.core.validators.MaxValueValidator(1), django.core.validators.MinValueValidator(0)]),
        ),
        migrations.AlterField(
            model_name='data',
            name='option4',
            field=models.IntegerField(blank=True, validators=[django.core.validators.MaxValueValidator(1), django.core.validators.MinValueValidator(0)]),
        ),
    ]
