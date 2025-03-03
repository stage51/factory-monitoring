#!/bin/bash

set -e

echo "Сборка и тестирование сервиса api-gateway.."

mvn clean verify

echo "Сборка и тестирование завершены."
