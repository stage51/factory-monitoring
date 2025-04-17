#!/bin/bash

export PGBACKREST_CONF=/etc/pgbackrest/pgbackrest.conf
export PGUSER=postgres_admin
export PGPASSWORD=postgres_password

echo ">>> Backup started at: $(date)"
WEEKDAY=$(date +%u)

CONFIGS=("user" "daily-report" "five-minute-report" "mode-report")

if [[ "$WEEKDAY" -eq 7 ]]; then
  TYPE="full"
else
  TYPE="incr"
fi

for DB in "${CONFIGS[@]}"; do
  echo ">>> Running pgBackRest backup for: $DB ($TYPE)"
  pgbackrest --stanza="$DB" --type="$TYPE" backup
done

echo ">>> Backup finished at: $(date)"
