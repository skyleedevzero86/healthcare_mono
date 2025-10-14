# healthcare_mono

## 웹버전
![System Architecture](https://github.com/user-attachments/assets/a2a4ebde-590b-4b29-81d2-6c5b06395ef9)<br/>

## 모바일버전
<img width="399" height="869" alt="image" src="https://github.com/user-attachments/assets/6a8a2b7c-ee65-401b-a4e3-5d13dc01477b" /><br/>


## 📋 프로젝트 개요
`healthcare_mono`는 스마트워치를 통해 수집된 사용자의 생체 데이터를 활용하여 실시간 건강 모니터링과 AI 기반 건강 분석을 제공하는 웹 기반 헬스케어 플랫폼입니다.

## 🏗️ 시스템 아키텍처

### MSA (Microservice Architecture) 구조
- **API Gateway**: `api.gateway` - 모든 서비스의 진입점
- **Service Discovery**: `service.discovery` - 서비스 등록 및 발견
- **Config Service**: `service.config` - 중앙화된 설정 관리
- **Auth Service**: `service.auth` - 사용자 인증 및 권한 관리
- **Healthcare Service**: `service.healthcare` - 건강 데이터 처리 및 분석
- **Communication Service**: `service.commu` - 커뮤니티 기능
- **Web Application**: `web.healthcare` - 사용자 인터페이스

### 기술 스택
- **Backend**: Spring Boot, Spring Cloud, Java
- **Database**: PostgreSQL
- **ORM**: MyBatis (XML Mapper), JPA
- **Frontend**: HTML, CSS, JavaScript, jQuery
- **Authentication**: JWT Token
- **AI Integration**: ChatGPT API
- **Configuration**: Spring Cloud Config, Spring Cloud Bus

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

## 📁 프로젝트 구조
```
healthcare_mono/
├── api.gateway/                 # API 게이트웨이
├── service.discovery/           # 서비스 디스커버리
├── service.config/              # 설정 관리 서비스
├── service.auth/                # 인증 서비스
├── service.healthcare/          # 헬스케어 서비스
├── service.commu/               # 커뮤니티 서비스
└── web.healthcare/              # 웹 애플리케이션
    ├── src/main/java/           # Java 소스코드
    ├── src/main/webapp/         # 웹 리소스
    │   ├── WEB-INF/jsp/         # JSP 페이지
    │   │   ├── user/            # 사용자 관련 페이지
    │   │   ├── health/          # 건강 정보 페이지
    │   │   ├── community/       # 커뮤니티 페이지
    │   │   └── include/         # 공통 포함 파일
    │   ├── css/                 # 스타일시트
    │   ├── js/                  # JavaScript 파일
    └── images/                  # 이미지 리소스
```

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 21+
- PostgreSQL / H2SQL
- Spring Boot 3.x
- Node.js (프론트엔드 빌드용)

### 실행 방법
1. PostgreSQL/H2SQL 데이터베이스 설정
2. 각 서비스별 application.yml 설정
3. 서비스 순서대로 실행:
   - Config Service
   - Discovery Service
   - Auth Service
   - Healthcare Service
   - Communication Service
   - API Gateway
   - Web Application

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
- JWT 토큰 기반 인증
- 사용자 데이터 암호화 (SHA-256)
- 세션 관리
- 권한별 접근 제어

## 📱 반응형 웹 디자인
- 모바일 및 데스크톱 환경 지원
- 사용자 친화적인 UI/UX
- 실시간 데이터 시각화
