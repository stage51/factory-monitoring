#!/bin/bash

set -e

echo "Сборка сервиса auth-server.."

mvn clean package -DskipTests

echo "Сборка завершена."
