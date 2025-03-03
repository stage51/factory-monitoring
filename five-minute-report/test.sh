#!/bin/bash

set -e

echo "Тестирование сервиса five-minute-report.."

java -jar targer/five-minute-report-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"