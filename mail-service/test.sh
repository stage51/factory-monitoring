#!/bin/bash

set -e

echo "Тестирование сервиса mail-service.."

java -jar target/mail-service-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"