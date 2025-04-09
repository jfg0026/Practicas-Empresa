#!/bin/bash

# Ruta del archivo caddy.json
CADDY_JSON_PATH="/ruta/a/tu/caddy.json"  # <-- EDITA esta línea con la ruta real

# Crear el script Python
cat <<EOF | sudo tee /usr/local/bin/watchdog_caddy.py > /dev/null
import time
import subprocess
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import os

CADDY_JSON_PATH = "$CADDY_JSON_PATH"
CADDY_ADMIN_API = "http://localhost:2019/load"

class CaddyReloadHandler(FileSystemEventHandler):
    def on_modified(self, event):
        if event.src_path.endswith("caddy.json"):
            print(f"Archivo modificado: {event.src_path}. Recargando Caddy...")
            try:
                subprocess.run(
                    ["curl", "-X", "POST", CADDY_ADMIN_API, "--data-binary", f"@{CADDY_JSON_PATH}"],
                    check=True
                )
                print("Caddy recargado exitosamente.")
            except subprocess.CalledProcessError as e:
                print("Error al recargar Caddy:", e)

if __name__ == "__main__":
    path = os.path.dirname(CADDY_JSON_PATH)
    event_handler = CaddyReloadHandler()
    observer = Observer()
    observer.schedule(event_handler, path=path, recursive=False)
    observer.start()
    print(f"Vigilando cambios en: {CADDY_JSON_PATH}")

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()
EOF

# Hacer ejecutable
sudo chmod +x /usr/local/bin/watchdog_caddy.py

# Crear el servicio systemd
cat <<EOF | sudo tee /etc/systemd/system/watchdog_caddy.service > /dev/null
[Unit]
Description=Watchdog para recargar Caddy automáticamente al modificar caddy.json
After=network.target

[Service]
ExecStart=/usr/bin/python3 /usr/local/bin/watchdog_caddy.py
Restart=on-failure
User=root
WorkingDirectory=/usr/local/bin

[Install]
WantedBy=multi-user.target
EOF

# Activar el servicio
sudo systemctl daemon-reexec
sudo systemctl daemon-reload
sudo systemctl enable watchdog_caddy.service
sudo systemctl start watchdog_caddy.service

echo "Servicio watchdog_caddy instalado y ejecutándose."