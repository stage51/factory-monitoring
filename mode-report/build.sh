#!/bin/bash

set -e

echo "Сборка сервиса mode-report.."

mvn clean package -DskipTests


echo "Сборка завершена."

