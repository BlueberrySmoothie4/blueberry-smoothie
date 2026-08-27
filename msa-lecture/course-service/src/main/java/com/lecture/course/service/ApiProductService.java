package com.lecture.course.service;

import com.lecture.course.dto.ApiProductDto;
import com.lecture.course.entity.ApiProduct;
import com.lecture.course.repository.ApiProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiProductService {

    private final ApiProductRepository apiProductRepository;
    private final UserServiceClient userServiceClient;

    /**
     * API 상품 등록 (제공사만 가능 - SecurityConfig에서 role 검증)
     */
    @Transactional
    public ApiProductDto.ApiProductResponse createProduct(ApiProductDto.CreateRequest request, Long providerId) {
        ApiProduct product = ApiProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .providerId(providerId)
                .build();

        ApiProduct saved = apiProductRepository.save(product);
        return ApiProductDto.ApiProductResponse.from(saved, userServiceClient.getProviderName(providerId));
    }

    /**
     * API 상품 단건 조회
     */
    public ApiProductDto.ApiProductResponse getProduct(Long id) {
        ApiProduct product = findProductById(id);
        return ApiProductDto.ApiProductResponse.from(
                product, userServiceClient.getProviderName(product.getProviderId()));
    }

    /**
     * 판매 중인 상품 전체 목록 조회
     */
    public List<ApiProductDto.ApiProductResponse> getAllProducts() {
        return toResponses(apiProductRepository.findByStatus(ApiProduct.Status.PUBLISHED));
    }

    /**
     * 카테고리별 상품 조회
     */
    public List<ApiProductDto.ApiProductResponse> getProductsByCategory(ApiProduct.Category category) {
        return toResponses(apiProductRepository.findByCategoryAndStatus(category, ApiProduct.Status.PUBLISHED));
    }

    /**
     * 상품 존재 여부 확인 (Subscription Service → Product Service REST 호출용)
     */
    public boolean existsProduct(Long id) {
        return apiProductRepository.existsById(id);
    }

    /**
     * 구독자 수 증가 (Subscription Service 구독 활성화 시 호출)
     */
    @Transactional
    public void increaseSubscriberCount(Long productId) {
        ApiProduct product = findProductById(productId);
        product.increaseSubscriberCount();
    }

    /**
     * 추천 서비스용: 카테고리별 미구독 상품 조회
     * - excludeProductIds: 이미 구독한 상품 ID 목록
     */
    public List<ApiProductDto.ApiProductResponse> getRecommendProducts(
            ApiProduct.Category category, List<Long> excludeProductIds) {

        List<ApiProduct> products = excludeProductIds.isEmpty()
                ? apiProductRepository.findByCategoryAndStatus(category, ApiProduct.Status.PUBLISHED)
                : apiProductRepository.findByCategoryAndStatusAndIdNotIn(
                        category, ApiProduct.Status.PUBLISHED, excludeProductIds);

        // 구독자 수 기준 내림차순 정렬
        return toResponses(products.stream()
                .sorted((a, b) -> b.getSubscriberCount() - a.getSubscriberCount())
                .collect(Collectors.toList()));
    }

    /**
     * 목록 응답 변환 - 제공사 이름을 붙인다.
     * 상품 건수만큼 user-service를 부르지 않도록 providerId 중복은 한 번만 조회한다.
     */
    private List<ApiProductDto.ApiProductResponse> toResponses(List<ApiProduct> products) {
        Map<Long, String> providerNames = new HashMap<>();
        products.stream()
                .map(ApiProduct::getProviderId)
                .distinct()
                .forEach(id -> providerNames.put(id, userServiceClient.getProviderName(id)));

        return products.stream()
                .map(p -> ApiProductDto.ApiProductResponse.from(p, providerNames.get(p.getProviderId())))
                .collect(Collectors.toList());
    }

    private ApiProduct findProductById(Long id) {
        return apiProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API 상품을 찾을 수 없습니다: " + id));
    }
}
