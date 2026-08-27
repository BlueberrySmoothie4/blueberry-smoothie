import logging
from collections import Counter
from typing import List, Optional

from app.client.product_client import product_client
from app.client.subscription_client import subscription_client
from app.model.schemas import ProductCategory, ApiProductResponse, RecommendResponse

logger = logging.getLogger(__name__)


class RecommendService:
    """
    규칙 기반 API 상품 추천 서비스

    추천 규칙:
    1. 사용자가 구독 중인 상품의 카테고리 분석
    2. 가장 많이 구독한 카테고리 선택 (최빈 카테고리)
    3. 해당 카테고리에서 미구독 상품 조회 (= 멀티벤더 비교·대체재 탐색)
    4. 구독자 수 기준 내림차순 정렬하여 반환
    5. 구독 이력 없으면 전체 상품 중 인기순 반환
    """

    MAX_RECOMMEND_COUNT = 5  # 최대 추천 상품 수

    async def get_recommendations(self, user_id: int) -> RecommendResponse:
        logger.info(f"[RecommendService] 추천 시작 - userId: {user_id}")

        # 1. 구독 이력 조회
        history = await subscription_client.get_subscription_history(user_id)
        active_product_ids = history.activeProductIds

        # 2. 구독 이력 없는 신규 사용자 처리
        if not active_product_ids:
            return await self._recommend_for_new_user(user_id)

        # 3. 구독한 상품의 카테고리 분석 → 최빈 카테고리 선택
        dominant_category = await self._find_dominant_category(active_product_ids)
        if not dominant_category:
            return await self._recommend_for_new_user(user_id)

        # 4. 최빈 카테고리 기반 미구독 상품 조회
        recommended = await product_client.get_recommend_products(
            category=dominant_category,
            exclude_ids=active_product_ids
        )

        # 5. 최대 추천 수 제한
        recommended = recommended[:self.MAX_RECOMMEND_COUNT]

        logger.info(f"[RecommendService] 추천 완료 - userId: {user_id}, "
                    f"category: {dominant_category}, count: {len(recommended)}")

        return RecommendResponse(
            userId=user_id,
            recommendedProducts=recommended,
            basedOnCategory=dominant_category,
            message=f"{dominant_category.value} 카테고리 기반 추천 API 상품입니다"
        )

    async def _find_dominant_category(
        self, product_ids: List[int]
    ) -> Optional[ProductCategory]:
        """
        구독한 상품들의 카테고리 분석 → 최빈 카테고리 반환
        Product Service에서 각 상품 정보를 조회하여 카테고리 집계
        """
        all_products = await product_client.get_all_products()
        product_map = {p.id: p for p in all_products}

        categories = [
            product_map[pid].category
            for pid in product_ids
            if pid in product_map
        ]

        if not categories:
            return None

        # Counter로 최빈 카테고리 선택
        most_common = Counter(categories).most_common(1)
        return most_common[0][0] if most_common else None

    async def _recommend_for_new_user(self, user_id: int) -> RecommendResponse:
        """
        신규 사용자: 구독자 수 기준 전체 인기 API 상품 추천
        """
        logger.info(f"[RecommendService] 신규 사용자 추천 - userId: {user_id}")

        all_products = await product_client.get_all_products()
        popular = sorted(
            all_products,
            key=lambda p: p.subscriberCount,
            reverse=True
        )[:self.MAX_RECOMMEND_COUNT]

        return RecommendResponse(
            userId=user_id,
            recommendedProducts=popular,
            basedOnCategory=None,
            message="인기 API 상품 추천입니다"
        )


recommend_service = RecommendService()
