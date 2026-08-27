package com.lecture.enrollment.service;

import com.lecture.enrollment.entity.Subscription;
import com.lecture.enrollment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionWriteService {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * 반드시 독립 트랜잭션으로 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Subscription createPendingSubscription(Long userId, Long productId) {

        Subscription subscription = subscriptionRepository.save(
                Subscription.builder()
                        .userId(userId)
                        .productId(productId)
                        .build()
        );

        log.info("[SubscriptionWriteService] PENDING subscription 생성 완료 - subscriptionId: {}, userId: {}, productId: {}",
                subscription.getId(), userId, productId);

        return subscription;
    }
}
