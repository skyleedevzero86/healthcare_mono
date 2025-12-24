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
        FAIL_TESTS_ON_FAILURE = "${env.FAIL_TESTS_ON_FAILURE ?: 'false'}"
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
                    def failedServices = []
                    def testSummary = [:]
                    
                    services.each { service ->
                        testResults[service] = {
                            echo ""
                            echo "----------------------------------------"
                            echo "테스트 실행 중: ${service}"
                            echo "----------------------------------------"
                            
                            dir(service) {
                                try {
                                    def testOutput = sh(
                                        script: """
                                            chmod +x gradlew || true
                                            ./gradlew test --no-daemon 2>&1 || echo "EXIT_CODE:1"
                                        """,
                                        returnStdout: true
                                    )
                                    
                                    def testReport = sh(
                                        script: "find build/test-results -name '*.xml' 2>/dev/null | head -1",
                                        returnStdout: true
                                    ).trim()
                                    
                                    if (testReport) {
                                        def stats = sh(
                                            script: """
                                                if [ -f "${testReport}" ]; then
                                                    tests=\$(grep -oP 'tests=\"\\K[0-9]+' "${testReport}" | head -1 || echo 0)
                                                    failures=\$(grep -oP 'failures=\"\\K[0-9]+' "${testReport}" | head -1 || echo 0)
                                                    errors=\$(grep -oP 'errors=\"\\K[0-9]+' "${testReport}" | head -1 || echo 0)
                                                    skipped=\$(grep -oP 'skipped=\"\\K[0-9]+' "${testReport}" | head -1 || echo 0)
                                                    echo "\${tests}:\${failures}:\${errors}:\${skipped}"
                                                else
                                                    echo "0:0:0:0"
                                                fi
                                            """,
                                            returnStdout: true
                                        ).trim()
                                        
                                        def (total, failures, errors, skipped) = stats.split(':')
                                        def passed = (total as Integer) - (failures as Integer) - (errors as Integer) - (skipped as Integer)
                                        
                                        testSummary[service] = [
                                            total: total,
                                            passed: passed,
                                            failed: (failures as Integer) + (errors as Integer),
                                            skipped: skipped
                                        ]
                                        
                                        if ((failures as Integer) > 0 || (errors as Integer) > 0) {
                                            failedServices.add(service)
                                            echo "${service} 테스트 실패: 총 ${total}개 중 ${failures + errors}개 실패"
                                            
                                            def failedTestDetails = sh(
                                                script: """
                                                    if [ -f "${testReport}" ]; then
                                                        grep -A 10 '<testcase' "${testReport}" | grep -B 5 '<failure\\|<error' | head -30 || echo "상세 정보 없음"
                                                    fi
                                                """,
                                                returnStdout: true
                                            ).trim()
                                            
                                            if (failedTestDetails && failedTestDetails != "상세 정보 없음") {
                                                echo "실패한 테스트 상세:"
                                                echo failedTestDetails
                                            }
                                            
                                            currentBuild.result = 'UNSTABLE'
                                        } else {
                                            echo "${service} 테스트 성공: 총 ${total}개 중 ${passed}개 통과"
                                        }
                                    } else {
                                        echo "${service}: 테스트 리포트를 찾을 수 없습니다"
                                        testSummary[service] = [total: 0, passed: 0, failed: 0, skipped: 0]
                                    }
                                    
                                    if (testOutput.contains("EXIT_CODE:1") || testOutput.contains("FAILED")) {
                                        if (!failedServices.contains(service)) {
                                            failedServices.add(service)
                                        }
                                    }
                                    
                                } catch (Exception e) {
                                    echo "${service} 테스트 실행 중 오류: ${e.getMessage()}"
                                    failedServices.add(service)
                                    testSummary[service] = [total: 0, passed: 0, failed: 1, skipped: 0]
                                    currentBuild.result = 'UNSTABLE'
                                }
                            }
                        }
                    }
                    
                    parallel testResults
                    
                    echo ""
                    echo "========================================="
                    echo "테스트 결과 요약"
                    echo "========================================="
                    
                    def totalTests = 0
                    def totalPassed = 0
                    def totalFailed = 0
                    def totalSkipped = 0
                    
                    testSummary.each { service, stats ->
                        totalTests += stats.total as Integer
                        totalPassed += stats.passed as Integer
                        totalFailed += stats.failed as Integer
                        totalSkipped += stats.skipped as Integer
                        
                        def status = stats.failed > 0 ? "[FAIL]" : "[PASS]"
                        echo "${status} ${service}: 총 ${stats.total}개 (통과: ${stats.passed}, 실패: ${stats.failed}, 건너뜀: ${stats.skipped})"
                    }
                    
                    echo ""
                    echo "전체 통계:"
                    echo "  총 테스트: ${totalTests}개"
                    echo "  통과: ${totalPassed}개"
                    echo "  실패: ${totalFailed}개"
                    echo "  건너뜀: ${totalSkipped}개"
                    
                    if (totalTests > 0) {
                        def failRate = (totalFailed * 100.0 / totalTests).round(2)
                        echo "  실패율: ${failRate}%"
                        
                        if (failRate > 20) {
                            echo ""
                            echo "경고: 실패율이 20%를 초과합니다!"
                        }
                    }
                    
                    if (failedServices.size() > 0) {
                        echo ""
                        echo "========================================="
                        echo "테스트 실패한 서비스 목록"
                        echo "========================================="
                        failedServices.each { service ->
                            echo "  - ${service}"
                        }
                        echo "총 ${failedServices.size()}개 서비스에서 테스트 실패"
                        
                        if (env.FAIL_TESTS_ON_FAILURE == 'true') {
                            error("테스트 실패로 인해 빌드 중단 (FAIL_TESTS_ON_FAILURE=true)")
                        }
                    }
                    
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
                
                try {
                    def failureReport = sh(
                        script: """
                            echo "========================================="
                            echo "실패한 테스트 상세 리포트"
                            echo "========================================="
                            echo ""
                            
                            total_failures=0
                            for xml_file in \$(find . -path '*/build/test-results/*.xml' 2>/dev/null); do
                                if [ -f "\$xml_file" ]; then
                                    failures=\$(grep -oP 'failures=\"\\K[0-9]+' "\$xml_file" | head -1 || echo 0)
                                    errors=\$(grep -oP 'errors=\"\\K[0-9]+' "\$xml_file" | head -1 || echo 0)
                                    total=\$((failures + errors))
                                    
                                    if [ \$total -gt 0 ]; then
                                        total_failures=\$((total_failures + total))
                                        echo "파일: \${xml_file}"
                                        echo "실패: \${failures}개, 오류: \${errors}개"
                                        
                                        grep -oP '<testcase[^>]*name=\"\\K[^\"]+' "\$xml_file" | while read testname; do
                                            if grep -A 20 "name=\"\${testname}\"" "\$xml_file" | grep -q '<failure\\|<error'; then
                                                echo "  - \${testname}"
                                            fi
                                        done
                                        echo "---"
                                    fi
                                fi
                            done
                            
                            if [ \$total_failures -eq 0 ]; then
                                echo "실패한 테스트가 없습니다."
                            else
                                echo ""
                                echo "총 실패한 테스트: \$total_failures개"
                            fi
                        """,
                        returnStdout: true
                    )
                    echo failureReport
                } catch (Exception e) {
                    echo "실패 리포트 생성 실패 (무시): ${e.getMessage()}"
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

