package com.falcon.shop.api.shop;

import com.falcon.shop.service.NowPaymentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment/crypto")
@RequiredArgsConstructor
public class CryptoPaymentApi {

    private final NowPaymentsService nowPaymentsService;

    /**
     * API 상태 테스트
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testApi() {
        try {
            Map<String, Object> status = nowPaymentsService.getApiStatus();
            
            if (status != null) {
                return ResponseEntity.ok(Map.of("message", "API 연결 성공", "data", status));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "API 연결에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("API 테스트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 암호화폐 환율 조회
     */
    @PostMapping("/estimate")
    public ResponseEntity<Map<String, Object>> getEstimate(@RequestBody Map<String, String> request) {
        try {
            String priceAmount = request.get("price_amount");
            String priceCurrency = request.get("price_currency");
            String payCurrency = request.get("pay_currency");

            Map<String, Object> estimate = nowPaymentsService.getEstimate(priceAmount, priceCurrency, payCurrency);
            
            if (estimate != null) {
                return ResponseEntity.ok(estimate);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "환율 조회에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("환율 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 암호화폐 결제 생성
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        try {
            // 결제 데이터 구성
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("price_amount", request.get("price_amount"));
            paymentData.put("price_currency", request.get("price_currency"));
            paymentData.put("pay_currency", request.get("pay_currency"));
            paymentData.put("order_id", request.get("orderId"));
            paymentData.put("order_description", "Falcon Cartons Shop Order #" + request.get("orderId"));
            
            // 콜백 URL 설정
            paymentData.put("ipn_callback_url", "http://localhost:8080/api/payment/crypto/callback");
            paymentData.put("success_url", "http://localhost:8080/pay/crypto/success?orderId=" + request.get("orderId"));
            paymentData.put("cancel_url", "http://localhost:8080/pay/crypto/cancel?orderId=" + request.get("orderId"));

            Map<String, Object> payment = nowPaymentsService.createPayment(paymentData);
            
            if (payment != null) {
                return ResponseEntity.ok(payment);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "결제 생성에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("결제 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 결제 상태 조회
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable("paymentId") String paymentId) {
        try {
            Map<String, Object> status = nowPaymentsService.getPaymentStatus(paymentId);
            
            if (status != null) {
                return ResponseEntity.ok(status);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "결제 상태 조회에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("결제 상태 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * NowPayments 콜백 처리
     */
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> callbackData) {
        try {
            log.info("NowPayments callback received: {}", callbackData);
            
            String paymentStatus = (String) callbackData.get("payment_status");
            String orderId = (String) callbackData.get("order_id");
            
            // 결제 상태에 따른 처리
            if ("finished".equals(paymentStatus)) {
                // 결제 완료 처리
                log.info("Payment completed for order: {}", orderId);
                // TODO: 주문 상태 업데이트 및 이메일 발송
            } else if ("failed".equals(paymentStatus)) {
                // 결제 실패 처리
                log.info("Payment failed for order: {}", orderId);
                // TODO: 주문 실패 처리
            }
            
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("콜백 처리 실패", e);
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }

    /**
     * 사용 가능한 암호화폐 목록 조회
     */
    @GetMapping("/currencies")
    public ResponseEntity<Map<String, Object>> getAvailableCurrencies() {
        try {
            Map<String, Object> currencies = nowPaymentsService.getAvailableCurrencies();
            
            if (currencies != null) {
                return ResponseEntity.ok(currencies);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "통화 목록 조회에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("통화 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }
}
