# 이미지 배경 투명화 프로그램 (모든 형식 지원)

이 프로그램은 `F:\DEV\FALCON\FALCON\NOTE\FALCON` 폴더에 있는 다양한 형식의 이미지들의 배경을 투명하게 만들어 PNG로 저장합니다.

## 지원하는 이미지 형식

- PNG, JPG, JPEG
- BMP, GIF, WEBP
- TIFF, TIF

**모든 이미지가 투명 배경을 가진 PNG 파일로 저장됩니다.**

## 설치 및 실행

### 방법 1: 배치 파일 사용 (추천)
1. `run.bat` 파일을 더블클릭하여 실행
2. 자동으로 필요한 라이브러리가 설치되고 프로그램이 실행됩니다.

### 방법 2: 수동 실행
1. 필요한 라이브러리 설치:
   ```
   pip install -r requirements.txt
   ```

2. 프로그램 실행:
   ```
   python re_bg.py
   ```

## 기능

### 1. 자동 배경색 감지 (추천)
- 이미지의 모서리 픽셀을 분석하여 배경색을 자동으로 감지
- 감지된 배경색과 유사한 색상을 투명하게 처리
- 다양한 배경색에 대응 가능

### 2. 흰색 배경 제거
- 흰색 또는 흰색에 가까운 배경만 투명하게 처리
- 흰색 배경이 확실한 경우 사용

### 3. GIF 애니메이션 유지 (NEW!)
- GIF 애니메이션의 모든 프레임에서 배경을 투명하게 처리
- 애니메이션을 유지하면서 WebP 형식으로 저장
- 더 작은 파일 크기와 더 나은 압축률

## 출력

- 처리된 이미지는 `NOTE/AUTO/output/` 폴더에 저장됩니다.
- 파일명 앞에 `transparent_` 접두사가 붙습니다.
- **일반 이미지**: PNG 형식으로 변환
- **애니메이션 GIF** (옵션 3 선택시): WebP 형식으로 변환
- 예: 
  - `logo.jpg` → `transparent_logo.png`
  - `animation.gif` → `transparent_animation.webp` (애니메이션 유지)

## 주의사항

1. 원본 파일은 수정되지 않고 새로운 파일로 저장됩니다.
2. 복잡한 배경이나 그라데이션 배경의 경우 완벽하게 제거되지 않을 수 있습니다.
3. 처리 시간은 이미지 크기와 개수에 따라 달라집니다.

## 오류 해결

### PIL/Pillow 설치 오류
```
pip install --upgrade pip
pip install Pillow
```

### numpy 설치 오류
```
pip install numpy
```

### 경로 오류
- `F:\DEV\FALCON\FALCON\NOTE\FALCON` 폴더가 존재하는지 확인
- PNG 파일이 해당 폴더에 있는지 확인
