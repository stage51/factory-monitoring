#!/bin/bash

set -e

echo "Тестирование сервиса five-minute-report.."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"