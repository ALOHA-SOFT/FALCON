package com.falcon.shop.controller.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.CustomUser;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.service.shop.CancellationService;
import com.falcon.shop.service.shop.OrderService;
import com.falcon.shop.service.users.AddressService;
import com.falcon.shop.service.users.CartService;
import com.falcon.shop.service.users.UserService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/my")
public class MyController {

  @Autowired AddressService addressService;
  @Autowired OrderService orderService;
  @Autowired CancellationService cancellationService;
  @Autowired UserService userService;
  @Autowired CartService cartService;

  @GetMapping("")
  public String my() {
    return "page/my/my";
  }

  @GetMapping("/orders")
  public String orders(
    @AuthenticationPrincipal CustomUser customUser,
    Pagination pagination,
    QueryParams queryParams,
    Model model
  ) {
    // 사용자 번호로 주문 목록 조회
    Long userNo = customUser.getUser().getNo();
    log.info("Fetching orders for userNo: {}", userNo);
    model.addAttribute("userNo", userNo);
    PageInfo<Orders> pageInfo = orderService.pageByUserNo(queryParams, userNo);
    model.addAttribute("pageInfo", pageInfo);
    Long total = pageInfo.getTotal();
    pagination.setPage(queryParams.getPage());
    pagination.setSize(queryParams.getSize());
    pagination.setTotal(total);
    model.addAttribute("pagination", pagination);
    model.addAttribute("orderList", pageInfo.getList());
    log.info("################################ LIST {}", pageInfo.getList());
    // Uri 빌더
    String pageUri = UriComponentsBuilder.fromPath("/my/orders")
                                        // Pagination
                                         .queryParam("size", pagination.getSize())
                                          .build()
                                          .toUriString();
    model.addAttribute("pageUri", pageUri);

    return "page/my/orders";
  }

  @GetMapping("/cancel/{id}")
  public String cancelCreate(
    @AuthenticationPrincipal CustomUser customUser,
    @PathVariable("id") String id,
    Model model
  ) {
    log.info("Cancel order request for order ID: {}", id);
    Orders order = orderService.selectById(id);
    if (order == null) {
      log.warn("Order not found for ID: {}", id);
      model.addAttribute("error", "주문 정보를 찾을 수 없습니다.");
      return "page/my/error";
    }
    model.addAttribute("order", order);
    return "page/my/cancel/create";
  }

  @GetMapping("/cancel")
  public String cancel(
    @AuthenticationPrincipal CustomUser customUser,
    Pagination pagination,
    QueryParams queryParams,
    Model model
  ) {
    // 사용자 번호로 취소 목록 조회
    Long userNo = customUser.getUser().getNo();
    log.info("Fetching cancellations for userNo: {}", userNo);
    model.addAttribute("userNo", userNo);
    PageInfo<?> pageInfo = cancellationService.page(queryParams, userNo);
    model.addAttribute("pageInfo", pageInfo);
    Long total = pageInfo.getTotal();
    pagination.setPage(queryParams.getPage());
    pagination.setSize(queryParams.getSize());
    pagination.setTotal(total);
    model.addAttribute("pagination", pagination);
    model.addAttribute("cancellationList", pageInfo.getList());
    log.info("################################ LIST {}", pageInfo.getList());
    // Uri 빌더
    String pageUri = UriComponentsBuilder.fromPath("/my/cancel")
                                        // Pagination
                                         .queryParam("size", pagination.getSize())
                                          .build()
                                          .toUriString();
    model.addAttribute("pageUri", pageUri);
    
    return "page/my/cancel";
  }

  @GetMapping("/carts")
  public String carts(
    @AuthenticationPrincipal CustomUser customUser,
    Pagination pagination,
    QueryParams queryParams,
    Model model
  ) {
    // 사용자 번호로 장바구니 목록 조회
    Long userNo = customUser.getUser().getNo();
    log.info("Fetching carts for userNo: {}", userNo);
    PageInfo<?> pageInfo = cartService.pageByUserNo(queryParams, userNo);
    log.info("Carts pageInfo: {}", pageInfo);
    model.addAttribute("pageInfo", pageInfo);
    // Uri 빌더
    String pageUri = UriComponentsBuilder.fromPath("/my/carts")
                                        // Pagination
                                         .queryParam("size", pagination.getSize())
                                         .build()
                                         .toUriString();
    model.addAttribute("pageUri", pageUri);

    return "page/my/carts";
  }

  @GetMapping("/edit")
  public String edit(
    @AuthenticationPrincipal CustomUser customUser,
    Model model
  ) {
    // 사용자 정보 조회
    if (customUser == null || customUser.getUser() == null) {
      log.warn("사용자 정보가 없습니다.");
      model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
      return "page/my/error";
    }
    Users user = userService.select(customUser.getUser().getNo());
    model.addAttribute("user", user);
    log.info("Editing user: {}", user);
    return "page/my/edit";
  }

  @GetMapping("/delete")
  public String delete(
    @AuthenticationPrincipal CustomUser customUser,
    Model model
  ) {
    // 사용자 정보 조회
    if (customUser == null || customUser.getUser() == null) {
      log.warn("사용자 정보가 없습니다.");
      model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
      return "page/my/error";
    }
    Users user = userService.select(customUser.getUser().getNo());
    model.addAttribute("user", user);
    log.info("Deleting user: {}", user);
    
    // 사용자 번호로 배송 목록 조회
    List<?> addressList = addressService.listByUser(user.getNo());
    model.addAttribute("addressList", addressList);

    return "page/my/delete";
  }


  
  @GetMapping("/address")
  public String address(
    @AuthenticationPrincipal CustomUser customUser,
    Model model
  ) {
    // customeUser의 user 의 no 로 
    Long userNo = customUser.getUser().getNo();
    log.info("Fetching address for userNo: {}", userNo);
    model.addAttribute("userNo", userNo);
    // 사용자 번호로 배송 목록 조회
    List<?> addressList = addressService.listByUser(userNo);
    model.addAttribute("addressList", addressList);


    return "page/my/address";
  }
}
