#!/bin/bash

set -e

echo "Тестирование сервиса config-server.."

java -jar target/config-server-0.0.1-SNAPSHOT.jar --tests

echo "Тестирование завершено"