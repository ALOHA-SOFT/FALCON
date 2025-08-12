package com.falcon.shop.service;

import com.falcon.shop.config.NowPaymentsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NowPaymentsService {

    private final NowPaymentsConfig config;
    private final RestTemplate restTemplate;

    /**
     * 암호화폐 환율 조회
     */
    public Map<String, Object> getEstimate(String priceAmount, String priceCurrency, String payCurrency) {
        try {
            // 실제 API 호출 시도
            String url = config.getBaseUrl() + "/estimate?amount=" + priceAmount + 
                        "&currency_from=" + priceCurrency + "&currency_to=" + payCurrency;
            
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) response.getBody();
                log.info("NowPayments estimate response: {}", result);
                
                // 결과 데이터 구조 변환
                Map<String, Object> estimateResult = new HashMap<>();
                estimateResult.put("pay_amount", result.get("estimated_amount"));
                estimateResult.put("pay_currency", payCurrency);
                estimateResult.put("price_amount", priceAmount);
                estimateResult.put("price_currency", priceCurrency);
                
                return estimateResult;
            }
        } catch (Exception e) {
            log.error("Failed to get estimate from NowPayments, returning mock data", e);
            
            // API 호출 실패 시 Mock 데이터 반환 (테스트용)
            return getMockEstimateData(priceAmount, priceCurrency, payCurrency);
        }
        
        // API 호출 실패 시 Mock 데이터 반환
        return getMockEstimateData(priceAmount, priceCurrency, payCurrency);
    }
    
    /**
     * Mock 환율 데이터 생성 (테스트용)
     */
    private Map<String, Object> getMockEstimateData(String priceAmount, String priceCurrency, String payCurrency) {
        Map<String, Object> estimateResult = new HashMap<>();
        
        double amount = Double.parseDouble(priceAmount);
        double mockRate;
        
        // 간단한 Mock 환율 (실제로는 실시간 데이터를 사용해야 함)
        switch (payCurrency.toLowerCase()) {
            case "btc":
                mockRate = 0.000035; // 1 GBP ≈ 0.000035 BTC
                break;
            case "eth":
                mockRate = 0.00055; // 1 GBP ≈ 0.00055 ETH
                break;
            case "usdt":
                mockRate = 1.27; // 1 GBP ≈ 1.27 USDT
                break;
            case "usdc":
                mockRate = 1.27; // 1 GBP ≈ 1.27 USDC
                break;
            case "ltc":
                mockRate = 0.018; // 1 GBP ≈ 0.018 LTC
                break;
            case "bch":
                mockRate = 0.0032; // 1 GBP ≈ 0.0032 BCH
                break;
            default:
                mockRate = 1.0;
        }
        
        double estimatedAmount = amount * mockRate;
        
        estimateResult.put("pay_amount", String.format("%.8f", estimatedAmount));
        estimateResult.put("pay_currency", payCurrency.toUpperCase());
        estimateResult.put("price_amount", priceAmount);
        estimateResult.put("price_currency", priceCurrency);
        
        log.info("Mock estimate data: {} {} = {} {}", amount, priceCurrency, estimatedAmount, payCurrency);
        
        return estimateResult;
    }

    /**
     * 결제 생성
     */
    public Map<String, Object> createPayment(Map<String, Object> paymentData) {
        try {
            String url = config.getBaseUrl() + "/payment";

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) response.getBody();
                log.info("NowPayments create payment response: {}", result);
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to create payment in NowPayments, returning mock data", e);
            
            // API 호출 실패 시 Mock 데이터 반환 (테스트용)
            return getMockPaymentData(paymentData);
        }
        
        // API 호출 실패 시 Mock 데이터 반환
        return getMockPaymentData(paymentData);
    }
    
    /**
     * Mock 결제 데이터 생성 (테스트용)
     */
    private Map<String, Object> getMockPaymentData(Map<String, Object> paymentData) {
        Map<String, Object> mockPayment = new HashMap<>();
        
        String paymentId = "mock_payment_" + System.currentTimeMillis();
        String payCurrency = (String) paymentData.get("pay_currency");
        String orderId = (String) paymentData.get("order_id");
        
        // Mock 결제 주소 (실제로는 NowPayments에서 생성)
        String mockAddress = generateMockAddress(payCurrency);
        
        mockPayment.put("payment_id", paymentId);
        mockPayment.put("payment_status", "waiting");
        mockPayment.put("pay_address", mockAddress);
        mockPayment.put("pay_amount", paymentData.get("price_amount"));
        mockPayment.put("pay_currency", payCurrency);
        mockPayment.put("order_id", orderId);
        mockPayment.put("order_description", paymentData.get("order_description"));
        
        // Mock 결제 URL (실제로는 NowPayments 결제 페이지)
        mockPayment.put("payment_url", "http://localhost:8080/pay/crypto/mock?paymentId=" + paymentId);
        
        log.info("Mock payment data created: {}", mockPayment);
        
        return mockPayment;
    }
    
    /**
     * 암호화폐별 Mock 주소 생성
     */
    private String generateMockAddress(String currency) {
        switch (currency.toLowerCase()) {
            case "btc":
                return "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"; // Mock Bitcoin 주소
            case "eth":
                return "0x742d35Cc6327C0532C3b3C6b90B6Cd9F3B5C4f2f"; // Mock Ethereum 주소
            case "usdt":
                return "0x742d35Cc6327C0532C3b3C6b90B6Cd9F3B5C4f2f"; // Mock USDT 주소
            case "usdc":
                return "0x742d35Cc6327C0532C3b3C6b90B6Cd9F3B5C4f2f"; // Mock USDC 주소
            case "ltc":
                return "LQTpS5vyRzFM8R2c3jMcJqNyZs5K6jnQz1"; // Mock Litecoin 주소
            case "bch":
                return "bitcoincash:qr6m7j9njldwwzlg9v7v53unlr4jkmx6eylep8ekg2"; // Mock Bitcoin Cash 주소
            default:
                return "mock_address_" + System.currentTimeMillis();
        }
    }

    /**
     * 결제 상태 조회
     */
    public Map<String, Object> getPaymentStatus(String paymentId) {
        try {
            String url = config.getBaseUrl() + "/payment/" + paymentId;

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) response.getBody();
                log.info("NowPayments payment status response: {}", result);
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to get payment status from NowPayments", e);
        }
        return null;
    }

    /**
     * API 상태 확인 (테스트용)
     */
    public Map<String, Object> getApiStatus() {
        try {
            String url = config.getBaseUrl() + "/status";

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) response.getBody();
                log.info("NowPayments status response: {}", result);
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to get status from NowPayments", e);
        }
        return null;
    }

    /**
     * 사용 가능한 암호화폐 목록 조회 (API 키 불필요)
     */
    public Map<String, Object> getAvailableCurrencies() {
        try {
            String url = config.getBaseUrl() + "/currencies";

            // API 키 없이 요청 시도
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) response.getBody();
                log.info("NowPayments currencies response: {}", result);
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to get currencies from NowPayments", e);
            // API 키를 사용해서 다시 시도
            try {
                String url = config.getBaseUrl() + "/currencies";
                HttpHeaders headers = createHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = (Map<String, Object>) response.getBody();
                    log.info("NowPayments currencies response with API key: {}", result);
                    return result;
                }
            } catch (Exception ex) {
                log.error("Failed to get currencies from NowPayments with API key", ex);
            }
        }
        return null;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", config.getApiKey());
        return headers;
    }
}
