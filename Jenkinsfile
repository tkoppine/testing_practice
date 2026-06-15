pipeline {

    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'Java-17'
    }

    environment {
        APP_NAME        = 'calculator-app'
        DOCKER_HUB_USER = 'tkoppine'
        DOCKER_IMAGE    = "${DOCKER_HUB_USER}/${APP_NAME}"
        DOCKER_TAG      = "${BUILD_NUMBER}"
        DOCKER_CREDS    = credentials('dockerhub-credentials')
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        // ----------------------------------------
        // STAGE 1 - CHECKOUT
        // ----------------------------------------
        stage('Checkout') {
            steps {
                echo '========================================'
                echo '  STAGE 1 : CHECKOUT CODE'
                echo '========================================'
                checkout scm
                echo "Branch : ${env.GIT_BRANCH}"
                echo "Commit : ${env.GIT_COMMIT}"
                echo 'Checkout completed.'
            }
        }

        // ----------------------------------------
        // STAGE 2 - ENVIRONMENT SETUP
        // ----------------------------------------
        stage('Environment Setup') {
            when { anyOf { branch 'main'; changeRequest() } }
            steps {
                echo '========================================'
                echo '  STAGE 2 : ENVIRONMENT SETUP'
                echo '========================================'
                sh '''
                    echo "--- Java Version ---"
                    java -version

                    echo "--- Maven Version ---"
                    mvn -version

                    echo "--- Docker Version ---"
                    docker --version
                '''
                echo 'Environment verified.'
            }
        }

        // ----------------------------------------
        // STAGE 3 - UNIT TESTS
        // Tests Calculator class in isolation
        // No Spring, No DB, No Mocks
        // ----------------------------------------
        stage('Unit Tests') {
            when { anyOf { branch 'main'; changeRequest() } }
            steps {
                echo '========================================'
                echo '  STAGE 3 : UNIT TESTS'
                echo '  Class  : CalculatorTest'
                echo '  No Spring | No DB | No Mocks'
                echo '========================================'
                sh 'mvn test -Dtest=CalculatorTest 2>&1'
            }
            post {
                always  { junit '**/target/surefire-reports/TEST-*CalculatorTest*.xml' }
                success { echo 'Unit tests PASSED.' }
                failure { echo 'Unit tests FAILED.' }
            }
        }

        // ----------------------------------------
        // STAGE 4 - MOCK TESTS
        // Tests CalculatorService with Mockito mocks
        // No Spring, No DB
        // ----------------------------------------
        stage('Mock Tests') {
            when { anyOf { branch 'main'; changeRequest() } }
            steps {
                echo '========================================'
                echo '  STAGE 4 : MOCK TESTS'
                echo '  Class  : CalculatorMockTest'
                echo '  Mockito only | No Spring | No DB'
                echo '========================================'
                sh 'mvn test -Dtest=CalculatorMockTest 2>&1'
            }
            post {
                always  { junit '**/target/surefire-reports/TEST-*CalculatorMockTest*.xml' }
                success { echo 'Mock tests PASSED.' }
                failure { echo 'Mock tests FAILED.' }
            }
        }

        // ----------------------------------------
        // STAGE 5 - INTEGRATION TESTS
        // Tests real CalculatorService + real Calculator
        // No mocks, No DB, No Spring
        // ----------------------------------------
        stage('Integration Tests') {
            when { anyOf { branch 'main'; changeRequest() } }
            steps {
                echo '========================================'
                echo '  STAGE 5 : INTEGRATION TESTS'
                echo '  Class  : CalculatorServiceIntegrationTest'
                echo '  Real components | No mocks | No DB'
                echo '========================================'
                sh 'mvn test -Dtest=CalculatorServiceIntegrationTest 2>&1'
            }
            post {
                always  { junit '**/target/surefire-reports/TEST-*CalculatorServiceIntegrationTest*.xml' }
                success { echo 'Integration tests PASSED.' }
                failure { echo 'Integration tests FAILED.' }
            }
        }

        // ----------------------------------------
        // STAGE 6 - FULL E2E TESTS
        // Testcontainers starts real PostgreSQL Docker container
        // Spring Boot starts real embedded Tomcat server
        // REST Assured makes real HTTP calls to the API
        // DB assertions verify data was saved correctly
        // ----------------------------------------
        stage('Full E2E Tests') {
            when {
                expression { return false }  // skipped: Testcontainers needs Docker-in-Docker setup
            }
            steps {
                echo 'E2E tests skipped in CI - requires Docker-in-Docker configuration.'
            }
        }

        // ----------------------------------------
        // STAGE 8 - BUILD JAR ARTIFACT
        // Skips tests (already ran above)
        // Output: target/calculator-1.0-SNAPSHOT.jar
        // ----------------------------------------
        stage('Build JAR') {
            when { branch 'main' }
            steps {
                echo '========================================'
                echo '  STAGE 8 : BUILD JAR ARTIFACT'
                echo '  Command: mvn package -DskipTests'
                echo '========================================'
                sh 'mvn package -DskipTests 2>&1'
                sh 'ls -lh target/*.jar'
                echo 'JAR built successfully.'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    echo 'JAR archived in Jenkins.'
                }
                failure { echo 'JAR build FAILED.' }
            }
        }

        // ----------------------------------------
        // STAGE 9 - BUILD DOCKER IMAGE
        // Uses Dockerfile in project root
        // Tags as: yourdockerhub/calculator-app:<build-number>
        // ----------------------------------------
        stage('Build Docker Image') {
            when { branch 'main' }
            steps {
                echo '========================================'
                echo '  STAGE 9 : BUILD DOCKER IMAGE'
                echo "  Image : ${DOCKER_IMAGE}:${DOCKER_TAG}"
                echo '========================================'
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                sh "docker images | grep ${APP_NAME}"
                echo 'Docker image built successfully.'
            }
            post {
                success { echo "Image ${DOCKER_IMAGE}:${DOCKER_TAG} created." }
                failure { echo 'Docker image build FAILED. Check Dockerfile.' }
            }
        }

        // ----------------------------------------
        // STAGE 10 - PUSH TO DOCKER HUB
        // Pushes :<build-number> and :latest tags
        // Requires: dockerhub-credentials in Jenkins
        // ----------------------------------------
        stage('Push to Docker Hub') {
            when { branch 'main' }
            steps {
                echo '========================================'
                echo '  STAGE 10 : PUSH IMAGE TO DOCKER HUB'
                echo "  Pushing : ${DOCKER_IMAGE}:${DOCKER_TAG}"
                echo "  Pushing : ${DOCKER_IMAGE}:latest"
                echo '========================================'
                sh "echo ${DOCKER_CREDS_PSW} | docker login -u ${DOCKER_CREDS_USR} --password-stdin"
                sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
                echo 'Image pushed to Docker Hub successfully.'
            }
            post {
                always  { sh 'docker logout' }
                success { echo "Image available at: https://hub.docker.com/r/${DOCKER_IMAGE}" }
                failure { echo 'Push to Docker Hub FAILED. Check dockerhub-credentials in Jenkins.' }
            }
        }
    }

    // ----------------------------------------
    // POST PIPELINE - runs after all stages
    // ----------------------------------------
    post {
        success {
            echo '========================================'
            echo '  PIPELINE COMPLETED SUCCESSFULLY'
            echo "  Build : #${BUILD_NUMBER}"
            echo "  Image : ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo '========================================'
        }
        failure {
            echo '========================================'
            echo '  PIPELINE FAILED'
            echo "  Build : #${BUILD_NUMBER}"
            echo '  Check stage logs above for details.'
            echo '========================================'
        }
        always {
            echo 'Cleaning up local Docker images...'
            sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
            sh "docker rmi ${DOCKER_IMAGE}:latest || true"
            cleanWs()
            echo 'Workspace cleaned.'
        }
    }
}