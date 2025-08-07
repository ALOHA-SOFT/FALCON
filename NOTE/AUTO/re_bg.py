import os
import sys
from PIL import Image
import numpy as np
from pathlib import Path

def remove_background(image_path, output_path, threshold=10):
    """
    이미지의 배경을 투명하게 만드는 함수
    
    Args:
        image_path (str): 입력 이미지 경로
        output_path (str): 출력 이미지 경로
        threshold (int): 색상 차이 임계값 (0-255)
    """
    try:
        # 이미지 열기
        img = Image.open(image_path)
        
        # GIF인 경우 첫 번째 프레임만 사용
        if img.format == 'GIF':
            img = img.convert('RGB')
        
        # RGBA 모드로 변환 (투명도 지원)
        if img.mode != 'RGBA':
            img = img.convert('RGBA')
        
        # 이미지를 numpy 배열로 변환
        data = np.array(img)
        
        # 배경색 감지 (모서리 픽셀의 평균값 사용)
        h, w = data.shape[:2]
        
        # 모서리 픽셀들 수집
        edge_pixels = []
        # 상단, 하단 모서리
        edge_pixels.extend(data[0, :])
        edge_pixels.extend(data[h-1, :])
        # 좌측, 우측 모서리
        edge_pixels.extend(data[:, 0])
        edge_pixels.extend(data[:, w-1])
        
        edge_pixels = np.array(edge_pixels)
        
        # 가장 많이 나타나는 색상을 배경색으로 설정
        from collections import Counter
        colors = [tuple(pixel[:3]) for pixel in edge_pixels]
        bg_color = Counter(colors).most_common(1)[0][0]
        
        print(f"감지된 배경색: RGB{bg_color}")
        
        # 배경색과 유사한 픽셀을 투명하게 만들기
        red, green, blue, alpha = data[:,:,0], data[:,:,1], data[:,:,2], data[:,:,3]
        
        # 배경색과의 거리 계산
        mask = (abs(red - bg_color[0]) < threshold) & \
               (abs(green - bg_color[1]) < threshold) & \
               (abs(blue - bg_color[2]) < threshold)
        
        # 배경 부분을 투명하게 설정
        data[mask] = [0, 0, 0, 0]
        
        # 결과 이미지 생성
        result_img = Image.fromarray(data, 'RGBA')
        
        # 저장
        result_img.save(output_path, 'PNG')
        print(f"처리 완료: {os.path.basename(output_path)}")
        
        return True
        
    except Exception as e:
        print(f"오류 발생 ({os.path.basename(image_path)}): {str(e)}")
        return False

def remove_gif_background_animated(image_path, output_path, threshold=10):
    """
    GIF 애니메이션의 배경을 투명하게 만들어 WebP로 저장하는 함수
    
    Args:
        image_path (str): 입력 GIF 경로
        output_path (str): 출력 WebP 경로
        threshold (int): 색상 차이 임계값 (0-255)
    """
    try:
        # GIF 열기
        gif = Image.open(image_path)
        
        # 애니메이션이 아닌 경우 일반 처리
        if not getattr(gif, 'is_animated', False):
            return remove_background(image_path, output_path, threshold)
        
        print(f"애니메이션 GIF 처리: {os.path.basename(image_path)} ({gif.n_frames}프레임)")
        
        # 첫 번째 프레임에서 배경색 감지
        first_frame = gif.copy().convert('RGBA')
        data = np.array(first_frame)
        h, w = data.shape[:2]
        
        # 모서리 픽셀들 수집
        edge_pixels = []
        edge_pixels.extend(data[0, :])
        edge_pixels.extend(data[h-1, :])
        edge_pixels.extend(data[:, 0])
        edge_pixels.extend(data[:, w-1])
        
        edge_pixels = np.array(edge_pixels)
        
        # 배경색 감지
        from collections import Counter
        colors = [tuple(pixel[:3]) for pixel in edge_pixels]
        bg_color = Counter(colors).most_common(1)[0][0]
        
        print(f"감지된 배경색: RGB{bg_color}")
        
        # 모든 프레임 처리
        processed_frames = []
        durations = []
        
        for frame_num in range(gif.n_frames):
            gif.seek(frame_num)
            frame = gif.copy().convert('RGBA')
            
            # 프레임 데이터를 numpy 배열로 변환
            frame_data = np.array(frame)
            
            # 배경색과 유사한 픽셀을 투명하게 만들기
            red, green, blue, alpha = frame_data[:,:,0], frame_data[:,:,1], frame_data[:,:,2], frame_data[:,:,3]
            
            # 배경색과의 거리 계산
            mask = (abs(red - bg_color[0]) < threshold) & \
                   (abs(green - bg_color[1]) < threshold) & \
                   (abs(blue - bg_color[2]) < threshold)
            
            # 배경 부분을 투명하게 설정
            frame_data[mask] = [0, 0, 0, 0]
            
            # 처리된 프레임 저장
            processed_frame = Image.fromarray(frame_data, 'RGBA')
            processed_frames.append(processed_frame)
            
            # 프레임 지속시간 저장 (밀리초)
            duration = gif.info.get('duration', 100)
            durations.append(duration)
        
        # WebP 애니메이션으로 저장
        processed_frames[0].save(
            output_path,
            format='WEBP',
            save_all=True,
            append_images=processed_frames[1:],
            duration=durations,
            loop=0,  # 무한 반복
            lossless=True  # 무손실 압축
        )
        
        print(f"애니메이션 WebP 저장 완료: {os.path.basename(output_path)}")
        return True
        
    except Exception as e:
        print(f"애니메이션 GIF 처리 오류 ({os.path.basename(image_path)}): {str(e)}")
        # 실패시 일반 처리로 폴백
        return remove_background(image_path, output_path.replace('.webp', '.png'), threshold)

def remove_white_background(image_path, output_path, threshold=30):
    """
    흰색 배경을 투명하게 만드는 함수 (간단한 방법)
    
    Args:
        image_path (str): 입력 이미지 경로
        output_path (str): 출력 이미지 경로
        threshold (int): 흰색 임계값 (0-255)
    """
    try:
        # 이미지 열기
        img = Image.open(image_path)
        
        # GIF인 경우 첫 번째 프레임만 사용
        if img.format == 'GIF':
            img = img.convert('RGB')
        
        # RGBA 모드로 변환
        if img.mode != 'RGBA':
            img = img.convert('RGBA')
        
        # 이미지를 numpy 배열로 변환
        data = np.array(img)
        
        # 흰색에 가까운 픽셀을 투명하게 만들기
        red, green, blue, alpha = data[:,:,0], data[:,:,1], data[:,:,2], data[:,:,3]
        
        # 흰색 마스크 생성 (R, G, B 모두 threshold 이상인 픽셀)
        white_mask = (red > (255 - threshold)) & \
                     (green > (255 - threshold)) & \
                     (blue > (255 - threshold))
        
        # 흰색 배경을 투명하게 설정
        data[white_mask] = [0, 0, 0, 0]
        
        # 결과 이미지 생성
        result_img = Image.fromarray(data, 'RGBA')
        
        # 저장
        result_img.save(output_path, 'PNG')
        print(f"흰색 배경 제거 완료: {os.path.basename(output_path)}")
        
        return True
        
    except Exception as e:
        print(f"오류 발생 ({os.path.basename(image_path)}): {str(e)}")
        return False

def process_images():
    """
    FALCON 폴더의 모든 이미지 파일을 처리하는 메인 함수
    """
    # 입력 및 출력 폴더 설정
    input_folder = Path("F:/DEV/FALCON/FALCON/NOTE/FALCON")
    output_folder = Path("F:/DEV/FALCON/FALCON/NOTE/AUTO/output")
    
    # 출력 폴더 생성
    output_folder.mkdir(exist_ok=True)
    
    if not input_folder.exists():
        print(f"입력 폴더가 존재하지 않습니다: {input_folder}")
        return
    
    # 지원하는 이미지 파일 확장자
    supported_extensions = [".png", ".jpg", ".jpeg", ".bmp", ".gif", ".webp", ".tiff", ".tif"]
    
    # 모든 지원되는 이미지 파일 찾기
    image_files = []
    for ext in supported_extensions:
        image_files.extend(input_folder.glob(f"*{ext}"))
        image_files.extend(input_folder.glob(f"*{ext.upper()}"))
    
    if not image_files:
        print("처리할 이미지 파일이 없습니다.")
        print(f"지원하는 형식: {', '.join(supported_extensions)}")
        return
    
    print(f"총 {len(image_files)}개의 이미지 파일을 찾았습니다.")
    print("지원하는 형식:", ", ".join(supported_extensions))
    print("-" * 50)
    
    success_count = 0
    
    # 처리 방법 선택
    print("배경 제거 방법을 선택하세요:")
    print("1. 자동 배경색 감지 (추천)")
    print("2. 흰색 배경만 제거")
    print("3. GIF 애니메이션 유지하여 WebP로 변환")
    
    try:
        choice = input("선택 (1, 2, 또는 3): ").strip()
    except KeyboardInterrupt:
        print("\n프로그램이 중단되었습니다.")
        return
    
    # 각 이미지 파일 처리
    for image_file in image_files:
        # 출력 파일명 생성
        base_name = image_file.stem  # 확장자 제외한 파일명
        
        # GIF 파일이고 애니메이션 유지 옵션을 선택한 경우
        if choice == "3" and image_file.suffix.lower() in ['.gif']:
            output_path = output_folder / f"transparent_{base_name}.webp"
            success = remove_gif_background_animated(str(image_file), str(output_path))
        else:
            # 일반 처리 (모든 파일을 PNG로 저장)
            output_path = output_folder / f"transparent_{base_name}.png"
            
            if choice == "2":
                # 흰색 배경 제거
                success = remove_white_background(str(image_file), str(output_path))
            else:
                # 자동 배경색 감지
                success = remove_background(str(image_file), str(output_path))
        
        if success:
            success_count += 1
    
    print("-" * 50)
    print(f"처리 완료: {success_count}/{len(image_files)} 파일 성공")
    print(f"결과 파일 위치: {output_folder}")
    if choice == "3":
        print("애니메이션 GIF는 WebP로, 나머지는 PNG로 저장되었습니다.")
    else:
        print("모든 이미지가 PNG 형식으로 저장되었습니다.")

def main():
    """
    메인 실행 함수
    """
    print("=" * 60)
    print("이미지 배경 투명화 프로그램 (PNG 변환)")
    print("=" * 60)
    
    # 필요한 라이브러리 확인
    try:
        import PIL
        import numpy as np
        print("✓ 필요한 라이브러리가 모두 설치되어 있습니다.")
    except ImportError as e:
        print(f"❌ 필요한 라이브러리가 설치되지 않았습니다: {e}")
        print("다음 명령어로 설치하세요:")
        print("pip install Pillow numpy")
        return
    
    print()
    process_images()
    
    input("\n계속하려면 Enter를 누르세요...")

if __name__ == "__main__":
    main()
