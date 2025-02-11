# MariaDB Dockerfile (mariadb/Dockerfile)
FROM mariadb:10.6

# Set environment variables for MariaDB
ENV MYSQL_ROOT_PASSWORD=rootpassword
ENV MYSQL_DATABASE=bangflix
ENV MYSQL_USER=varc
ENV MYSQL_PASSWORD=varcpw!

# Copy the custom initialization script to create the database and user
COPY init.sql /docker-entrypoint-initdb.d/
COPY zz_dummy_data.sql /docker-entrypoint-initdb.d/

# Expose port 3306 to connect MariaDB
EXPOSE 3306