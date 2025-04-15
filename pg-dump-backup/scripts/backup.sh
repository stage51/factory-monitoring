#!/bin/bash

export PGUSER
export PGPASSWORD

echo ">>> Starting pg_dump backup: $(date)"

databases=("user" "daily-report" "five-minute-report" "mode-report")

for db in "${databases[@]}"; do
  echo ">>> Dumping database: $db"
  pg_dump -h "${db}_db" -d "$db" | gzip > "/backups/${db}_$(date +%Y-%m-%d).sql.gz"
done

echo ">>> Starting physical base backup"
for db in "${databases[@]}"; do
  basebackup_dir="/backups/${db}_basebackup_$(date +%Y-%m-%d)"
  mkdir -p "$basebackup_dir"
  pg_basebackup -h user_db -D "$basebackup_dir" -U "$PGUSER" -Fp -Xs -P
done

echo ">>> Cleaning up old logical backups (*.sql.gz, older than 30 days)"
find /backups -name "*.sql.gz" -mtime +30 -exec rm {} \;

echo ">>> Cleaning up old physical backups (*_basebackup_*, older than 30 days)"
find /backups -type d -name "*_basebackup_*" -mtime +30 -exec rm -rf {} \;

echo ">>> Backup finished: $(date)"
