#!/bin/bash

set -e

echo "Тестирование сервиса five-minute-report.."

java -jar target/five-minute-report-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"