 docker build -t taxi-postgres .
 docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres  taxi-postgres