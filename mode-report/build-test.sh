#!/bin/bash

set -e

echo "Сборка и тестирование сервиса mode-report.."

mvn clean verify

echo "Сборка и тестирование завершены."
