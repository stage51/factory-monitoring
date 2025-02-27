#!/bin/bash

set -e

echo "Тестирование сервиса auth-server..."

mvn verify -DskipTests=false

echo "Тестирование завершено"