package com.lecture.enrollment.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * Payment Service: 결제 요청 (동기 REST)
     */
    public PaymentResult requestPayment(Long userId, Long productId, BigDecimal amount) {
        try {
            PaymentRequest request = new PaymentRequest(userId, productId, amount);

            PaymentResult result = webClientBuilder.build()
                    .post()
                    .uri("http://payment-service:8084/api/payments/internal/request")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentResult.class)
                    .block();

            log.info("[PaymentServiceClient] 결제 요청 완료 - userId: {}, productId: {}, result: {}",
                    userId, productId, result != null ? result.getStatus() : "null");

            return result;
        } catch (Exception e) {
            log.error("[PaymentServiceClient] 결제 요청 실패 - userId: {}, productId: {}, error: {}",
                    userId, productId, e.getMessage(), e);
            throw new RuntimeException("Payment Service 연결 실패");
        }
    }

    @Getter
    @NoArgsConstructor
    static class PaymentRequest {
        private Long userId;
        private Long productId;
        private BigDecimal amount;

        PaymentRequest(Long userId, Long productId, BigDecimal amount) {
            this.userId = userId;
            this.productId = productId;
            this.amount = amount;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PaymentResult {
        private Long paymentId;
        private String status; // COMPLETED / FAILED
    }
}