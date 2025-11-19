# Etapa 1: Construção
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# COPIA TUDO (incluindo a pasta eventos)
COPY . .

# Entra na pasta 'eventos' para rodar o Maven
WORKDIR /app/eventos
RUN mvn clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Pega o .jar que foi gerado dentro da pasta eventos/target
COPY --from=build /app/eventos/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]