pipeline {
    agent any

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        // Variables de entorno para el proyecto
        DOCKER_IMAGE = 'ChoresFun'
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
            agent any

            steps {
                script {
                    if (isUnix()) {
                        // Si es Linux/Unix, usa el comando 'sh' simple (asumiendo que 'docker' está en el PATH)
                        sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    } else {
                        // Si es Windows, usa 'bat' y la ruta completa al ejecutable
                        // Usamos 'bat' para construir la imagen.
                        bat "\"${DOCKER_BIN}\" build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    }
                }
            }
        }
        // ---------------------------------------------------------------------------------

        stage('Deploy to Development') {
            // ... when block
            steps {
                script {
                    def dockerCommands = """
                        docker stop ${DOCKER_IMAGE}-dev || true
                        docker rm ${DOCKER_IMAGE}-dev || true
                        docker run -d --name ${DOCKER_IMAGE}-dev \\
                            -p 8081:8080 \\
                            -e DB_URL=jdbc:h2:mem:devdb \\
                            -e DB_USERNAME=sa \\
                            -e DB_PASSWORD=password \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """

                    if (isUnix()) {
                        // Linux/Unix usa '\' para saltos de línea y 'sh'
                        sh dockerCommands
                    } else {
                        // Windows usa '^' para saltos de línea y la variable DOCKER_BIN
                        // *ATENCIÓN*: El paso de 'bat' DEBE usar la variable DOCKER_BIN si docker no está en el PATH
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