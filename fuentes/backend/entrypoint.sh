#!/bin/bash
# entrypoint.sh

# Espera a que PostgreSQL esté listo
while ! nc -z postgres 5432; do
  echo "Esperando a que PostgreSQL esté listo..."
  sleep 1
done

echo "PostgreSQL está listo, iniciando la aplicación."

# Ejecuta el comando original de Dockerfile ENTRYPOINT para la aplicación
exec java -jar /app/app.jar