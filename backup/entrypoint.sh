#!/bin/bash

for stanza in user daily-report five-minute-report mode-report; do
  echo ">>> Инициализация стэнзы: $stanza"
  pgbackrest --stanza=$stanza stanza-create || true
done

cron -f