package com.falcon.shop.controller.shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.domain.shop.OrderItemOption;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.CustomUser;
import com.falcon.shop.service.shop.OrderItemOptionService;
import com.falcon.shop.service.shop.OrderItemService;
import com.falcon.shop.service.shop.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired OrderService orderService;
    @Autowired OrderItemService orderItemService;
    @Autowired OrderItemOptionService orderItemOptionService;

    /**
     * 주문 생성 (AJAX)
     * @param orderRequest 주문+주문상품+옵션 정보
     * @return 주문번호
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(
        @AuthenticationPrincipal CustomUser customUser,
        @RequestBody Orders order
    ) {
    
      log.info("주문 생성 요청: {}", order);
      List<OrderItem> orderItems = order.getOrderItems();
      if( orderItems == null || orderItems.isEmpty()) {
          log.error("주문 항목이 없습니다.");
          return ResponseEntity.badRequest().body("주문 항목이 비어 있습니다.");
      }
      for (OrderItem orderItem : orderItems) {
        log.info("주문 항목: {}", orderItem);
        List<OrderItemOption> orderItemOptions = orderItem.getOrderItemOptions();
        for (OrderItemOption option : orderItemOptions) {
            log.info("주문 항목 옵션: {}", option);
        }
      }
      

      Orders createdOrder = null;
      try {
          // 주문자 정보 설정
          order.setUserNo(customUser.getUser().getNo());
          createdOrder = orderService.createOrder(order);
      } catch (Exception e) {
          log.error("주문자 정보 설정 오류: {}", e.getMessage());
          return ResponseEntity.badRequest().body("주문자 정보를 확인해주세요.");
      }
      // 주문 생성 서비스 호출
      if (createdOrder == null) {
          log.error("주문 생성 실패: 주문 정보가 null입니다.");
          return ResponseEntity.badRequest().body("주문 생성에 실패했습니다. 다시 시도해주세요.");
      }

      return ResponseEntity.ok(createdOrder);

    }


}
