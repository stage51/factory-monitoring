#!/bin/bash

set -e

echo "Сборка и тестирование сервиса mail-service.."

mvn clean verify

echo "Сборка и тестирование завершены."

