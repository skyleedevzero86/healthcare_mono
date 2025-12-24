pipeline {
    agent any
    
    triggers {
        pollSCM('H/5 * * * *')
    }
    
    options {
        buildDiscarder(logRotator(
            numToKeepStr: '30',
            daysToKeepStr: '7'
        ))
        timeout(time: 15, unit: 'MINUTES')
        skipStagesAfterUnstable()
    }
    
    environment {
        PROJECT_ROOT = "${WORKSPACE}"
        DEPLOY_TARGET_SERVER = "${env.DEPLOY_TARGET_SERVER ?: 'localhost'}"
        DEPLOY_TARGET_USER = "${env.DEPLOY_TARGET_USER ?: 'ec2-user'}"
        DEPLOY_TARGET_DIR = "${env.DEPLOY_TARGET_DIR ?: '/app/services'}"
        DOCKER_REGISTRY = "${env.DOCKER_REGISTRY ?: ''}"
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
            when {
                expression { env.CLEAN_BUILD == 'true' }
            }
            steps {
                script {
                    echo "========================================="
                    echo "빌드 캐시 정리 (선택적)"
                    echo "========================================="
                    
                    sh '''
                        echo "Gradle 캐시 정리 시작"
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
        
        stage('Run Tests') {
            steps {
                script {
                    echo "========================================="
                    echo "테스트 자동화 실행"
                    echo "========================================="
                    
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'service.llm',
                        'web.healthcare'
                    ]
                    
                    def testResults = [:]
                    
                    services.each { service ->
                        testResults[service] = {
                            echo ""
                            echo "----------------------------------------"
                            echo "테스트 실행 중: ${service}"
                            echo "----------------------------------------"
                            
                            dir(service) {
                                try {
                                    sh """
                                        chmod +x gradlew || true
                                        ./gradlew test --no-daemon || echo "테스트 경고 (계속 진행)"
                                    """
                                    
                                    def testReport = sh(
                                        script: "find build/test-results -name '*.xml' 2>/dev/null | head -1",
                                        returnStdout: true
                                    ).trim()
                                    
                                    if (testReport) {
                                        echo "${service} 테스트 완료: ${testReport}"
                                    } else {
                                        echo "${service}: 테스트 리포트를 찾을 수 없습니다"
                                    }
                                    
                                } catch (Exception e) {
                                    echo "${service} 테스트 실패: ${e.getMessage()}"
                                    currentBuild.result = 'UNSTABLE'
                                }
                            }
                        }
                    }
                    
                    parallel testResults
                    
                    echo ""
                    echo "----------------------------------------"
                    echo "모바일 앱 테스트 실행"
                    echo "----------------------------------------"
                    
                    dir('mobile/healthcare_mobile') {
                        try {
                            sh '''
                                pwd
                                ls -la
                                
                                if [ ! -f "package.json" ]; then
                                    echo "package.json을 찾을 수 없습니다. 현재 디렉토리: $(pwd)"
                                    exit 1
                                fi
                                
                                if ! command -v node &> /dev/null; then
                                    echo "Node.js 설치 중..."
                                    curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
                                    apt-get install -y nodejs || yum install -y nodejs npm || true
                                fi
                                
                                node --version
                                npm --version
                                
                                if ! command -v pnpm &> /dev/null; then
                                    npm install -g pnpm
                                fi
                                pnpm --version
                                
                                pnpm install --frozen-lockfile
                                
                                pnpm test:ci
                            '''
                            
                            echo "모바일 앱 테스트 완료"
                            
                        } catch (Exception e) {
                            echo "모바일 앱 테스트 실패: ${e.getMessage()}"
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    echo "========================================="
                    echo "서비스 빌드 (병렬 실행)"
                    echo "========================================="
                    
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'service.llm',
                        'web.healthcare'
                    ]
                    
                    def buildResults = [:]
                    
                    services.each { service ->
                        buildResults[service] = {
                            echo ""
                            echo "----------------------------------------"
                            echo "빌드 중: ${service}"
                            echo "----------------------------------------"
                            
                            dir(service) {
                                try {
                                    sh """
                                        chmod +x gradlew || true
                                        ./gradlew build -x test --no-daemon
                                    """
                                    
                                    def jarFiles = sh(
                                        script: "find build/libs -name '*.jar' ! -name '*-plain.jar' | head -1",
                                        returnStdout: true
                                    ).trim()
                                    
                                    if (jarFiles) {
                                        echo "${service} 빌드 성공: ${jarFiles}"
                                    } else {
                                        error "${service}: JAR 파일을 찾을 수 없습니다"
                                    }
                                    
                                } catch (Exception e) {
                                    echo "${service} 빌드 실패: ${e.getMessage()}"
                                    currentBuild.result = 'UNSTABLE'
                                }
                            }
                        }
                    }
                    
                    parallel buildResults
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    echo "========================================="
                    echo "Docker 이미지 빌드"
                    echo "========================================="
                    
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'service.llm',
                        'web.healthcare'
                    ]
                    
                    def imageResults = [:]
                    
                    services.each { service ->
                        imageResults[service] = {
                            dir(service) {
                                def jarFile = sh(
                                    script: "find build/libs -name '*.jar' ! -name '*-plain.jar' 2>/dev/null | head -1",
                                    returnStdout: true
                                ).trim()
                                
                                if (jarFile && fileExists('Dockerfile')) {
                                    def imageName = "${service}:${env.BUILD_NUMBER}"
                                    def imageTag = "${service}:latest"
                                    
                                    echo "Docker 이미지 빌드: ${service}"
                                    sh """
                                        docker build -t ${imageName} -t ${imageTag} .
                                    """
                                    echo "${service} Docker 이미지 빌드 성공: ${imageName}"
                                } else {
                                    echo "${service}: Dockerfile이 없거나 JAR 파일을 찾을 수 없습니다"
                                }
                            }
                        }
                    }
                    
                    parallel imageResults
                }
            }
        }
        
        stage('Build Mobile App') {
            steps {
                script {
                    echo "========================================="
                    echo "모바일 앱 빌드 (React Native/Expo)"
                    echo "========================================="
                    
                    dir('mobile/healthcare_mobile') {
                        try {
                            sh '''
                                pwd
                                ls -la
                                
                                if [ ! -f "package.json" ]; then
                                    echo "package.json을 찾을 수 없습니다. 현재 디렉토리: $(pwd)"
                                    exit 1
                                fi
                                
                                if ! command -v node &> /dev/null; then
                                    echo "Node.js 설치 중..."
                                    curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
                                    apt-get install -y nodejs || yum install -y nodejs npm || true
                                fi
                                
                                node --version
                                npm --version
                                
                                if ! command -v pnpm &> /dev/null; then
                                    npm install -g pnpm
                                fi
                                pnpm --version
                                
                                pnpm install --frozen-lockfile
                                
                                pnpm tsc --noEmit
                            '''
                            
                            echo "모바일 앱 빌드 준비 완료"
                            
                        } catch (Exception e) {
                            echo "모바일 앱 빌드 실패: ${e.getMessage()}"
                            currentBuild.result = 'UNSTABLE'
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
                        'service.llm',
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
                                echo "${service}: ${jarFile}"
                                successCount++
                            } else {
                                echo "${service}: 빌드 실패"
                                failCount++
                            }
                        }
                    }
                    
                    echo ""
                    echo "성공: ${successCount}, 실패: ${failCount}"
                }
            }
        }
        
        stage('Push Docker Images') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "Docker 이미지 레지스트리 푸시"
                    echo "========================================="
                    
                    def dockerRegistry = env.DOCKER_REGISTRY ?: ''
                    def services = [
                        'service.discovery',
                        'service.config',
                        'api.gateway',
                        'service.auth',
                        'service.comm',
                        'service.healthcare',
                        'service.usermanagement',
                        'service.llm',
                        'web.healthcare'
                    ]
                    
                    if (dockerRegistry) {
                        services.each { service ->
                            def imageName = "${dockerRegistry}/${service}:${env.BUILD_NUMBER}"
                            def imageLatest = "${dockerRegistry}/${service}:latest"
                            
                            sh """
                                docker tag ${service}:latest ${imageName} || true
                                docker tag ${service}:latest ${imageLatest} || true
                                docker push ${imageName} || echo 'Push 실패 (무시)'
                                docker push ${imageLatest} || echo 'Push 실패 (무시)'
                            """
                            echo "${service} 이미지 푸시: ${imageName}"
                        }
                    } else {
                        echo "DOCKER_REGISTRY가 설정되지 않아 이미지 푸시를 건너뜁니다"
                    }
                }
            }
        }
        
        stage('Deploy to Server') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "서버 무중단 배포"
                    echo "========================================="
                    
                    def deployServer = env.DEPLOY_TARGET_SERVER ?: 'localhost'
                    def dockerRegistry = env.DOCKER_REGISTRY ?: ''
                    
                    if (deployServer != 'localhost') {
                        def services = [
                            'service.discovery',
                            'service.config',
                            'api.gateway',
                            'service.auth',
                            'service.comm',
                            'service.healthcare',
                            'service.usermanagement',
                            'service.llm',
                            'web.healthcare'
                        ]
                        
                        if (dockerRegistry) {
                            echo "Docker 레지스트리에서 이미지 pull 후 배포"
                        } else {
                            echo "로컬 이미지를 서버로 전송 후 배포"
                            
                            services.each { service ->
                                def imageName = "${service}:latest"
                                def imageFile = "/tmp/${service}-${env.BUILD_NUMBER}.tar"
                                
                                echo "이미지 저장: ${service}"
                                sh """
                                    docker save ${imageName} -o ${imageFile} || echo '이미지 저장 실패'
                                """
                                
                                if (fileExists(imageFile)) {
                                    echo "이미지 전송: ${service}"
                                    sh """
                                        scp -i ${env.DEPLOY_SSH_KEY ?: '~/.ssh/id_rsa'} ${imageFile} ${env.DEPLOY_TARGET_USER}@${deployServer}:/tmp/ || echo '이미지 전송 실패'
                                        ssh -i ${env.DEPLOY_SSH_KEY ?: '~/.ssh/id_rsa'} ${env.DEPLOY_TARGET_USER}@${deployServer} "docker load -i /tmp/${service}-${env.BUILD_NUMBER}.tar && rm -f /tmp/${service}-${env.BUILD_NUMBER}.tar" || echo '이미지 로드 실패'
                                    """
                                    sh "rm -f ${imageFile}"
                                }
                            }
                        }
                        
                        dir('scripts') {
                            sh """
                                chmod +x deploy-docker.sh
                                BUILD_NUMBER=${env.BUILD_NUMBER} DOCKER_REGISTRY='${dockerRegistry}' ./deploy-docker.sh --all
                            """
                        }
                    } else {
                        echo "DEPLOY_TARGET_SERVER가 localhost로 설정되어 배포를 건너뜁니다"
                        echo "실제 서버 배포를 원하시면 Jenkins 환경 변수에 DEPLOY_TARGET_SERVER를 설정하세요"
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
                        ['service.llm', '8086'],
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
                                echo "${service} (포트 ${port}): 정상"
                            } else {
                                echo "${service} (포트 ${port}): 헬스 체크 실패"
                            }
                        } catch (Exception e) {
                            echo "${service} (포트 ${port}): 헬스 체크 오류"
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
                
                try {
                    archiveArtifacts artifacts: '**/build/test-results/**/*.xml', fingerprint: true, allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/build/reports/tests/**/*', fingerprint: true, allowEmptyArchive: true
                    archiveArtifacts artifacts: 'mobile/**/coverage/**/*', fingerprint: true, allowEmptyArchive: true
                } catch (Exception e) {
                    echo "테스트 리포트 아카이브 실패 (무시): ${e.getMessage()}"
                }
                
                try {
                    junit '**/build/test-results/**/*.xml'
                } catch (Exception e) {
                    echo "JUnit 리포트 발행 실패 (무시): ${e.getMessage()}"
                }
            }
        }
        
        success {
            echo "========================================="
            echo "빌드 성공"
            echo "========================================="
        }
        
        failure {
            echo "========================================="
            echo "빌드 실패"
            echo "========================================="
        }
        
        unstable {
            echo "========================================="
            echo "빌드 불안정"
            echo "========================================="
        }
    }
}

