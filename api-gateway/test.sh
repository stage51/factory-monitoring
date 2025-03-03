#!/bin/bash

set -e

echo "Тестирование сервиса api-gateway.."

mvn verify -DskipCompile -DskipRepackage

echo "Тестирование завершено"