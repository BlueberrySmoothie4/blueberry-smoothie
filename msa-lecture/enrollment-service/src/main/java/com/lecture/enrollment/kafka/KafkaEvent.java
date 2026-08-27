package com.lecture.enrollment.kafka;

import lombok.*;

/**
 * Kafka 이벤트 메시지 DTO
 *
 * 주의: 여기 필드명이 곧 JSON 키다.
 * courseId → productId 치환 시 recommend-service(Python) consumer.py의
 * event.get("productId") 도 반드시 같은 타이밍에 바꿔야 조용히 None이 되지 않는다.
 */
public class KafkaEvent {

    /**
     * Payment Service → Subscription Service
     * 결제 완료 이벤트 수신
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentCompletedEvent {
        private Long paymentId;
        private Long userId;
        private Long productId;
        private String status; // COMPLETED
    }

    /**
     * Subscription Service → Recommend Service
     * 구독 활성화 완료 이벤트 발행
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionCompletedEvent {
        private Long subscriptionId;
        private Long userId;
        private Long productId;
    }
}
