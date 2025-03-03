#!/bin/bash

set -e

echo "Сборка и тестирование сервиса daily-report.."

mvn clean package

echo "Сборка и тестирование завершены."
