{
    "admin": {
        "listen": "167.235.155.72:2019"
    },
    "apps": {
        "http": {
            "servers": {
                "Cloud_Guardian": {
                    "listen": ["0.0.0.0:80"],  // Define que el servidor escucha en todas las interfaces de red disponibles en el puerto 80
                    "timeouts": {
                        "read_header_timeout": "15s",  // Tiempo máximo para leer los encabezados de una solicitud HTTP
                        "idle_timeout": "5m",  // Tiempo máximo de inactividad antes de cerrar una conexión
                        "write_timeout": "15s",  // Tiempo máximo para escribir la respuesta
                        "header_timeout": "10s"  // Tiempo máximo para leer los encabezados de la respuesta
                    },
                    "max_connections": 50,  // Número máximo de conexiones simultáneas permitidas al servidor
                    "max_requests_per_connection": 20,  // Número máximo de solicitudes permitidas por cada conexión
                    "routes": [  // Definición de las rutas que manejará el servidor
                        {
                            "match": [
                                {
                                    "host": ["juan.localhost"]  // Coincide con las solicitudes cuyo encabezado Host es 'juan.localhost'
                                }
                            ],
                            "handle": [  // Acciones a realizar cuando se cumpla la coincidencia
                                {
                                    "handler": "remote_ip",  // Manejo de las IPs de origen de las solicitudes
                                    "allow": ["192.168.1.1", "10.0.0.5"],  // Solo permite el acceso a estas IPs
                                    "deny": ["0.0.0.0/0", "192.168.1.100"]  // Bloquea todas las IPs, excepto las permitidas
                                },
                                {
                                    "handler": "rate_limit",  // Configura una limitación de solicitudes
                                    "rate_limit": {
                                        "requests": 3,  // Máximo 3 solicitudes
                                        "window": "1m"  // En un período de 1 minuto
                                    }
                                },
                                {
                                    "handler": "authenticate",  // Solicita autenticación básica
                                    "basic": {
                                        "users": {
                                            "juan": "1234"  // Usuario 'juan' con contraseña '1234'
                                        }
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático
                                    "body": "Acceso concedido a Juan"  // Mensaje que se envía como respuesta cuando la solicitud es procesada
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "host": ["pepe.localhost"]  // Coincide con las solicitudes cuyo encabezado Host es 'pepe.localhost'
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "remote_ip",  // Manejo de las IPs de origen de las solicitudes
                                    "allow": ["192.168.2.2", "10.0.0.10"],  // Solo permite el acceso a estas IPs
                                    "deny": ["0.0.0.0/0", "192.168.1.100"]  // Bloquea todas las IPs, excepto las permitidas
                                },
                                {
                                    "handler": "rate_limit",  // Configura una limitación de solicitudes
                                    "rate_limit": {
                                        "requests": 5,  // Máximo 5 solicitudes
                                        "window": "1m"  // En un período de 1 minuto
                                    }
                                },
                                {
                                    "handler": "authenticate",  // Solicita autenticación básica
                                    "basic": {
                                        "users": {
                                            "pepe": "5678"  // Usuario 'pepe' con contraseña '5678'
                                        }
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático
                                    "body": "Acceso concedido a Pepe"  // Mensaje que se envía como respuesta cuando la solicitud es procesada
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "path": ["/api/*"]  // Coincide con solicitudes cuyo path comienza con '/api/'
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "reverse_proxy",  // Redirige la solicitud a un servidor backend
                                    "upstreams": [
                                        {
                                            "dial": "localhost:5000"  // Redirige la solicitud al servidor backend en localhost:5000
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "path": ["/"]  // Coincide con solicitudes cuyo path es '/'
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático
                                    "body": "Página principal accesible para IPs permitidas"  // Mensaje que se envía como respuesta para la página principal
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }
}