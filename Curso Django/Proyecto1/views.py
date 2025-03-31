from django.http import HttpResponse

def saludo(request): #Primera vista
    
    return HttpResponse ("Hola alumnos esta en nuestra primera pagina Django")

def despedida(request):
    
    return HttpResponse("Hasta luego alumnos de Django")