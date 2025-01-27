#!/bin/bash

set -e

echo "Запуск mvn clean verify для всех сервисов..."

services=("auth-server" "daily-report" "five-minute-report" "mail-service" "mode-report")

for service in "${services[@]}"; do
  echo "Тестирование сервиса: $service"
  if [ -d "$service" ]; then
    cd "$service"
    mvn clean verify
    cd ..
  else
    echo "Ошибка: директория $service не найдена!"
    exit 1
  fi
done

echo "Все тесты пройдены. Готово к сборке Docker-образов!"

docker-compose build

docker-compose up -d

