{
  // Configuración del panel de administración de Caddy
  "admin": {
    // Dirección y puerto donde escucha el panel de administración
    "listen": "167.235.155.72:2019"
  },
  "apps": {
    "http": {
      "servers": {
        // Nombre del servidor HTTP
        "Cloud_Guardian": {
          // Puertos y direcciones IP en los que escucha el servidor
          "listen": ["0.0.0.0:80"],
          // Configuración de tiempos de espera (timeouts)
          "timeouts": {
            "read_header_timeout": "15s",  // Tiempo para leer cabecera
            "idle_timeout": "5m",          // Tiempo de espera de conexión inactiva
            "write_timeout": "15s",        // Tiempo para escribir respuesta
            "header_timeout": "10s"        // Tiempo límite para procesar cabeceras
          },
          // Límite máximo de conexiones simultáneas
          "max_connections": 50,
          // Límite de solicitudes por conexión
          "max_requests_per_connection": 20,
          // Rutas (reglas de manejo de peticiones)
          "routes": [
            {
              // Bloqueo de IPs específicas para el usuario "juan"
              "match": [
                {
                  "remote_ip": {
                    "deny": ["192.168.1.0/24"]  // Rango de IPs denegadas
                  },
                  "host": ["juan.localhost"]     // Solo aplica a este dominio
                }
              ],
              "handle": [
                {
                  "handler": "static_response",  // Manejador de respuesta estática
                  "status_code": 403,            // Código de estado HTTP (Prohibido)
                  "body": "Acceso denegado por IP para Juan"  // Mensaje devuelto
                }
              ]
            },
            {
              // Permitir acceso solo desde una IP concreta para "juan"
              "match": [
                {
                  "remote_ip": {
                    "allow": ["192.168.1.100/32"]  // IP permitida (un solo host)
                  },
                  "host": ["juan.localhost"]
                }
              ],
              "handle": [
                {
                  "handler": "static_response",
                  "body": "Acceso concedido a Juan desde IP permitida"
                }
              ]
            },
            {
              // Bloqueo de IPs específicas para "pepe"
              "match": [
                {
                  "remote_ip": {
                    "deny": ["10.10.0.0/16"]  // Rango de IPs denegadas
                  },
                  "host": ["pepe.localhost"]
                }
              ],
              "handle": [
                {
                  "handler": "static_response",
                  "status_code": 403,
                  "body": "Acceso denegado por IP para Pepe"
                }
              ]
            },
            {
              // Permitir acceso solo desde una IP específica para "pepe"
              "match": [
                {
                  "remote_ip": {
                    "allow": ["10.10.1.50"]  // IP concreta permitida
                  },
                  "host": ["pepe.localhost"]
                }
              ],
              "handle": [
                {
                  "handler": "static_response",
                  "body": "Acceso concedido a Pepe desde IP permitida"
                }
              ]
            },
            {
              // Redirección de tráfico a un backend (API)
              "match": [
                {
                  "path": ["/api/*"]  // Coincide con cualquier ruta bajo /api
                }
              ],
              "handle": [
                {
                  "handler": "reverse_proxy",  // Proxy inverso
                  "upstreams": [
                    {
                      "dial": "localhost:5000"  // Dirección interna del backend
                    }
                  ]
                }
              ]
            },
            {
              // Ruta por defecto para otras IPs/dominios no bloqueados
              "match": [
                {
                  "path": ["/"]  // Ruta raíz
                }
              ],
              "handle": [
                {
                  "handler": "static_response",
                  "body": "Página principal accesible para IPs permitidas"
                }
              ]
            }
          ]
        }
      }
    }
  }
}