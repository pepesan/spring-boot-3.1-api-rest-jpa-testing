# syntax=docker/dockerfile:1

# --- Etapa de build ---
# JDK 25 (LTS), igual que java.version en pom.xml.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# --- Etapa de ejecución ---
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
