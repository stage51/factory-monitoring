#!/bin/bash

set -e

echo "Сборка и тестирование сервиса daily-report.."

mvn clean verify

echo "Сборка и тестирование завершены."
