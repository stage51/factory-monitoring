FROM postgres:17.2

RUN apt-get update && \
    apt-get install -y cron gzip && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /backups /scripts

COPY scripts/backup.sh /scripts/backup.sh
COPY scripts/cronjob /etc/cron.d/pgdump-cron
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /scripts/backup.sh /entrypoint.sh
RUN chmod 0644 /etc/cron.d/pgdump-cron
RUN crontab /etc/cron.d/pgdump-cron

VOLUME /backups

ENTRYPOINT ["/entrypoint.sh"]
