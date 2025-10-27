pipeline {
    agent any

    tools {
        maven 'Maven 3.9.5'  // Asegúrate de tener esta versión configurada en Jenkins
        jdk 'JDK 17'         // Asegúrate de tener JDK 17 configurado en Jenkins
    }

    environment {
        // Variables de entorno para el proyecto
        DOCKER_IMAGE = 'quehaceres-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Obtener código del repositorio
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Compilar el proyecto con Maven
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                // Ejecutar pruebas unitarias
                sh 'mvn test'
            }

        }



        stage('Build Docker Image') {
        agent {
                        docker {
                            image 'maven:3.9.5-jdk17' // Ejecuta Maven en un contenedor limpio
                        }
                    }
            steps {
                script {
                    // Construir imagen Docker
                   def builtImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")

                                           echo "Image built: ${builtImage.id}"
                }
            }
        }

        stage('Deploy to Development') {
            when {
                branch 'develop'  // Solo se ejecuta en la rama develop
            }
            steps {
                script {
                    // Desplegar en entorno de desarrollo
                    sh """
                        docker stop ${DOCKER_IMAGE}-dev || true
                        docker rm ${DOCKER_IMAGE}-dev || true
                        docker run -d --name ${DOCKER_IMAGE}-dev \
                            -p 8081:8080 \
                            -e DB_URL=jdbc:h2:mem:devdb \
                            -e DB_USERNAME=sa \
                            -e DB_PASSWORD=password \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'  // Solo se ejecuta en la rama main
            }
            steps {
                // Requiere aprobación manual antes de desplegar a producción
                input message: '¿Desplegar a producción?'
                
                script {
                    // Desplegar en entorno de producción
                    sh """
                        docker stop ${DOCKER_IMAGE}-prod || true
                        docker rm ${DOCKER_IMAGE}-prod || true
                        docker run -d --name ${DOCKER_IMAGE}-prod \
                            -p 8080:8080 \
                            -e DB_URL=jdbc:h2:tcp://proddb:9092/proddb \
                            -e DB_USERNAME=\${PROD_DB_USERNAME} \
                            -e DB_PASSWORD=\${PROD_DB_PASSWORD} \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }

    post {
        success {
            // Notificar éxito
            emailext (
                subject: "BUILD SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """BUILD SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                    Check console output at ${env.BUILD_URL}""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        
        failure {
            // Notificar fallo
            emailext (
                subject: "BUILD FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """BUILD FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
                    Check console output at ${env.BUILD_URL}""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }

        always {
            // Limpiar workspace
            cleanWs()
        }
    }
}