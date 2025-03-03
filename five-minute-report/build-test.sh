#!/bin/bash

set -e

echo "Сборка и тестирование сервиса five-minute-report.."

mvn clean verify

echo "Сборка и тестирование завершены."
