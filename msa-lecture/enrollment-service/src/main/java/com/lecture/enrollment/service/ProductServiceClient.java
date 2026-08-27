package com.lecture.enrollment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * Product Service(course-service 컨테이너): API 상품 존재 여부 확인 (동기 REST)
     */
    public boolean existsProduct(Long productId) {
        try {
            Boolean exists = webClientBuilder.build()
                    .get()
                    .uri("http://course-service/api/products/internal/exists/{id}", productId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("[ProductServiceClient] 상품 존재 확인 실패 - productId: {}, error: {}",
                    productId, e.getMessage());
            throw new RuntimeException("Product Service 연결 실패");
        }
    }

    /**
     * Product Service: API 상품 상세 조회
     * - 내 구독 목록 응답에 product 정보를 붙일 때 사용
     * - course-service 쪽에 GET /api/products/internal/{id} 엔드포인트가 있어야 함
     */
    public Map<String, Object> getProduct(Long productId) {
        try {
            Map<String, Object> responseBody = webClientBuilder.build()
                    .get()
                    .uri("http://course-service/api/products/internal/{id}", productId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (responseBody == null) {
                throw new RuntimeException("Product Service 응답 본문이 비어 있습니다.");
            }

            log.info("[ProductServiceClient] 상품 상세 조회 성공 - productId: {}", productId);
            log.debug("[ProductServiceClient] 상품 상세 응답 - productId: {}, body: {}", productId, responseBody);

            /*
             * 응답 형태가 다음 둘 중 하나일 수 있으므로 둘 다 처리
             *
             * 1) 래퍼 응답
             * {
             *   "success": true,
             *   "message": "성공",
             *   "data": { ...product fields... }
             * }
             *
             * 2) 바로 상품 객체 반환
             * {
             *   "id": 1,
             *   "title": "...",
             *   ...
             * }
             */
            Object data = responseBody.get("data");
            if (data instanceof Map<?, ?> dataMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> productMap = (Map<String, Object>) dataMap;
                return productMap;
            }

            return responseBody;
        } catch (Exception e) {
            log.error("[ProductServiceClient] 상품 상세 조회 실패 - productId: {}, error: {}",
                    productId, e.getMessage());
            throw new RuntimeException("Product Service 상품 상세 조회 실패");
        }
    }

    /**
     * Product Service: 구독자 수 증가 (구독 활성화 시 호출)
     */
    public void increaseSubscriberCount(Long productId) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://course-service/api/products/internal/{id}/subscriber-count", productId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("[ProductServiceClient] 구독자 수 증가 완료 - productId: {}", productId);
        } catch (Exception e) {
            log.error("[ProductServiceClient] 구독자 수 증가 실패 - productId: {}, error: {}",
                    productId, e.getMessage());
        }
    }
}
