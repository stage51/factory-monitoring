#!/bin/bash

set -e

echo "Сборка и тестирование сервиса mode-report.."

mvn clean package

echo "Сборка и тестирование завершены."
