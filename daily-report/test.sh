#!/bin/bash

set -e

echo "Тестирование сервиса daily-report.."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"