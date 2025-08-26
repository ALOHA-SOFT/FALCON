package com.falcon.shop.api.shop;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.service.email.EmailService;
import com.falcon.shop.service.shop.OrderService;
import com.falcon.shop.service.users.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/shop/order")
public class OrderApi {
  
  @Autowired private OrderService orderService;
  @Autowired private EmailService emailService;
  @Autowired private UserService userService;
  
  @GetMapping()
  public ResponseEntity<?> getAll(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "size", required = false, defaultValue = "10") int size
  ) {
      try {
          return new ResponseEntity<>(orderService.page(page, size), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(orderService.selectById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> createForm(Orders order) {
      log.info("## FORM ##");
      log.info("order={}", order);
      try {
          return new ResponseEntity<>(orderService.insert(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> createMultiPartForm(Orders order) {
      log.info("## MULTIPART ##");
      log.info("order={}", order);
      try {
          return new ResponseEntity<>(orderService.insert(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody Orders order) {
      log.info("## JSON ##");
      log.info("order={}", order);
      try {
          return new ResponseEntity<>(orderService.insert(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateForm(Orders order) {
      try {
          return new ResponseEntity<>(orderService.updateById(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> updateMultiPartForm(Orders order) {
      try {
          return new ResponseEntity<>(orderService.updateById(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> update(@RequestBody Orders order) {
      try {
          return new ResponseEntity<>(orderService.updateById(order), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> destroy(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(orderService.deleteById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  /**
   * 현금 결제 처리
   */
  @PostMapping("/cash-payment")
  public ResponseEntity<String> processCashPayment(
      @RequestBody Map<String, String> request) {
      
      try {
          String orderId = request.get("orderId");
          String paymentMethod = request.get("paymentMethod");

          log.info("현금 결제 처리 요청 받음 - orderId: {}, paymentMethod: {}", orderId, paymentMethod);
          log.info("현금 결제 처리 시작: orderId={}, paymentMethod={}", orderId, paymentMethod);
          
          // 주문 정보 조회
          Orders order = orderService.selectById(orderId);
          if (order == null) {
              log.error("주문을 찾을 수 없습니다: {}", orderId);
              return new ResponseEntity<>("ORDER_NOT_FOUND", HttpStatus.BAD_REQUEST);
          }

          // 주문 사용자 조회
          Long userNo = order.getUserNo();
          Users user = userService.select(userNo);
          if (user == null) {
              log.error("사용자를 찾을 수 없습니다: {}", userNo);
              return new ResponseEntity<>("USER_NOT_FOUND", HttpStatus.BAD_REQUEST);
          }

          // 주소 번호
          String addressNo = request.get("addressNo");
          if (addressNo != null) {
              order.setAddressNo(Long.valueOf(addressNo));
          }

          // 주문 사용자 정보 업데이트
          order.setGuestEmail(request.get("buyerEmail"));
          order.setGuestFirstName(request.get("buyerFirstName"));
          order.setGuestLastName(request.get("buyerLastName"));
          order.setPaymentMethod(paymentMethod);
          orderService.updateById(order);
          
          // 이메일 발송
          boolean emailSent = false;
          try {
              emailSent = emailService.sendPaymentEmail(
                  order,
                  paymentMethod,
                  order.getGuestEmail(),
                  order.getGuestFirstName() + " " + order.getGuestLastName()
              );
              
              if (emailSent) {
                  log.info("결제 안내 이메일 발송 성공: {}", order.getGuestEmail());
              } else {
                  log.warn("결제 안내 이메일 발송 실패: {}", order.getGuestEmail());
              }
          } catch (Exception e) {
              log.error("이메일 발송 중 오류 발생: " + e.getMessage(), e);
          }
          
          return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
          
      } catch (Exception e) {
          log.error("현금 결제 처리 실패: " + e.getMessage(), e);
          return new ResponseEntity<>("PAYMENT_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  /**
   * 코인 결제 처리
   */
  @PostMapping("/coin-payment")
  public ResponseEntity<String> processCoinPayment(
      @RequestParam("orderId") String orderId,
      @RequestParam("paymentMethod") String paymentMethod,
      @RequestParam("coinAmount") Integer coinAmount) {
      
      try {
          log.info("코인 결제 처리 시작: orderId={}, paymentMethod={}, coinAmount={}", orderId, paymentMethod, coinAmount);
          
          // 주문 정보 조회
          Orders order = orderService.selectById(orderId);
          if (order == null) {
              log.error("주문을 찾을 수 없습니다: {}", orderId);
              return new ResponseEntity<>("ORDER_NOT_FOUND", HttpStatus.BAD_REQUEST);
          }
          
          // 주문 상태를 결제 완료로 업데이트
          order.setPaymentMethod(paymentMethod);
          order.setStatus("결제완료");
          orderService.updateById(order);
          
          // 이메일 발송
          boolean emailSent = false;
          try {
              emailSent = emailService.sendPaymentEmail(
                  order,
                  paymentMethod + " (" + coinAmount.toString() + " 코인)",
                  order.getGuestEmail(),
                  order.getGuestFirstName() + " " + order.getGuestLastName()
              );
              
              if (emailSent) {
                  log.info("결제 안내 이메일 발송 성공: {}", order.getGuestEmail());
              } else {
                  log.warn("결제 안내 이메일 발송 실패: {}", order.getGuestEmail());
              }
          } catch (Exception e) {
              log.error("이메일 발송 중 오류 발생: " + e.getMessage(), e);
          }
          
          return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
          
      } catch (Exception e) {
          log.error("코인 결제 처리 실패: " + e.getMessage(), e);
          return new ResponseEntity<>("PAYMENT_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

}
