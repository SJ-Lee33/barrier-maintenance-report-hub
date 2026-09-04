# Export 기능 문서

## 1. 개요

Export 기능은 승인된 유지보수 리포트를 외부 제출용 파일 형식으로 변환하는 기능이다.

내부 리포트 데이터는 시스템 운영에 적합한 구조로 저장되어 있으며, Export 단계에서는 이를 외부 제출에 적합한 필드와 파일 포맷으로 변환한다.

지원하는 Export 형식은 다음과 같다.

- JSON
- CSV
- EXCEL

---

## 2. Export 가능 상태

리포트는 아래 상태일 때만 Export할 수 있다.

| 상태          | Export 가능 여부 | 설명                            |
|-------------|-------------:|-------------------------------|
| DRAFT       |           불가 | 작성 중인 리포트                     |
| SUBMITTED   |           불가 | 기사 제출 후 관리자 검토 전 상태           |
| REVIEWING   |           불가 | 관리자 검토 중 상태                   |
| APPROVED    |           가능 | 관리자 승인 완료 상태                  |
| EXPORTED    |           가능 | 이미 Export된 리포트의 재Export 가능 상태 |
| REJECTED    |           불가 | 관리자 반려 상태                     |
| RESUBMITTED |           불가 | 반려 후 재제출된 상태                  |

최초 Export는 `APPROVED` 상태에서 가능하다.

한 번 Export가 완료되면 리포트 상태는 `EXPORTED`로 변경된다.  
`EXPORTED` 상태에서도 JSON, CSV, EXCEL 등 다른 형식으로 재Export할 수 있다.

---

## 3. Export 권한

Export는 관리자 권한의 사용자만 수행할 수 있다.

| Role       | Export 가능 여부 |
|------------|-------------:|
| TECHNICIAN |           불가 |
| MANAGER    |           가능 |
| ADMIN      |           가능 |

기사는 리포트를 생성, 수정, 삭제, 제출, 재제출할 수 있지만 Export는 수행할 수 없다.

---

## 4. Export API

### 4.1 Export 실행

```http
POST /api/repair-reports/{reportId}/export
```

#### 요청 Header

```http
Authorization: Bearer {MANAGER_TOKEN}
Content-Type: application/json
```

#### 요청 Body

```json
{
  "exportType": "JSON",
  "reason": "외부 제출용 JSON 파일 생성"
}
```

#### 지원하는 exportType

| exportType | 생성 파일   |
|------------|---------|
| JSON       | `.json` |
| CSV        | `.csv`  |
| EXCEL      | `.xlsx` |

-> 성공 시 리포트 상태는 `EXPORTED`로 변경되고, Export 이력이 저장된다.

---

## 5. Export 이력 조회 API

```http
GET /api/repair-reports/{reportId}/exports
```

#### 요청 Header

```http
Authorization: Bearer {MANAGER_TOKEN}
```

#### 응답 예시

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

** Export 이력은 최신순으로 조회된다.

---

## 6. 외부 제출용 데이터 포맷

Export 파일은 공통 Export DTO를 기반으로 생성된다.

| 내부 데이터                          | 외부 제출용 필드 |
|---------------------------------|-----------|
| technician.user.name            | 작업자       |
| device.serialNo                 | 장비기번      |
| device.location                 | 장비위치      |
| reportErrorTypes.errorType.name | 고장유형      |
| repairReport.description        | 고장내용      |
| repairReport.repairAction       | 조치내용      |
| repairReport.occurredAt         | 고장발생시각    |
| repairReport.repairedAt         | 수리완료시각    |

날짜 형식은 `yyyy-MM-dd HH:mm` 으로 통일한다.

---

## 7. JSON Export 예시

```json
{
  "작업자": "김기사",
  "장비기번": "GATE-2024-001",
  "장비위치": "서울 강남구 A주차장 입구",
  "고장유형": "센서 오류, 차단봉 걸림",
  "고장내용": "외부 제출용 JSON 파일 생성 테스트 리포트",
  "조치내용": "센서 케이블 재연결 및 차단봉 동작 확인",
  "고장발생시각": "2026-09-04 15:00",
  "수리완료시각": "2026-09-04 16:00"
}
```

## 8. CSV Export 예시

```csv 
작업자,장비기번,장비위치,고장유형,고장내용,조치내용,고장발생시각,수리완료시각
김기사,GATE-2024-001,서울 강남구 A주차장 입구,"센서 오류, 차단봉 걸림",외부 제출용 CSV 파일 생성 테스트 리포트,센서 케이블 재연결 및 차단봉 동작 확인,2026-09-04 17:00,2026-09-04 18:00
```

---

## 9. Excel Export 예시

Excel Export는 **Apache POI** 를 사용해 .xlsx 파일을 생성한다.
Excel 파일에는 한글 헤더와 리포트 데이터가 포함된다.

---

## 10. 파일 저장 경로

Export 파일은 로컬 환경에서 아래 경로에 저장된다.

```text
exports/reports/{reportId}/{uuid}.{extension}
예시: exports/reports/12/0a1b2c3d-example.xlsx
```

DB에는 실제 로컬 경로가 아니라 클라이언트 접근용 URL이 저장된다. (`/exports/reports/{reportId}/{filename}`)

---

## 11. 파일 접근 URL

Export 파일은 아래 URL로 접근할 수 있다.

```text
http://localhost:8080/exports/reports/{reportId}/{filename}
```

---

## 12. 주요 예외 응답

### 12.1 존재하지 않는 리포트

HTTP/1.1 404
{
"status": 404,
"error": "Not Found",
"message": "리포트를 찾을 수 없습니다.",
"path": "/api/repair-reports/99999/export"
}

### 12.2 승인 전 리포트 Export

HTTP/1.1 400

{
"status": 400,
"error": "Bad Request",
"message": "승인 또는 내보내기 완료 상태의 리포트만 Export할 수 있습니다.",
"path": "/api/repair-reports/13/export"
}

### 12.3 권한 없는 사용자 Export

HTTP/1.1 403

TECHNICIAN 권한 사용자는 Export API를 호출할 수 없다.

### 12.4 잘못된 exportType

HTTP/1.1 400

{
"status": 400,
"error": "Bad Request",
"message": "지원하지 않는 Export 형식입니다. exportType은 JSON, CSV, EXCEL 중 하나여야 합니다.",
"path": "/api/repair-reports/13/export"
}

### 12.5 exportType 누락

HTTP/1.1 400

{
"status": 400,
"error": "Bad Request",
"message": "Export 형식은 필수입니다.",
"path": "/api/repair-reports/13/export"
}

---

## 13. 구현한 클래스

| 클래스                          | 역할                           |
|------------------------------|------------------------------|
| ExternalReportExportResponse | 외부 제출용 공통 Export DTO         |
| ExportMappingService         | RepairReport를 외부 제출용 DTO로 변환 |
| ExportFileStorage            | Export 파일 저장 경로와 URL 생성      |
| JsonExportFileGenerator      | JSON 파일 생성                   |
| CsvExportFileGenerator       | CSV 파일 생성                    |
| ExcelExportFileGenerator     | Excel 파일 생성                  |
| ReportExport                 | Export 이력 엔티티                |
| ReportExportRepository       | Export 이력 조회                 |
| ReportExportResponse         | Export 이력 조회 응답 DTO          |

