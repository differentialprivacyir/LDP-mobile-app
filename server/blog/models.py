import time
# Create your models here.
from threading import Thread

from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models


class TurnOffTThread(Thread):

    def run(self):
        t = int(AdminVariable.objects.filter(name="t")[0].value)
        time.sleep(t + 1)
        need_to_update = AdminVariable.objects.filter(name="need_to_update")[0]
        need_to_update.value = "0"
        need_to_update.save()


class Data(models.Model):
    option1 = models.IntegerField(validators=[MaxValueValidator(1), MinValueValidator(0)], blank=True)
    option2 = models.IntegerField(validators=[MaxValueValidator(1), MinValueValidator(0)], blank=True)
    option3 = models.IntegerField(validators=[MaxValueValidator(1), MinValueValidator(0)], blank=True)
    option4 = models.IntegerField(validators=[MaxValueValidator(1), MinValueValidator(0)], blank=True)

    class Meta:
        verbose_name_plural = "Data"


class AdminVariable(models.Model):
    name = models.CharField(max_length=100)
    value = models.CharField(max_length=200)

    def save(self, *args, **kwargs):
        if self.name == "need_to_update" and self.value == "1":
            t = TurnOffTThread()
            t.start()
        super(AdminVariable, self).save(*args, **kwargs)

class OptionsAsStringFrequency(models.Model):
    string = models.CharField(max_length=5)
    estimated_frequency = models.IntegerField()
    
