#!/bin/bash

set -e

echo "Сборка и тестирование сервиса api-gateway.."

mvn clean package

echo "Сборка и тестирование завершены."
