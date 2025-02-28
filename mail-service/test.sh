#!/bin/bash

set -e

echo "Тестирование сервиса mail-service.."

mvn verify -DskipCompile -DskipRepackage

echo "Тестирование завершено"