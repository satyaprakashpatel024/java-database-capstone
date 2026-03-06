# Stage 1: Build stage with Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Cache dependencies by copying ONLY pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage with minimal Java runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# 3. Security: Create a non-root user and group to run the app
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 4. Copy the built JAR from the builder stage and assign ownership to appuser
COPY --chown=appuser:appuser --from=builder /app/target/*.jar app.jar

# 5. Security: Switch to the non-root user
USER appuser

# 6. Expose the application port
EXPOSE 8080

# 7. Flexibility: Allow passing JVM arguments via JAVA_OPTS environment variable
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]