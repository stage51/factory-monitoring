#!/bin/bash

echo ">>> Starting pg_dump backup: $(date)"

databases=("user" "daily-report" "five-minute-report" "mode-report")

for db in "${databases[@]}"; do
  echo ">>> Dumping database: $db"
  pg_dump -U postgres -h "${db//-/_}_db" -d "$db" | gzip > "/backups/${db}_$(date +%Y-%m-%d).sql.gz"
done

echo ">>> Cleaning up old backups (older than 30 days)"
find /backups -name "*.sql.gz" -mtime +30 -exec rm {} \;

echo ">>> Backup finished: $(date)"
