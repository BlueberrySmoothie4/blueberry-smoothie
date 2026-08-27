package com.lecture.enrollment.service;

import com.lecture.enrollment.dto.SubscriptionDto;
import com.lecture.enrollment.entity.Subscription;
import com.lecture.enrollment.kafka.SubscriptionKafkaProducer;
import com.lecture.enrollment.kafka.KafkaEvent;
import com.lecture.enrollment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final SubscriptionKafkaProducer kafkaProducer;
    private final SubscriptionWriteService subscriptionWriteService;

    /**
     * 구독 신청 전체 흐름
     * 1. 상품 존재 확인
     * 2. 중복 구독 확인
     * 3. Subscription 생성 및 즉시 커밋 (PENDING)
     * 4. 결제 요청 (월 구독료 정액제)
     */
    public SubscriptionDto.SubscriptionResponse subscribe(Long userId, Long productId) {
        if (!productServiceClient.existsProduct(productId)) {
            throw new IllegalArgumentException("존재하지 않는 API 상품입니다: " + productId);
        }

        if (subscriptionRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("이미 구독 신청한 API 상품입니다");
        }

        Subscription subscription = subscriptionWriteService.createPendingSubscription(userId, productId);

        paymentServiceClient.requestPayment(userId, productId, BigDecimal.valueOf(99000));

        log.info("[SubscriptionService] 구독 신청 완료 (결제 대기) - subscriptionId: {}", subscription.getId());
        return SubscriptionDto.SubscriptionResponse.from(subscription);
    }

    /**
     * 구독 활성화
     */
    @Transactional
    public void activateSubscription(Long userId, Long productId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "구독 정보를 찾을 수 없습니다 - userId: " + userId + ", productId: " + productId));

        subscription.activate();

        productServiceClient.increaseSubscriberCount(productId);

        kafkaProducer.publishSubscriptionCompleted(
                KafkaEvent.SubscriptionCompletedEvent.builder()
                        .subscriptionId(subscription.getId())
                        .userId(userId)
                        .productId(productId)
                        .build()
        );

        log.info("[SubscriptionService] 구독 활성화 완료 - subscriptionId: {}", subscription.getId());
    }

    /**
     * 사용자 구독 목록 조회
     * - course-service(Product Service)에서 상품 상세 정보를 붙여서 반환
     */
    public List<SubscriptionDto.SubscriptionResponse> getSubscriptionsByUser(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);

        return subscriptions.stream()
                .map(subscription -> {
                    Map<String, Object> productInfo = productServiceClient.getProduct(subscription.getProductId());

                    SubscriptionDto.ProductSummary productSummary = SubscriptionDto.ProductSummary.builder()
                            .id(toLong(productInfo.get("id")))
                            .title((String) productInfo.get("title"))
                            .description((String) productInfo.get("description"))
                            .category(normalizeCategory((String) productInfo.get("category")))
                            .price(toInteger(productInfo.get("price")))
                            .thumbnail((String) productInfo.get("thumbnail"))
                            .providerName(
                                    firstNonNull(
                                            (String) productInfo.get("providerName"),
                                            (String) productInfo.get("vendorName"),
                                            (String) productInfo.get("provider_name")
                                    )
                            )
                            .subscriberCount(toInteger(
                                    firstNonNullObject(
                                            productInfo.get("subscriberCount"),
                                            productInfo.get("subscriber_count")
                                    )
                            ))
                            .build();

                    return SubscriptionDto.SubscriptionResponse.from(subscription, productSummary);
                })
                .collect(Collectors.toList());
    }

    /**
     * 구독 이력 조회 - 추천 서비스용
     */
    public SubscriptionDto.SubscriptionHistoryResponse getSubscriptionHistory(Long userId) {
        List<Long> activeProductIds = subscriptionRepository
                .findByUserIdAndStatus(userId, Subscription.Status.ACTIVE)
                .stream()
                .map(Subscription::getProductId)
                .collect(Collectors.toList());

        return SubscriptionDto.SubscriptionHistoryResponse.builder()
                .userId(userId)
                .activeProductIds(activeProductIds)
                .build();
    }

    private String normalizeCategory(String category) {
        if (category == null) return null;

        return switch (category) {
            case "VISION" -> "비전";
            case "SPEECH" -> "음성";
            case "LANGUAGE" -> "언어";
            case "DOCUMENT" -> "문서";
            case "GENERATIVE" -> "생성형";
            case "FORECAST" -> "예측";
            case "ANOMALY" -> "이상탐지";
            case "OTHER" -> "기타";
            default -> category;
        };
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

    private String firstNonNull(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private Object firstNonNullObject(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
