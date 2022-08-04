from django.contrib import admin
from .models import Data,AdminVariable
# Register your models here.

class data(admin.ModelAdmin):
    list_display = ('id','option1', 'option2', 'option3','option4')


admin.site.register(Data,data)

class admin_variable_data(admin.ModelAdmin):
    list_display = ('name','value')


admin.site.register(AdminVariable,admin_variable_data)