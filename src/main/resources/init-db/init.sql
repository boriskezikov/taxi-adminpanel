CREATE USER adminapp WITH password 'adminapp';

CREATE DATABASE "taxi_db" WITH OWNER = postgres ENCODING = 'UTF8';

\connect "taxi_db";
