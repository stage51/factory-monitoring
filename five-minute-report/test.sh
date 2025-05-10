#!/bin/bash

set -e

echo "Тестирование сервиса five-minute-report.."

mvn verify -DskipCompile -DskipRepackage


echo "Тестирование завершено"