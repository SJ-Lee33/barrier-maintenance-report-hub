# Milestone 3. 사진 업로드 및 파일 관리 완료 보고서

## 1. 목표

Milestone 3의 목표는 현장 유지보수 리포트에 수리 증빙 이미지를 첨부할 수 있도록 파일 업로드 기능을 구현하는 것이었다.

구체적으로는 다음 기능을 구현 대상으로 삼았다.

```text
- 이미지 메타데이터 조회 API 구현
- multipart/form-data 기반 로컬 이미지 업로드 API 구현
- 이미지 파일 검증
- 로컬 파일 시스템에 이미지 파일 저장
- 이미지 업로드 및 삭제 권한 검증
- 이미지 삭제 시 DB row 및 실제 파일 삭제
- 업로드된 이미지 URL 접근 지원
```

이번 단계에서는 클라우드 스토리지 연동보다 로컬 파일 저장 방식을 먼저 구현하여, 파일 업로드 기능의 기본 구조와 검증 흐름을 안정적으로 완성하는 데 집중했다.

---

## 2. 구현 범위

### 2.1 이미지 업로드 API

리포트에 이미지를 첨부하기 위해 multipart 요청을 처리하는 API를 구현했다.

```http
POST /api/repair-reports/{reportId}/images
```

요청은 `multipart/form-data` 형식을 사용하며, 하나의 리포트에 여러 이미지를 업로드할 수 있도록 `List<MultipartFile>` 구조로 처리했다.

요청 예시는 다음과 같다.

```bash
curl -i -X POST "http://localhost:8080/api/repair-reports/2/images?imageType=BEFORE" \
  -H "Authorization: Bearer {accessToken}" \
  -F "files=@test-image.JPG"
```

이미지 유형은 enum으로 관리했다.

```text
BEFORE
AFTER
ETC
```

이를 통해 수리 전 사진, 수리 후 사진, 기타 참고 사진을 구분할 수 있게 했다.

---

### 2.2 이미지 메타데이터 조회 API

특정 리포트에 첨부된 이미지 목록을 조회하는 API를 구현했다.

```http
GET /api/repair-reports/{reportId}/images
```

응답에는 실제 파일 자체가 아니라 DB에 저장된 이미지 메타데이터를 반환한다.

응답 예시는 다음과 같다.

```json
[
  {
    "id": 1,
    "reportId": 2,
    "imageUrl": "/uploads/report-images/2/05c3a2da-6d1a-4063-894d-2fec48b40fb2.jpeg",
    "imageType": "BEFORE",
    "uploadedAt": "2026-08-17T14:43:42.765327"
  }
]
```

이를 통해 프론트엔드에서는 리포트 상세 화면에서 첨부 이미지를 목록 형태로 보여줄 수 있다.

---

### 2.3 이미지 삭제 API

첨부된 이미지를 삭제하는 API를 구현했다.

```http
DELETE /api/repair-reports/{reportId}/images/{imageId}
```

삭제 시에는 단순히 DB row만 제거하지 않고, 로컬 파일 시스템에 저장된 실제 이미지 파일도 함께 삭제하도록 처리했다.

삭제 흐름은 다음과 같다.

```text
1. reportId로 리포트 존재 여부 확인
2. 현재 로그인 사용자가 해당 리포트의 소유자인지 검증
3. imageId와 reportId로 이미지 메타데이터 조회
4. 로컬 파일 삭제
5. report_images DB row 삭제
```

이를 통해 DB에는 삭제되었지만 실제 파일은 남아 있는 불일치 상황을 줄였다.

---

## 3. 파일 저장 구조

이미지 파일은 프로젝트 루트의 `uploads` 디렉터리 아래에 저장되도록 구성했다.

```text
uploads/report-images/{reportId}/{uuid}.{extension}
```

예시는 다음과 같다.

```text
uploads/report-images/2/05c3a2da-6d1a-4063-894d-2fec48b40fb2.jpeg
```

파일명은 원본 파일명을 그대로 사용하지 않고 UUID 기반으로 생성했다.

이를 통해 다음 문제를 방지할 수 있다.

```text
- 같은 이름의 파일이 업로드될 때 덮어쓰기 발생
- 원본 파일명에 포함된 특수문자 문제
- 사용자 파일명이 그대로 노출되는 문제
```

DB에는 실제 파일 바이너리를 저장하지 않고, 다음과 같은 메타데이터만 저장했다.

```text
- report_id
- image_url
- image_type
- uploaded_at
```

이 방식은 DB 용량 증가를 줄이고, 추후 S3나 Cloudflare R2 같은 외부 스토리지로 확장하기 쉬운 구조다.

---

## 4. 정적 리소스 접근 설정

업로드된 이미지를 브라우저에서 직접 확인할 수 있도록 정적 리소스 매핑을 추가했다.

브라우저 접근 URL은 다음과 같다.

```text
http://localhost:8080/uploads/report-images/{reportId}/{filename}
```

예시는 다음과 같다.

```text
http://localhost:8080/uploads/report-images/2/05c3a2da-6d1a-4063-894d-2fec48b40fb2.jpeg
```

이를 위해 `/uploads/report-images/**` 요청을 실제 로컬 저장 경로인 `uploads/report-images`와 연결했다.

또한 Spring Security에서 해당 정적 리소스 경로는 접근 가능하도록 허용했다.

```text
/uploads/report-images/**
```

브라우저에서 업로드된 이미지가 정상적으로 표시되는 것까지 확인했다.

---

## 5. 파일 검증

이미지 업로드 시 잘못된 파일이 저장되지 않도록 검증 로직을 추가했다.

검증 항목은 다음과 같다.

| 검증 항목             | 처리     |
|-------------------|--------|
| 빈 파일              | 업로드 차단 |
| 허용되지 않은 확장자       | 업로드 차단 |
| 이미지가 아닌 MIME type | 업로드 차단 |
| 5MB 초과 파일         | 업로드 차단 |
| 잘못된 imageType     | 요청 차단  |

허용한 확장자는 다음과 같다.

```text
jpg
jpeg
png
webp
```

허용한 MIME type은 다음과 같다.

```text
image/jpeg
image/png
image/webp
```

파일 크기는 이미지 1개당 5MB 이하로 제한했다.

```yaml
file:
  max-image-size: 5242880
```

Spring multipart 요청 자체의 제한과 서비스 정책상 파일 크기 제한은 역할이 다르기 때문에, multipart 설정도 별도로 관리했다.

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 30MB
```

이를 통해 Spring이 요청을 받는 기술적 상한은 10MB로 두고, 실제 서비스 정책상 5MB 초과 여부는 애플리케이션 검증 로직에서 처리하도록 구성했다.

---

## 6. 예외 처리 개선

파일 검증 과정에서 잘못된 요청은 서버 내부 오류가 아니라 클라이언트 요청 오류로 처리되어야 한다.

따라서 다음 케이스가 모두 `400 Bad Request`로 응답되도록 예외 처리를 보강했다.

```text
- 이미지가 아닌 파일 업로드
- 허용되지 않은 확장자 업로드
- 5MB 초과 파일 업로드
- 빈 파일 업로드
- 잘못된 imageType 요청
```

특히 `imageType=WRONG`처럼 enum 변환에 실패하는 요청은 Controller 메서드 내부에 진입하기 전에 Spring MVC 바인딩 단계에서 예외가 발생한다.

따라서 `MethodArgumentTypeMismatchException` 계열 예외를 처리하여 다음과 같은 응답이 내려가도록 했다.

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "imageType은 BEFORE, AFTER, ETC 중 하나여야 합니다.",
  "path": "/api/repair-reports/2/images"
}
```

또한 multipart 파일 크기 초과와 관련된 예외도 500이 아니라 400으로 처리되도록 개선했다.

---

## 7. 권한 검증

이미지 업로드와 삭제는 리포트의 소유자인 기사만 수행할 수 있도록 제한했다.

권한 정책은 다음과 같다.

| Role       | 이미지 조회 |      이미지 업로드 |       이미지 삭제 |
|------------|-------:|-------------:|-------------:|
| TECHNICIAN |     가능 |   본인 리포트만 가능 |   본인 리포트만 가능 |
| MANAGER    |     가능 |           불가 |           불가 |
| ADMIN      |     가능 | 정책에 따라 확장 가능 | 정책에 따라 확장 가능 |

특히 TECHNICIAN role을 가지고 있더라도, 다른 기사의 리포트에는 이미지를 업로드하거나 삭제할 수 없도록 소유자 검증을 추가했다.

검증 기준은 다음과 같다.

```text
현재 로그인한 사용자 ID
=
리포트 작성 Technician과 연결된 User ID
```

이 검증을 통해 단순히 role만 확인하는 것이 아니라, 실제 리포트 소유자 여부까지 확인하도록 했다.

---

## 8. 테스트 및 검증 이력

이번 Milestone에서는 curl을 활용해 정상 케이스와 실패 케이스를 모두 직접 검증했다.

### 8.1 정상 케이스

다음 항목이 정상 동작함을 확인했다.

```text
이미지 업로드 201 Created
이미지 메타데이터 조회 200 OK
이미지 삭제 204 No Content
로컬 파일 시스템에 실제 파일 저장
삭제 시 실제 파일 삭제
삭제 시 DB row 삭제
브라우저에서 이미지 URL 접근 성공
```

업로드 후 실제 파일 저장 여부는 다음 명령어로 확인했다.

```bash
find uploads -type f
```

예시 결과:

```text
uploads/report-images/2/05c3a2da-6d1a-4063-894d-2fec48b40fb2.jpeg
```

---

### 8.2 권한 실패 케이스

다음 권한 테스트를 수행했다.

```text
다른 기사 토큰으로 김기사 리포트 이미지 업로드 시 403
다른 기사 토큰으로 김기사 리포트 이미지 삭제 시 403
MANAGER 토큰으로 이미지 조회 시 200
MANAGER 토큰으로 이미지 업로드 시 403
MANAGER 토큰으로 이미지 삭제 시 403
```

이를 통해 이미지 API에도 기존 Milestone 2에서 구현한 인증 사용자 기반 권한 구조가 잘 적용되는 것을 확인했다.

---

### 8.3 파일 검증 실패 케이스

다음 잘못된 요청들이 모두 400으로 처리되는 것을 확인했다.

```text
이미지가 아닌 test.txt 업로드
허용되지 않는 확장자 업로드
5MB 초과 파일 업로드
빈 파일 업로드
imageType=WRONG 요청
```

초기에는 5MB 초과 파일과 잘못된 imageType 요청에서 500 에러가 발생했다.

해당 문제를 통해 Controller 내부 검증뿐 아니라, Spring MVC의 multipart 파싱 단계와 enum 변환 단계에서 발생하는 예외도 별도로 처리해야 한다는 점을 확인했다.

이후 GlobalExceptionHandler를 보강하여 두 케이스 모두 400으로 내려가도록 수정했다.

---

## 9. 트러블슈팅 기록

### 9.1 curl 파일 경로 문제

이미지 업로드 테스트 중 다음 에러가 발생했다.

```text
curl: (26) Failed to open/read local data from file/application
```

원인은 서버 문제가 아니라, curl 명령어를 실행한 현재 디렉터리에 업로드하려는 파일이 없었기 때문이다.

해결 방법은 다음과 같다.

```bash
ls -l test-image.JPG
```

로 파일 존재 여부를 확인하고, 파일이 다른 위치에 있을 경우 절대 경로를 사용하거나 프로젝트 루트로 복사했다.

```bash
cp ~/Downloads/test-image.JPG .
```

---

### 9.2 curl 줄바꿈 문제

curl 명령어를 여러 줄로 입력할 때 백슬래시 뒤에 공백이나 다른 인자가 붙어 에러가 발생했다.

잘못된 예시는 다음과 같다.

```bash
curl -i -X POST "http://localhost:8080/api/repair-reports/2/images?imageType=WRONG" \ -H "Authorization: Bearer $TECH_TOKEN" \ -F "files=@test-image.JPG"
```

이 경우 curl이 `-H`, `-F`, 파일명을 잘못된 URL 또는 호스트로 해석할 수 있다.

올바른 방식은 다음과 같다.

```bash
curl -i -X POST "http://localhost:8080/api/repair-reports/2/images?imageType=WRONG" \
  -H "Authorization: Bearer $TECH_TOKEN" \
  -F "files=@test-image.JPG"
```

헷갈릴 때는 한 줄로 입력하는 방식도 사용했다.

---

### 9.3 JWT 토큰 문제

터미널에서 다음 명령어를 실행했을 때 아무것도 나오지 않는 경우가 있었다.

```bash
echo $TECH_TOKEN
```

이는 토큰이 만료된 것이 아니라, 현재 터미널 세션에 환경변수가 등록되어 있지 않은 상태였다.

해결 방법은 로그인 API를 다시 호출하여 accessToken을 발급받고, 이를 변수에 저장하는 방식이었다.

```bash
TECH_TOKEN="발급받은_accessToken"
```

이후 `/api/auth/me`를 호출하여 토큰이 정상인지 확인했다.

```bash
curl -i "http://localhost:8080/api/auth/me" \
  -H "Authorization: Bearer $TECH_TOKEN"
```

---

## 10. 구현 결과

Milestone 3에서 계획했던 로컬 이미지 업로드 기능은 모두 구현 및 검증 완료했다.

최종 달성 항목은 다음과 같다.

```text
이미지 메타데이터 조회 API 구현 완료
로컬 업로드 API 구현 완료
이미지 파일 검증 구현 완료
로컬 파일 시스템 저장 구현 완료
이미지 삭제 시 실제 파일 및 DB row 삭제 완료
이미지 업로드/삭제 권한 검증 완료
업로드 이미지 URL 접근 완료
multipart 및 enum 관련 예외 처리 개선 완료
```

이번 단계까지 완료함으로써, 현장 기사가 유지보수 리포트에 수리 전후 사진을 첨부하고, 관리팀이 해당 이미지를 조회할 수 있는 기본 파일 관리 흐름이 완성되었다.

---

## 11. 남은 확장 과제

현재 구현은 로컬 파일 저장 기반이므로, 운영 환경에서는 다음 확장이 필요하다.

```text
- AWS S3 또는 Cloudflare R2 기반 외부 스토리지 연동
- 업로드 실패 시 파일과 DB 저장 간 롤백 처리 강화
- 이미지 개수 제한 정책 추가
- 이미지 타입별 최대 업로드 개수 제한
- 실제 이미지 파일 내용 검증 강화
- 테스트 코드 작성
- 운영 환경에서 정적 리소스 공개 범위 재검토
```