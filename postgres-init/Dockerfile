FROM debian:bullseye-slim

RUN apt-get update && \
    apt-get install -y build-essential libreadline-dev zlib1g-dev wget git

WORKDIR /usr/src
RUN wget https://ftp.postgresql.org/pub/source/v17.2/postgresql-17.2.tar.gz && \
    tar -xzf postgresql-17.2.tar.gz && \
    cd postgresql-17.2 && \
    ./configure --with-includes=/usr/include --with-libraries=/usr/lib --with-incremental-backup && \
    make && make install

ENV PATH="/usr/local/pgsql/bin:$PATH"

RUN mkdir -p /var/lib/postgresql/data /var/lib/postgresql/wal_archive
VOLUME ["/var/lib/postgresql/data", "/var/lib/postgresql/wal_archive"]

CMD ["postgres", "-D", "/var/lib/postgresql/data"]
