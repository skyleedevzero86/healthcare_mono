# healthcare_mono

![System Architecture](https://github.com/user-attachments/assets/a2a4ebde-590b-4b29-81d2-6c5b06395ef9)

## 📋 프로젝트 개요

`healthcare_mono`는 스마트워치를 통해 수집된 사용자의 생체 데이터를 활용하여 실시간 건강 모니터링과 AI 기반 건강 분석을 제공하는 헬스케어 플랫폼입니다. 웹 애플리케이션과 모바일 앱을 통해 다양한 플랫폼에서 건강 데이터를 관리하고 분석할 수 있습니다.

## 🏗️ 시스템 아키텍처

### MSA (Microservice Architecture) 구조

- **API Gateway**: `api.gateway` - 모든 서비스의 진입점 (Spring Cloud Gateway)
- **Service Discovery**: `service.discovery` - 서비스 등록 및 발견 (Eureka Server)
- **Config Service**: `service.config` - 중앙화된 설정 관리 (Spring Cloud Config)
- **Auth Service**: `service.auth` - 사용자 인증 및 권한 관리 (JWT)
- **User Management Service**: `service.usermanagement` - 사용자 정보 관리
- **Healthcare Service**: `service.healthcare` - 건강 데이터 처리 및 분석
- **Communication Service**: `service.comm` - 커뮤니티 기능
- **Web Application**: `web.healthcare` - 웹 사용자 인터페이스 (JSP)
- **Mobile Application**: `mobile/healthcare_mobile` - 모바일 앱 (React Native/Expo)

### 기술 스택

- **Backend**: Spring Boot 3.5.6, Spring Cloud 2025.0.0, Java 21
- **Database**: PostgreSQL, H2 (개발용)
- **ORM**: MyBatis (XML Mapper), JPA
- **Frontend (Web)**: HTML, CSS, JavaScript, jQuery, JSP, Apache Tiles
- **Frontend (Mobile)**: React Native 0.81.4, Expo SDK 54, TypeScript
- **Authentication**: JWT Token (jjwt)
- **AI Integration**: ChatGPT API
- **Configuration**: Spring Cloud Config, Spring Cloud Bus (RabbitMQ)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway (WebFlux)
- **Logging**: Log4j2

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
├── service.comm/                 # 커뮤니티 서비스
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
└── mobile/healthcare_mobile/    # 모바일 애플리케이션
    ├── src/                     # React Native 소스코드
    ├── assets/                  # 이미지 및 리소스
    ├── android/                 # Android 네이티브 코드
    ├── App.tsx                  # 메인 앱 컴포넌트
    └── package.json             # Node.js 의존성
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

#### 백엔드 서비스 실행 순서

1. PostgreSQL/H2 데이터베이스 설정
2. 각 서비스별 `application.yml` 설정
3. 서비스 순서대로 실행:
   - **Config Service** (`service.config`)
   - **Discovery Service** (`service.discovery`)
   - **Auth Service** (`service.auth`)
   - **User Management Service** (`service.usermanagement`)
   - **Healthcare Service** (`service.healthcare`)
   - **Communication Service** (`service.comm`)
   - **API Gateway** (`api.gateway`)
   - **Web Application** (`web.healthcare`)

#### 모바일 앱 실행

```bash
cd mobile/healthcare_mobile
pnpm install
pnpm start          # Expo 개발 서버 시작
pnpm android        # Android 에뮬레이터에서 실행
pnpm ios            # iOS 시뮬레이터에서 실행
```

## 📊 데이터베이스 설계

### 주요 테이블

- `user_mng`: 사용자 정보
- `user_auth_info`: 사용자 권한 정보
- `health_data_minute`: 분 단위 건강 데이터
- `health_data_day`: 일 단위 건강 데이터
- `community_temp`: 커뮤니티 게시글
- `healthcare_ai_handler`: AI 응답 데이터

## 🤖 AI 통합

### ChatGPT API 활용

- 사용자 생체 데이터를 기반으로 한 개인화된 건강 조언
- 질병 예방 정보 및 음식 추천
- 연령대별 맞춤형 건강 관리 가이드

## 📈 프로젝트 기간 및 팀

- **개발 기간**: 2025.01.01 ~ 2025.05.24 (5개월) / 프로젝트 이관: 2025.10.03 ~ 2025.10.13
- **개발자**: 궁금하면 500원
- **프로젝트 유형**: 개인 프로젝트

## 🔐 보안 기능

- JWT 토큰 기반 인증 및 인가
- 사용자 데이터 암호화 (AES-256)
- 입력 데이터 검증 및 Sanitization
- Rate Limiting (API 요청 제한)
- Spring Security 통합
- 권한별 접근 제어 (환자/보호자/의사)

## 📱 플랫폼 지원

- **웹**: 반응형 웹 디자인 (모바일 및 데스크톱 지원)
- **모바일**: React Native 기반 크로스 플랫폼 앱 (iOS/Android)
- 사용자 친화적인 UI/UX
- 실시간 데이터 시각화
- 모바일 푸시 알림 지원 (Expo Notifications)
