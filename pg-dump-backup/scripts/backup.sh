#!/bin/bash

export PGUSER
export PGPASSWORD
export PGSSLMODE

echo ">>> Backup started at: $(date)"

databases=("user" "daily-report" "five-minute-report" "mode-report")
day_of_week=$(date +%u)

for db in "${databases[@]}"; do
  if [ "$day_of_week" -eq 7 ]; then
    echo ">>> [${db}] FULL logical dump"
    pg_dump -h "${db}_db" -d "$db" | gzip > "/backups/${db}_dump_full_$(date +%F).sql.gz"

    echo ">>> [${db}] FULL physical backup"
    full_dir="/backups/${db}_full_$(date +%F)"
    mkdir -p "$full_dir"
    pg_basebackup \
      -h "${db}_db" -D "$full_dir" -U "$PGUSER" \
      -Fp -Xs -P --write-recovery-conf

  else
    echo ">>> [${db}] INCREMENTAL backup"
    last_manifest=$(ls -d /backups/${db}_{full,incr}_* | sort | tail -n1)/backup_manifest
    if [ -f "$last_manifest" ]; then
      echo ">>> [${db}] INCREMENTAL backup using manifest: $last_manifest"
      incr_dir="/backups/${db}_incr_$(date +%F)"
      mkdir -p "$incr_dir"
      pg_basebackup \
            --incremental="$last_manifest" \
            -h "${db}_db" -D "$incr_dir" -U "$PGUSER" \
            -Fp -Xs -P --write-recovery-conf
    else
      echo ">>> [${db}] No manifest found — performing FULL backup instead"
      full_dir="/backups/${db}_full_$(date +%F)"
      mkdir -p "$full_dir"
      pg_basebackup \
            -h "${db}_db" -D "$full_dir" -U "$PGUSER" \
            -Fp -Xs -P --write-recovery-conf
    fi
  fi
done

echo ">>> Cleaning up old logical backups (*.sql.gz, older than 30 days)"
find /backups -name "*.sql.gz" -mtime +30 -exec rm {} \;

echo ">>> Cleaning up old physical backups (*_{full,incr}_*, older than 30 days)"
find /backups -type d \( -name "*_full_*" -o -name "*_incr_*" \) -mtime +30 -exec rm -rf {} \;

echo ">>> Backup completed at: $(date)"
