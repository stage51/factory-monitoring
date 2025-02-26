#!/bin/bash

set -e

echo "Сборка всех сервисов..."

services=("auth-server" "daily-report" "five-minute-report" "mail-service" "mode-report")

for service in "${services[@]}"; do
  echo "Сборка сервиса: $service"
  if [ -d "$service" ]; then
    cd "$service"
    mvn clean package -DskipTests
    cd ..
  else
    echo "Ошибка: директория $service не найдена!"
    exit 1
  fi
done

echo "Сборка завершена."
