#!/bin/bash

set -e

echo "Тестирование сервиса mode-report.."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"