#!/bin/bash

set -e

echo "Тестирование сервиса daily-report.."

mvn verify -DskipTests=false

echo "Тестирование завершено"