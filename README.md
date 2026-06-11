# barrier-maintenance-report-hub
> 주차차단기 현장 유지보수 리포트를 표준 데이터로 수집하고, 관리팀 승인 후 외부 보고 양식으로 변환하며, 장비별 오류 재발률을 분석하는 Spring Boot 기반 백엔드 시스템

---

## 문제 배경

기존 유지보수 보고 프로세스는 현장 실무자가 클라이언트사 보고 시스템에 직접 접근할 수 없어, 카카오톡으로 수리 내용을 전달하고 관리팀이 이를 다시 취합·전달하는 수동 구조였다.

이 과정에서 다음과 같은 문제가 발생할 수 있었다.

- 보고 누락
- 사진·기기번호·수리시간 등 정보 불일치
- 수리 이력 추적 어려움
- 반복 오류 분석 불가능

현장 실무자가 웹에 수리 정보를 등록하면, 시스템이 이를 표준 데이터로 저장하고 클라이언트 보고 양식으로 변환하며, 내부적으로 오류 재발률과 장비별 이슈를 분석할 수 있는 백엔드 시스템을 설계한다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot, Spring Data JPA, Spring Security |
| Database | PostgreSQL |
| Build | Gradle |
| Test | JUnit5, Mockito, Testcontainers |
| Infra | Docker, Docker Compose, GitHub Actions |
| Docs | Swagger / OpenAPI |

---

## 핵심 기능

- 현장 기사의 유지보수 리포트 등록 및 사진 업로드
- 관리팀의 리포트 검토·승인·반려 워크플로우
- 승인된 리포트의 외부 보고 양식 변환 (Excel / CSV / JSON)
- 장비별 오류 재발률, 오류 유형 TOP N, 월별 오류 추이 통계 API
- Role 기반 접근 제어 (TECHNICIAN / MANAGER / ADMIN)

---

## 리포트 상태 전이

```
DRAFT → SUBMITTED → REVIEWING → APPROVED → EXPORTED

반려 흐름:
SUBMITTED → REJECTED → RESUBMITTED
```

---

## ERD (초안)

| 테이블 | 설명 |
|--------|------|
| users | 계정 정보, role 포함 |
| technicians | 현장 기사 프로필 (users 확장) |
| devices | 장비 정보 (시리얼번호, 위치, 모델명) |
| error_types | 오류 유형 코드 테이블 |
| repair_reports | 핵심 테이블. 리포트 본문, 상태, 처리 시간 |
| report_images | 리포트에 첨부된 이미지 메타데이터 |
| report_status_histories | 상태 변경 이력 (변경자, 사유, 시각) |
| report_exports | export 이력 (형식, 파일 URL, 실행자) |

<details>
<summary>테이블 컬럼 상세</summary>

**users**
- id, name, email, password, role, created_at

**technicians**
- id, user_id, phone, department, emp_no

**devices**
- id, serial_no, location, model_name, installed_at

**repair_reports**
- id, technician_id, device_id, error_type_id
- title, description, repair_action
- status, occurred_at, repaired_at
- created_at, updated_at

**report_images**
- id, report_id, image_url, image_type, uploaded_at

**error_types**
- id, code, name, description
- 실제 리포트에는 한 리포트에 에러타입이 여러개 등록될 수 있으니 DB 관리에 유의

**report_status_histories**
- id, report_id, from_status, to_status, changed_by, reason, changed_at

**report_exports**
- id, report_id, export_type, exported_by, exported_at, file_url

</details>

---

## API 명세 (주요 엔드포인트)

```
# 리포트
POST   /api/reports
GET    /api/reports
GET    /api/reports/{reportId}
PATCH  /api/reports/{reportId}
DELETE /api/reports/{reportId}

# 상태 변경
PATCH  /api/reports/{reportId}/status

# 이미지
POST   /api/reports/{reportId}/images
DELETE /api/reports/{reportId}/images/{imageId}

# Export
POST   /api/reports/{reportId}/export
GET    /api/reports/{reportId}/export/excel
GET    /api/reports/{reportId}/export/json

# 통계
GET    /api/statistics/error-types/top
GET    /api/statistics/devices/{deviceId}/recurrence-rate
GET    /api/statistics/monthly-error-rate
GET    /api/statistics/half-year-comparison
GET    /api/statistics/technicians/performance
```

* TODO: 전체 명세는 배포 후 Swagger UI에서 확인 가능하도록

---

## 권한 구조

| Role | 권한 |
|------|------|
| TECHNICIAN | 본인 리포트 등록·조회·수정, 이미지 업로드 |
| MANAGER | 전체 리포트 조회, 승인·반려, Export |
| ADMIN | 사용자 관리, 오류 유형 관리, 통계 조회 |

---

## 마일스톤

### Milestone 0. 요구사항 정리 및 도메인 모델링

실제 업무 흐름을 백엔드 도메인으로 구조화한다.

**할 일**
- 사용자 유형 정의 (TECHNICIAN / MANAGER / ADMIN)
- 유지보수 리포트 필드 정의 (기사 정보, 현장 위치, 기기번호, 오류 유형, 수리 내용, 사진, 처리 시간, 상태)
- 리포트 상태 정의 및 전이 조건 정리
- ERD 초안 작성
- API 목록 초안 작성

**산출물**
- 요구사항 정의서
- ERD
- API 명세 초안
- 상태 전이 다이어그램

---

### Milestone 1. 기본 CRUD API 구현

Spring Boot로 유지보수 리포트를 등록하고 조회할 수 있게 만든다.

**구현 기능**
- 기사 등록
- 장비 등록
- 유지보수 리포트 생성·조회·수정·삭제 (soft delete)
- 오류 유형 관리

**산출물**
- Spring Boot REST API
- PostgreSQL 기반 스키마
- Swagger/OpenAPI 문서

---

### Milestone 2. 권한 관리 및 워크플로우 구현

Role 기반 접근 제어와 리포트 승인 흐름을 구현한다.

**구현 기능**
- 로그인 / 회원가입
- JWT 인증
- Role 기반 API 접근 제어
- 기사: 본인 리포트만 조회·수정
- 관리팀: 전체 리포트 검토·승인·반려
- 관리자: 사용자·오류 유형 관리
- 상태 변경 이력 저장

**산출물**
- Spring Security + JWT 인증 구조
- 리포트 상태 전이 로직
- 상태 변경 이력 테이블

---

### Milestone 3. 사진 업로드 및 파일 관리

현장 수리 증빙 사진을 다룬다.

**구현 기능**
- 수리 전/후 사진 다중 업로드
- 리포트-이미지 연결
- 파일 확장자·용량 검증
- 업로드 실패 예외 처리
- 삭제 시 파일 정리

**구현 순서**
1. 로컬 파일 스토리지
2. AWS S3 또는 Cloudflare R2 확장

**산출물**
- Multipart file upload API
- 파일 메타데이터 테이블
- 이미지 URL 조회 API

---

### Milestone 4. 외부 보고 양식 변환 (Export Layer)

승인된 리포트를 클라이언트 제출 양식으로 변환한다.

**구현 기능**
- 승인된 리포트만 export 가능
- Excel / CSV / JSON 변환
- 클라이언트 제출용 필드 매핑
- 누락 필드 검증
- Export 이력 저장

**변환 예시**

내부 데이터:
```json
{
  "technicianName": "홍길동",
  "deviceSerial": "GATE-2024-001",
  "repairType": "SENSOR_ERROR",
  "description": "센서 인식 불량으로 부품 교체",
  "repairedAt": "2026-06-08T14:00:00"
}
```

외부 제출용:
```json
{
  "작업자": "홍길동",
  "장비기번": "GATE-2024-001",
  "고장유형": "센서 오류",
  "조치내용": "센서 인식 불량으로 부품 교체",
  "작업일시": "2026-06-08 14:00"
}
```

**산출물**
- Export Service
- Excel/CSV 변환 기능
- 외부 보고 양식 매핑 문서
- Export 이력 관리

---

### Milestone 5. 통계·분석 API 구현

저장된 데이터를 내부 의사결정 지표로 가공한다.

**구현 기능**
- 장비별 오류 발생 횟수
- 오류 유형별 발생 빈도 TOP N
- 월별 오류 추이
- 6개월 단위 오류율 비교
- 연간 오류 재발률
- 기사별 처리 건수 및 평균 처리 시간
- 동일 장비·동일 오류 유형 재발률

**핵심 쿼리 포인트**

GROUP BY, COUNT, DATE_TRUNC, 기간 조건, 재발률 계산, 페이징, 인덱스 설계

**산출물**
- 통계 API
- JPA / QueryDSL 쿼리
- 인덱스 설계
- 통계 결과 샘플

---

### Milestone 6. 테스트 코드 작성

검증 가능한 백엔드 프로젝트로 만든다.

**구현 범위**
- Service 단위 테스트
- Repository 테스트
- Controller 통합 테스트
- 권한별 API 접근 테스트
- 리포트 상태 전이 테스트
- Export 변환 테스트
- 통계 API 테스트

**기술**

JUnit5, Mockito, SpringBootTest, Testcontainers (PostgreSQL)

**테스트 케이스 예시**
- 기사 권한 사용자는 본인 리포트만 조회 가능해야 한다
- 승인되지 않은 리포트는 export할 수 없어야 한다
- 반려된 리포트는 재제출 가능해야 한다
- 동일 장비·동일 오류 유형이 일정 기간 내 반복되면 재발로 집계되어야 한다

---

### Milestone 7. Docker 및 CI/CD 구성

실행 환경과 배포 안정성을 갖춘다.

**구현 기능**
- Dockerfile 작성
- Docker Compose 구성 (Spring Boot + PostgreSQL + Redis 선택)
- GitHub Actions 구성 (build / test / docker image build)
- PR 시 테스트 자동 실행
- main merge 시 배포 (선택)

**산출물**
- Dockerfile
- docker-compose.yml
- GitHub Actions workflow

---

### Milestone 8. 배포 및 운영 문서화

실제 서비스처럼 동작하도록 배포하고 문서를 완성한다.

**구현 기능**
- AWS EC2 또는 Render/Fly.io 배포
- PostgreSQL: Supabase / RDS / Neon 중 선택
- Swagger URL 공개
- 트러블슈팅 문서 작성

---

## MVP 범위

### 1차 MVP

- 기사·관리자 로그인
- 유지보수 리포트 등록
- 사진 업로드
- 관리팀 승인·반려
- Excel Export
- 오류 유형별 통계 조회

### 2차 확장

- 재발률 계산
- 6개월 단위 오류율 비교
- Redis 캐싱
- Testcontainers
- GitHub Actions
- AWS 배포

---

## 로컬 실행 방법

> TODO: 작성 (Milestone 7 완료 후 업데이트)


---

## 트러블슈팅

> TODO: 작성 (개발 진행 중 업데이트)
