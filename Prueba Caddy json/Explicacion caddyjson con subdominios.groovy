{
    "admin": {
        "listen": "167.235.155.72:2019"  // Define la dirección IP y el puerto donde el panel de administración de Caddy escuchará
    },
    "apps": {
        "http": {
            "servers": {
                "Cloud_Guardian": {
                    "listen": ["0.0.0.0:80"],  // El servidor escucha en todas las interfaces de red disponibles en el puerto 80
                    "timeouts": {
                        "read_header_timeout": "15s",  // Tiempo máximo para leer los encabezados de una solicitud HTTP
                        "idle_timeout": "5m",  // Tiempo máximo de inactividad antes de cerrar la conexión
                        "write_timeout": "15s",  // Tiempo máximo para escribir la respuesta
                        "header_timeout": "10s"  // Tiempo máximo para leer los encabezados de la respuesta
                    },
                    "max_connections": 50,  // Límite global de conexiones simultáneas permitidas al servidor
                    "max_requests_per_connection": 20,  // Número máximo de solicitudes permitidas por cada conexión
                    "routes": [  // Definición de las rutas que manejará el servidor
                        {
                            "match": [
                                {
                                    "host": ["juan.localhost"],  // Coincide con solicitudes cuyo encabezado Host es 'juan.localhost'
                                    "path": ["/login"]  // Ruta específica para el login de Juan
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "rate_limit",  // Limitar los intentos de login para Juan
                                    "rate_limit": {
                                        "requests": 3,  // Máximo de 3 intentos de login
                                        "window": "1m"  // En 1 minuto
                                    }
                                },
                                {
                                    "handler": "authenticate",  // Solicita autenticación básica para Juan
                                    "basic": {
                                        "users": {
                                            "juan": "1234"  // Usuario 'juan' con contraseña '1234'
                                        }
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático si la autenticación es exitosa
                                    "body": "Acceso concedido a Juan"  // Mensaje que se envía como respuesta cuando la solicitud es procesada
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "host": ["juan.localhost"],  // Coincide con solicitudes cuyo encabezado Host es 'juan.localhost'
                                    "path": ["/"]  // Ruta principal accesible después de login de Juan
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "rate_limit",  // Limitar las solicitudes HTTP de Juan
                                    "rate_limit": {
                                        "requests": 5,  // Juan puede realizar hasta 5 solicitudes por minuto
                                        "window": "1m"  // En 1 minuto
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático
                                    "body": "Página principal accesible para Juan"  // Mensaje de bienvenida para Juan
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "host": ["pepe.localhost"],  // Coincide con solicitudes cuyo encabezado Host es 'pepe.localhost'
                                    "path": ["/login"]  // Ruta específica para el login de Pepe
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "rate_limit",  // Limitar los intentos de login para Pepe
                                    "rate_limit": {
                                        "requests": 3,  // Máximo de 3 intentos de login
                                        "window": "1m"  // En 1 minuto
                                    }
                                },
                                {
                                    "handler": "authenticate",  // Solicita autenticación básica para Pepe
                                    "basic": {
                                        "users": {
                                            "pepe": "5678"  // Usuario 'pepe' con contraseña '5678'
                                        }
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático si la autenticación es exitosa
                                    "body": "Acceso concedido a Pepe"  // Mensaje que se envía como respuesta cuando la solicitud es procesada
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "host": ["pepe.localhost"],  // Coincide con solicitudes cuyo encabezado Host es 'pepe.localhost'
                                    "path": ["/"]  // Ruta principal accesible después de login de Pepe
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "rate_limit",  // Limitar las solicitudes HTTP de Pepe
                                    "rate_limit": {
                                        "requests": 7,  // Pepe puede realizar hasta 7 solicitudes por minuto
                                        "window": "1m"  // En 1 minuto
                                    }
                                },
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático
                                    "body": "Página principal accesible para Pepe"  // Mensaje de bienvenida para Pepe
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
                                    "handler": "reverse_proxy",  // Redirige las solicitudes a un servidor backend
                                    "upstreams": [
                                        {
                                            "dial": "localhost:5000"  // Redirige las solicitudes a 'localhost:5000'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "path": ["/"]  // Ruta principal accesible para cualquier IP permitida
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "static_response",  // Responde con un mensaje estático para la página principal
                                    "body": "Página principal accesible para IPs permitidas"  // Mensaje de bienvenida para usuarios permitidos
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }
}