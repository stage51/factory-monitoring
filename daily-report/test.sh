#!/bin/bash

set -e

echo "Тестирование сервиса daily-report.."

mvn verify -DskipCompile -DskipRepackage

echo "Тестирование завершено"