# Etapa 1: Construcción del jar
FROM eclipse-temurin:17-jdk as build

WORKDIR /app
COPY . .

# Compilar el proyecto (DskipTests para que no corra los tests en build)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final para ejecutar
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiar el jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
