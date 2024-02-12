# Utiliza la imagen base oficial de Java (JDK) para contenedores ligeros
FROM openjdk:17-jdk-slim

# Instala netcat
RUN apt-get update && \
    apt-get install -y netcat && \
    rm -rf /var/lib/apt/lists/*

# Argumento de directorio de trabajo opcional para especificar la ubicación del directorio de trabajo en el contenedor
ARG WORKDIR=/app

# Establece el directorio de trabajo en el contenedor
WORKDIR ${WORKDIR}

# Copia el archivo JAR de tu proyecto Spring Boot y el script de entrada al contenedor
COPY ./target/challenge-0.0.1-SNAPSHOT.jar ${WORKDIR}/app.jar
COPY entrypoint.sh entrypoint.sh

# Hace el script de entrada ejecutable y lo configura como ENTRYPOINT
RUN chmod +x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]

# Expone el puerto en el que se ejecuta tu aplicación
EXPOSE 8080
