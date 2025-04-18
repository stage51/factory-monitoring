FROM debian:bullseye-slim

ENV PG_VERSION=17.2
ENV PATH="/usr/local/pgsql/bin:$PATH"

RUN apt-get update && \
    apt-get install -y build-essential libreadline-dev zlib1g-dev wget git \
    libicu-dev pkg-config cron gzip && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src
RUN wget https://ftp.postgresql.org/pub/source/v$PG_VERSION/postgresql-$PG_VERSION.tar.gz && \
    tar -xzf postgresql-$PG_VERSION.tar.gz && \
    cd postgresql-$PG_VERSION && \
    ./configure --with-includes=/usr/include --with-libraries=/usr/lib --with-incremental-backup && \
    make && make install

RUN mkdir -p /backups /scripts

COPY scripts/backup.sh /scripts/backup.sh
COPY scripts/cronjob /etc/cron.d/pgdump-cron
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /scripts/backup.sh /entrypoint.sh
RUN chmod 0644 /etc/cron.d/pgdump-cron
RUN crontab /etc/cron.d/pgdump-cron

VOLUME /backups

ENTRYPOINT ["/entrypoint.sh"]
