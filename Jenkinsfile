pipeline {
    agent any

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        // Variables de entorno para el proyecto
        DOCKER_IMAGE = 'quehaceres-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        // Definición de la RUTA COMPLETA de Docker para Windows
        DOCKER_BIN = 'C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Compilar con Maven (Funciona bien en agente Windows con herramientas instaladas)
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                // Ejecutar pruebas unitarias
                sh 'mvn test'
            }
        }

        // ---------------------------------------------------------------------------------
        stage('Build Docker Image') {
            agent any // NO usamos el agente Docker aquí. Usamos el agente Windows directamente.

            steps {
                script {
                    // **NOTA IMPORTANTE:**
                    // El comando 'docker.build()' es un paso de Pipeline DSL y NO es un comando de consola.
                    // Este paso funciona siempre y cuando el BINARIO 'docker.exe' esté en el PATH
                    // del usuario que ejecuta el servicio Jenkins, y el plugin de Docker esté instalado.
                    // Si el error persiste aquí, DEBES usar la solución de permisos del servicio Jenkins.

                    def builtImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    echo "Image built: ${builtImage.id}"

                    // Si 'docker.build' falla con 'docker: not found', usa 'bat' para una construcción directa:
                    /*
                    bat "\"${DOCKER_BIN}\" build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    */
                }
            }
        }
        // ---------------------------------------------------------------------------------

        stage('Deploy to Development') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Usamos 'bat' y la variable DOCKER_BIN para llamar al ejecutable de Docker
                    bat """
                        "${DOCKER_BIN}" stop ${DOCKER_IMAGE}-dev || exit 0
                        "${DOCKER_BIN}" rm ${DOCKER_IMAGE}-dev || exit 0
                        "${DOCKER_BIN}" run -d --name ${DOCKER_IMAGE}-dev ^
                            -p 8081:8080 ^
                            -e DB_URL=jdbc:h2:mem:devdb ^
                            -e DB_USERNAME=sa ^
                            -e DB_PASSWORD=password ^
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: '¿Desplegar a producción?'

                script {
                    // Usamos 'bat' y la variable DOCKER_BIN para llamar al ejecutable de Docker
                    bat """
                        "${DOCKER_BIN}" stop ${DOCKER_IMAGE}-prod || exit 0
                        "${DOCKER_BIN}" rm ${DOCKER_IMAGE}-prod || exit 0
                        "${DOCKER_BIN}" run -d --name ${DOCKER_IMAGE}-prod ^
                            -p 8080:8080 ^
                            -e DB_URL=jdbc:h2:tcp://proddb:9092/proddb ^
                            -e DB_USERNAME=\${PROD_DB_USERNAME} ^
                            -e DB_PASSWORD=\${PROD_DB_PASSWORD} ^
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }

    post {
        success {
            emailext (
                subject: "BUILD SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """BUILD SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                    Check console output at ${env.BUILD_URL}""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }

        failure {
            emailext (
                subject: "BUILD FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """BUILD FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                    Check console output at ${env.BUILD_URL}""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }

        always {
            cleanWs()
        }
    }
}