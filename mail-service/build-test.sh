#!/bin/bash

set -e

echo "Сборка сервиса mail-service.."

mvn clean package

echo "Сборка завершена."
