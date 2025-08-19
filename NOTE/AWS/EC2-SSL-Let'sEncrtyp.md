# AWS Linux EC2에서 Apache/Nginx 및 Let's Encrypt로 SSL 인증서 세팅하기

## 1. EC2 인스턴스 준비
- Amazon Linux 2/2023 권장
- SSH로 접속

## 2-A. Apache 설치 (선택)
```bash
sudo yum update -y
sudo yum install httpd -y
sudo systemctl start httpd
sudo systemctl enable httpd
```

## 2-B. Nginx 설치 (선택)
```bash
sudo yum update -y
sudo dnf install nginx -y

# 포트 충돌 확인 (80번 포트 사용 중인 서비스 확인)
sudo netstat -tlnp | grep :80
# 또는
sudo ss -tlnp | grep :80

# Apache가 실행 중이면 중지
sudo systemctl stop httpd
sudo systemctl disable httpd

# Nginx 설정 테스트
sudo nginx -t

# Nginx 시작
sudo systemctl start nginx
sudo systemctl enable nginx

# 문제 발생 시 로그 확인
sudo systemctl status nginx.service
sudo journalctl -xeu nginx.service
```

## 3. 방화벽 설정 (AWS 보안 그룹)
- AWS 콘솔에서 보안 그룹 편집
- 80(HTTP), 443(HTTPS) 포트 오픈

## 4. 도메인 연결
- 도메인이 EC2 퍼블릭 IP를 가리키도록 DNS 설정

## 5. Certbot 설치 (Let's Encrypt)
### Apache용:
```bash
sudo yum install certbot python3-certbot-apache -y
```

### Nginx용 ✅
```bash
sudo yum install certbot python3-certbot-nginx -y
```

## 6-A. SSL 인증서 발급 (Apache)
```bash
sudo certbot --apache -d falconcartons.com -d www.falconcartons.com
```

## 6-B. SSL 인증서 발급 (Nginx) ✅
```bash
sudo certbot --nginx -d falconcartons.com -d www.falconcartons.com
```
- 안내에 따라 이메일 입력 및 동의

이메일 입력 오류 시 아래 명령어 사용:
```bash
sudo certbot --nginx -d falconcartons.com -d www.falconcartons.com --email info@falconcartons.com --agree-tos --non-interactive
```

### server_name 설정 오류 시:
nginx 설정 파일에 server_name 추가:
```bash
sudo nano /etc/nginx/conf.d/falconcartons.conf
```

server 블록에 추가:
```nginx
server {
    listen 80;
    server_name falconcartons.com www.falconcartons.com;
    
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
    }
}
```

설정 후 nginx 재시작:
```bash
sudo nginx -t
sudo systemctl reload nginx
```

### SSL 인증서 발급 실패 시 체크사항:
1. nginx가 정상 실행 중인지 확인: `sudo systemctl status nginx`
2. 80번 포트 접근 가능한지 확인: `sudo netstat -tlnp | grep :80`
3. AWS 보안 그룹에서 80번 포트 오픈 확인
4. 도메인이 EC2 IP를 정확히 가리키는지 확인: `nslookup falconcartons.com`
5. 웹브라우저에서 http://falconcartons.com 접근 테스트

### 80→8080 포트 포워딩 설정한 경우:
SSL 인증서 발급 전에 포트 포워딩 규칙을 임시 제거:
```bash
sudo iptables -t nat -D PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
```
인증서 발급 후 다시 설정:
```bash
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
```

## 7. 인증서 갱신 테스트
```bash
sudo certbot renew --dry-run
```

## 8. Nginx SSL 설정 확인
- `/etc/nginx/sites-available/default` 또는 설정 파일에서 `listen 443 ssl;` 확인

## 9. 서비스 재시작
### Apache:
```bash
sudo systemctl reload httpd
```

### Nginx:
```bash
sudo systemctl reload nginx
```

## 참고
- 인증서는 90일마다 자동 갱신됨 (Certbot이 cron 또는 systemd 타이머로 관리)
- AWS 보안그룹에서 80/443 포트 오픈 필요
- Amazon Linux에서는 `yum` 사용

## 완료 후 확인사항
1. HTTPS 접속 테스트: https://falconcartons.com, https://www.falconcartons.com
2. 포트 포워딩 복원(필요시): `sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080`
3. 자동 갱신 테스트: `sudo certbot renew --dry-run`
4. nginx 상태 확인: `sudo systemctl status nginx`

## Nginx에서 80→8080 포트 포워딩 설정
SSL 적용 후 nginx를 통한 포트 포워딩:

1. nginx 설정 파일 수정:
```bash
sudo nano /etc/nginx/conf.d/falconcartons.conf
```

2. server 블록에 proxy_pass 추가:
```nginx
server {
    listen 80;
    server_name falconcartons.com www.falconcartons.com;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 443 ssl;
    server_name falconcartons.com www.falconcartons.com;
    
    # SSL 설정은 certbot이 자동 추가한 것 유지
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. 설정 적용:
```bash
sudo nginx -t
sudo systemctl reload nginx
```
