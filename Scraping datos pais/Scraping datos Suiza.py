import requests
from bs4 import BeautifulSoup

url = 'https://datosmacro.expansion.com/paises/suiza'
respuesta = requests.get(url)
soup = BeautifulSoup(respuesta.text, 'html.parser')
tabla = soup.find('table', {'class': 'table'})
   
if tabla:
        filas = tabla.find_all('tr')

        
        for fila in filas:
            columnas = fila.find_all('td')
            if columnas:
                
                for columna in columnas:
                    print(columna.get_text(strip=True))