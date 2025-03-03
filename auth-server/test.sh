#!/bin/bash

set -e

echo "Тестирование сервиса auth-server..."

mvn verify -DskipCompile -DskipRepackage

echo "Тестирование завершено"