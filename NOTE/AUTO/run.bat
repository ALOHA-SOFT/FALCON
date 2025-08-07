@echo off
chcp 65001 > nul
cd /d "%~dp0"

echo 이미지 배경 투명화 프로그램 (모든 형식 지원)
echo ===================================================
echo.

REM Python 설치 확인
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python이 설치되어 있지 않습니다!
    echo.
    echo Python 설치 방법:
    echo 1. Microsoft Store에서 "Python" 검색하여 설치
    echo 2. 또는 https://www.python.org/downloads/ 에서 다운로드
    echo.
    echo 자세한 설치 방법은 INSTALL_PYTHON.md 파일을 참고하세요.
    echo.
    pause
    exit /b 1
)

echo ✓ Python이 설치되어 있습니다.

REM pip 확인
pip --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ pip이 설치되어 있지 않습니다!
    echo Python을 다시 설치하거나 pip을 별도로 설치하세요.
    pause
    exit /b 1
)

echo ✓ pip이 설치되어 있습니다.
echo.

REM 필요한 라이브러리 설치 확인
echo 필요한 라이브러리를 설치합니다...
pip install -r requirements.txt

if %errorlevel% neq 0 (
    echo.
    echo ❌ 라이브러리 설치에 실패했습니다.
    echo 인터넷 연결을 확인하거나 수동으로 설치해보세요:
    echo pip install Pillow numpy
    echo.
    pause
    exit /b 1
)

echo.
echo ✓ 모든 라이브러리가 설치되었습니다.
echo.
echo 프로그램을 실행합니다...
echo.

python re_bg.py

pause
