./mvnw -f ledger-poster-app/pom.xml clean package -DskipTests

docker build -t ledger-poster-app -f ledger-poster-app/Dockerfile .

docker-compose -f docker-compose.yaml up --build