// # `$ajax` 함수 문서

## 설명

`$ajax` 함수는 jQuery의 `$.ajax`를 래핑하여 CSRF 토큰 자동 처리, FormData 지원, 에러 핸들링을 제공합니다.

---
```js
const $form = document.getElementById('form');
const formData = new FormData($form);
const url = ``
$ajax({
    url: url,
    type: 'POST',
    data: formData,
    processData: false, 
    contentType: false
}).then(response => {
    console.log(response);
});
```
---

## 사용법

```js
// GET 요청 예시
$ajax({
    url: '/api/items',
    type: 'GET',
    data: { id: 1 }
}).then(response => {
    console.log(response);
});

// POST 요청 예시
$ajax({
    url: '/api/items',
    type: 'POST',
    data: { name: 'item1', price: 1000 }
}).then(response => {
    console.log(response);
});

// FormData 전송 예시
const formData = new FormData();
formData.append('file', fileInput.files[0]);
$ajax({
    url: '/api/upload',
    type: 'POST',
    data: formData
}).then(response => {
    console.log(response);
});
```

---

## 파라미터

| 이름   | 타입           | 설명                           |
|--------|----------------|--------------------------------|
| url    | string         | 요청할 URL                     |
| type   | string         | HTTP 메서드 (GET, POST 등)     |
| data   | object/FormData| 전송할 데이터                  |

---

## 반환값

- **성공:** 서버 응답 데이터
- **실패:** 문자열 `"FAIL"`

---

## 참고

- CSRF 토큰은 `<meta name="_csrf">`, `<meta name="_csrf_header">`에서 자동 추출
- FormData 사용 시 `contentType`, `processData` 자동 처리
