# AWS CloudFront 완벽 가이드

## 1. CloudFront란?

**Amazon CloudFront**는 AWS에서 제공하는 글로벌 콘텐츠 전송 네트워크(CDN) 서비스입니다.

### 주요 특징
- **전세계 엣지 로케이션**: 200+ 개 도시에 분산된 데이터 센터
- **빠른 콘텐츠 전송**: 사용자와 가장 가까운 엣지에서 콘텐츠 제공
- **자동 확장**: 트래픽 증가에 따른 자동 스케일링
- **보안 강화**: DDoS 보호, SSL/TLS 암호화 지원

## 2. CloudFront의 장점

### 성능 향상
- **지연 시간 감소**: 엣지 로케이션을 통한 빠른 응답
- **캐싱**: 정적 콘텐츠의 효율적인 캐싱
- **압축**: 자동 Gzip 압축으로 전송 속도 향상

### 비용 절감
- **오리진 서버 부하 감소**: 캐싱을 통한 서버 요청 감소
- **데이터 전송 비용**: S3 직접 접근 대비 저렴한 전송 비용
- **사용량 기반 과금**: 실제 사용한 만큼만 비용 지불

### 보안 및 가용성
- **DDoS 보호**: AWS Shield와 통합된 보안
- **SSL/TLS**: 무료 SSL 인증서 제공
- **접근 제어**: 서명된 URL/쿠키를 통한 접근 제한
- **WAF 통합**: 웹 애플리케이션 방화벽 연동

## 3. CloudFront 작동 원리

```
[사용자] → [가장 가까운 엣지 로케이션] → [오리진 서버 (S3/EC2/ALB)]
```

1. **사용자 요청**: 콘텐츠 요청
2. **엣지 로케이션 확인**: 캐시된 콘텐츠 존재 여부 확인
3. **캐시 히트**: 캐시된 콘텐츠 즉시 반환
4. **캐시 미스**: 오리진 서버에서 콘텐츠 가져와서 캐시 후 반환

## 4. FALCON 프로젝트 적용 시나리오

### 현재 상황
- **이미지 소스**: imgur URL 사용 중
- **S3 버킷**: `falconcartons-bucket` 설정 완료
- **CloudFront 배포**: `E281LC8HEYUK83` 생성됨

### 적용 목표
1. **이미지 호스팅**: imgur → S3 + CloudFront 이전
2. **글로벌 접근**: 전세계 사용자 대상 빠른 이미지 로딩
3. **비용 최적화**: 효율적인 이미지 전송

## 5. CloudFront 설정 단계별 가이드

### Step 1: S3 버킷 준비

#### 1.1 버킷 정책 설정 (CloudFront 전용)
```json
{
    "Version": "2012-10-17",
    "Id": "PolicyForCloudFrontOnly",
    "Statement": [
        {
            "Sid": "AllowCloudFrontServicePrincipal",
            "Effect": "Allow",
            "Principal": {
                "Service": "cloudfront.amazonaws.com"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::falconcartons-bucket/*",
            "Condition": {
                "ArnLike": {
                    "AWS:SourceArn": "arn:aws:cloudfront::516723930299:distribution/E281LC8HEYUK83"
                }
            }
        }
    ]
}
```

#### 1.2 CORS 설정
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "HEAD"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": []
    }
]
```

### Step 2: CloudFront 배포 생성

#### 2.1 CLI로 CloudFront 배포 생성
```bash
# CloudFront 배포 설정 파일 생성
cat > cloudfront-config.json << 'EOF'
{
    "CallerReference": "falcon-shop-$(date +%s)",
    "Comment": "Falcon Shop S3 Images Distribution",
    "DefaultRootObject": "",
    "Origins": {
        "Quantity": 1,
        "Items": [
            {
                "Id": "S3-falconcartons-bucket",
                "DomainName": "falconcartons-bucket.s3.eu-west-2.amazonaws.com",
                "S3OriginConfig": {
                    "OriginAccessIdentity": ""
                }
            }
        ]
    },
    "DefaultCacheBehavior": {
        "TargetOriginId": "S3-falconcartons-bucket",
        "ViewerProtocolPolicy": "redirect-to-https",
        "AllowedMethods": {
            "Quantity": 2,
            "Items": ["GET", "HEAD"],
            "CachedMethods": {
                "Quantity": 2,
                "Items": ["GET", "HEAD"]
            }
        },
        "TrustedSigners": {
            "Enabled": false,
            "Quantity": 0
        },
        "ForwardedValues": {
            "QueryString": false,
            "Cookies": {
                "Forward": "none"
            }
        },
        "MinTTL": 0,
        "DefaultTTL": 86400,
        "MaxTTL": 31536000
    },
    "Enabled": true,
    "PriceClass": "PriceClass_All"
}
EOF

# CloudFront 배포 생성
aws cloudfront create-distribution --distribution-config file://cloudfront-config.json
```

#### 2.2 배포 상태 확인
```bash
# 배포 목록 확인
aws cloudfront list-distributions

# 특정 배포 상태 확인
aws cloudfront get-distribution --id E281LC8HEYUK83
```

### Step 3: 도메인 확인 및 테스트

#### 3.1 CloudFront 도메인 확인
```bash
# 배포 정보에서 도메인 추출
aws cloudfront get-distribution --id E281LC8HEYUK83 \
    --query 'Distribution.DomainName' --output text
```

#### 3.2 테스트 이미지 업로드 및 확인
```bash
# S3에 테스트 이미지 업로드
aws s3 cp test-image.jpg s3://falconcartons-bucket/images/test-image.jpg

# CloudFront를 통한 접근 테스트
curl -I https://d1234567890abc.cloudfront.net/images/test-image.jpg
```

## 6. 애플리케이션 코드 수정

### 6.1 application.properties 설정 추가
```properties
# CloudFront 설정
cloudfront.domain=https://d1234567890abc.cloudfront.net
cloudfront.enabled=true

# S3 설정 (기존)
aws.s3.url=https://falconcartons-bucket.s3.eu-west-2.amazonaws.com
aws.s3.bucket=falconcartons-bucket
```

### 6.2 이미지 URL 생성 서비스 수정

#### 기존 코드:
```java
// 직접 S3 URL 사용
String imageUrl = "https://falconcartons-bucket.s3.eu-west-2.amazonaws.com/images/product.jpg";
```

#### 수정된 코드:
```java
@Service
public class ImageUrlService {
    
    @Value("${cloudfront.domain}")
    private String cloudfrontDomain;
    
    @Value("${cloudfront.enabled:false}")
    private boolean cloudfrontEnabled;
    
    @Value("${aws.s3.url}")
    private String s3Url;
    
    public String getImageUrl(String imagePath) {
        if (cloudfrontEnabled) {
            return cloudfrontDomain + "/" + imagePath;
        } else {
            return s3Url + "/" + imagePath;
        }
    }
}
```

### 6.3 템플릿 수정
```html
<!-- 기존 imgur URL -->
<img th:src="'https://i.imgur.com/example.jpg'" alt="Product Image">

<!-- CloudFront URL로 변경 -->
<img th:src="${imageUrlService.getImageUrl('products/example.jpg')}" alt="Product Image">
```

## 7. 성능 최적화 설정

### 7.1 캐시 정책 설정
```bash
# 캐시 정책 생성 (이미지용)
aws cloudfront create-cache-policy \
    --cache-policy-config '{
        "Name": "FalconImageCachePolicy",
        "Comment": "Cache policy for product images",
        "DefaultTTL": 86400,
        "MaxTTL": 31536000,
        "MinTTL": 0,
        "ParametersInCacheKeyAndForwardedToOrigin": {
            "EnableAcceptEncodingGzip": true,
            "EnableAcceptEncodingBrotli": true,
            "QueryStringsConfig": {
                "QueryStringBehavior": "none"
            },
            "HeadersConfig": {
                "HeaderBehavior": "none"
            },
            "CookiesConfig": {
                "CookieBehavior": "none"
            }
        }
    }'
```

### 7.2 압축 설정
```bash
# Gzip 압축 활성화
aws cloudfront update-distribution \
    --id E281LC8HEYUK83 \
    --distribution-config '{
        "DefaultCacheBehavior": {
            "Compress": true
        }
    }'
```

## 8. 보안 강화

### 8.1 Origin Access Control (OAC) 설정
```bash
# OAC 생성
aws cloudfront create-origin-access-control \
    --origin-access-control-config '{
        "Name": "falcon-s3-oac",
        "Description": "OAC for Falcon S3 bucket",
        "OriginAccessControlOriginType": "s3",
        "SigningBehavior": "always",
        "SigningProtocol": "sigv4"
    }'
```

### 8.2 S3 버킷 정책 업데이트 (OAC 사용)
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "Service": "cloudfront.amazonaws.com"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::falconcartons-bucket/*",
            "Condition": {
                "StringEquals": {
                    "AWS:SourceArn": "arn:aws:cloudfront::516723930299:distribution/E281LC8HEYUK83"
                }
            }
        }
    ]
}
```

## 9. 모니터링 및 분석

### 9.1 CloudWatch 메트릭 확인
```bash
# CloudFront 메트릭 조회
aws cloudwatch get-metric-statistics \
    --namespace AWS/CloudFront \
    --metric-name Requests \
    --dimensions Name=DistributionId,Value=E281LC8HEYUK83 \
    --start-time 2024-01-01T00:00:00Z \
    --end-time 2024-01-02T00:00:00Z \
    --period 3600 \
    --statistics Sum
```

### 9.2 실시간 로그 설정
```bash
# 실시간 로그 설정
aws cloudfront create-realtime-log-config \
    --name falcon-realtime-logs \
    --end-points '{
        "StreamType": "Kinesis",
        "KinesisStreamConfig": {
            "RoleArn": "arn:aws:iam::516723930299:role/CloudFrontRealtimeLogRole",
            "StreamArn": "arn:aws:kinesis:us-east-1:516723930299:stream/falcon-logs"
        }
    }' \
    --fields timestamp request-id client-ip method uri status
```

## 10. 비용 최적화

### 10.1 Price Class 설정
```bash
# 가격 등급 변경 (일부 지역만 사용)
aws cloudfront update-distribution \
    --id E281LC8HEYUK83 \
    --distribution-config '{
        "PriceClass": "PriceClass_100"
    }'
```

**Price Class 옵션:**
- `PriceClass_All`: 모든 엣지 로케이션 (최고 성능, 최고 비용)
- `PriceClass_200`: 북미, 유럽, 아시아 주요 지역
- `PriceClass_100`: 북미, 유럽 (최저 비용, 제한된 성능)

### 10.2 캐시 히트율 최적화
```javascript
// 이미지 파일명에 버전 또는 해시 포함
const imageUrl = `/products/image_v1.jpg`;
const imageUrlWithHash = `/products/image_${fileHash}.jpg`;
```

## 11. 문제 해결 (Troubleshooting)

### 11.1 일반적인 문제들

#### 403 Forbidden 오류
```bash
# S3 버킷 정책 확인
aws s3api get-bucket-policy --bucket falconcartons-bucket

# CloudFront OAC 설정 확인
aws cloudfront get-origin-access-control --id YOUR_OAC_ID
```

#### 캐시 무효화 (Cache Invalidation)
```bash
# 특정 파일 캐시 무효화
aws cloudfront create-invalidation \
    --distribution-id E281LC8HEYUK83 \
    --paths "/images/product.jpg"

# 전체 캐시 무효화 (비용 주의)
aws cloudfront create-invalidation \
    --distribution-id E281LC8HEYUK83 \
    --paths "/*"
```

#### 배포 업데이트
```bash
# 배포 설정 변경 후 재배포
aws cloudfront update-distribution \
    --id E281LC8HEYUK83 \
    --distribution-config file://updated-config.json
```

## 12. 실제 적용 체크리스트

### 사전 준비
- [ ] S3 버킷 설정 완료
- [ ] 이미지 파일 S3 업로드
- [ ] CloudFront 배포 생성
- [ ] 도메인 확인

### 애플리케이션 수정
- [ ] `application.properties` CloudFront 설정 추가
- [ ] `ImageUrlService` 생성
- [ ] 템플릿 URL 변경
- [ ] 테스트 환경에서 확인

### 성능 최적화
- [ ] 캐시 정책 설정
- [ ] 압축 활성화
- [ ] Price Class 최적화
- [ ] 모니터링 설정

### 보안 설정
- [ ] OAC 설정
- [ ] S3 버킷 정책 업데이트
- [ ] HTTPS 강제 설정
- [ ] 접근 로그 활성화

## 13. 예상 효과

### 성능 개선
- **로딩 시간**: 30-50% 단축 예상
- **글로벌 접근**: 전세계 동일한 속도
- **서버 부하**: 70-80% 감소

### 비용 절감
- **데이터 전송**: S3 직접 접근 대비 20-30% 절약
- **서버 비용**: EC2 인스턴스 크기 축소 가능

### 사용자 경험
- **이미지 로딩**: 즉시 로딩 체감
- **지역 제한**: 해결
- **안정성**: 99.9% 가용성

## 결론

CloudFront 도입으로 FALCON 쇼핑몰의 이미지 서비스 품질을 크게 향상시킬 수 있습니다. 특히 imgur 의존성을 제거하고 안정적인 AWS 인프라를 활용함으로써 글로벌 서비스의 기반을 마련할 수 있습니다.