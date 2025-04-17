#!/bin/bash

mkdir -p /backups /var/log/pgbackrest

echo ">>> Initializing pgBackRest stanzas..."

CONFIGS=("user" "daily-report" "five-minute-report" "mode-report")
for STANZA in "${CONFIGS[@]}"; do
  pgbackrest --stanza="$STANZA" stanza-create
done

echo ">>> Starting cron for pgBackRest backup..."
cron -f
