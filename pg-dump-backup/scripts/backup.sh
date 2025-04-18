#!/bin/bash

export PGUSER=postgres_admin
export PGPASSWORD=your_password
export PGSSLMODE=disable

echo ">>> Backup started at: $(date)"

databases=("user" "daily-report" "five-minute-report" "mode-report")

day_of_week=$(date +%u)

if [ "$day_of_week" -eq 7 ]; then
  echo ">>> Performing FULL backups (logical + physical)"

  for db in "${databases[@]}"; do
    echo ">>> Dumping database: $db"
    pg_dump -h "${db}_db" -d "$db" | gzip > "/backups/${db}_dump_full_$(date +%F).sql.gz"
  done

  for db in "${databases[@]}"; do
    backup_dir="/backups/${db}_backup_full_$(date +%F)"
    mkdir -p "$backup_dir"
    pg_basebackup -h "${db}_db" -D "$backup_dir" -U "$PGUSER" -Fp -Xs -P --write-recovery-conf
  done

else
  echo ">>> Performing INCREMENTAL physical backups only"

  for db in "${databases[@]}"; do
    backup_dir="/backups/${db}_backup_incr_$(date +%F)"
    mkdir -p "$backup_dir"
    pg_basebackup -h "${db}_db" -D "$backup_dir" -U "$PGUSER" -Fp -Xs -P \
      --incremental-mode=delta --write-recovery-conf
  done
fi

echo ">>> Cleaning up old logical backups (*.sql.gz, older than 30 days)"
find /backups -name "*.sql.gz" -mtime +30 -exec rm {} \;

echo ">>> Cleaning up old physical backups (*_backup_*, older than 30 days)"
find /backups -type d -name "*_backup_*" -mtime +30 -exec rm -rf {} \;

echo ">>> Backup completed at: $(date)"
