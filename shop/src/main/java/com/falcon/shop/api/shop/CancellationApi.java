package com.falcon.shop.api.shop;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.falcon.shop.domain.shop.Cancellations;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.shop.Payments;
import com.falcon.shop.service.shop.CancellationService;
import com.falcon.shop.service.shop.OrderService;
import com.falcon.shop.service.shop.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/shop/cancellation")
public class CancellationApi {
  
  @Autowired private CancellationService cancellationService;
  @Autowired private OrderService orderService;
  @Autowired private PaymentService paymentService;
  
  @GetMapping()
  public ResponseEntity<?> getAll(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "size", required = false, defaultValue = "10") int size
  ) {
      try {
          return new ResponseEntity<>(cancellationService.page(page, size), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(cancellationService.selectById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> createForm(Cancellations cancellation) {
      log.info("## FORM ##");
      log.info("cancellation={}", cancellation);
      try {
        boolean result = cancellationService.insert(cancellation);
        if (!result) {
          return new ResponseEntity<>("Failed to create cancellation", HttpStatus.BAD_REQUEST);
        }
        // 주문 상태 업데이트
        Orders order = Orders.builder()
                              .no(cancellation.getOrderNo())
                              .status("주문취소")
                              .build();
        boolean orderUpdateResult = orderService.update(order);
        return new ResponseEntity<>(result && orderUpdateResult, HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> createMultiPartForm(Cancellations cancellation) {
      log.info("## MULTIPART ##");
      log.info("cancellation={}", cancellation);
      try {
        boolean result = cancellationService.insert(cancellation);
        if (!result) {
          return new ResponseEntity<>("Failed to create cancellation", HttpStatus.BAD_REQUEST);
        }
        // 주문 상태 업데이트
        Orders order = Orders.builder()
                              .no(cancellation.getOrderNo())
                              .status("주문취소")
                              .build();
        boolean orderUpdateResult = orderService.update(order);
        return new ResponseEntity<>(result && orderUpdateResult, HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody Cancellations cancellation) {
      log.info("## JSON ##");
      try {
        boolean result = cancellationService.insert(cancellation);
        if (!result) {
          return new ResponseEntity<>("Failed to create cancellation", HttpStatus.BAD_REQUEST);
        }
        // 주문 상태 업데이트
        Orders order = Orders.builder()
                              .no(cancellation.getOrderNo())
                              .status("주문취소")
                              .build();
        boolean orderUpdateResult = orderService.update(order);
        return new ResponseEntity<>(result && orderUpdateResult, HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  /**
   * 토스페이먼츠 환불 처리 API
   * 
   * @param refundRequest 환불 요청 정보 (cancellationId, cancelReason, cancelAmount)
   * @return 환불 처리 결과
   */
  @PostMapping("/refund")
  @Transactional
  public ResponseEntity<?> processRefund(@RequestBody Map<String, Object> refundRequest) {
      try {
          // 요청 파라미터 검증
          String cancellationId = (String) refundRequest.get("cancellationId");
          String cancelReason = (String) refundRequest.get("cancelReason");
          Object cancelAmountObj = refundRequest.get("cancelAmount");
          
          if (cancellationId == null || cancelReason == null) {
              return new ResponseEntity<>("취소 ID와 취소 사유는 필수입니다.", HttpStatus.BAD_REQUEST);
          }
          
          // 취소/반품 정보 조회
          Cancellations cancellation = cancellationService.selectById(cancellationId);
          if (cancellation == null) {
              return new ResponseEntity<>("취소/반품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
          }
          
          // 주문번호로 결제 정보 조회
          Payments payment = paymentService.selectByOrderNo(cancellation.getOrderNo());
          if (payment == null) {
              return new ResponseEntity<>("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
          }
          
          if (payment.getPaymentKey() == null) {
              return new ResponseEntity<>("결제키가 없습니다.", HttpStatus.BAD_REQUEST);
          }
          
          // 토스페이먼츠 결제 취소 API 호출
          // String secretKey = "test_gsk_DnyRpQWGrNlZDOL6MJbg3Kwv1M9E"; // 테스트 시크릿키
        //   String secretKey = "live_gsk_E92LAa5PVb5l0ADKvXaY37YmpXyJ"; // 실제 운영 시크릿키
          String secretKey = "live_sk_kYG57Eba3G6LBJOyoe4l8pWDOxmA"; // 실제 운영 시크릿키

          WebClient webClient = WebClient.builder()
              .baseUrl("https://api.tosspayments.com")
              .defaultHeaders(headers -> headers.setBasicAuth(secretKey, ""))
              .build();
          
          // 환불 요청 데이터 준비
          Map<String, Object> cancelRequest = new HashMap<>();
          cancelRequest.put("cancelReason", cancelReason);
          
          // 부분 환불인 경우 금액 설정
          if (cancelAmountObj != null) {
              Long cancelAmount = null;
              if (cancelAmountObj instanceof Integer) {
                  cancelAmount = ((Integer) cancelAmountObj).longValue();
              } else if (cancelAmountObj instanceof Long) {
                  cancelAmount = (Long) cancelAmountObj;
              } else if (cancelAmountObj instanceof String) {
                  try {
                      cancelAmount = Long.parseLong((String) cancelAmountObj);
                  } catch (NumberFormatException e) {
                      return new ResponseEntity<>("취소 금액 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
                  }
              }
              
              if (cancelAmount != null && cancelAmount > 0) {
                  cancelRequest.put("cancelAmount", cancelAmount);
              }
          }
          
          // 토스페이먼츠 결제 취소 API 호출
          String response = webClient.post()
              .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
              .bodyValue(cancelRequest)
              .retrieve()
              .onStatus(
                  status -> status.is4xxClientError() || status.is5xxServerError(),
                  clientResponse -> clientResponse.bodyToMono(String.class).map(body -> new RuntimeException(body))
              )
              .bodyToMono(String.class)
              .block();
          
          // 환불 성공 시 DB 상태 업데이트
          if (response != null) {
              // 취소/반품 상태 업데이트
              cancellation.setStatus("환불완료");
              cancellation.setIsRefund(true);
              cancellationService.updateById(cancellation);
              
              // 주문 상태 업데이트
              Orders order = orderService.selectById(String.valueOf(cancellation.getOrderNo()));
              if (order != null) {
                  order.setStatus("환불완료");
                  orderService.updateById(order);
              }
              
              // 결제 상태 업데이트
              payment.setStatus("환불완료");
              paymentService.updateById(payment);
              
              // 성공 응답
              Map<String, Object> result = new HashMap<>();
              result.put("success", true);
              result.put("message", "환불이 성공적으로 처리되었습니다.");
              result.put("tossResponse", response);
              
              return new ResponseEntity<>(result, HttpStatus.OK);
          } else {
              return new ResponseEntity<>("환불 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
          }
          
      } catch (Exception e) {
          log.error("환불 처리 중 오류 발생: {}", e.getMessage(), e);
          
          // 토스페이먼츠 에러 응답 처리
          String errorMsg = e.getMessage();
          String errorCode = null;
          String errorMessage = null;
          
          try {
              ObjectMapper mapper = new ObjectMapper();
              @SuppressWarnings("unchecked")
              Map<String, Object> errorMap = mapper.readValue(errorMsg, Map.class);
              errorCode = (String) errorMap.getOrDefault("code", null);
              errorMessage = (String) errorMap.getOrDefault("message", errorMsg);
          } catch (Exception parseEx) {
              errorMessage = errorMsg;
          }
          
          log.error("토스페이먼츠 환불 실패: code={}, message={}", errorCode, errorMessage);
          
          Map<String, Object> errorResult = new HashMap<>();
          errorResult.put("success", false);
          errorResult.put("errorCode", errorCode);
          errorResult.put("message", errorMessage != null ? errorMessage : "환불 처리 중 오류가 발생했습니다.");
          
          return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
      }
  }
  
  @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateForm(Cancellations cancellation) {
      try {
          return new ResponseEntity<>(cancellationService.updateById(cancellation), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> updateMultiPartForm(Cancellations cancellation) {
      try {
          return new ResponseEntity<>(cancellationService.updateById(cancellation), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> update(@RequestBody Cancellations cancellation) {
      try {
          return new ResponseEntity<>(cancellationService.updateById(cancellation), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> destroy(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(cancellationService.deleteById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

}
