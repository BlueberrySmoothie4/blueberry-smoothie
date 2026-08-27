package com.lecture.course.repository;

import com.lecture.course.entity.ApiProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiProductRepository extends JpaRepository<ApiProduct, Long> {

    // 카테고리별 상품 조회 (추천 서비스 사용)
    List<ApiProduct> findByCategoryAndStatus(ApiProduct.Category category, ApiProduct.Status status);

    // 제공사별 상품 조회
    List<ApiProduct> findByProviderId(Long providerId);

    // 판매 중인 상품 전체 조회
    List<ApiProduct> findByStatus(ApiProduct.Status status);

    // 카테고리별 + 특정 ID 제외 조회 (추천 서비스: 이미 구독한 상품 제외)
    List<ApiProduct> findByCategoryAndStatusAndIdNotIn(
            ApiProduct.Category category,
            ApiProduct.Status status,
            List<Long> excludeIds
    );
}
