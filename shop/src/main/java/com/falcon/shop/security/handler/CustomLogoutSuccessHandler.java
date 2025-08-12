package com.falcon.shop.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                 Authentication authentication) throws IOException, ServletException {
      log.info("로그아웃 성공: {}", authentication.getName());
      // redirect 파라미터 있으면, 해당 페이지로 이동
      String redirect = request.getParameter("redirect");
      if( redirect != null && !redirect.isEmpty() ) {
        response.sendRedirect(redirect);
        return;
      }

      String targetUrl = "/login?logout";
      response.sendRedirect(targetUrl);
    }
}
