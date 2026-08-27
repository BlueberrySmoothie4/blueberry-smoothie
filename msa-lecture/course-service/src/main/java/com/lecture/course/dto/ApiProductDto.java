package com.lecture.course.dto;

import com.lecture.course.entity.ApiProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ApiProductDto {

    // API 상품 등록 요청
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "API 상품명은 필수입니다")
        private String title;

        private String description;

        @NotNull(message = "카테고리는 필수입니다")
        private ApiProduct.Category category;

        @NotNull(message = "월 구독료는 필수입니다")
        @PositiveOrZero(message = "월 구독료는 0 이상이어야 합니다")
        private BigDecimal price;
    }

    // API 상품 응답
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiProductResponse {
        private Long id;
        private String title;
        private String description;
        private ApiProduct.Category category;
        private BigDecimal price;
        private Long providerId;
        // user-service에서 해석한 제공사 이름 (조회 실패 시 null)
        private String providerName;
        private Integer subscriberCount;
        private ApiProduct.Status status;
        private LocalDateTime createdAt;

        public static ApiProductResponse from(ApiProduct product) {
            return from(product, null);
        }

        public static ApiProductResponse from(ApiProduct product, String providerName) {
            return ApiProductResponse.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .description(product.getDescription())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .providerId(product.getProviderId())
                    .providerName(providerName)
                    .subscriberCount(product.getSubscriberCount())
                    .status(product.getStatus())
                    .createdAt(product.getCreatedAt())
                    .build();
        }
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

    // 추천 서비스용 응답 (카테고리 기반 미구독 상품 목록)
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendResponse {
        private List<ApiProductResponse> products;
        private ApiProduct.Category category;
    }
}
