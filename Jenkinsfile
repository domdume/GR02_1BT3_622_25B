pipeline {
    agent any // Assuming this agent is Linux/Unix

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        // Variables de entorno para el proyecto
        DOCKER_IMAGE = 'ChoresFun'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        // Eliminamos DOCKER_BIN porque ya no necesitamos la ruta de Windows
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Comando 'sh' estándar para Maven en Linux
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                // Comando 'sh' estándar
                sh 'mvn test'
            }
        }

        // ----------------------------------------------------------------------
        stage('Build Docker Image') {
            agent any

            steps {
                script {
                    // Comando 'sh' simple para Docker en Linux
                    // **NOTA:** Asumimos que solucionarás el error 'docker: not found'
                    // agregando el usuario 'jenkins' al grupo 'docker' en tu sistema operativo.
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }
        // ----------------------------------------------------------------------

        stage('Deploy to Development') {
            // ... when block
            steps {
                script {
                    // Comandos SH: Usamos el caracter de escape de línea de Unix (\)
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
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: '¿Desplegar a producción?'

                script {
                    // Comandos SH: Usamos el caracter de escape de línea de Unix (\)
                    sh """
                        docker stop ${DOCKER_IMAGE}-prod || true
                        docker rm ${DOCKER_IMAGE}-prod || true
                        docker run -d --name ${DOCKER_IMAGE}-prod \\
                            -p 8080:8080 \\
                            -e DB_URL=jdbc:h2:tcp://proddb:9092/proddb \\
                            -e DB_USERNAME=\${PROD_DB_USERNAME} \\
                            -e DB_PASSWORD=\${PROD_DB_PASSWORD} \\
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