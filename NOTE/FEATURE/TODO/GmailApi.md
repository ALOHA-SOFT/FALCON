구글 메일(Gmail) 연동용 **App ID**를 생성하려면
Google Cloud Console에서 **OAuth 2.0 클라이언트 ID**를 발급해야 합니다.

아래 절차를 따르면 됩니다.

---

## 📌 1. Google Cloud Console 접속

* [Google Cloud Console](https://console.cloud.google.com/) 로그인
* **새 프로젝트** 생성 또는 기존 프로젝트 선택
  → 예: `gmail-integration`

---

## 📌 2. Gmail API 활성화

1. 왼쪽 메뉴에서 **API 및 서비스 → 라이브러리** 이동
2. 검색창에 **Gmail API** 입력
3. **사용** 버튼 클릭

---

## 📌 3. OAuth 동의 화면 설정

1. **API 및 서비스 → OAuth 동의 화면** 이동
2. 사용자 유형 선택:

   * **외부**: G Suite 외부 사용자까지 허용 (일반 서비스)
   * **내부**: 조직 내부만
3. 앱 정보 입력

   * 앱 이름: 예) `My Gmail App`
   * 지원 이메일
   * 앱 로고(선택)
4. **범위(Scope)** 설정
   예: Gmail 읽기/쓰기 권한

   ```
   https://mail.google.com/  # 전체 Gmail 액세스
   https://www.googleapis.com/auth/gmail.readonly # 읽기 전용
   ```
5. 저장 후 진행

---

## 📌 4. OAuth 클라이언트 ID 생성

1. **API 및 서비스 → 사용자 인증 정보** 이동
2. **사용자 인증 정보 만들기 → OAuth 클라이언트 ID**
3. 앱 유형 선택:

   * **웹 애플리케이션**: 서버 연동
   * **데스크톱 앱**: 로컬 앱/스크립트
4. 승인된 리디렉션 URI 입력 (OAuth 인증 후 토큰 받을 URL)
   예:

   ```
   http://localhost:8080/oauth2callback
   https://yourdomain.com/oauth2callback
   ```
5. **만들기** 클릭

---

## 📌 5. App ID / Client Secret 확인

* 생성 후 **클라이언트 ID**와 \*\*클라이언트 보안 비밀번호(Client Secret)\*\*가 발급됨
* 이 값이 Gmail API 연동 시 필요한 **App ID**입니다.

---

## 📌 6. 테스트

OAuth 2.0 Playground 또는 Postman에서 인증 흐름 테스트 가능
예:

```bash
GET https://accounts.google.com/o/oauth2/v2/auth
?client_id=YOUR_CLIENT_ID
&redirect_uri=http://localhost:8080/oauth2callback
&response_type=code
&scope=https://mail.google.com/
&access_type=offline
```

---

원하시면 제가 **Spring Boot Gmail API 연동 예제**를 만들어서
방금 만든 App ID / Secret 넣어서 메일 읽기 & 보내기까지 동작하게 해드릴 수 있습니다.
그렇게 하면 바로 실행 가능하게 만들 수 있습니다.
