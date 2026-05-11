# Etapa de construcción (Build Stage)
FROM eclipse-temurin:17-jdk-jammy AS builder

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos necesarios para descargar dependencias primero
# Esto permite aprovechar la caché de Docker y no descargar todo en cada cambio de código
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Damos permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Descargamos las dependencias (el fallo aquí es normal si no hay código fuente, pero cachea lo bajado)
RUN ./gradlew dependencies --no-daemon || true

# Copiamos el código fuente de la aplicación
COPY src src

# Construimos el ejecutable (saltando los tests para que la imagen se construya más rápido en CI/CD)
RUN ./gradlew build -x test --no-daemon

# Etapa de producción (Run Stage)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copiamos solo el JAR generado en la etapa anterior
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# Exponemos el puerto en el que corre Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]