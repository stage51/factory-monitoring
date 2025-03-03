#!/bin/bash

set -e

echo "Сборка и тестирование сервиса config-server.."

mvn clean verify

echo "Сборка и тестирование завершены."

