# Generated by Django 3.1.7 on 2021-06-10 04:31

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0007_auto_20210407_1934'),
    ]

    operations = [
        migrations.CreateModel(
            name='OptionsAsStringFrequency',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('string', models.CharField(max_length=5)),
                ('estimated_frequency', models.IntegerField()),
            ],
        ),
    ]