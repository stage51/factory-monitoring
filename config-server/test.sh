#!/bin/bash

set -e

echo "Тестирование сервиса config-server.."

java -jar targer/config-server-0.0.1-SNAPSHOT.jar --tests


echo "Тестирование завершено"