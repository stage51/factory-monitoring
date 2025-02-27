#!/bin/bash

set -e

echo "Тестирование сервиса config-server.."

mvn verify -DskipTests=false

echo "Тестирование завершено"