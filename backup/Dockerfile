FROM postgres:15

RUN apt-get update && \
    apt-get install -y pgbackrest cron && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /etc/pgbackrest /var/log/pgbackrest /var/lib/pgbackrest /backups

COPY pgbackrest.conf /etc/pgbackrest/pgbackrest.conf
COPY scripts/backup.sh /usr/local/bin/backup.sh
COPY scripts/cronjob /etc/cron.d/pgbackrest-cron
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /usr/local/bin/backup.sh /entrypoint.sh
RUN chmod 0644 /etc/cron.d/pgbackrest-cron && crontab /etc/cron.d/pgbackrest-cron

ENTRYPOINT ["/entrypoint.sh"]
