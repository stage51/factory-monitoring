#!/bin/bash

set -e

echo "Тестирование сервиса mode-report.."

mvn verify -DskipCompile -DskipRepackage


echo "Тестирование завершено"