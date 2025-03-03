#!/bin/bash

set -e

echo "Сборка и тестирование сервиса five-minute-report.."

mvn clean package

echo "Сборка и тестирование завершены."
