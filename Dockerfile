# Sử dụng OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

# Thiết lập working directory
WORKDIR /app

# Cài đặt các packages cần thiết bao gồm Maven
RUN apt-get update && apt-get install -y \
    curl \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper và pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

# Cấp quyền thực thi cho Maven wrapper và đảm bảo line endings đúng
RUN chmod +x mvnw && \
    sed -i 's/\r$//' mvnw

# Download dependencies (layer caching) với retry và verbose logging
RUN ./mvnw dependency:go-offline -B --no-transfer-progress || \
    mvn dependency:go-offline -B --no-transfer-progress

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests --no-transfer-progress || \
    mvn clean package -DskipTests --no-transfer-progress

# Expose port
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "target/SaleM-2025-0.0.1-SNAPSHOT.jar"]
