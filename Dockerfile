# Stage 1: Build
FROM gradle:jdk21 AS build
WORKDIR /app

# Copiar los archivos de configuración de Gradle
COPY build.gradle settings.gradle /app/

# Descargar dependencias antes de copiar el código fuente
RUN gradle dependencies --no-daemon || true

# Copiar el código fuente después de bajar dependencias
COPY src /app/src/

# Construir el JAR
RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/*.jar /app/user-query-service.jar
WORKDIR /app
CMD ["java", "-jar", "user-query-service.jar"]
