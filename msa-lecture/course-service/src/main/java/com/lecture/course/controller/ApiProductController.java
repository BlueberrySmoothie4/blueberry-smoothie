package com.lecture.course.controller;

import com.lecture.course.dto.ApiProductDto;
import com.lecture.course.entity.ApiProduct;
import com.lecture.course.service.ApiProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ApiProductController {

    private final ApiProductService apiProductService;

    /**
     * POST /products - API 상품 등록 (제공사만)
     * Gateway에서 전달한 X-User-Id 헤더로 제공사 ID 추출
     */
    @PostMapping
    public ResponseEntity<ApiProductDto.ApiResponse<ApiProductDto.ApiProductResponse>> createProduct(
            @Valid @RequestBody ApiProductDto.CreateRequest request,
            @RequestHeader("X-User-Id") Long providerId) {

        ApiProductDto.ApiProductResponse response = apiProductService.createProduct(request, providerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiProductDto.ApiResponse.success(response));
    }

    /**
     * GET /products - 전체 API 상품 목록
     */
    @GetMapping
    public ResponseEntity<ApiProductDto.ApiResponse<List<ApiProductDto.ApiProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(
                ApiProductDto.ApiResponse.success(apiProductService.getAllProducts())
        );
    }

    /**
     * GET /products/{id} - API 상품 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiProductDto.ApiResponse<ApiProductDto.ApiProductResponse>> getProduct(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiProductDto.ApiResponse.success(apiProductService.getProduct(id))
        );
    }

    /**
     * GET /products/category/{category} - 카테고리별 API 상품
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiProductDto.ApiResponse<List<ApiProductDto.ApiProductResponse>>> getProductsByCategory(
            @PathVariable ApiProduct.Category category) {
        return ResponseEntity.ok(
                ApiProductDto.ApiResponse.success(apiProductService.getProductsByCategory(category))
        );
    }

    /**
     * GET /products/internal/exists/{id} - 상품 존재 여부 (Subscription Service 호출)
     */
    @GetMapping("/internal/exists/{id}")
    public ResponseEntity<Boolean> existsProduct(@PathVariable Long id) {
        return ResponseEntity.ok(apiProductService.existsProduct(id));
    }

    /**
     * GET /products/internal/{id} - 상품 상세 조회 (Subscription Service 내부 호출용)
     * - 내 구독 목록 응답 조립 시 사용
     * - 래퍼 없이 ApiProductResponse만 직접 반환
     */
    @GetMapping("/internal/{id}")
    public ResponseEntity<ApiProductDto.ApiProductResponse> getProductInternal(@PathVariable Long id) {
        return ResponseEntity.ok(apiProductService.getProduct(id));
    }

    /**
     * POST /products/internal/{id}/subscriber-count - 구독자 수 증가 (Subscription Service 호출)
     */
    @PostMapping("/internal/{id}/subscriber-count")
    public ResponseEntity<Void> increaseSubscriberCount(@PathVariable Long id) {
        apiProductService.increaseSubscriberCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /products/internal/recommend - 추천 서비스용 미구독 상품 조회
     * category: 카테고리, excludeIds: 이미 구독한 상품 ID 목록
     */
    @GetMapping("/internal/recommend")
    public ResponseEntity<List<ApiProductDto.ApiProductResponse>> getRecommendProducts(
            @RequestParam ApiProduct.Category category,
            @RequestParam(defaultValue = "") List<Long> excludeIds) {
        return ResponseEntity.ok(apiProductService.getRecommendProducts(category, excludeIds));
    }
}
