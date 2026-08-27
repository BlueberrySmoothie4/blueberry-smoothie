package com.lecture.course.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * User Service REST 클라이언트
 * - api_products 는 provider_id(FK)만 보관하므로, 화면에 보여줄 제공사 이름은 여기서 해석한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.user-service.url}")
    private String userServiceUrl;

    /**
     * GET /api/users/internal/{id} - 제공사 이름 조회
     *
     * 제공사 이름은 부가 정보라 실패해도 상품 조회 자체를 막지 않는다.
     * (조회 실패 시 null → 프론트가 '제공사 정보 없음'으로 폴백)
     */
    public String getProviderName(Long providerId) {
        if (providerId == null) return null;

        try {
            Map<String, Object> user = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/internal/{id}", providerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (user == null) return null;

            // 래퍼 응답({success, message, data})과 단건 응답 둘 다 처리
            Object data = user.get("data");
            if (data instanceof Map<?, ?> dataMap) {
                return (String) dataMap.get("name");
            }
            return (String) user.get("name");
        } catch (Exception e) {
            log.warn("[UserServiceClient] 제공사 이름 조회 실패 - providerId: {}, error: {}",
                    providerId, e.getMessage());
            return null;
        }
    }
}
