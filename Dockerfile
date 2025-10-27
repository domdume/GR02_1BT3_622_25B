# Usar Tomcat 10 que soporta Jakarta EE 9
FROM tomcat:10-jdk17

# Argumentos para configuración de la base de datos
ARG DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
ARG DB_USERNAME=sa
ARG DB_PASSWORD=password

# Variables de entorno para la aplicación
ENV DB_URL=${DB_URL} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD}

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el proyecto
COPY . .

# Construir la aplicación con Maven
RUN mvn clean package

# Eliminar el webapps por defecto de Tomcat y copiar nuestra aplicación
RUN rm -rf /usr/local/tomcat/webapps/* && \
    cp target/*.war /usr/local/tomcat/webapps/ROOT.war

# Puerto por defecto de Tomcat
EXPOSE 8080

# Comando para ejecutar Tomcat
CMD ["catalina.sh", "run"]