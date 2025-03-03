#!/bin/bash

set -e

echo "Сборка сервиса five-minute-report.."

mvn clean package -DskipTests

echo "Сборка завершена."
