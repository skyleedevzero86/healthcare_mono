# healthcare_mono

## 웹버전

![System Architecture](https://github.com/user-attachments/assets/a2a4ebde-590b-4b29-81d2-6c5b06395ef9)

## 모바일버전

<img width="1269" height="650" alt="image" src="https://github.com/user-attachments/assets/ee046d87-a9e0-4e48-a5d0-4a1c51b9ad28" />

---

## 📋 프로젝트 개요

`healthcare_mono`는 **React Native 모바일 앱과 Spring Cloud 기반 백엔드 MSA를 연동하여 사용자의 건강 데이터를 기록·조회·시각화하고, AI 기반 건강 리포트와 생활습관 조언을 제공하는 헬스케어 플랫폼**입니다.

현재는 React Native 모바일 앱에서 입력한 건강 데이터와 샘플 데이터를 기반으로 **심박수, 혈압, 체온, 스트레스, 수면 정보**를 화면에 시각화합니다. 또한 **Expo Sensors의 Pedometer**를 활용하여 사용자의 걸음 수 데이터를 수집하고, Spring API와 연동하여 건강 대시보드에 표시하는 구조로 구현했습니다.

향후에는 Expo Development Build 환경에서 **HealthKit 및 Health Connect**를 연동하여 Apple Watch, Galaxy Watch 등 웨어러블 기기 또는 건강 앱에 저장된 건강 데이터를 앱에서 조회하고 서버와 동기화하는 구조로 확장할 예정입니다.

> 본 프로젝트의 건강 데이터 분석 및 AI 응답은 의료 진단이 아닌 **참고용 건강 정보와 생활습관 조언**을 목적으로 합니다.

---

## 🎯 프로젝트 목적

이 프로젝트는 단순 CRUD형 건강관리 앱이 아니라, 다음 역량을 보여주기 위한 개인 포트폴리오 프로젝트입니다.

- Spring Cloud 기반 MSA 구조 설계
- API Gateway, Eureka, Config Server 기반 서비스 분리
- JWT 인증/인가 및 역할 기반 접근 제어
- 건강 데이터 저장, 조회, 차트 시각화
- React Native 모바일 앱과 Spring API 연동
- Expo Pedometer 기반 걸음 수 데이터 수집
- ChatGPT API 기반 건강 데이터 요약 및 생활습관 조언 생성
- Docker, Jenkins, Nginx 기반 배포 구조 설계

---

## 💼 비즈니스 모델

- **서비스 형태**: B2C 기반 개인 건강관리 서비스
- **주요 사용자**: 개인 사용자, 보호자, 의사 역할 사용자
- **서비스 제공 범위**
  - 건강 데이터 기록 및 조회
  - 걸음 수 데이터 수집 및 표시
  - 기간별 건강 데이터 차트 제공
  - AI 기반 건강 리포트 및 생활습관 조언
  - 보호자/의사와의 사용자 매핑
  - 커뮤니티 기반 건강 정보 공유

---

## ⭐ 핵심 가치

- **모바일 기반 건강 데이터 관리**  
  React Native 앱을 통해 건강 데이터를 입력하고 웹/모바일에서 조회할 수 있습니다.

- **실제 센서 기반 걸음 수 수집**  
  Expo Sensors의 Pedometer를 활용하여 사용자의 걸음 수 데이터를 수집하고 대시보드에 표시합니다.

- **AI 기반 건강 참고 리포트**  
  ChatGPT API를 활용하여 사용자의 건강 데이터를 요약하고 생활습관 개선 방향을 제안합니다.

- **역할 기반 헬스케어 서비스 구조**  
  환자, 보호자, 의사 역할을 분리하여 사용자별 접근 권한과 기능을 다르게 구성했습니다.

- **확장 가능한 백엔드 아키텍처**  
  Spring Cloud 기반 MSA 구조로 인증, 사용자 관리, 헬스케어, 커뮤니티, LLM 서비스를 분리했습니다.

---

## 🏗️ 시스템 아키텍처

### MSA 구조

이 프로젝트는 마이크로서비스 아키텍처를 기반으로 구성되어 있으며, 각 서비스는 독립적으로 개발, 실행, 배포할 수 있도록 분리되어 있습니다.

### 서비스 구성 및 포트

| 서비스                    | 포트 | 설명                        | 주요 기술                         |
| ------------------------- | ---- | --------------------------- | --------------------------------- |
| **API Gateway**           | 8080 | 모든 서비스의 단일 진입점   | Spring Cloud Gateway, WebFlux     |
| **Service Discovery**     | 8761 | 서비스 등록 및 발견         | Netflix Eureka Server             |
| **Config Service**        | 8888 | 중앙화된 설정 관리          | Spring Cloud Config Server        |
| **Auth Service**          | 8082 | 사용자 인증 및 권한 관리    | Spring Boot, JWT, Spring Security |
| **User Management**       | 8087 | 사용자 정보 관리            | Spring Boot, MyBatis              |
| **Healthcare Service**    | 8084 | 건강 데이터 처리 및 AI 분석 | Spring Boot, MyBatis, ChatGPT API |
| **LLM Service**           | 8086 | 로컬 LLM 실험/확장 서비스   | Spring Boot, Llama.cpp            |
| **Communication Service** | 8085 | 커뮤니티 기능               | Spring Boot, JPA                  |
| **Web Application**       | 8981 | 웹 사용자 인터페이스        | Spring Boot, JSP, Apache Tiles    |
| **Mobile App**            | -    | 모바일 애플리케이션         | React Native, Expo SDK 54         |

---

## 🛠️ 기술 스택

### Backend

- **Framework**: Spring Boot 3.5.6, Spring Cloud 2025.0.0
- **Language**: Java 21
- **Database**: PostgreSQL, H2
- **Persistence**: MyBatis XML Mapper, JPA
- **Build Tool**: Gradle 8.x

### Frontend

- **Web**: HTML5, CSS3, JavaScript, jQuery, JSP, Apache Tiles
- **Mobile**: React Native 0.81.4, Expo SDK 54, TypeScript

### Mobile Sensor

- **Step Count**: Expo Sensors Pedometer
- **Local Storage**: AsyncStorage
- **Notification**: Expo Notifications
- **Future Extension**: HealthKit, Health Connect

### Infrastructure & Middleware

- **API Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka
- **Configuration**: Spring Cloud Config Server
- **Message Queue**: RabbitMQ
- **Cache**: Redis
- **Circuit Breaker**: Resilience4j
- **Distributed Tracing**: Zipkin, Brave
- **Monitoring**: Spring Boot Actuator, Micrometer
- **CI/CD**: Jenkins
- **Container**: Docker
- **Reverse Proxy**: Nginx

### Security

- **Authentication**: JWT, Access Token, Refresh Token
- **Authorization**: RBAC 기반 역할별 접근 제어
- **Encryption**: AES-256
- **Security Framework**: Spring Security
- **Validation**: Spring Validation, Custom Sanitizer

### Logging

- **Logging Framework**: Log4j2
- **SQL Logging**: Log4jdbc, P6Spy

---

## 👥 사용자 역할

### 1. 환자 Patient

- 본인 건강 데이터 등록 및 조회
- 걸음 수 및 건강 데이터 차트 확인
- AI 기반 건강 리포트 확인
- 커뮤니티 참여

### 2. 보호자 Guardian

- 연결된 환자의 건강 상태 조회
- 환자와의 연결 관리
- 이상 패턴 또는 주의 데이터 확인

### 3. 의사 Doctor

- 매핑된 환자 건강 데이터 조회
- 환자별 건강 상태 참고
- 상담 및 관리 목적의 데이터 확인

---

## 🏥 주요 기능

### 1. 모바일 건강 데이터 시각화

- React Native 모바일 앱에서 건강 데이터를 입력하고 서버에 저장
- 샘플 데이터를 기반으로 심박수, 혈압, 체온, 스트레스, 수면 정보를 화면에 표시
- Expo Sensors의 Pedometer를 활용하여 걸음 수 데이터를 수집 및 표시
- Spring API를 통해 건강 데이터를 조회하고 대시보드/차트로 시각화

### 2. 건강 데이터 관리

- 사용자 입력 데이터 또는 샘플 데이터를 기반으로 건강 데이터 저장
- 건강 지표 조회 및 기간별 변화 확인
- 관리 가능한 건강 지표
  - 심박수 Heart Rate
  - 혈압 Blood Pressure
  - 체온 Temperature
  - 스트레스 지수 Stress Index
  - 수면 시간 Sleep Time
  - 걸음 수 Step Count
  - 산소포화도 SpO2
  - 호흡수 Respiration Rate

> 현재 실제 모바일 센서 기반 수집 항목은 **걸음 수**입니다.  
> 심박수, 혈압, 체온, 스트레스, 수면 정보는 현재 사용자 입력 또는 샘플 데이터 기반으로 표시합니다.

### 3. 대시보드 및 차트

- 건강 상태 대시보드 제공
- 최근 건강 데이터 조회
- 기간별 건강 데이터 차트 제공
  - 1일
  - 1주일
  - 1개월
  - 1년
- 건강 점수 계산 및 표시
- 목표 걸음 수 및 달성률 표시

### 4. AI 기반 건강 분석

- ChatGPT API를 활용한 건강 데이터 요약
- 사용자 건강 지표 기반 생활습관 조언
- 건강 상태별 주의사항 안내
- 맞춤형 음식 및 관리 방향 추천

> “질병 진단”이나 “의료 판단”이 아닌, 사용자가 입력한 데이터를 바탕으로 한 **참고용 건강 리포트** 기능입니다.

### 5. 커뮤니티 기능

- 연령대별 건강 정보 공유
- 사용자 간 건강 경험담 교환
- 게시글 작성, 조회, 수정
- 건강관리 관련 커뮤니티 기능 제공

### 6. 사용자 관리

- 회원가입 및 로그인
- 환자/보호자/의사 역할 구분
- 프로필 관리
- 보호자-환자 연결 관리
- 의사-환자 매핑 관리

### 7. 웨어러블 데이터 연동 확장 계획

현재는 사용자 입력 데이터와 샘플 데이터를 기반으로 건강 정보를 시각화합니다.

향후 Expo Development Build 환경에서 HealthKit 및 Health Connect를 연동하여 다음 구조로 확장할 예정입니다.

```text
Apple Watch / Galaxy Watch / 건강 앱
        ↓
HealthKit / Health Connect
        ↓
React Native Mobile App
        ↓
Spring API Gateway
        ↓
Healthcare Service
        ↓
PostgreSQL
        ↓
웹/모바일 대시보드 시각화
```

확장 예정 데이터 항목은 다음과 같습니다.

- 심박수
- 수면 데이터
- 활동량
- 걸음 수
- 체온
- 혈압
- 산소포화도
- 호흡수

---

## 📌 구현 범위

| 기능                          | 상태      | 설명                                     |
| ----------------------------- | --------- | ---------------------------------------- |
| Spring Cloud MSA 구조         | 구현      | Gateway, Eureka, Config 기반 서비스 분리 |
| JWT 인증/인가                 | 구현      | Access Token, Refresh Token, RBAC 적용   |
| 사용자 관리                   | 구현      | 사용자 정보 조회/수정/삭제               |
| 건강 데이터 저장/조회         | 구현      | 건강 지표 등록 및 조회 API               |
| 건강 데이터 차트              | 구현      | 기간별 데이터 조회 및 차트 표시          |
| React Native 모바일 화면      | 구현      | 모바일 대시보드 및 건강 데이터 화면 구성 |
| Expo Pedometer 걸음 수 수집   | 구현      | 모바일 센서를 활용한 걸음 수 수집        |
| AI 건강 리포트                | 구현      | ChatGPT API 기반 건강 데이터 요약        |
| 커뮤니티 기능                 | 구현      | 게시글 작성, 조회, 수정                  |
| Llama.cpp 로컬 LLM            | 실험/확장 | 로컬 LLM 연동을 위한 별도 서비스 구성    |
| HealthKit/Health Connect 연동 | 확장 예정 | 웨어러블 건강 데이터 연동 예정           |

---

## 📁 프로젝트 구조

```text
healthcare_mono/
├── api.gateway/                 # API 게이트웨이
├── service.discovery/           # 서비스 디스커버리
├── service.config/              # 설정 관리 서비스
├── service.auth/                # 인증 서비스
├── service.usermanagement/      # 사용자 관리 서비스
├── service.healthcare/          # 헬스케어 서비스
├── service.llm/                 # LLM 실험/확장 서비스
├── service.comm/                # 커뮤니티 서비스
├── web.healthcare/              # 웹 애플리케이션
│   ├── src/main/java/
│   └── src/main/webapp/
│       ├── WEB-INF/jsp/
│       │   ├── user/
│       │   ├── health/
│       │   ├── community/
│       │   └── include/
│       ├── css/
│       ├── js/
│       └── images/
├── mobile/healthcare_mobile/    # React Native 모바일 앱
│   ├── src/
│   ├── assets/
│   ├── android/
│   ├── App.tsx
│   └── package.json
├── scripts/
│   ├── deploy-docker.sh
│   ├── deploy.sh
│   ├── jenkins-setup.sh
│   ├── fix-bom.sh
│   ├── prod/
│   │   ├── setup-nginx.sh
│   │   ├── nginx.conf
│   │   └── docker-compose.prod.yml
│   └── docs/
│       ├── DEPLOYMENT_GUIDE.md
│       ├── DOMAIN_DEPLOYMENT_GUIDE.md
│       └── JENKINS_SETUP.md
└── Jenkinsfile
```

---

## 🔧 개발 환경 설정

### Backend

- Java 21+
- PostgreSQL 또는 H2
- Spring Boot 3.5.6+
- Gradle 8.x+

### Web

- Node.js 18+

### Mobile

- Node.js 18+
- pnpm 9.x+
- Expo CLI
- Android Studio
- Xcode  
  iOS 개발은 macOS 환경에서만 가능

---

## ▶️ 실행 방법

### 1. 데이터베이스 준비

```bash
# PostgreSQL 설치 및 데이터베이스 생성
# 개발 환경에서는 H2 인메모리 데이터베이스 사용 가능
```

### 2. Redis 실행

```bash
redis-server
```

### 3. RabbitMQ 실행

```bash
rabbitmq-server
```

### 4. 백엔드 서비스 실행 순서

#### Config Service

```bash
cd service.config
./gradlew bootRun
```

#### Discovery Service

```bash
cd service.discovery
./gradlew bootRun
# Eureka Dashboard: http://localhost:8761
```

#### Auth Service

```bash
cd service.auth
./gradlew bootRun
```

#### User Management Service

```bash
cd service.usermanagement
./gradlew bootRun
```

#### Healthcare Service

```bash
cd service.healthcare
./gradlew bootRun
```

#### LLM Service

```bash
cd service.llm
./gradlew bootRun
```

#### Communication Service

```bash
cd service.comm
./gradlew bootRun
```

#### API Gateway

```bash
cd api.gateway
./gradlew bootRun
# API Gateway: http://localhost:8080
```

#### Web Application

```bash
cd web.healthcare
./gradlew bootRun
# Web Application: http://localhost:8981
```

### 5. 모바일 앱 실행

```bash
cd mobile/healthcare_mobile
pnpm install
pnpm start
pnpm android
pnpm ios
pnpm web
```

---

## 📡 API 엔드포인트

> 모든 API는 API Gateway `http://localhost:8080`을 통해 접근합니다.

### 인증 서비스 `/auth/**`

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

### 헬스케어 서비스 `/healthcare/**`

| Method | Endpoint                          | 설명                    | 인증 필요 |
| ------ | --------------------------------- | ----------------------- | --------- |
| POST   | `/healthcare/v1/insertHealthInfo` | 건강 데이터 저장        | ✅        |
| POST   | `/healthcare/v1/healthInfo`       | 건강 정보 조회          | ✅        |
| POST   | `/healthcare/v1/healthInfoChart`  | 건강 정보 차트          | ✅        |
| POST   | `/healthcare/v1/realtimeBiodata`  | 최근 건강 데이터 조회   | ✅        |
| POST   | `/healthcare/v1/graphBiodata`     | 건강 데이터 그래프 조회 | ✅        |
| POST   | `/healthcare/v1/dailydata`        | 일일 데이터 조회        | ✅        |
| POST   | `/healthcare/v1/healthScoreList`  | 건강 점수 목록          | ✅        |
| POST   | `/healthcare/v1/chat_ai`          | AI 건강 리포트/상담     | ✅        |

### 커뮤니티 서비스 `/community/**`

| Method | Endpoint                      | 설명        | 인증 필요 |
| ------ | ----------------------------- | ----------- | --------- |
| POST   | `/community/v1/writeBoard`    | 게시글 작성 | ✅        |
| POST   | `/community/v1/findBoard`     | 게시글 조회 | ✅        |
| POST   | `/community/v1/findBoardList` | 게시글 목록 | ✅        |
| POST   | `/community/v1/updateBoard`   | 게시글 수정 | ✅        |

### 사용자 관리 서비스 `/management/**`

| Method | Endpoint                        | 설명             | 인증 필요 |
| ------ | ------------------------------- | ---------------- | --------- |
| POST   | `/management/v1/userInfo`       | 사용자 정보 조회 | ✅        |
| POST   | `/management/v1/updateUserInfo` | 사용자 정보 수정 | ✅        |
| POST   | `/management/v1/deleteUserInfo` | 사용자 정보 삭제 | ✅        |
| POST   | `/management/v1/updatePasswd`   | 비밀번호 변경    | ✅        |
| POST   | `/management/v1/list`           | 사용자 목록      | ✅        |

---

## 📊 데이터베이스 설계

### 사용자 관련

- `user_mng`: 사용자 기본 정보
- `user_auth_info`: 사용자 권한 정보
- `doctor_mapping_info`: 의사-환자 매핑 정보
- `guardian_mapping_info`: 보호자-환자 매핑 정보

### 건강 데이터 관련

- `health_data_minute`: 분 단위 건강 데이터
- `health_data_day`: 일 단위 집계 건강 데이터
- `healthcare_ai_handler`: AI 응답 데이터 저장

### 커뮤니티 관련

- `community_temp`: 커뮤니티 게시글
- `likeboard`: 게시글 좋아요 정보
- `recommend`: 추천 게시글 정보

---

## 🤖 AI LLM 서비스

### ChatGPT API 기반 건강 리포트

- 사용자의 건강 지표 데이터를 요약
- 주의가 필요한 데이터를 설명
- 생활습관 개선 방향 제안
- 음식 및 관리 방향 추천

### 로컬 LLM 확장 구조

- Llama.cpp 기반 로컬 LLM 실행 실험
- Docker 컨테이너 기반 LLM 서버 운영 구조
- 외부 API 의존도를 줄이기 위한 확장 포인트

---

## 🔐 보안 기능

### 인증 및 인가

- JWT 기반 인증
- Access Token 및 Refresh Token 분리
- 역할 기반 접근 제어 RBAC
- Redis 기반 로그아웃 토큰 블랙리스트
- Spring Security 보안 필터 체인 구성

### 데이터 보호

- AES-256 기반 민감 정보 암호화
- Spring Validation 기반 입력값 검증
- Custom Sanitizer를 활용한 입력 데이터 정제
- MyBatis PreparedStatement 기반 SQL Injection 방지
- XSS 방지를 위한 입력값 Sanitization

### API 보안

- Redis 기반 Rate Limiting
- CORS 설정
- Request Size 제한
- HTTPS 운영 환경 지원
- Security Headers 적용
- 민감 정보 로깅 방지

---

## 📱 플랫폼 지원

### 웹 애플리케이션

- JSP 기반 서버 사이드 렌더링
- Apache Tiles 기반 레이아웃 관리
- 모바일, 태블릿, 데스크톱 반응형 화면 지원
- Chart.js 기반 건강 데이터 시각화

### 모바일 애플리케이션

- React Native 기반 크로스 플랫폼 앱
- Expo 기반 개발 환경
- Expo Sensors Pedometer 기반 걸음 수 수집
- AsyncStorage 기반 로컬 임시 저장
- Expo Notifications 기반 알림 기능
- 카메라, 위치 정보 등 네이티브 기능 확장 가능
- 온라인/오프라인 네트워크 상태 감지

---

## 🚀 배포 및 운영

### CI/CD 파이프라인

- Jenkins 기반 자동 빌드 및 배포
- Git SCM Polling 기반 빌드 트리거
- 서비스별 병렬 빌드 구성
- Docker 이미지 빌드 및 배포
- Nginx Reverse Proxy 구성
- 배포 후 헬스 체크 수행

---

## 🛠️ 개발 가이드

### 아키텍처 패턴

- **MSA**: 서비스별 책임 분리
- **Gateway Pattern**: API Gateway를 통한 단일 진입점 제공
- **Service Discovery**: Eureka 기반 서비스 등록/탐색
- **Config Server**: 공통 설정 중앙화
- **RBAC**: 환자/보호자/의사/관리자 역할 기반 접근 제어
- **MyBatis**: Healthcare, UserManagement 서비스에서 사용
- **JPA**: Communication 서비스에서 사용

### 코드 스타일

- Java: Google Java Style Guide 지향
- TypeScript: ESLint 규칙 준수
- 커밋 메시지: Conventional Commits 형식 권장

### 테스트

- 단위 테스트: JUnit 5
- 통합 테스트: Spring Boot Test
- API 테스트: Postman 또는 curl 사용

### 환경 설정

- 개발 환경: `application-dev.yml`
- 운영 환경: `application-prod.yml`
- 공통 설정: `application-common.yml`

### 배포 스크립트

- `deploy-docker.sh`: Docker 기반 배포 스크립트
- `deploy.sh`: JAR 기반 배포 스크립트
- `setup-nginx.sh`: 운영 서버 Nginx 초기 설정
- `fix-bom.sh`: BOM 문자 제거 스크립트

---

## ❓ 트러블슈팅

### 서비스가 시작되지 않는 경우

1. Config Service가 먼저 실행되었는지 확인
2. Discovery Service가 실행되었는지 확인
3. 데이터베이스 연결 정보 확인
4. Redis 및 RabbitMQ 실행 상태 확인

### API Gateway에서 503 에러 발생

1. 대상 서비스가 Eureka에 등록되었는지 확인
2. 서비스 헬스 체크 상태 확인
3. Gateway 라우팅 설정 확인
4. Circuit Breaker 설정 확인

### 인증 실패

1. JWT 토큰 만료 시간 확인
2. 토큰 시크릿 키 일치 여부 확인
3. Redis 연결 상태 확인
4. 권한 Role 설정 확인

### 모바일 앱 API 연동 실패

1. API Gateway 주소 확인
2. 모바일 환경에서 접근 가능한 IP 사용 여부 확인
3. CORS 설정 확인
4. Android Emulator 사용 시 `localhost` 대신 `10.0.2.2` 사용 여부 확인

### Pedometer 걸음 수가 표시되지 않는 경우

1. 실제 디바이스에서 테스트하는지 확인
2. 앱 권한 설정 확인
3. 시뮬레이터/에뮬레이터 환경의 센서 지원 여부 확인
4. Expo Sensors 패키지 설치 여부 확인

### 배포 실패

1. Jenkins 환경 변수 확인
2. 운영 서버 SSH 접근 권한 확인
3. Docker 이미지 빌드 및 전송 상태 확인
4. Nginx 설정 확인
5. 서비스별 포트 충돌 확인

---

## 📈 프로젝트 기간 및 팀

- **개발 기간**: 2025.01.01 ~ 2026.??.??
- **프로젝트 이관 및 구조 정리**: 2025.10.03 ~ 2025.10.13
- **프로젝트 유형**: 개인 프로젝트
- **담당 역할**: 백엔드 설계 및 구현, 웹 화면 구성, 모바일 앱 연동, 배포 구조 정리

---

## 📝 라이선스

이 프로젝트는 개인 포트폴리오 프로젝트입니다.

---

## 📞 문의

프로젝트 관련 문의사항은 GitHub Issue를 통해 등록해주세요.
