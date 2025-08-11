package com.falcon.shop.controller.shop;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.shop.Payments;
import com.falcon.shop.domain.users.CustomUser;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.service.shop.OrderService;
import com.falcon.shop.service.shop.PaymentService;
import com.falcon.shop.service.users.AddressService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired private OrderService orderService;
    @Autowired private AddressService addressService;
    @Autowired private PaymentService paymentService;

    // 주문서 페이지
    @GetMapping("/order/{id}")
    public String orderPage(
        @PathVariable("id") String id, 
        @AuthenticationPrincipal CustomUser customUser, 
        Model model                           
    ) {
        log.info("주문서 페이지 요청: {}", id);

        // 사용자 정보
        Users user = customUser.getUser();
        model.addAttribute("user", user);

        // 배송지 정보
        if (user != null) {
            model.addAttribute("addressList", addressService.listByUser(user.getNo()));
        } else {
            log.warn("사용자 정보가 없습니다.");
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "page/pay/error";
        }
        

        // 주문 정보 조회
        Orders order = orderService.selectById(id);
        log.info("주문 정보: {}", order);
        if (order == null) {
            log.warn("주문 정보가 없습니다: {}", id);
            model.addAttribute("error", "주문 정보를 찾을 수 없습니다.");
            return "page/pay/error";
        }
        model.addAttribute("order", order);
        return "page/pay/order";
    }

    // 결제 성공 콜백
    @GetMapping("/success")
    public String paySuccess(
        @RequestParam("paymentKey") String paymentKey,
        @RequestParam("orderId") String orderId,
        @RequestParam("amount") Long amount,
        @RequestParam("paymentType") String paymentType,
        Model model
    ) {
        // 파라미터 유효성 사전 체크
        if (paymentKey == null || paymentKey.isEmpty() || orderId == null || orderId.isEmpty() || amount <= 0) {
            log.warn("결제 승인 파라미터 오류: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);
            model.addAttribute("error", "결제 승인 파라미터가 올바르지 않습니다. 다시 시도해주세요.");
            return "page/pay/fail";
        }
        if (orderId.length() < 6 || orderId.length() > 64 || !orderId.matches("^[A-Za-z0-9-_]+$")) {
            log.warn("orderId 형식 오류: {}", orderId);
            model.addAttribute("error", "주문번호 형식이 올바르지 않습니다. 다시 시도해주세요.");
            return "page/pay/fail";
        }
        // 토스페이먼츠 결제 승인 API 호출
        // String secretKey = "test_gsk_DnyRpQWGrNlZDOL6MJbg3Kwv1M9E"; // 테스트 시크릿키
        // String secretKey = "live_gsk_E92LAa5PVb5l0ADKvXaY37YmpXyJ"; // 실제 운영 시크릿키
        String secretKey = "live_sk_kYG57Eba3G6LBJOyoe4l8pWDOxmA"; // 실제 운영 시크릿키
        WebClient webClient = WebClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultHeaders(headers -> headers.setBasicAuth(secretKey, ""))
            // .defaultHeader("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
            .build();
        try {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("paymentKey", paymentKey);
            req.put("orderId", orderId);
            req.put("amount", amount);
            // 승인 API 호출 (에러 응답도 body로 파싱)
            String response = webClient.post()
                .uri("/v1/payments/confirm")
                .bodyValue(req)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class).map(body -> new RuntimeException(body))
                )
                .bodyToMono(String.class)
                .block();
            // 승인 성공 처리 (주문 상태 변경 등)
            Orders order = orderService.selectById(orderId);
            if (order != null) {
                order.setStatus("결제완료");
                orderService.updateById(order);
            }
            model.addAttribute("result", response);

            // 결제 정보 등록
            Payments payment = Payments.builder()
                .method(paymentType)
                .paymentKey(paymentKey)
                .orderNo(order.getNo())
                .amount(amount)
                .status("결제완료")
                .build();

            boolean payResult = paymentService.insert(payment);
            log.info("결제 정보 등록 결과: {}", payResult);

            return "page/pay/success";
        } catch (Exception e) {
            // 토스페이먼츠 에러 응답 body 파싱 (JSON 형태)
            String errorMsg = e.getMessage();
            String errorCode = null;
            String errorMessage = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> errorMap = mapper.readValue(errorMsg, java.util.Map.class);
                errorCode = (String) errorMap.getOrDefault("code", null);
                errorMessage = (String) errorMap.getOrDefault("message", errorMsg);
            } catch (Exception parseEx) {
                errorMessage = errorMsg;
            }
            log.error("토스페이먼츠 결제 승인 실패: code={}, message={}, paymentKey={}, orderId={}, amount={}", errorCode, errorMessage, paymentKey, orderId, amount);

            if( errorCode != null && errorCode.equals("ALREADY_PROCESSED_PAYMENT") ) {
                // 이미 결제된 주문인 경우
                Orders order = orderService.selectById(orderId);
                if (order != null) {
                    model.addAttribute("order", order);
                    model.addAttribute("message", "이미 결제된 주문입니다. 주문 정보를 확인해주세요.");
                    return "page/pay/success";
                }
                model.addAttribute("error", "이미 결제된 주문입니다. 주문 정보를 확인해주세요.");
                return "page/pay/fail";
            }

            model.addAttribute("error", "결제 승인에 실패했습니다. [" + (errorCode != null ? errorCode : "") + "] " + (errorMessage != null ? errorMessage : ""));
            return "page/pay/fail";
        }
    }

    // 결제 실패 콜백
    @GetMapping("/fail")
    public String payFail(@ModelAttribute String orderId) {
        return "page/pay/fail";
    }


    // 현금 결제 성공 페이지
    @GetMapping("/cash/success")
    public String cashPaySuccess(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "page/pay/cash/success";
    }

    // 현금 결제 실패 페이지
    @GetMapping("/cash/fail")
    public String cashPayFail(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "page/pay/cash/fail";
    }

    // 코인 결제 성공 페이지
    @GetMapping("/coin/success")
    public String coinPaySuccess(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "page/pay/coin/success";
    }

    // 코인 결제 실패 페이지
    @GetMapping("/coin/fail")
    public String coinPayFail(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "page/pay/coin/fail";
    }

}
