#!/bin/bash

export PGUSER
export PGPASSWORD

echo ">>> Starting pg_dump backup: $(date)"

databases=("user" "daily-report" "five-minute-report" "mode-report")
containers=("user_db" "daily-report_db" "five-minute-report_db" "mode-report_db")

for i in "${!databases[@]}"; do
  db="${databases[$i]}"
  container="${containers[$i]}"
  echo ">>> Dumping $db from container $container"
  pg_dump -h "$container" -d "$db" | gzip > "/backups/${db}_$(date +%Y-%m-%d).sql.gz"
done

for i in "${!databases[@]}"; do
  db="${databases[$i]}"
  container="${containers[$i]}"
  basebackup_dir="/backups/${db}_basebackup_$(date +%Y-%m-%d)"

  echo ">>> Stopping container: $container"
  docker stop "$container"

  echo ">>> Copying data from volume of $container"
  volume=$(docker inspect "$container" --format '{{range .Mounts}}{{if eq .Destination "/var/lib/postgresql/data"}}{{.Name}}{{end}}{{end}}')
  mkdir -p "$basebackup_dir"
  docker run --rm -v "${volume}:/data:ro" -v "${basebackup_dir}:/backup" alpine sh -c "cp -a /data/. /backup/"

  echo ">>> Starting container: $container"
  docker start "$container"
done

echo ">>> Cleaning up old logical backups (*.sql.gz, older than 30 days)"
find /backups -name "*.sql.gz" -mtime +30 -exec rm {} \;

echo ">>> Cleaning up old physical backups (*_basebackup_*, older than 30 days)"
find /backups -type d -name "*_basebackup_*" -mtime +30 -exec rm -rf {} \;

echo ">>> Backup finished: $(date)"
