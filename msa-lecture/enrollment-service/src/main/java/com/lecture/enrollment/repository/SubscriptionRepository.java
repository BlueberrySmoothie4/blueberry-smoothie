package com.lecture.enrollment.repository;

import com.lecture.enrollment.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByUserIdAndStatus(Long userId, Subscription.Status status);

    Optional<Subscription> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // 구독 활성화(ACTIVE)된 상품 ID 목록 - 추천 서비스용
    List<Subscription> findByUserIdAndStatusIn(Long userId, List<Subscription.Status> statuses);
}
