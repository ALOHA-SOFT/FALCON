# AWS S3 "Content not viewable in your region" 해결 가이드

## 문제 상황
- S3 버킷에 업로드된 이미지나 파일에 접근 시 "Content not viewable in your region" 오류 발생
- 특정 지역에서 S3 콘텐츠에 접근할 수 없는 상황

## 원인 분석
1. **S3 버킷 리전 제한**: 버킷이 특정 리전에만 접근 허용하도록 설정된 경우
2. **버킷 정책 제한**: 지역별 접근 제한 정책이 적용된 경우
3. **CloudFront 필요**: 글로벌 콘텐츠 전송을 위한 CDN 미설정

## 해결 방법

### 1. S3 버킷 정책 확인 및 수정

#### 현재 버킷 정책 확인
```bash
aws s3api get-bucket-policy --bucket your-bucket-name
```

#### 전세계 접근 허용 정책 적용
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::your-bucket-name/*"
        }
    ]
}
```

### 2. S3 버킷 CORS 설정

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

### 3. CloudFront 배포 설정 (권장)

#### CloudFront가 필요한 이유:
- **글로벌 접근**: 전세계 어디서나 빠른 접근 가능
- **지역 제한 우회**: 리전별 제한 문제 해결
- **성능 향상**: 엣지 로케이션을 통한 빠른 콘텐츠 전송
- **비용 절감**: S3 직접 접근 대비 데이터 전송 비용 절약

#### CloudFront 배포 생성

```bash
# CloudFront 배포 생성
aws cloudfront create-distribution --distribution-config file://cloudfront-config.json
```

#### cloudfront-config.json 예시:
```json
{
    "CallerReference": "falcon-shop-images-$(date +%s)",
    "Comment": "Falcon Shop S3 Images Distribution",
    "DefaultRootObject": "",
    "Origins": {
        "Quantity": 1,
        "Items": [
            {
                "Id": "S3-falcon-shop-images",
                "DomainName": "your-bucket-name.s3.amazonaws.com",
                "S3OriginConfig": {
                    "OriginAccessIdentity": ""
                }
            }
        ]
    },
    "DefaultCacheBehavior": {
        "TargetOriginId": "S3-falcon-shop-images",
        "ViewerProtocolPolicy": "redirect-to-https",
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
        "MinTTL": 0
    },
    "Enabled": true
}
```

### 4. 단계별 해결 과정

#### Step 1: S3 버킷 퍼블릭 접근 확인
```bash
# 퍼블릭 액세스 차단 해제
aws s3api put-public-access-block \
    --bucket your-bucket-name \
    --public-access-block-configuration \
    BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false
```

#### Step 2: 버킷 정책 적용
```bash
aws s3api put-bucket-policy \
    --bucket your-bucket-name \
    --policy file://bucket-policy.json
```

#### Step 3: CloudFront 배포 생성 (권장)
```bash
# CloudFront 배포 생성
aws cloudfront create-distribution --distribution-config file://cloudfront-config.json

# 배포 상태 확인
aws cloudfront list-distributions
```

### 5. 애플리케이션 코드 수정

#### 기존 S3 직접 접근:
```java
// 문제가 있는 방식
String imageUrl = "https://your-bucket-name.s3.amazonaws.com/images/product.jpg";
```

#### CloudFront 도메인 사용:
```java
// 권장하는 방식
String imageUrl = "https://d1234567890abc.cloudfront.net/images/product.jpg";
```

### 6. 설정 확인 및 테스트

#### 다양한 지역에서 접근 테스트:
```bash
# 다른 지역에서 접근 테스트
curl -I https://your-cloudfront-domain.net/test-image.jpg

# 응답 헤더 확인
# 200 OK가 나와야 정상
```

## 권장 해결책: CloudFront 사용

### CloudFront 사용의 장점:
1. **지역 제한 해결**: 전세계 어디서나 접근 가능
2. **성능 향상**: 엣지 로케이션을 통한 빠른 로딩
3. **비용 효율**: S3 직접 접근 대비 비용 절약
4. **보안 강화**: Origin Access Identity (OAI) 사용 가능
5. **캐싱**: 자동 캐싱으로 응답 속도 향상

### 설정 순서:
1. S3 버킷 생성 및 콘텐츠 업로드
2. CloudFront 배포 생성
3. Origin을 S3 버킷으로 설정
4. 애플리케이션에서 CloudFront 도메인 사용

## 긴급 해결 방법

지금 당장 해결이 필요한 경우:

```bash
# 1. 버킷 퍼블릭 접근 허용
aws s3api put-public-access-block --bucket your-bucket-name \
    --public-access-block-configuration BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false

# 2. 퍼블릭 읽기 정책 적용
aws s3api put-bucket-policy --bucket your-bucket-name --policy '{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::your-bucket-name/*"
        }
    ]
}'
```

## 결론
**CloudFront 사용을 강력히 권장**합니다. 지역 제한 문제를 근본적으로 해결하고, 성능과 비용 면에서도 이점이 많습니다.
