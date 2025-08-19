# AWS Linux에서 JDK 17 설치 가이드

## 1. 패키지 업데이트
```
sudo yum update -y
```

## 2. OpenJDK 17 설치
```
sudo yum install -y java-17-amazon-corretto-devel
```

## 3. 설치 확인
```
java -version
```

## 4. JAVA_HOME 환경 변수 설정
```
echo "export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which java)))) )" | sudo tee /etc/profile.d/jdk17.sh
source /etc/profile.d/jdk17.sh
```

## 5. 영구 적용 (재부팅 후에도 적용)
- `/etc/profile.d/jdk17.sh` 파일이 자동 적용됨

## 6. 기타
- Amazon Linux 2 기준
- 다른 리눅스 배포판은 패키지명이 다를 수 있음
- Oracle JDK가 필요하다면 직접 다운로드 및 설치 필요

---
문의사항 있으면 추가로 요청해 주세요.
