pipeline {
    agent any
    
    options {
        buildDiscarder(logRotator(
            numToKeepStr: '30',
            daysToKeepStr: '7'
        ))
        timeout(time: 30, unit: 'MINUTES')
    }
    
    environment {
        PROJECT_ROOT = "${WORKSPACE}"
        DEPLOY_TARGET_SERVER = 'localhost'
        DEPLOY_TARGET_USER = 'ec2-user'
        DEPLOY_TARGET_DIR = '/app/services'
    }
    
    tools {
        jdk 'jdk21'
        gradle 'Gradle'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "========================================="
                    echo "소스코드 체크아웃"
                    echo "========================================="
                    echo "Branch: ${env.BRANCH_NAME}"
                    echo "Commit: ${env.GIT_COMMIT}"
                }
                
                checkout scm
                
                script {
                    sh 'git log -1 --pretty=format:"%h - %an, %ar : %s"'
                }
            }
        }
        
        stage('Clean Build Cache') {
            steps {
                script {
                    echo "========================================="
                    echo "빌드 캐시 정리"
                    echo "========================================="
                    
                    sh '''
                        echo "Gradle 캐시 정리 시작"
                        find . -name ".gradle" -type d -exec rm -rf {} + 2>/dev/null || true
                        find . -name "build" -type d -exec rm -rf {} + 2>/dev/null || true
                        find . -name "out" -type d -exec rm -rf {} + 2>/dev/null || true
                        find . -name "bin" -type d -exec rm -rf {} + 2>/dev/null || true
                        echo "빌드 캐시 정리 완료"
                    '''
                }
            }
        }
        
        stage('Remove BOM') {
            steps {
                script {
                    echo "========================================="
                    echo "BOM 문자 제거"
                    echo "========================================="
                    
                    sh '''
                        echo "BOM 제거 시작"
                        find . -name "*.java" -type f | while read -r file; do
                            if [ -f "$file" ] && head -c 3 "$file" | od -An -tx1 | grep -q "ef bb bf"; then
                                perl -0777 -i -pe "s/^\\xEF\\xBB\\xBF//" "$file"
                                echo "BOM 제거: $file"
                            fi
                        done
                        echo "BOM 제거 완료"
                    '''
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    echo "========================================="
                    echo "서비스 빌드 (순차 실행)"
                    echo "========================================="
                    
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'web.healthcare'
                    ]
                    
                    services.each { service ->
                        echo ""
                        echo "----------------------------------------"
                        echo "빌드 중: ${service}"
                        echo "----------------------------------------"
                        
                        dir(service) {
                            try {
                                sh """
                                    chmod +x gradlew || true
                                    ./gradlew clean --no-daemon || true
                                    rm -rf .gradle build out bin 2>/dev/null || true
                                    ./gradlew build -x test --no-daemon --refresh-dependencies
                                """
                                
                                def jarFiles = sh(
                                    script: "find build/libs -name '*.jar' ! -name '*-plain.jar' | head -1",
                                    returnStdout: true
                                ).trim()
                                
                                if (jarFiles) {
                                    echo "✓ ${service} 빌드 성공: ${jarFiles}"
                                } else {
                                    error "${service}: JAR 파일을 찾을 수 없습니다"
                                }
                                
                            } catch (Exception e) {
                                echo "✗ ${service} 빌드 실패: ${e.getMessage()}"
                                currentBuild.result = 'UNSTABLE'
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Summary') {
            steps {
                script {
                    echo "========================================="
                    echo "빌드 결과 요약"
                    echo "========================================="
                    
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'web.healthcare'
                    ]
                    
                    def successCount = 0
                    def failCount = 0
                    
                    services.each { service ->
                        dir(service) {
                            def jarFile = sh(
                                script: "find build/libs -name '*.jar' ! -name '*-plain.jar' 2>/dev/null | head -1",
                                returnStdout: true
                            ).trim()
                            
                            if (jarFile) {
                                echo "✓ ${service}: ${jarFile}"
                                successCount++
                            } else {
                                echo "✗ ${service}: 빌드 실패"
                                failCount++
                            }
                        }
                    }
                    
                    echo ""
                    echo "성공: ${successCount}, 실패: ${failCount}"
                }
            }
        }
        
        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            
            steps {
                script {
                    echo "========================================="
                    echo "서비스 배포"
                    echo "========================================="
                    
                    dir('scripts') {
                        sh """
                            chmod +x deploy.sh
                            ./deploy.sh --all
                        """
                    }
                }
            }
        }
        
        stage('Health Check') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            
            steps {
                script {
                    echo "========================================="
                    echo "서비스 헬스 체크"
                    echo "========================================="
                    
                    def healthChecks = [
                        ['service.discovery', '8761'],
                        ['service.config', '8888'],
                        ['api.gateway', '8080'],
                        ['service.auth', '8082'],
                        ['service.comm', '8085'],
                        ['service.healthcare', '8084'],
                        ['service.usermanagement', '8087'],
                        ['web.healthcare', '8981']
                    ]
                    
                    def server = env.DEPLOY_TARGET_SERVER ?: 'localhost'
                    
                    healthChecks.each { service, port ->
                        def healthUrl = "http://${server}:${port}/actuator/health"
                        
                        try {
                            def response = sh(
                                script: "curl -f -s ${healthUrl} || echo 'FAILED'",
                                returnStdout: true
                            ).trim()
                            
                            if (response != 'FAILED' && response.contains('"status":"UP"')) {
                                echo "✓ ${service} (포트 ${port}): 정상"
                            } else {
                                echo "✗ ${service} (포트 ${port}): 헬스 체크 실패"
                            }
                        } catch (Exception e) {
                            echo "✗ ${service} (포트 ${port}): 헬스 체크 오류"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "========================================="
                echo "빌드 정리"
                echo "========================================="
                
                try {
                    archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true, allowEmptyArchive: true
                } catch (Exception e) {
                    echo "아티팩트 아카이브 실패 (무시): ${e.getMessage()}"
                }
            }
        }
        
        success {
            echo "========================================="
            echo "✓ 빌드 성공"
            echo "========================================="
        }
        
        failure {
            echo "========================================="
            echo "✗ 빌드 실패"
            echo "========================================="
        }
        
        unstable {
            echo "========================================="
            echo "⚠ 빌드 불안정"
            echo "========================================="
        }
    }
}

