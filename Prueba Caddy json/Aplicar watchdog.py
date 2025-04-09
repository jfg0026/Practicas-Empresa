import time
import subprocess
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import os

# RUTA al caddy.json (está en el mismo directorio)
CADDY_JSON_PATH = os.path.join(os.path.dirname(__file__), "caddy.json")
CADDY_ADMIN_API = "http://167.235.155.72:2019/load"  # Tu IP pública en el admin listen

class CaddyReloadHandler(FileSystemEventHandler):
    def on_modified(self, event):
        if event.src_path.endswith("caddy.json"):
            print(f"[INFO] Modificado: {event.src_path}. Recargando Caddy...")
            try:
                subprocess.run(
                    ["curl", "-X", "POST", CADDY_ADMIN_API, "--data-binary", f"@{CADDY_JSON_PATH}"],
                    check=True
                )
                print("[SUCCESS] Caddy recargado.")
            except subprocess.CalledProcessError as e:
                print("[ERROR] Fallo al recargar Caddy:", e)

if __name__ == "__main__":
    path = os.path.dirname(CADDY_JSON_PATH)
    event_handler = CaddyReloadHandler()
    observer = Observer()
    observer.schedule(event_handler, path=path, recursive=False)
    observer.start()
    print(f"[WATCHDOG] Vigilando cambios en: {CADDY_JSON_PATH}")

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()