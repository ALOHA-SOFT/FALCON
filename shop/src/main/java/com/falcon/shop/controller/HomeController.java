package com.falcon.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.admin.Banners;
import com.falcon.shop.domain.admin.Popups;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.service.admin.BannerService;
import com.falcon.shop.service.admin.PopupService;
import com.falcon.shop.service.products.ProductService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;





@Slf4j
@Controller
public class HomeController {

  @Autowired private ProductService productService;
  @Autowired private BannerService bannerService;
  @Autowired private PopupService popupService;

  /**
   * 💻 메인
   * @return
   */
  @GetMapping("/")
  public String home(
    QueryParams queryParams,
    Model model
  ) {
    queryParams.setPage(1);
    queryParams.setSize(4);

    // 메인 배너
    List<Banners> mainBanners = bannerService.listByType("MAIN");
    model.addAttribute("mainBanners", mainBanners);
    // 메인 팝업
    List<Popups> mainPopups = popupService.listByTypeOpen("MAIN");
    model.addAttribute("mainPopups", mainPopups);
    // BEST 상품 4개
    PageInfo<Products> bestPageInfo = productService.pageBEST(queryParams);
    model.addAttribute("bestProducts", bestPageInfo.getList());
    // NEW 상품 4개
    PageInfo<Products> newPageInfo = productService.pageNEW(queryParams);
    model.addAttribute("newProducts", newPageInfo.getList());
    

    return "index";
  }

  /**
   * 💻 회원가입
   * @return
   */
  @GetMapping({"/join", "/signup"})
  public String getMethodName() {
      return "page/join";
  }

  /**
   * 💻 로그인
   * @return
   */
  @GetMapping({"/login", "/signin"})
  public String login(
    @CookieValue(value = "remember-id", required = false) Cookie cookie,
    Model model
  ) {
    log.info(":::::::::: 로그인 페이지 ::::::::::");
    String username = "";
    boolean rememberId = false;
    if( cookie != null ) {
        log.info("CookieName : " + cookie.getName());
        log.info("CookieValue : " + cookie.getValue());
        username = cookie.getValue();
        rememberId = true;
    }
    model.addAttribute("username", username);
    model.addAttribute("rememberId", rememberId);
    return "page/login";
  }

  /**
   * 💻 아이디 찾기
   * @return
   */
  @GetMapping("/find-id")
  public String findId() {
      log.info(":::::::::: 아이디 찾기 페이지 ::::::::::");
      return "page/find-id";
  }

  /**
   * 💻 비밀번호 찾기
   * @return
   */
  @GetMapping("/find-pw")
  public String findPassword() {
      log.info(":::::::::: 비밀번호 찾기 페이지 ::::::::::");
      return "page/find-pw";
  }

  /**
   * 이용약관
   */
  @GetMapping("/terms/{page}")
  public String termsPage(@PathVariable("page") String page) {
      return "page/terms/" + page;
  }
  

  /**
   * 고객안내
   * @return
   */
  @GetMapping("/info")
  public String info() {
      return "page/info";
  }
  
  
  /**
  * 로그아웃
  * @param param
  * @return
  */
  @GetMapping("/logout")
  public String logout(
    HttpSession session
  ) {
    log.info("로그아웃 요청");
    // 세션 무효화
    if (session != null) {
        session.invalidate();
        log.info("세션 무효화 완료");
    }
    return "redirect:/";
  }
  
  // /sample
  @GetMapping("/editor")
  public String sample() {
      log.info("샘플 페이지 요청");
      return "page/editor";
  }  



}


