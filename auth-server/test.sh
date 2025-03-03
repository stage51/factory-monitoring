#!/bin/bash

set -e

echo "Тестирование сервиса auth-server..."

java -jar targer/auth-server-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"