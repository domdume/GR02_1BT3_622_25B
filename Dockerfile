# -------------------------------------------------------------
# Dockerfile Optimizado: Solo copia el artefacto compilado
# -------------------------------------------------------------

# Usar Tomcat 10 que soporta Jakarta EE 9 como base
FROM tomcat:10-jdk17

# Variables de entorno para la aplicación.
# Se usan los valores ARG/ENV para que puedan ser sobrescritos en 'docker run' (Jenkinsfile)
ARG DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
ARG DB_USERNAME=sa
ARG DB_PASSWORD=password

ENV DB_URL=${DB_URL} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD}

# --- OPTIMIZACIÓN: ELIMINACIÓN DE HERRAMIENTAS DE COMPILACIÓN ---
# Se elimina la instalación de Maven y la compilación, ya que Jenkins lo hizo.
# Esto reduce el tamaño de la imagen final y el tiempo de construcción.
# -----------------------------------------------------------------

# Establecer el directorio de trabajo (opcional, pero útil)
WORKDIR /usr/local/tomcat/webapps

# Copiar el artefacto compilado por Jenkins (el archivo .war) al directorio ROOT de Tomcat.
# El archivo 'target/ChoresFun.war' (o el nombre que tenga tu WAR) ya debe existir en el workspace.
# Por convención, Jenkins lo pondrá en 'target' después de 'mvn package'.
# Copiamos el .war generado por Jenkins como ROOT.war para que sea la app por defecto.
COPY target/*.war ROOT.war

# Puerto por defecto de Tomcat
EXPOSE 8080

# Comando para ejecutar Tomcat
CMD ["catalina.sh", "run"]