#!/bin/bash

set -e

echo "Тестирование сервиса api-gateway.."

mvn verify -DskipTests=false

echo "Тестирование завершено"