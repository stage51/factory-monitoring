#!/bin/bash

set -e

echo "Тестирование сервиса config-server.."

mvn verify -DskipTests=false -DskipRepackage

echo "Тестирование завершено"