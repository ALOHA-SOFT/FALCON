package com.falcon.shop.security.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
      log.info("로그인 성공: {}", authentication.getName());

      // redirect 파라미터 있으면, 해당 페이지로 이동
      String redirect = request.getParameter("redirect");
      if( redirect != null && !redirect.isEmpty() ) {
        response.sendRedirect(redirect);
        return;
      }

      log.info("로그인 성공!");

      // 아이디 저장
      String rememberId = request.getParameter("remember-id");    // ✅ 아이디 저장 여부
      String username = request.getParameter("username");         // 👩‍💼 아이디

      try {
          // 쿠키 값에 공백이나 특수문자가 포함될 수 있으므로 URL 인코딩
          String encodedUsername = username != null ? URLEncoder.encode(username, StandardCharsets.UTF_8) : "";
          
          // 아이디 저장 체크 ✅ 
          if( rememberId != null && rememberId.equals("on") ) {
              Cookie cookie = new Cookie("remember-id", encodedUsername);     // 쿠키에 아이디 등록
              cookie.setMaxAge(60 * 60 * 24 * 7);                           // 유효기간 : 7일
              cookie.setPath("/");
              response.addCookie(cookie);
          }
          // 아이디 저장 체크 ❌ 
          else {
              Cookie cookie = new Cookie("remember-id", encodedUsername);     // 쿠키에 아이디 등록
              cookie.setMaxAge(0);                                   // 유효기간 : 0 (만료)
              cookie.setPath("/");
              response.addCookie(cookie);
          }
      } catch (Exception e) {
          log.error("쿠키 저장 중 오류 발생: {}", e.getMessage(), e);
      }
      
      String targetUrl = "/";
      response.sendRedirect(targetUrl);
    }
  
}
