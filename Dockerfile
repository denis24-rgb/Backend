# Etapa 1: Construcción del jar
FROM eclipse-temurin:21-jdk as build
#DIRECTORY
WORKDIR /app
#COPY
COPY . .

# Compilar el proyecto (DskipTests para que no corra los tests en build)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final para ejecutar
FROM eclipse-temurin:21-jdk
#set the working
WORKDIR /app

# Copiar el jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084

# Ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
