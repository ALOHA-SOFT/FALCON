# Python 설치 가이드

현재 시스템에 Python이 설치되어 있지 않습니다. 다음 방법 중 하나를 선택하여 Python을 설치하세요.

## 방법 1: Microsoft Store에서 설치 (추천)

1. Windows 시작 메뉴에서 "Microsoft Store" 검색
2. Microsoft Store 열기
3. "Python"으로 검색
4. "Python 3.11" 또는 최신 버전 선택하여 설치
5. 설치 완료 후 명령 프롬프트에서 `python --version` 확인

## 방법 2: python.org에서 직접 다운로드

1. https://www.python.org/downloads/ 방문
2. "Download Python" 버튼 클릭 (최신 버전)
3. 다운로드된 설치 파일 실행
4. **중요**: "Add Python to PATH" 체크박스 반드시 선택
5. "Install Now" 클릭

## 방법 3: Chocolatey 사용 (고급 사용자)

PowerShell을 관리자 권한으로 열고:

```powershell
# Chocolatey 설치 (없는 경우)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Python 설치
choco install python
```

## 설치 확인

설치 완료 후 새 명령 프롬프트 또는 PowerShell을 열고:

```cmd
python --version
pip --version
```

둘 다 버전 정보가 표시되면 설치 성공입니다.

## 다음 단계

Python 설치 후:

1. `F:\DEV\FALCON\FALCON\NOTE\AUTO` 폴더로 이동
2. `run.bat` 실행 또는 다음 명령어 실행:
   ```cmd
   pip install -r requirements.txt
   python re_bg.py
   ```
