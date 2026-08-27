import httpx
import logging
from typing import List
from app.config.settings import settings
from app.model.schemas import ApiProductResponse, ProductCategory

logger = logging.getLogger(__name__)


class ProductServiceClient:
    """
    Product Service(course-service 컨테이너) REST 클라이언트
    - 카테고리별 미구독 API 상품 목록 조회
    """

    def __init__(self):
        self.base_url = settings.product_service_url

    async def get_recommend_products(
        self,
        category: ProductCategory,
        exclude_ids: List[int]
    ) -> List[ApiProductResponse]:
        """
        GET /products/internal/recommend
        카테고리 기반 미구독 상품 목록 조회 (구독자 수 기준 정렬)
        """
        url = f"{self.base_url}/api/products/internal/recommend"
        params = {"category": category.value}
        if exclude_ids:
            params["excludeIds"] = ",".join(str(i) for i in exclude_ids)

        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(url, params=params)
                response.raise_for_status()
                return [ApiProductResponse(**p) for p in response.json()]
        except httpx.HTTPError as e:
            logger.error(f"[ProductClient] 추천 상품 조회 실패 - category: {category}, error: {e}")
            return []

    async def get_all_products(self) -> List[ApiProductResponse]:
        """
        GET /products - 전체 API 상품 목록 조회
        구독 이력이 없는 신규 사용자 추천용
        """
        url = f"{self.base_url}/api/products"
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(url)
                response.raise_for_status()
                data = response.json()
                products = data.get("data", [])
                return [ApiProductResponse(**p) for p in products]
        except httpx.HTTPError as e:
            logger.error(f"[ProductClient] 전체 상품 조회 실패 - error: {e}")
            return []


product_client = ProductServiceClient()
