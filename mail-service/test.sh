#!/bin/bash

set -e

echo "Тестирование сервиса mail-service.."

mvn verify -DskipTests=false

echo "Тестирование завершено"