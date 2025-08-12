package com.falcon.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/pay/crypto")
@RequiredArgsConstructor
public class CryptoPaymentController {

    /**
     * 암호화폐 결제 성공 페이지
     */
    @GetMapping("/success")
    public String success(@RequestParam(required = false) String orderId) {
        log.info("Crypto payment success page accessed for order: {}", orderId);
        return "page/pay/crypto/success";
    }

    /**
     * 암호화폐 결제 취소 페이지
     */
    @GetMapping("/cancel")
    public String cancel(@RequestParam(required = false) String orderId) {
        log.info("Crypto payment cancel page accessed for order: {}", orderId);
        return "page/pay/crypto/cancel";
    }
}
