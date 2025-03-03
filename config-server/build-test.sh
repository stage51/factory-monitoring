#!/bin/bash

set -e

echo "Сборка и тестирование сервиса config-server.."

mvn clean package

echo "Сборка и тестирование завершены."

