#!/bin/bash

set -e

echo "Тестирование сервиса config-server.."

mvn verify -DskipCompile -DskipRepackage


echo "Тестирование завершено"