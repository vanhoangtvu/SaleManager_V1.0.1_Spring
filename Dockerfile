# Sử dụng OpenJDK 17 JDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Cài đặt Maven
RUN apt-get update && apt-get install -y maven curl && rm -rf /var/lib/apt/lists/*

# Copy pom.xml trước để cache dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN mvn clean package -DskipTests -B

# Expose port
EXPOSE 8081

# Run the jar file
CMD ["java", "-jar", "target/SaleM-2025-0.0.1-SNAPSHOT.jar"]
