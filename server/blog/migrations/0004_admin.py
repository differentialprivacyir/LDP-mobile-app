# Generated by Django 3.1.7 on 2021-04-07 14:36

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0003_auto_20210407_1123'),
    ]

    operations = [
        migrations.CreateModel(
            name='Admin',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=200)),
                ('value', models.CharField(max_length=200)),
            ],
            options={
                'verbose_name_plural': 'Admin',
            },
        ),
    ]