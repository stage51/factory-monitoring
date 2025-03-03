#!/bin/bash

set -e

echo "Тестирование сервиса daily-report.."

java -jar targer/daily-report-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"