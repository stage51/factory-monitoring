#!/bin/bash

set -e

echo "Сборка и тестирование сервиса auth-server.."

mvn clean verify

echo "Сборка и тестирование завершены."
