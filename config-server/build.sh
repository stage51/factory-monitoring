#!/bin/bash

set -e

echo "Сборка сервиса config-server.."

mvn clean package -DskipTests

echo "Сборка завершена."
