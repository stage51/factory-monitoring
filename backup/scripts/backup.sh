#!/bin/bash

echo ">>> Starting pgBackRest backup..."

DAY=$(date +%u)
BACKUP_TYPE="diff"

if [ "$DAY" -eq 7 ]; then
  echo ">>> Today is Sunday. Performing FULL backup."
  BACKUP_TYPE="full"
else
  echo ">>> Performing incremental (DIFF) backup."
fi

STANZAS=(user daily-report five-minute-report mode-report)

for STANZA in "${STANZAS[@]}"; do
  echo ">>> Backing up stanza: $STANZA"
  pgbackrest --stanza="$STANZA" backup --type
done

echo ">>> Backup completed."