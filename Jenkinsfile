pipeline {
    agent any

        tools {
            maven 'Maven 3.9.5'
            jdk 'JDK 17'
        }

        environment {
            // Asegúrate de que esto esté en minúsculas
            DOCKER_IMAGE = 'choresfun'
            DOCKER_TAG = "${env.BUILD_NUMBER}"
        }

        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Build') {
                steps {
                    sh 'mvn clean package -DskipTests'
                }
            }

            stage('Test') {
                steps {
                    sh 'mvn test'
                }
            }

            // ----------------------------------------------------------------------
            stage('Build Docker Image') {
                // CAMBIO CLAVE: Usamos un agente temporal con la imagen de Docker.
                // Jenkins automáticamente montará el workspace y el socket.
                agent {
                    docker {
                        image 'docker:latest'
                        args '-v /var/run/docker.sock:/var/run/docker.sock' // Monta el socket
                    }
                }

                steps {
                    // Aquí usamos 'docker build'. No necesitamos 'sudo' porque
                    // el agente 'docker:latest' ya está configurado para usar el socket montado.
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
            // ----------------------------------------------------------------------

            stage('Deploy to Development') {
                when { branch 'develop' }

                // CAMBIO CLAVE: Reutilizamos el agente Docker con el socket montado.
                agent {
                    docker {
                        image 'docker:latest'
                        args '-v /var/run/docker.sock:/var/run/docker.sock'
                    }
                }
                steps {
                    // Los comandos Docker ahora funcionarán perfectamente
                    sh """
                        docker stop ${DOCKER_IMAGE}-dev || true
                        docker rm ${DOCKER_IMAGE}-dev || true
                        docker run -d --name ${DOCKER_IMAGE}-dev \\
                            -p 8081:8080 \\
                            -e DB_URL=jdbc:h2:mem:devdb \\
                            -e DB_USERNAME=sa \\
                            -e DB_PASSWORD=password \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }

            stage('Deploy to Production') {
                when { branch 'main' }
                input message: '¿Desplegar a producción?'

                // CAMBIO CLAVE: Reutilizamos el agente Docker
                agent {
                    docker {
                        image 'docker:latest'
                        args '-v /var/run/docker.sock:/var/run/docker.sock'
                    }
                }
                steps {
                    sh """
                        docker stop ${DOCKER_IMAGE}-prod || true
                        docker rm ${DOCKER_IMAGE}-prod || true
                        docker run -d --name ${DOCKER_IMAGE}-prod \\
                            -p 8082:8080 \\ // Usamos 8082 para evitar colisión con Jenkins (8080)
                            -e DB_URL=jdbc:h2:tcp://proddb:9092/proddb \\
                            -e DB_USERNAME=\${PROD_DB_USERNAME} \\
                            -e DB_PASSWORD=\${PROD_DB_PASSWORD} \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
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