#!/bin/bash

echo ">>> Starting pgBackRest backup..."

DAY=$(date +%u)

if [ "$DAY" -eq 7 ]; then
  pgbackrest --stanza=user backup --type=full
else
  pgbackrest --stanza=user backup --type=diff
fi
