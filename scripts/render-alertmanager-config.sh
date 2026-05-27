#!/bin/bash
# Renderiza alertmanager.yml sustituyendo variables de entorno
envsubst < ./monitoring/alertmanager/alertmanager.yml.template \
         > ./monitoring/alertmanager/alertmanager.yml
echo "✅ alertmanager.yml generado"
