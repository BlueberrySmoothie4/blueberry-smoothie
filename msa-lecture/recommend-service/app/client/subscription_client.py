import httpx
import logging
from app.config.settings import settings
from app.model.schemas import SubscriptionHistoryResponse

logger = logging.getLogger(__name__)


class SubscriptionServiceClient:
    """
    Subscription Service(enrollment-service 컨테이너) REST 클라이언트
    - 구독 이력 조회 (ACTIVE 상품 ID 목록)
    """

    def __init__(self):
        self.base_url = settings.subscription_service_url

    async def get_subscription_history(self, user_id: int) -> SubscriptionHistoryResponse:
        """
        GET /subscriptions/internal/history/{userId}
        사용자가 구독 중인 API 상품 ID 목록 조회
        """
        url = f"{self.base_url}/api/subscriptions/internal/history/{user_id}"
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(url)
                response.raise_for_status()
                data = response.json()
                return SubscriptionHistoryResponse(**data)
        except httpx.HTTPError as e:
            logger.error(f"[SubscriptionClient] 구독 이력 조회 실패 - userId: {user_id}, error: {e}")
            # 실패 시 빈 이력 반환 (추천 서비스는 비핵심 기능)
            return SubscriptionHistoryResponse(userId=user_id, activeProductIds=[])


subscription_client = SubscriptionServiceClient()
