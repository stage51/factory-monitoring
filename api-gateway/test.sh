#!/bin/bash

set -e

echo "Тестирование сервиса api-gateway.."

java -jar target/api-gateway-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"