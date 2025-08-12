package com.falcon.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "nowpayments")
public class NowPaymentsConfig {
    private String apiKey = "F8AR5QN-XET4YMK-JV0ZAKF-W9YK5AM";
    private String baseUrl = "https://api-sandbox.nowpayments.io/v1"; // sandbox URL로 변경
    private boolean sandboxMode = true;
}
