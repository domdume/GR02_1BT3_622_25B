pipeline {
    agent any // Se ejecutará en cualquier agente Linux/Unix disponible

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        // Variables de entorno para el proyecto
        DOCKER_IMAGE = 'choresfun'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        // La variable DOCKER_BIN se elimina completamente
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
            agent any

            steps {
                // Simplificamos eliminando el bloque 'script' innecesario para un solo comando 'sh'
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }
        // ----------------------------------------------------------------------

        stage('Deploy to Development') {
            when {
                branch 'develop' // Añadí el 'when' block que faltaba
            }
            steps {
                // Eliminamos el bloque 'script' y el objeto 'dockerCommands' innecesarios
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
            when {
                branch 'main'
            }
            steps {
                input message: '¿Desplegar a producción?'

                // Simplificamos eliminando el bloque 'script' innecesario
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