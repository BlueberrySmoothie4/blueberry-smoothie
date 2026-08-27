package com.lecture.enrollment.dto;

import com.lecture.enrollment.entity.Subscription;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SubscriptionDto {

    // 구독 신청 요청
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscribeRequest {
        @NotNull(message = "API 상품 ID는 필수입니다")
        private Long productId;
    }

    // API 상품 요약 정보 (내 구독 목록 표시용)
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSummary {
        private Long id;
        private String title;
        private String description;
        private String category;
        private Integer price;
        private String thumbnail;
        private String providerName;
        private Integer subscriberCount;
    }

    // 구독 응답
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionResponse {
        private Long id;
        private Long userId;
        private Long productId;
        private Subscription.Status status;
        private LocalDateTime createdAt;

        // 추가
        private ProductSummary product;

        public static SubscriptionResponse from(Subscription subscription) {
            return SubscriptionResponse.builder()
                    .id(subscription.getId())
                    .userId(subscription.getUserId())
                    .productId(subscription.getProductId())
                    .status(subscription.getStatus())
                    .createdAt(subscription.getCreatedAt())
                    .build();
        }

        public static SubscriptionResponse from(Subscription subscription, ProductSummary product) {
            return SubscriptionResponse.builder()
                    .id(subscription.getId())
                    .userId(subscription.getUserId())
                    .productId(subscription.getProductId())
                    .status(subscription.getStatus())
                    .createdAt(subscription.getCreatedAt())
                    .product(product)
                    .build();
        }
    }

    // 추천 서비스용: 구독 이력 조회 응답
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionHistoryResponse {
        private Long userId;
        private List<Long> activeProductIds;
    }

    // 공통 API 응답 래퍼
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message("성공")
                    .data(data)
                    .build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .build();
        }
    }
}
