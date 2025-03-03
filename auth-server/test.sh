#!/bin/bash

set -e

echo "Тестирование сервиса auth-server..."

java -jar target/auth-server-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"