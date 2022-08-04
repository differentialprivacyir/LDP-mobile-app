# users/urls.py

from django.conf.urls import url
from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='blog-home'),
    path('token/', views.token, name='blog-token'),
    path('need_to_update/', views.need_to_update, name='blog-need_to_update'),
    path('get_data/', views.get_data, name='blog_get_data'),
    path('update_estimated_frequency_table/', views.update_estimated_frequency_table, name="update_estimated_frequency_table"),
    path('get_estimated_frequency/', views.get_estimated_frequency, name="get_estimated_frequency"),

    path('reset/', views.reset, name="reset"),

]