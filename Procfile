./mvnw clean
./mvnw build
./mvnw package -Pproduction
web: java $JAVA_OPTS -jar target/*.jar --server.port=$PORT