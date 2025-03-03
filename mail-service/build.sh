#!/bin/bash

set -e

echo "Сборка сервиса mail-service.."

mvn clean package -DskipTests

echo "Сборка завершена."

