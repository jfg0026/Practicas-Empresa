import json
import copy

# Diccionario de usuarios con sus contraseñas
usuarios = {
    "admin": "1234",
    "Alberto": "5678",
    "Ian": "9123",
    "Jose": "4567",
    "Jaime": "8901"
}

# JSON base actualizado con la nueva ruta /api/*
caddy_base = {
    "admin": {
        "listen": "167.235.155.72:2019"
    },
    "apps": {
        "http": {
            "servers": {
                "Cloud_Guardian": {
                    "listen": [":80"],
                    "timeouts": {
                        "read_header_timeout": "15s",
                        "idle_timeout": "5m",
                        "write_timeout": "15s",
                        "header_timeout": "10s"
                    },
                    "max_connections": 50,
                    "max_requests_per_connection": 20,
                    "routes": [
                        {
                            "match": [
                                {
                                    "path": ["/login"]
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "rate_limit",
                                    "rate_limit": {
                                        "requests": 3,
                                        "window": "1m"
                                    }
                                },
                                {
                                    "handler": "authenticate",
                                    "basic": {
                                        "users": {
                                            "admin": "1234",
                                            "Alberto": "5678",
                                            "Ian": "9123",
                                            "Jose": "4567",
                                            "Jaime": "8901"
                                        }
                                    }
                                },
                                {
                                    "handler": "static_response",
                                    "body": "Acceso a /login concedido"
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "path": ["/api/*"]
                                }
                            ],
                            "handle": [
                                {
                                    "handler": "reverse_proxy",
                                    "upstreams": [
                                        {
                                            "dial": "localhost:5000"
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "match": [
                                {
                                    "path": ["/"]
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
            },
            "security": {
                "remote_ip": {
                    "allow": ["100.10.10.1", "192.168.1.100"],
                    "deny": ["0.0.0.0/0", "192.168.0.0/24"]
                }
            }
        }
    }
}

# Crear un archivo caddy_{usuario}.json para cada usuario
for usuario, password in usuarios.items():
    caddy_usuario = copy.deepcopy(caddy_base)
    rutas = caddy_usuario["apps"]["http"]["servers"]["Cloud_Guardian"]["routes"]

    for ruta in rutas:
        for handler in ruta.get("handle", []):
            if handler.get("handler") == "authenticate":
                # Reemplazar la sección "users" por el usuario actual
                handler["basic"]["users"] = {usuario: password}

    nombre_archivo = f"caddy_{usuario}.json"
    with open(nombre_archivo, "w") as f:
        json.dump(caddy_usuario, f, indent=4)

    print(f"✅ Archivo generado: {nombre_archivo}")