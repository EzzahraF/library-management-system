pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK11'
    }

    environment {
        SONAR_HOST_URL = 'http://localhost:9100'
        SONAR_PROJECT_KEY = 'library-management-system'
        MAVEN_OPTS = '-Xmx1024m'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        ansiColor('xterm')
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source'
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                echo '🔨 Compilation + tests unitaires'
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                }
            }
        }

        stage('Code Coverage (JaCoCo)') {
            steps {
                echo '📊 Génération du rapport de couverture'
                sh 'mvn jacoco:report'
                publishHTML(target: [
                    reportName: 'JaCoCo Coverage',
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    keepAll: true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ])
                archiveArtifacts artifacts: 'target/site/jacoco/**', fingerprint: true
            }
        }

stage('SonarQube Analysis') {
    when {
        branch 'main'
    }
    steps {
        withSonarQubeEnv('SonarQube') {
            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                    -Dsonar.host.url=${SONAR_HOST_URL} \
                    -Dsonar.login=$SONAR_TOKEN \
                    -Dsonar.coverage.exclusions=**/model/**,**/dto/**
                """
            }
        }
    }
}


        stage('Quality Gate') {
            when {
                branch 'main'
            }
            steps {
                echo '🚦 Vérification du Quality Gate'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Packaging de l’application'
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy (Local)') {
            when {
                branch 'main'
            }
            steps {
                echo ' Déploiement local'
                sh '''
                    cp target/*.jar /tmp/library-management-system.jar
                    echo "Application copiée dans /tmp/"
                '''
            }
        }
    }

    post {
        success {
            echo ' PIPELINE CI/CD RÉUSSI'
        }
        failure {
            echo '❌ PIPELINE CI/CD ÉCHOUÉ'
        }
        always {
            cleanWs()
        }
    }
}
