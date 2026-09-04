# 분석 기능 문서

## 1. 개요

분석 기능은 유지보수 리포트 데이터를 기반으로 장비별 고장 이력과 오류 유형별 발생 현황을 집계하는 기능이다.

이 기능을 통해 관리자는 특정 장비에서 어떤 오류가 반복적으로 발생하는지 확인하고, 유지보수 우선순위나 장비 교체 필요성을 판단할 수 있다.

---

## 2. 분석 대상 리포트 기준

분석에는 관리자 검증이 완료된 리포트만 포함한다.

분석 대상에 포함되는 리포트 상태는 다음과 같다.

| 상태          | 분석 포함 여부 | 설명                      |
|-------------|---------:|-------------------------|
| DRAFT       |       제외 | 작성 중인 리포트               |
| SUBMITTED   |       제외 | 기사 제출 후 관리자 검토 전 상태     |
| REVIEWING   |       제외 | 관리자 검토 중 상태             |
| APPROVED    |       포함 | 관리자 승인 완료 상태            |
| EXPORTED    |       포함 | 외부 제출까지 진행된 리포트         |
| REJECTED    |       제외 | 관리자 반려 상태               |
| RESUBMITTED |       제외 | 반려 후 재제출되었지만 아직 승인 전 상태 |

삭제 처리된 리포트는 분석 대상에서 제외한다.

```text
deleted = false
status in (APPROVED, EXPORTED)
```

---

## 3. 분석 기준

### 3.1 장비별 총 리포트 수

장비별 총 리포트 수는 특정 장비에 연결된 분석 대상 리포트의 개수로 계산한다.

```text
장비별 총 리포트 수 = 해당 장비의 APPROVED 또는 EXPORTED 리포트 수
```

### 3.2 오류 유형별 발생 횟수

하나의 리포트에는 여러 오류 유형이 연결될 수 있다.

예를 들어 하나의 리포트에 아래 오류 유형이 함께 연결되어 있다면:

```text
SENSOR_ERROR
ARM_STUCK
```

각 오류 유형은 각각 1회씩 집계한다.

```text
SENSOR_ERROR +1
ARM_STUCK +1
```

### 3.3 장비별 오류 재발률

장비별 오류 재발률은 특정 장비의 전체 분석 대상 리포트 수 대비 특정 오류 유형이 발생한 비율로 계산한다.

```text
장비별 오류 재발률 = 특정 오류 유형 발생 횟수 / 해당 장비의 총 리포트 수 * 100
```

예시:

```text
장비 A의 총 리포트 수: 10건
SENSOR_ERROR 발생 횟수: 4건

SENSOR_ERROR 재발률 = 4 / 10 * 100 = 40.0%
```

재발률은 소수점 첫째 자리까지 표시한다.

---

## 4. 구현 완료 API

| Method | URL                               | 설명                 | 권한             |
|--------|-----------------------------------|--------------------|----------------|
| GET    | /api/analytics/error-types        | 전체 오류 유형별 발생 횟수 조회 | MANAGER, ADMIN |
| GET    | /api/analytics/devices/{deviceId} | 특정 장비의 분석 통계 조회    | MANAGER, ADMIN |
| GET    | /api/analytics/devices            | 전체 장비별 분석 요약 조회    | MANAGER, ADMIN |

---

## 5. 전체 오류 유형 통계 API

전체 오류 유형 통계는 모든 분석 대상 리포트를 기준으로 오류 유형별 발생 횟수를 집계한다.

```http
GET /api/analytics/error-types
```

### 응답 필드

| 필드             | 설명                  |
|----------------|---------------------|
| errorTypeId    | 오류 유형 ID            |
| code           | 오류 유형 코드            |
| name           | 오류 유형 이름            |
| count          | 발생 횟수               |
| recurrenceRate | 전체 오류 유형 통계에서는 null |

정렬 기준은 발생 횟수 내림차순이다.

### 응답 예시

```json
[
  {
    "errorTypeId": 1,
    "code": "SENSOR_ERROR",
    "name": "센서 오류",
    "count": 8,
    "recurrenceRate": null
  },
  {
    "errorTypeId": 3,
    "code": "ARM_STUCK",
    "name": "차단봉 걸림",
    "count": 6,
    "recurrenceRate": null
  }
]
```

---

## 6. 특정 장비 분석 통계 API

특정 장비 분석 API는 하나의 장비를 기준으로 리포트 수와 오류 유형별 발생 횟수, 재발률을 반환한다.

```http
GET /api/analytics/devices/{deviceId}
```

### 응답 필드

| 필드               | 설명           |
|------------------|--------------|
| deviceId         | 장비 ID        |
| serialNo         | 장비 기번        |
| location         | 장비 위치        |
| totalReportCount | 분석 대상 리포트 수  |
| errorTypeStats   | 오류 유형별 통계 목록 |

### errorTypeStats 필드

| 필드             | 설명          |
|----------------|-------------|
| errorTypeId    | 오류 유형 ID    |
| code           | 오류 유형 코드    |
| name           | 오류 유형 이름    |
| count          | 발생 횟수       |
| recurrenceRate | 해당 장비 내 재발률 |

### 응답 예시

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

## 7. 전체 장비 분석 요약 API

전체 장비 분석 요약 API는 모든 장비의 분석 통계를 목록으로 반환한다.

```http
GET /api/analytics/devices
```

### 응답 필드

| 필드                         | 설명                     |
|----------------------------|------------------------|
| deviceId                   | 장비 ID                  |
| serialNo                   | 장비 기번                  |
| location                   | 장비 위치                  |
| totalReportCount           | 분석 대상 리포트 수            |
| topErrorTypeName           | 가장 많이 발생한 오류 유형        |
| topErrorTypeCount          | 가장 많이 발생한 오류 유형의 발생 횟수 |
| topErrorTypeRecurrenceRate | 가장 많이 발생한 오류 유형의 재발률   |

정렬 기준은 총 리포트 수 내림차순이다.

### 응답 예시

```json
[
  {
    "deviceId": 1,
    "serialNo": "GATE-2024-001",
    "location": "서울 강남구 A주차장 입구",
    "totalReportCount": 7,
    "topErrorTypeName": "센서 오류",
    "topErrorTypeCount": 7,
    "topErrorTypeRecurrenceRate": 100.0
  }
]
```

---

## 8. 권한 정책

분석 API는 관리자 성격의 기능이므로 MANAGER와 ADMIN만 접근할 수 있다.

| Role       | 분석 API 접근 |
|------------|----------:|
| TECHNICIAN |        불가 |
| MANAGER    |        가능 |
| ADMIN      |        가능 |

SecurityConfig에서는 다음 경로에 대해 MANAGER, ADMIN 권한만 허용한다.

```text
/api/analytics/**
```

---

## 9. 예외 처리 기준

### 9.1 존재하지 않는 장비 조회

```http
HTTP/1.1 404
```

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "장비를 찾을 수 없습니다.",
  "path": "/api/analytics/devices/99999"
}
```

### 9.2 권한 없는 사용자 접근

```http
HTTP/1.1 403
```

TECHNICIAN 권한 사용자는 분석 API를 호출할 수 없다.

### 9.3 분석 대상 데이터가 없는 경우

분석 대상 데이터가 없는 경우 404가 아니라 count 0 또는 빈 목록으로 응답한다.

예시:

```json
{
  "deviceId": 1,
  "serialNo": "GATE-2024-001",
  "location": "서울 강남구 A주차장 입구",
  "totalReportCount": 0,
  "errorTypeStats": []
}
```

---

## 10. 구현 클래스

| 클래스                            | 역할                      |
|--------------------------------|-------------------------|
| AnalyticsController            | 분석 API 요청 처리            |
| AnalyticsService               | 분석 통계 계산                |
| ErrorTypeStatisticsResponse    | 오류 유형별 통계 응답 DTO        |
| DeviceAnalyticsResponse        | 특정 장비 분석 응답 DTO         |
| DeviceAnalyticsSummaryResponse | 전체 장비 분석 요약 응답 DTO      |
| ErrorTypeStatisticsProjection  | 오류 유형별 집계 Projection    |
| DeviceReportCountProjection    | 장비별 리포트 수 집계 Projection |
| RepairReportRepository         | 장비별 리포트 수 집계 쿼리 제공      |
| ReportErrorTypeRepository      | 오류 유형별 발생 횟수 집계 쿼리 제공   |

---

## 11. 구현 완료 항목

- 분석 대상 리포트 기준 정의
- 오류 유형별 발생 횟수 집계
- 특정 장비의 분석 대상 리포트 수 집계
- 특정 장비의 오류 유형별 발생 횟수 집계
- 장비별 오류 재발률 계산
- 전체 장비별 분석 요약 조회
- 분석 API 권한 설정
- 분석 API Swagger 문서화
- 존재하지 않는 장비 조회 예외 처리

---

## 12. 테스트 명령어

### 12.1 전체 오류 유형별 발생 횟수 조회

```bash
curl -i "http://localhost:8080/api/analytics/error-types" \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

### 12.2 특정 장비 분석 통계 조회

```bash
curl -i "http://localhost:8080/api/analytics/devices/1" \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

### 12.3 전체 장비 분석 요약 조회

```bash
curl -i "http://localhost:8080/api/analytics/devices" \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

### 12.4 TECHNICIAN 접근 차단 확인

```bash
curl -i "http://localhost:8080/api/analytics/devices" \
  -H "Authorization: Bearer $TECH_TOKEN"
```

기대 응답:

```http
HTTP/1.1 403
```

### 12.5 존재하지 않는 장비 조회 확인

```bash
curl -i "http://localhost:8080/api/analytics/devices/99999" \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

기대 응답:

```http
HTTP/1.1 404
```
