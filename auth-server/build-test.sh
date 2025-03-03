#!/bin/bash

set -e

echo "Сборка и тестирование сервиса auth-server.."

mvn clean package

echo "Сборка и тестирование завершены."
