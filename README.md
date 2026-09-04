# barrier-maintenance-report-hub

> 주차차단기 현장 유지보수 리포트를 표준 데이터로 수집하고, 관리팀 승인 후 외부 제출용 파일(JSON / CSV / Excel)로 변환하며, 장비별 오류 재발률을 분석하는 Spring Boot 기반 백엔드 시스템

---

## 1. 프로젝트 개요

`barrier-maintenance-report-hub`는 주차장 차단기 유지보수 현장의 보고 업무를 표준화하기 위한 백엔드 프로젝트입니다.

기존에는 현장 기사가 수리 내용과 사진을 메신저로 전달하고, 관리팀이 이를 다시 취합해 클라이언트 보고 시스템에 입력하는 수동 프로세스가 필요했습니다. 이 과정에서는 보고 누락, 장비 번호 불일치, 사진 누락, 수리
이력 추적 어려움, 반복 오류 분석 불가 등의 문제가 발생할 수 있었습니다.

본 프로젝트는 현장 리포트 등록부터 관리자 승인, 외부 제출용 파일 변환, 장비별 오류 분석까지 하나의 백엔드 흐름으로 구현하는 것을 목표로 합니다.

---

## 2. 문제 배경

기존 유지보수 보고 프로세스는 다음과 같은 구조였습니다.

```text
현장 기사
→ 카카오톡 등 메신저로 수리 내용/사진 전달
→ 관리팀이 수동 취합
→ 클라이언트 보고 시스템에 재입력
```

이 방식에서는 다음 문제가 발생할 수 있습니다.

- 수리 내용 또는 사진 누락
- 기기번호, 수리시간, 조치내용 불일치
- 리포트 승인 여부 추적 어려움
- 장비별 고장 이력 관리 어려움
- 반복 오류 및 재발률 분석 불가능
- 외부 제출 양식 변환 작업의 반복 발생

이를 해결하기 위해 현장 리포트를 표준 데이터로 저장하고, 승인된 리포트만 외부 제출용 파일로 변환하며, 축적된 데이터를 기반으로 장비별 오류 재발률을 분석하는 백엔드 시스템을 설계했습니다.

---

## 3. 핵심 기능

| 구분        | 기능                                   |
|-----------|--------------------------------------|
| 인증/인가     | JWT 기반 로그인, Role 기반 접근 제어            |
| 사용자 관리    | TECHNICIAN / MANAGER / ADMIN 역할 구분   |
| 리포트 관리    | 유지보수 리포트 생성, 조회, 수정, 삭제              |
| 상태 워크플로우  | 제출, 검토, 승인, 반려, 재제출, Export 상태 전이    |
| 이미지 관리    | 수리 전/후/기타 이미지 업로드, 조회, 삭제            |
| Export    | 승인된 리포트를 JSON, CSV, Excel 파일로 변환     |
| Export 이력 | Export 형식, 실행자, 실행 시각, 파일 URL 저장     |
| 분석 API    | 오류 유형별 발생 횟수, 장비별 오류 재발률, 장비별 요약 통계  |
| 예외 처리     | 전역 예외 응답 구조 통일                       |
| API 문서    | Swagger / OpenAPI 기반 API 문서화         |
| 테스트       | 인증, 리포트 상태 변경, Export, 분석 API 통합 테스트 |

---

## 4. 기술 스택

| 분류          | 기술                              |
|-------------|---------------------------------|
| Language    | Java 17                         |
| Framework   | Spring Boot 4.0.6               |
| Web         | Spring MVC                      |
| Persistence | Spring Data JPA                 |
| Security    | Spring Security, JWT            |
| Database    | PostgreSQL 16                   |
| Build       | Gradle                          |
| File        | Local File Storage              |
| Export      | Jackson, Apache POI             |
| Test        | JUnit5, MockMvc, SpringBootTest |
| Infra       | Docker, Docker Compose          |
| Docs        | Swagger / OpenAPI               |

---

## 5. 시스템 흐름

```text
TECHNICIAN 로그인
→ 유지보수 리포트 작성
→ 수리 전/후 사진 업로드
→ 리포트 제출
→ MANAGER 검토/승인
→ 승인 리포트 Export
→ JSON / CSV / Excel 파일 생성
→ Export 이력 저장
→ 장비별 오류 재발률 분석
```

---

## 6. 역할 및 권한

| Role       | 주요 권한                                             |
|------------|---------------------------------------------------|
| TECHNICIAN | 본인 리포트 생성, 조회, 수정, 삭제, 제출, 재제출, 본인 리포트 이미지 업로드/삭제 |
| MANAGER    | 전체 리포트 조회, 이미지 조회, 검토, 승인, 반려, Export, 분석 API 조회  |
| ADMIN      | 사용자 관리, 기사 관리, 장비 관리, 오류 유형 관리, Export, 분석 API 조회 |

### 인증 사용자 기반 처리 정책

- 리포트 생성 시 `technicianId`는 요청값으로 받지 않고, JWT 인증 사용자와 연결된 기사 프로필을 사용합니다.
- 제출, 검토, 승인, 반려, 재제출, Export 처리자는 요청값으로 받지 않고 현재 로그인 사용자를 기록합니다.
- TECHNICIAN은 본인이 작성한 리포트만 수정, 삭제, 제출, 재제출할 수 있습니다.
- MANAGER와 ADMIN은 전체 리포트를 검토, 승인, 반려, Export할 수 있습니다.
- 분석 API는 MANAGER와 ADMIN만 접근할 수 있습니다.

---

## 7. 리포트 상태 전이

### 기본 승인 흐름

```text
DRAFT → SUBMITTED → APPROVED → EXPORTED
```

### 검토 상태 포함 흐름

```text
DRAFT → SUBMITTED → REVIEWING → APPROVED → EXPORTED
```

### 반려 및 재제출 흐름

```text
SUBMITTED → REJECTED → RESUBMITTED → SUBMITTED
REVIEWING → REJECTED → RESUBMITTED → SUBMITTED
```

### 상태별 의미

| 상태          | 의미                 | 비고          |
|-------------|--------------------|-------------|
| DRAFT       | 기사가 작성 중인 리포트      | 제출 전        |
| SUBMITTED   | 기사가 제출한 리포트        | 관리자 확인 대기   |
| REVIEWING   | 관리팀이 추가 검토 중인 리포트  | 선택 상태       |
| APPROVED    | 관리팀이 승인한 리포트       | Export 가능   |
| EXPORTED    | 외부 제출용 파일로 변환된 리포트 | 재Export 가능  |
| REJECTED    | 관리팀이 반려한 리포트       | 수정 후 재제출 가능 |
| RESUBMITTED | 반려 후 재제출된 리포트      | 다시 제출 가능    |

### 주요 처리 시점 기록

| 이벤트    | 기록 필드                          |
|--------|--------------------------------|
| 제출     | `submitted_at`, `submitted_by` |
| 승인     | `approved_at`, `approved_by`   |
| Export | `exported_at`, `exported_by`   |

상태 변경 전체 흐름은 `report_status_histories` 테이블에 감사 로그로 저장합니다.

---

## 8. ERD 요약

| 테이블                     | 설명                 |
|-------------------------|--------------------|
| users                   | 계정 정보, 역할, 인증 정보   |
| technicians             | 현장 기사 프로필          |
| devices                 | 차단기 장비 정보          |
| error_types             | 오류 유형 코드 관리        |
| repair_reports          | 유지보수 리포트 핵심 데이터    |
| report_error_types      | 리포트와 오류 유형의 다대다 연결 |
| report_images           | 리포트 첨부 이미지 메타데이터   |
| report_status_histories | 리포트 상태 변경 이력       |
| report_exports          | Export 이력          |

### 관계 요약

```text
users 1 ── 1 technicians

technicians 1 ── N repair_reports
devices     1 ── N repair_reports

users 1 ── N repair_reports submitted_by
users 1 ── N repair_reports approved_by
users 1 ── N repair_reports exported_by

repair_reports 1 ── N report_error_types
error_types    1 ── N report_error_types

repair_reports 1 ── N report_images
repair_reports 1 ── N report_status_histories
repair_reports 1 ── N report_exports
```

<details>
<summary>테이블 컬럼 상세</summary>

### users

- `id`
- `name`
- `email`
- `phone`
- `password`
- `role`
- `created_at`
- `updated_at`

특징:

- `email`은 unique 제약을 가집니다.
- `name`, `phone` 조합은 unique 제약을 가집니다.
- `password`는 BCrypt로 암호화하여 저장합니다.

### technicians

- `id`
- `user_id`
- `phone`
- `department`
- `emp_no`

특징:

- `user_id`는 `users.id`를 참조합니다.
- `user_id`는 unique 제약을 가지며, 하나의 사용자는 하나의 기사 프로필만 가질 수 있습니다.
- `emp_no`는 unique 제약을 가집니다.

### devices

- `id`
- `serial_no`
- `location`
- `model_name`
- `installed_at`

### error_types

- `id`
- `code`
- `name`
- `description`

운영 중 새로운 오류 유형은 `error_types` 테이블에 추가하는 방식으로 확장할 수 있습니다.

### repair_reports

- `id`
- `technician_id`
- `device_id`
- `title`
- `description`
- `repair_action`
- `status`
- `occurred_at`
- `repaired_at`
- `submitted_at`
- `submitted_by`
- `approved_at`
- `approved_by`
- `exported_at`
- `exported_by`
- `deleted`
- `created_at`
- `updated_at`

### report_error_types

- `id`
- `report_id`
- `error_type_id`
- `created_at`
- `updated_at`

하나의 리포트에 여러 오류 유형을 연결하기 위한 중간 테이블입니다.

### report_images

- `id`
- `report_id`
- `image_url`
- `image_type`
- `uploaded_at`

### report_status_histories

- `id`
- `report_id`
- `from_status`
- `to_status`
- `changed_by`
- `reason`
- `changed_at`

### report_exports

- `id`
- `report_id`
- `export_type`
- `exported_by`
- `exported_at`
- `file_url`

</details>

---

## 9. 주요 API

### 인증

| Method | URL                | 설명            | 권한            |
|--------|--------------------|---------------|---------------|
| POST   | `/api/auth/signup` | 회원가입          | Public        |
| POST   | `/api/auth/login`  | 로그인 및 JWT 발급  | Public        |
| GET    | `/api/auth/me`     | 현재 로그인 사용자 조회 | Authenticated |

### 사용자 / 기사 / 장비 / 오류 유형

| Method | URL                               | 설명        | 권한             |
|--------|-----------------------------------|-----------|----------------|
| POST   | `/api/users`                      | 사용자 생성    | ADMIN          |
| GET    | `/api/users`                      | 사용자 목록 조회 | ADMIN          |
| GET    | `/api/users/{userId}`             | 사용자 단건 조회 | ADMIN          |
| POST   | `/api/technicians`                | 기사 프로필 생성 | MANAGER, ADMIN |
| GET    | `/api/technicians`                | 기사 목록 조회  | MANAGER, ADMIN |
| GET    | `/api/technicians/{technicianId}` | 기사 단건 조회  | MANAGER, ADMIN |
| POST   | `/api/devices`                    | 장비 생성     | MANAGER, ADMIN |
| GET    | `/api/devices`                    | 장비 목록 조회  | MANAGER, ADMIN |
| GET    | `/api/devices/{deviceId}`         | 장비 단건 조회  | MANAGER, ADMIN |
| GET    | `/api/error-types`                | 오류 유형 조회  | Authenticated  |

### 리포트

| Method | URL                              | 설명        | 권한                 |
|--------|----------------------------------|-----------|--------------------|
| POST   | `/api/repair-reports`            | 리포트 생성    | TECHNICIAN         |
| GET    | `/api/repair-reports`            | 리포트 목록 조회 | Authenticated      |
| GET    | `/api/repair-reports/{reportId}` | 리포트 단건 조회 | Authenticated      |
| PATCH  | `/api/repair-reports/{reportId}` | 리포트 수정    | TECHNICIAN, 본인 리포트 |
| DELETE | `/api/repair-reports/{reportId}` | 리포트 삭제    | TECHNICIAN, 본인 리포트 |

### 상태 변경

| Method | URL                                        | 설명          | 권한                 |
|--------|--------------------------------------------|-------------|--------------------|
| PATCH  | `/api/repair-reports/{reportId}/submit`    | 리포트 제출      | TECHNICIAN, 본인 리포트 |
| PATCH  | `/api/repair-reports/{reportId}/review`    | 리포트 검토 시작   | MANAGER, ADMIN     |
| PATCH  | `/api/repair-reports/{reportId}/approve`   | 리포트 승인      | MANAGER, ADMIN     |
| PATCH  | `/api/repair-reports/{reportId}/reject`    | 리포트 반려      | MANAGER, ADMIN     |
| PATCH  | `/api/repair-reports/{reportId}/resubmit`  | 반려 리포트 재제출  | TECHNICIAN, 본인 리포트 |
| GET    | `/api/repair-reports/{reportId}/histories` | 상태 변경 이력 조회 | Authenticated      |

### 이미지

| Method | URL                                                      | 설명        | 권한                 |
|--------|----------------------------------------------------------|-----------|--------------------|
| POST   | `/api/repair-reports/{reportId}/images?imageType=BEFORE` | 이미지 업로드   | TECHNICIAN, 본인 리포트 |
| GET    | `/api/repair-reports/{reportId}/images`                  | 이미지 목록 조회 | Authenticated      |
| DELETE | `/api/repair-reports/{reportId}/images/{imageId}`        | 이미지 삭제    | TECHNICIAN, 본인 리포트 |

### Export

| Method | URL                                      | 설명            | 권한             |
|--------|------------------------------------------|---------------|----------------|
| POST   | `/api/repair-reports/{reportId}/export`  | 승인 리포트 Export | MANAGER, ADMIN |
| GET    | `/api/repair-reports/{reportId}/exports` | Export 이력 조회  | Authenticated  |

지원 Export 형식:

| exportType | 확장자     |
|------------|---------|
| JSON       | `.json` |
| CSV        | `.csv`  |
| EXCEL      | `.xlsx` |

### 분석

| Method | URL                                 | 설명                 | 권한             |
|--------|-------------------------------------|--------------------|----------------|
| GET    | `/api/analytics/error-types`        | 전체 오류 유형별 발생 횟수 조회 | MANAGER, ADMIN |
| GET    | `/api/analytics/devices/{deviceId}` | 특정 장비 분석 통계 조회     | MANAGER, ADMIN |
| GET    | `/api/analytics/devices`            | 전체 장비별 분석 요약 조회    | MANAGER, ADMIN |

---

## 10. Export 기능

승인된 리포트는 외부 제출용 파일로 변환할 수 있습니다.

### Export 가능 상태

| 상태       | Export 가능 여부 |
|----------|-------------:|
| APPROVED |           가능 |
| EXPORTED |           가능 |
| 그 외 상태   |           불가 |

`EXPORTED` 상태에서도 재Export를 허용하여, 하나의 승인 리포트를 JSON, CSV, Excel 등 여러 포맷으로 변환할 수 있습니다.

### 파일 저장 경로

```text
exports/reports/{reportId}/{uuid}.{extension}
```

### 파일 접근 URL

```text
http://localhost:8080/exports/reports/{reportId}/{filename}
```

### Export 이력 응답 예시

```json
[
  {
    "id": 1,
    "reportId": 12,
    "exportType": "EXCEL",
    "exportedByUserId": 2,
    "exportedByUserName": "박관리",
    "exportedAt": "2026-09-04T19:20:00.123456",
    "fileUrl": "/exports/reports/12/example.xlsx",
    "downloadUrl": "http://localhost:8080/exports/reports/12/example.xlsx",
    "fileName": "example.xlsx"
  }
]
```

---

## 11. 분석 기능

분석 API는 삭제되지 않고 관리자 검증이 완료된 리포트를 대상으로 합니다.

```text
deleted = false
status in (APPROVED, EXPORTED)
```

### 분석 기준

| 항목           | 기준                                    |
|--------------|---------------------------------------|
| 장비별 총 리포트 수  | 특정 장비의 APPROVED 또는 EXPORTED 리포트 수     |
| 오류 유형별 발생 횟수 | 리포트-오류유형 연결 기준 count                  |
| 장비별 오류 재발률   | 특정 오류 유형 발생 횟수 / 해당 장비의 총 리포트 수 * 100 |

재발률은 소수점 첫째 자리까지 반올림합니다.

### 특정 장비 분석 응답 예시

```json
{
  "deviceId": 1,
  "serialNo": "GATE-2024-001",
  "location": "서울 강남구 A주차장 입구",
  "totalReportCount": 7,
  "errorTypeStats": [
    {
      "errorTypeId": 1,
      "code": "SENSOR_ERROR",
      "name": "센서 오류",
      "count": 7,
      "recurrenceRate": 100.0
    },
    {
      "errorTypeId": 3,
      "code": "ARM_STUCK",
      "name": "차단봉 걸림",
      "count": 6,
      "recurrenceRate": 85.7
    }
  ]
}
```

---

## 12. 로컬 실행 가이드

### 12.1 사전 준비

| 항목             | 버전   |
|----------------|------|
| Java           | 17   |
| Gradle         | 9.x  |
| PostgreSQL     | 16   |
| Docker         | 29.x |
| Docker Compose | 5.x  |

### 12.2 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성합니다.

```bash
cp .env.example .env
```

`.env` 예시:

```env
JWT_SECRET=your-jwt-secret-key-your-jwt-secret-key-your-jwt-secret-key
JWT_ACCESS_TOKEN_VALIDITY=3600000
```

### 12.3 PostgreSQL 실행

```bash
docker compose up -d
```

컨테이너 실행 확인:

```bash
docker ps
```

PostgreSQL 접속 확인:

```bash
docker exec -it barrier-report-postgres psql -U local_user -d barrier_report
```

### 12.4 애플리케이션 실행

```bash
./run-local.sh
```

정상 실행 시 다음 로그를 확인할 수 있습니다.

```text
Tomcat started on port 8080
Started BarrierMaintenanceReportHubApplication
```

### 12.5 Swagger UI 접속

```text
http://localhost:8080/swagger-ui/index.html
```

인증이 필요한 API는 Swagger 우측 상단 `Authorize`에 JWT를 입력한 뒤 호출합니다.

```text
Bearer {accessToken}
```

---

## 13. 기본 테스트 계정

로컬 개발 환경에서는 초기 데이터로 다음 계정을 사용할 수 있습니다.

| 역할         | 이메일                    | 비밀번호           |
|------------|------------------------|----------------|
| TECHNICIAN | `tech3@example.com`    | `password1234` |
| MANAGER    | `manager1@example.com` | `password1234` |

### 로그인 예시

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"tech3@example.com","password":"password1234"}'
```

응답의 `accessToken`을 API 요청에 사용합니다.

```bash
curl "http://localhost:8080/api/auth/me" \
  -H "Authorization: Bearer {accessToken}"
```

---

## 14. 주요 API 사용 흐름

### 14.1 기사 리포트 작성 및 제출

```text
TECHNICIAN 로그인
→ POST /api/repair-reports
→ POST /api/repair-reports/{reportId}/images
→ PATCH /api/repair-reports/{reportId}/submit
```

### 14.2 관리자 검토 및 승인

```text
MANAGER 로그인
→ PATCH /api/repair-reports/{reportId}/review
→ PATCH /api/repair-reports/{reportId}/approve
```

### 14.3 외부 제출용 Export

```text
MANAGER 로그인
→ POST /api/repair-reports/{reportId}/export
→ GET /api/repair-reports/{reportId}/exports
→ /exports/reports/{reportId}/{filename} 접근
```

### 14.4 분석 API 조회

```text
MANAGER 로그인
→ GET /api/analytics/error-types
→ GET /api/analytics/devices/{deviceId}
→ GET /api/analytics/devices
```

---

## 15. 테스트 실행

### 전체 테스트

```bash
./gradlew test
```

### 전체 빌드

```bash
./gradlew build
```

### 특정 테스트 클래스 실행

```bash
./gradlew test --tests com.example.maintenance.domain.auth.AuthControllerTest
./gradlew test --tests com.example.maintenance.domain.report.RepairReportControllerTest
./gradlew test --tests com.example.maintenance.domain.report.ExportControllerTest
./gradlew test --tests com.example.maintenance.domain.analytics.AnalyticsControllerTest
```

### 테스트 환경

테스트는 `application-test.yml`과 `test` profile을 사용합니다.

```text
src/test/resources/application-test.yml
```

테스트용 파일 저장 경로:

```text
test-uploads/
test-exports/
```

---

## 16. 로컬 파일 저장 경로

### 이미지 업로드 파일

```text
uploads/report-images/{reportId}/{uuid}.{extension}
```

접근 URL:

```text
http://localhost:8080/uploads/report-images/{reportId}/{filename}
```

### Export 파일

```text
exports/reports/{reportId}/{uuid}.{extension}
```

접근 URL:

```text
http://localhost:8080/exports/reports/{reportId}/{filename}
```

다음 디렉터리는 Git에 포함하지 않습니다.

```gitignore
uploads/
exports/
test-uploads/
test-exports/
```

---

## 17. 마일스톤 진행 현황

| Milestone   | 내용                               | 상태   |
|-------------|----------------------------------|------|
| Milestone 1 | 기본 도메인 및 CRUD API 구현             | 완료   |
| Milestone 2 | JWT 인증, Role 권한, 리포트 상태 워크플로우 구현 | 완료   |
| Milestone 3 | 이미지 업로드 및 파일 관리                  | 완료   |
| Milestone 4 | JSON / CSV / Excel Export 기능 구현  | 완료   |
| Milestone 5 | 장비별 오류 재발률 및 분석 API 구현           | 완료   |
| Milestone 6 | 테스트 코드 및 품질 개선                   | 진행 중 |

---

## 18. 구현 문서

- [사진 업로드 및 파일 관리 구현 보고서](docs/image-upload.md)
- [Export 기능 문서](docs/export.md)
- [분석 기능 문서](docs/analytics.md)

---

## 19. 프로젝트에서 중점적으로 다룬 부분

### 19.1 역할 기반 책임 분리

현장 기사와 관리자의 업무를 Role 기반으로 분리했습니다.  
기사는 본인 리포트를 작성하고 제출하며, 관리자는 제출된 리포트를 검토하고 승인합니다.

### 19.2 상태 전이 기반 업무 흐름 구현

리포트 상태를 단순 문자열로 처리하지 않고, 도메인 메서드를 통해 상태 전이를 제한했습니다.  
이를 통해 승인 전 Export, 반려 전 재제출 등 잘못된 업무 흐름을 방지했습니다.

### 19.3 파일 메타데이터와 실제 파일 저장 분리

이미지와 Export 파일은 로컬 파일 시스템에 저장하고, DB에는 접근 가능한 URL과 메타데이터를 저장했습니다.  
이를 통해 추후 S3, R2 등 외부 스토리지로 확장하기 쉬운 구조를 고려했습니다.

### 19.4 외부 제출용 데이터 변환 계층 분리

내부 `RepairReport` 구조를 그대로 외부에 노출하지 않고, Export 전용 DTO로 변환한 뒤 JSON, CSV, Excel 생성기가 각각 파일을 생성하도록 구성했습니다.

### 19.5 운영 데이터 기반 분석 API

승인 또는 Export 완료된 리포트만 분석 대상으로 삼아, 검증된 데이터 기준의 장비별 오류 재발률과 오류 유형별 발생 횟수를 제공합니다.

### 19.6 전역 예외 응답 통일

비즈니스 예외, 권한 예외, 유효성 검증 실패, JSON 변환 실패를 전역 예외 처리기로 통일해 API 응답 일관성을 높였습니다.

---

## 20. 향후 개선 방향

- Testcontainers 기반 PostgreSQL 테스트 환경 도입
- GitHub Actions 기반 CI 구성
- AWS S3 또는 Cloudflare R2 파일 저장소 확장
- 분석 API 기간 조건 및 페이징 추가
- 장비별 월간 오류 추이 API 추가
- Dockerfile 작성 및 애플리케이션 컨테이너화
- 운영 환경 배포 및 Swagger URL 공개
