#!/bin/bash

set -e

echo "Тестирование сервиса api-gateway.."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"