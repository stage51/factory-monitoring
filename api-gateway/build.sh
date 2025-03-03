#!/bin/bash

set -e

echo "Сборка сервиса api-gateway.."

mvn clean package -DskipTests

echo "Сборка завершена."
