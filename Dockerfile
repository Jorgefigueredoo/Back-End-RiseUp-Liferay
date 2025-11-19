# Etapa 1: Construção
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# --- AQUI ESTÁ A MUDANÇA ---
# Copia o pom.xml de dentro da pasta 'eventos' para a raiz do container
COPY eventos/pom.xml .

# Copia a pasta src de dentro da pasta 'eventos' para a raiz do container
COPY eventos/src ./src
# ---------------------------

RUN mvn clean package -DskipTests

# Etapa 2: Execução (Igual ao anterior)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]