package com.falcon.shop.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.falcon.shop.domain.common.Files;
import com.falcon.shop.service.file.FileUploadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@CrossOrigin("*")
@RequestMapping("/CHEditor")
@Controller
public class CHEditor {
	
	// 업로드 경로 (로컬 환경에서만 사용)
	@Value("${upload.path:#{null}}")	
	private String uploadPath;
	
	String SAVE_DIR = "/CHEditor";
	String SAVE_URL = "/CHEditor/attach/";
	
	@Autowired
	private FileUtils fileUtils;
	
	@Autowired
	private FileUploadService fileUploadService;

	@ResponseBody
	@PostMapping("/upload")
	public String upload(@RequestParam("file") MultipartFile uFile, HttpServletRequest request, HttpSession session) {
		log.info("📤 CHEditor upload request - File: {}, Strategy: {}", 
				uFile.getOriginalFilename(), fileUploadService.getUploadType());

		try {
		    // 새로운 FileUploadService를 사용한 파일 업로드
		    Files uploadedFile = fileUploadService.uploadFile(uFile, "CHEditor");
		    
		    String fileName = uploadedFile.getFileName();
		    String filePath = uploadedFile.getSubPath();
		    long fileSize = uploadedFile.getFileSize();
		    
		    // 파일 URL 생성
		    String fileUrl = fileUploadService.getFileUrl(filePath);
		    
		    log.info("✅ CHEditor upload completed - File: {}, Size: {}, URL: {}", 
		            fileName, fileSize, fileUrl);
		    
		    // 응답 JSON 생성
		    String rData = String.format(
		        "{\"fileUrl\":\"%s\", \"filePath\":\"%s\", \"fileName\":\"%s\", \"fileSize\":\"%d\"}", 
		        fileUrl, filePath, fileName, fileSize
		    );
		    
		    return rData;

		} catch(Exception e) {
		    log.error("❌ CHEditor upload failed: {}", e.getMessage(), e);
		    return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
	
	
	// 에디터로 업로드한 이미지 미리보기
	@GetMapping("/attach/{fullName}")
	public ResponseEntity<byte[]> upload(HttpSession session, @PathVariable("fullName") String fullName) throws IOException {
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		
		fullName = uploadPath + SAVE_DIR + "/" + fullName;
		
		try {
			// 확장자
			String formatName = fullName.substring(fullName.lastIndexOf(".") + 1);
			
			MediaType mType = MediaUtils.getMediaType(formatName);
			
			HttpHeaders headers = new HttpHeaders();
			
			try {
				in = new FileInputStream(fullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 이미지 타입인지
			if( mType != null ) {
				headers.setContentType(mType);
			} else {
				// UID_XXX.PNG
				fullName = fullName.substring(fullName.lastIndexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition", "attachment; fullName=\"" + new String(fullName.getBytes("UTF-8"), "ISO-8859") + "\"");
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			if( in != null )
				in.close();
		}
		
		return entity;
		
	}
	
	// 에디터로 업로드한 이미지 미리보기
	@GetMapping("/attach/{userId}/{fullName}")
	public ResponseEntity<byte[]> tempEditorPreview(HttpSession session, @PathVariable("fullName") String fullName
										) throws IOException {
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		
		fullName = uploadPath + SAVE_DIR + "/" + fullName;
		
		try {
			// 확장자
			String formatName = fullName.substring(fullName.lastIndexOf(".") + 1);
			
			MediaType mType = MediaUtils.getMediaType(formatName);
			
			HttpHeaders headers = new HttpHeaders();
			
			try {
				in = new FileInputStream(fullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 이미지 타입인지
			if( mType != null ) {
				headers.setContentType(mType);
			} else {
				// UID_XXX.PNG
				fullName = fullName.substring(fullName.lastIndexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition", "attachment; fullName=\"" + new String(fullName.getBytes("UTF-8"), "ISO-8859") + "\"");
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			if( in != null )
				in.close();
		}
		
		return entity;
		
	}
	
	
	// 에디터로 업로드한 이미지 미리보기
	@GetMapping("/real/{fullName}")
	public ResponseEntity<byte[]> realEditorPreview(HttpSession session, @PathVariable("fullName") String fullName ) throws IOException {
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		
		fullName = uploadPath + SAVE_DIR + "/" + fullName;
		
		try {
			// 확장자
			String formatName = fullName.substring(fullName.lastIndexOf(".") + 1);
			
			MediaType mType = MediaUtils.getMediaType(formatName);
			
			HttpHeaders headers = new HttpHeaders();
			
			try {
				in = new FileInputStream(fullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 이미지 타입인지
			if( mType != null ) {
				headers.setContentType(mType);
			} else {
				// UID_XXX.PNG
				fullName = fullName.substring(fullName.lastIndexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition", "attachment; fullName=\"" + new String(fullName.getBytes("UTF-8"), "ISO-8859") + "\"");
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			if( in != null )
				in.close();
		}
		
		return entity;
		
	}
	
	
}










