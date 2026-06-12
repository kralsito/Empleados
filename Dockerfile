# ===== STAGE 1: Build con Gradle =====
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar primero los archivos de Gradle para aprovechar el cache.
# Si no cambian, las dependencias no se vuelven a descargar.
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Descarga de dependencias (queda cacheado mientras no cambien)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copiar el código y buildear
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Extraer el JAR en capas para mejor cacheo
RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination extracted

# ===== STAGE 2: Imagen final (runtime) =====
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

RUN apk add --no-cache curl

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar capas en orden de menor a mayor frecuencia de cambio
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]