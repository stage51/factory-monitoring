#!/bin/bash

set -e

echo "Тестирование сервиса mode-report.."

mvn verify -DskipTests=false

echo "Тестирование завершено"