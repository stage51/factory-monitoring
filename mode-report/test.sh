#!/bin/bash

set -e

echo "Тестирование сервиса mode-report.."

java -jar targer/mode-report-0.0.1-SNAPSHOT.jar --tests


echo "Тестирование завершено"