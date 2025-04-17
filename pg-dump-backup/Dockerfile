FROM postgres:17.2

RUN apt-get update && \
    apt-get install -y pgbackrest cron && \
    rm -rf /var/lib/apt/lists/*

COPY pgbackrest.conf /etc/pgbackrest/pgbackrest.conf
COPY backup.sh /scripts/backup.sh
COPY cronjob /etc/cron.d/pgbackrest-cron
COPY pgbackrest-entrypoint.sh /entrypoint.sh

RUN chmod +x /scripts/backup.sh /entrypoint.sh
RUN chmod 0644 /etc/cron.d/pgbackrest-cron
RUN crontab /etc/cron.d/pgbackrest-cron

VOLUME /backups

ENTRYPOINT ["/entrypoint.sh"]

