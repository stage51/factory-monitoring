#!/bin/bash

set -e

echo "Сборка сервиса daily-report.."

mvn clean package -DskipTests

echo "Сборка завершена."
