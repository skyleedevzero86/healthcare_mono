# healthcare_mono

![System Architecture](https://github.com/user-attachments/assets/a2a4ebde-590b-4b29-81d2-6c5b06395ef9)

## 📋 프로젝트 개요

`healthcare_mono`는 스마트워치를 통해 수집된 사용자의 생체 데이터를 활용하여 실시간 건강 모니터링과 AI 기반 건강 분석을 제공하는 헬스케어 플랫폼입니다. 웹 애플리케이션과 모바일 앱을 통해 다양한 플랫폼에서 건강 데이터를 관리하고 분석할 수 있습니다.

### 비즈니스 모델

- **B2C (Business-to-Consumer)**: 개인 사용자(환자) 중심의 헬스케어 서비스
- **주요 타겟**: 스마트워치를 사용하는 개인 사용자
- **서비스 제공**: 실시간 건강 모니터링, AI 기반 건강 분석, 커뮤니티 기능

### 핵심 가치

- **실시간 모니터링**: 스마트워치 데이터를 통한 24/7 건강 상태 추적
- **AI 기반 분석**: ChatGPT API를 활용한 개인화된 건강 조언
- **다중 플랫폼**: 웹과 모바일을 통한 통합된 사용자 경험
- **확장 가능한 아키텍처**: 마이크로서비스 기반의 유연한 시스템 구조

## 🏗️ 시스템 아키텍처

### MSA (Microservice Architecture) 구조

이 프로젝트는 마이크로서비스 아키텍처를 기반으로 구성되어 있으며, 각 서비스는 독립적으로 개발, 배포, 확장이 가능합니다.

#### 서비스 구성 및 포트

| 서비스                    | 포트 | 설명                        | 주요 기술                         |
| ------------------------- | ---- | --------------------------- | --------------------------------- |
| **API Gateway**           | 8080 | 모든 서비스의 단일 진입점   | Spring Cloud Gateway (WebFlux)    |
| **Service Discovery**     | 8761 | 서비스 등록 및 발견         | Netflix Eureka Server             |
| **Config Service**        | 8888 | 중앙화된 설정 관리          | Spring Cloud Config Server        |
| **Auth Service**          | 8082 | 사용자 인증 및 권한 관리    | Spring Boot, JWT, Spring Security |
| **User Management**       | 8087 | 사용자 정보 관리            | Spring Boot, MyBatis              |
| **Healthcare Service**    | 8084 | 건강 데이터 처리 및 AI 분석 | Spring Boot, MyBatis, ChatGPT API |
| **LLM Service**           | 8086 | LLM 모델 서비스             | Spring Boot, Llama.cpp            |
| **Communication Service** | 8085 | 커뮤니티 기능               | Spring Boot, JPA                  |
| **Web Application**       | 8981 | 웹 사용자 인터페이스        | Spring Boot, JSP, Apache Tiles    |
| **Mobile App**            | -    | 모바일 애플리케이션         | React Native, Expo SDK 54         |

### 기술 스택

#### Backend

- **Framework**: Spring Boot 3.5.6, Spring Cloud 2025.0.0
- **Language**: Java 21
- **Database**: PostgreSQL (운영), H2 (개발용)
- **ORM**: MyBatis (XML Mapper), JPA
- **Build Tool**: Gradle 8.x

#### Frontend

- **Web**: HTML5, CSS3, JavaScript (ES6+), jQuery, JSP, Apache Tiles
- **Mobile**: React Native 0.81.4, Expo SDK 54, TypeScript

#### 인프라 및 미들웨어

- **API Gateway**: Spring Cloud Gateway (WebFlux, Reactive)
- **Service Discovery**: Netflix Eureka
- **Configuration**: Spring Cloud Config Server
- **Message Queue**: RabbitMQ (Event-Driven Architecture)
- **Cache**: Redis (Token Blacklist, Rate Limiting)
- **Circuit Breaker**: Resilience4j
- **Distributed Tracing**: Zipkin, Brave
- **Monitoring**: Spring Boot Actuator, Micrometer
- **CI/CD**: Jenkins
- **Container**: Docker
- **Reverse Proxy**: Nginx

#### 보안 및 인증

- **Authentication**: JWT (jjwt 0.12.3)
- **Encryption**: AES-256
- **Security**: Spring Security
- **Input Validation**: Spring Validation, Custom Sanitizer

#### 로깅

- **Logging Framework**: Log4j2
- **SQL Logging**: Log4jdbc, P6Spy

## 👥 사용자 역할

### 1. 환자 (Patient)

- 개인 건강 데이터 조회 및 모니터링
- AI 기반 건강 분석 및 조언 수신
- 커뮤니티 참여

### 2. 보호자 (Guardian)

- 환자의 건강 상태 모니터링
- 환자와의 연결 및 관리

### 3. 의사 (Doctor)

- 환자 건강 데이터 분석
- 환자 관리 및 상담

## 🏥 주요 기능

### 1. 건강 데이터 모니터링

- **실시간 생체 데이터 수집**
  - 심박수 (Heart Rate)
  - 체온 (Temperature)
  - 혈압 (Blood Pressure)
  - 산소포화도 (SpO2)
  - 스트레스 지수
  - 수면 패턴
  - 운동량 (걸음수)
  - 호흡수

### 2. 대시보드 및 차트

- 실시간 건강 상태 대시보드
- 기간별 건강 데이터 차트 (1일, 1주일, 1개월, 1년)
- 건강 점수 계산 및 표시
- 목표 설정 및 달성률 추적

### 3. AI 기반 건강 분석

- ChatGPT API를 활용한 개인화된 건강 조언
- 생체 데이터 기반 질병 예방 정보 제공
- 맞춤형 음식 추천
- 건강 상태별 주의사항 안내

### 4. 커뮤니티 기능

- 연령대별 건강 정보 공유
- 사용자 간 건강 경험담 교환
- AI 의사 상담 기능

### 5. 사용자 관리

- 회원가입 및 로그인 (환자/보호자/의사 구분)
- 프로필 관리
- 보호자-환자 연결 관리
- 의사-환자 매핑

### 6. 모바일 앱 기능

- 건강 데이터 실시간 동기화
- 오프라인 데이터 저장 (AsyncStorage)
- 푸시 알림 (건강 이상 징후 감지 시)
- 카메라를 통한 건강 상태 기록
- 위치 기반 건강 데이터 수집
- 네트워크 상태 모니터링

## 📁 프로젝트 구조

```
healthcare_mono/
├── api.gateway/                 # API 게이트웨이 (Spring Cloud Gateway)
├── service.discovery/           # 서비스 디스커버리 (Eureka Server)
├── service.config/              # 설정 관리 서비스 (Config Server)
├── service.auth/                # 인증 서비스 (JWT)
├── service.usermanagement/      # 사용자 관리 서비스
├── service.healthcare/          # 헬스케어 서비스 (AI 통합)
├── service.llm/                 # LLM 모델 서비스
├── service.comm/                # 커뮤니티 서비스
├── web.healthcare/              # 웹 애플리케이션
│   ├── src/main/java/           # Java 소스코드
│   └── src/main/webapp/         # 웹 리소스
│       ├── WEB-INF/jsp/         # JSP 페이지
│       │   ├── user/            # 사용자 관련 페이지
│       │   ├── health/          # 건강 정보 페이지
│       │   ├── community/       # 커뮤니티 페이지
│       │   └── include/         # 공통 포함 파일
│       ├── css/                 # 스타일시트
│       ├── js/                  # JavaScript 파일
│       └── images/              # 이미지 리소스
├── mobile/healthcare_mobile/    # 모바일 애플리케이션
│   ├── src/                     # React Native 소스코드
│   ├── assets/                  # 이미지 및 리소스
│   ├── android/                 # Android 네이티브 코드
│   ├── App.tsx                  # 메인 앱 컴포넌트
│   └── package.json             # Node.js 의존성
├── scripts/                     # 배포 및 운영 스크립트
│   ├── deploy-docker.sh         # Docker 무중단 배포 스크립트
│   ├── deploy.sh                # JAR 파일 배포 스크립트
│   ├── jenkins-setup.sh         # Jenkins 초기 설정 스크립트
│   ├── fix-bom.sh               # BOM 문자 제거 스크립트
│   ├── prod/                    # 운영 환경 설정
│   │   ├── setup-nginx.sh       # Nginx 설정 스크립트
│   │   ├── nginx.conf           # Nginx 설정 파일
│   │   └── docker-compose.prod.yml  # 프로덕션 Docker Compose
│   └── docs/                    # 배포 및 설정 문서
│       ├── DEPLOYMENT_GUIDE.md  # 배포 가이드
│       ├── DOMAIN_DEPLOYMENT_GUIDE.md  # 도메인 연결 가이드
│       └── JENKINS_SETUP.md     # Jenkins 설정 가이드
└── Jenkinsfile                  # Jenkins CI/CD 파이프라인
```

## 🔧 개발 환경 설정

### 필수 요구사항

- **Backend**:
  - Java 21+
  - PostgreSQL / H2 (개발용)
  - Spring Boot 3.5.6+
  - Gradle 8.x+
- **Frontend (Web)**:
  - Node.js 18+ (빌드용)
- **Frontend (Mobile)**:
  - Node.js 18+
  - pnpm 9.x+
  - Expo CLI
  - Android Studio (Android 개발용)
  - Xcode (iOS 개발용, macOS만)

### 실행 방법

#### 사전 준비사항

1. **데이터베이스 설정**

   ```bash
   # PostgreSQL 설치 및 데이터베이스 생성
   # 또는 H2 인메모리 데이터베이스 사용 (개발용)
   ```

2. **Redis 설치 및 실행**

   ```bash
   # Redis 서버 실행 (토큰 블랙리스트, Rate Limiting용)
   redis-server
   ```

3. **RabbitMQ 설치 및 실행**
   ```bash
   # RabbitMQ 서버 실행 (Event-Driven Architecture용)
   rabbitmq-server
   ```

#### 백엔드 서비스 실행 순서

서비스는 다음 순서로 실행해야 합니다:

1. **Config Service** (`service.config`) - 포트 8888

   ```bash
   cd service.config
   ./gradlew bootRun
   ```

2. **Discovery Service** (`service.discovery`) - 포트 8761

   ```bash
   cd service.discovery
   ./gradlew bootRun
   # Eureka Dashboard: http://localhost:8761
   ```

3. **Auth Service** (`service.auth`) - 포트 8082

   ```bash
   cd service.auth
   ./gradlew bootRun
   ```

4. **User Management Service** (`service.usermanagement`) - 포트 8087

   ```bash
   cd service.usermanagement
   ./gradlew bootRun
   ```

5. **Healthcare Service** (`service.healthcare`) - 포트 8084

   ```bash
   cd service.healthcare
   ./gradlew bootRun
   ```

6. **LLM Service** (`service.llm`) - 포트 8086

   ```bash
   cd service.llm
   ./gradlew bootRun
   ```

7. **Communication Service** (`service.comm`) - 포트 8085

   ```bash
   cd service.comm
   ./gradlew bootRun
   ```

8. **API Gateway** (`api.gateway`) - 포트 8080

   ```bash
   cd api.gateway
   ./gradlew bootRun
   # API Gateway: http://localhost:8080
   ```

9. **Web Application** (`web.healthcare`) - 포트 8981
   ```bash
   cd web.healthcare
   ./gradlew bootRun
   # Web Application: http://localhost:8981
   ```

#### 모바일 앱 실행

```bash
cd mobile/healthcare_mobile
pnpm install
pnpm start          # Expo 개발 서버 시작 (포트 8981)
pnpm android        # Android 에뮬레이터에서 실행
pnpm ios            # iOS 시뮬레이터에서 실행
pnpm web            # 웹 브라우저에서 실행
```

## 📡 API 엔드포인트

### 인증 서비스 (`/auth/**`)

| Method | Endpoint                  | 설명             | 인증 필요 |
| ------ | ------------------------- | ---------------- | --------- |
| POST   | `/auth/v1/signin`         | 로그인           | ❌        |
| POST   | `/auth/v1/signup`         | 회원가입         | ❌        |
| POST   | `/auth/v1/refresh`        | 토큰 갱신        | ✅        |
| POST   | `/auth/v1/logout`         | 로그아웃         | ✅        |
| POST   | `/auth/v1/duplicateId`    | ID 중복 확인     | ❌        |
| POST   | `/auth/v1/duplicateEmail` | 이메일 중복 확인 | ❌        |
| POST   | `/auth/v1/findUserId`     | ID 찾기          | ❌        |
| POST   | `/auth/v1/findUserPw`     | 비밀번호 찾기    | ❌        |
| POST   | `/auth/v1/updateUserPw`   | 비밀번호 변경    | ✅        |

### 헬스케어 서비스 (`/healthcare/**`)

| Method | Endpoint                          | 설명               | 인증 필요 |
| ------ | --------------------------------- | ------------------ | --------- |
| POST   | `/healthcare/v1/insertHealthInfo` | 건강 데이터 저장   | ✅        |
| POST   | `/healthcare/v1/healthInfo`       | 건강 정보 조회     | ✅        |
| POST   | `/healthcare/v1/healthInfoChart`  | 건강 정보 차트     | ✅        |
| POST   | `/healthcare/v1/realtimeBiodata`  | 실시간 생체 데이터 | ✅        |
| POST   | `/healthcare/v1/graphBiodata`     | 그래프 생체 데이터 | ✅        |
| POST   | `/healthcare/v1/dailydata`        | 일일 데이터        | ✅        |
| POST   | `/healthcare/v1/healthScoreList`  | 건강 점수 목록     | ✅        |
| POST   | `/healthcare/v1/chat_ai`          | AI 건강 상담       | ✅        |

### 커뮤니티 서비스 (`/community/**`)

| Method | Endpoint                      | 설명        | 인증 필요 |
| ------ | ----------------------------- | ----------- | --------- |
| POST   | `/community/v1/writeBoard`    | 게시글 작성 | ✅        |
| POST   | `/community/v1/findBoard`     | 게시글 조회 | ✅        |
| POST   | `/community/v1/findBoardList` | 게시글 목록 | ✅        |
| POST   | `/community/v1/updateBoard`   | 게시글 수정 | ✅        |

### 사용자 관리 서비스 (`/management/**`)

| Method | Endpoint                        | 설명             | 인증 필요 |
| ------ | ------------------------------- | ---------------- | --------- |
| POST   | `/management/v1/userInfo`       | 사용자 정보 조회 | ✅        |
| POST   | `/management/v1/updateUserInfo` | 사용자 정보 수정 | ✅        |
| POST   | `/management/v1/deleteUserInfo` | 사용자 정보 삭제 | ✅        |
| POST   | `/management/v1/updatePasswd`   | 비밀번호 변경    | ✅        |
| POST   | `/management/v1/list`           | 사용자 목록      | ✅        |

> **참고**: 모든 API는 API Gateway (`http://localhost:8080`)를 통해 접근합니다.

## 📊 데이터베이스 설계

### 주요 테이블

#### 사용자 관련

- `user_mng`: 사용자 기본 정보 (이름, 이메일, 전화번호, 생년월일 등)
- `user_auth_info`: 사용자 권한 정보 (역할: 환자/보호자/의사/관리자)
- `doctor_mapping_info`: 의사-환자 매핑 정보
- `guardian_mapping_info`: 보호자-환자 매핑 정보

#### 건강 데이터 관련

- `health_data_minute`: 분 단위 건강 데이터 (심박수, 체온, 혈압 등)
- `health_data_day`: 일 단위 집계 건강 데이터
- `healthcare_ai_handler`: AI 응답 데이터 저장

#### 커뮤니티 관련

- `community_temp`: 커뮤니티 게시글
- `likeboard`: 게시글 좋아요 정보
- `recommend`: 추천 게시글 정보

## 🤖 AI LLM 서비스

- **로컬 LLM 모델**: Llama.cpp를 활용한 로컬 LLM 실행
- **모델 관리**: 모델 다운로드 및 버전 관리
- **Docker 기반**: LLM 서버를 Docker 컨테이너로 운영
- 사용자 생체 데이터를 기반으로 한 개인화된 건강 조언
- 질병 예방 정보 및 음식 추천
- 연령대별 맞춤형 건강 관리 가이드

## 📈 프로젝트 기간 및 팀

- **개발 기간**: 2025.01.01 ~ 2025.05.24 (5개월) / 프로젝트 이관: 2025.10.03 ~ 2025.10.13
- **개발자**: 궁금하면 500원
- **프로젝트 유형**: 개인 프로젝트

## 🔐 보안 기능

### 인증 및 인가

- **JWT 토큰 기반 인증**: Access Token 및 Refresh Token 방식
- **역할 기반 접근 제어 (RBAC)**: 환자/보호자/의사/관리자별 권한 관리
- **토큰 블랙리스트**: Redis를 활용한 로그아웃 토큰 관리
- **Spring Security 통합**: 보안 필터 체인 구성

### 데이터 보호

- **사용자 데이터 암호화**: AES-256 암호화 (개인정보, 생체정보)
- **입력 데이터 검증**: Spring Validation 및 Custom Sanitizer
- **SQL Injection 방지**: MyBatis PreparedStatement 사용
- **XSS 방지**: 입력 데이터 Sanitization

### API 보안

- **Rate Limiting**: Redis 기반 API 요청 제한 (초당 10건, 버스트 20건)
- **CORS 설정**: 허용된 Origin만 접근 가능
- **Request Size 제한**: 최대 요청 크기 제한 (10MB)
- **HTTPS 지원**: 프로덕션 환경에서 HTTPS 적용

### 보안 헤더

- Security Headers 자동 추가 (X-Content-Type-Options, X-Frame-Options 등)
- 민감 정보 로깅 방지

## 📱 플랫폼 지원

### 웹 애플리케이션

- **반응형 웹 디자인**: 모바일, 태블릿, 데스크톱 지원
- **JSP 기반**: 서버 사이드 렌더링
- **Apache Tiles**: 레이아웃 템플릿 관리
- **실시간 데이터 시각화**: Chart.js를 활용한 건강 데이터 차트

### 모바일 애플리케이션

- **크로스 플랫폼**: React Native 기반 (iOS/Android)
- **오프라인 지원**: AsyncStorage를 통한 로컬 데이터 저장
- **푸시 알림**: Expo Notifications를 활용한 건강 이상 징후 알림
- **네이티브 기능**: 카메라, 위치 정보, 디바이스 정보 접근
- **네트워크 모니터링**: 오프라인/온라인 상태 감지

## 🚀 배포 및 운영

### CI/CD 파이프라인

- **Jenkins**: 자동화된 빌드 및 배포 파이프라인
- **Git 기반 트리거**: 5분마다 SCM 폴링으로 자동 빌드
- **병렬 빌드**: 모든 서비스를 병렬로 빌드하여 빌드 시간 단축
- **Docker 이미지 빌드**: 각 서비스별 Docker 이미지 자동 생성
- **무중단 배포**: Blue-Green 배포 전략을 통한 제로 다운타임 배포
- **자동 헬스 체크**: 배포 후 서비스 상태 자동 확인

## 🛠️ 개발 가이드

### 아키텍처 패턴

- **Saga 패턴**: 분산 트랜잭션 관리를 위한 Saga 패턴 적용
  - 환자 등록 시: CreatePatient → CreateUserAccount → SendWelcomeNotification
  - 각 서비스별 독립적인 Saga 클래스 구현
  - 보상 트랜잭션을 통한 롤백 지원
- **Event Sourcing**: Healthcare Service에서 이벤트 소싱 패턴 적용
- **CQRS**: Command와 Query 분리를 통한 성능 최적화
- **MyBatis**: Healthcare, UserManagement 서비스에서 MyBatis 사용
- **JPA**: Communication 서비스에서 JPA 사용

### 코드 스타일

- **Java**: Google Java Style Guide 준수
- **TypeScript**: ESLint 규칙 준수
- **커밋 메시지**: Conventional Commits 형식 권장

### 테스트

- **단위 테스트**: JUnit 5
- **통합 테스트**: Spring Boot Test
- **API 테스트**: Postman 또는 curl 사용

### 환경 설정

- **개발 환경**: `application-dev.yml`
- **운영 환경**: `application-prod.yml`
- **공통 설정**: `application-common.yml`

### 배포 스크립트

- **deploy-docker.sh**: Docker 기반 무중단 배포 스크립트
- **deploy.sh**: JAR 파일 기반 배포 스크립트
- **setup-nginx.sh**: 운영 서버 Nginx 초기 설정 (1회 실행)
- **fix-bom.sh**: BOM 문자 제거 스크립트

## ❓ 트러블슈팅

### 서비스가 시작되지 않는 경우

1. Config Service가 먼저 실행되었는지 확인
2. Discovery Service가 실행되었는지 확인
3. 데이터베이스 연결 정보 확인
4. Redis 및 RabbitMQ 서버 실행 상태 확인

### API Gateway에서 503 에러 발생

1. 대상 서비스가 Eureka에 등록되었는지 확인
2. 서비스의 헬스 체크 상태 확인
3. Circuit Breaker 상태 확인

### 인증 실패

1. JWT 토큰 만료 시간 확인
2. 토큰 시크릿 키 일치 여부 확인
3. Redis 연결 상태 확인 (토큰 블랙리스트)

### 배포 실패

1. Jenkins 환경 변수 확인 (DEPLOY_TARGET_SERVER, DEPLOY_SSH_KEY 등)
2. 운영 서버 SSH 접근 권한 확인
3. Docker 이미지 빌드 및 전송 상태 확인
4. Nginx 설정 확인 (초기 설정 시 1회만 필요)

## 📝 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
