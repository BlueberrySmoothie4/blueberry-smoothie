package com.lecture.enrollment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.subscription-completed}")
    private String subscriptionCompletedTopic;

    /**
     * subscription.completed 이벤트 발행
     * → Recommend Service가 수신하여 추천 갱신
     */
    public void publishSubscriptionCompleted(KafkaEvent.SubscriptionCompletedEvent event) {
        log.info("[Kafka Producer] subscription.completed 발행 - subscriptionId: {}, userId: {}, productId: {}",
                event.getSubscriptionId(), event.getUserId(), event.getProductId());

        kafkaTemplate.send(subscriptionCompletedTopic, String.valueOf(event.getUserId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka Producer] subscription.completed 발행 실패: {}", ex.getMessage());
                    } else {
                        log.info("[Kafka Producer] subscription.completed 발행 성공 - offset: {}",
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
