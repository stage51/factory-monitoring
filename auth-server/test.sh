#!/bin/bash

set -e

echo "Тестирование сервиса auth-server..."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"