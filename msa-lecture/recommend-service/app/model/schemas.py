from pydantic import BaseModel
from typing import List, Optional
from enum import Enum
from decimal import Decimal
from datetime import datetime


class ProductCategory(str, Enum):
    VISION = "VISION"           # 이미지·영상 인식
    SPEECH = "SPEECH"           # 음성
    LANGUAGE = "LANGUAGE"       # 자연어 처리
    DOCUMENT = "DOCUMENT"       # 문서 처리
    GENERATIVE = "GENERATIVE"   # 생성형
    FORECAST = "FORECAST"       # 예측·시계열
    ANOMALY = "ANOMALY"         # 이상 탐지
    OTHER = "OTHER"


class ApiProductResponse(BaseModel):
    id: int
    title: str
    description: Optional[str] = None
    category: ProductCategory
    price: Decimal
    providerId: int
    providerName: Optional[str] = None
    subscriberCount: int
    status: str
    createdAt: Optional[datetime] = None


class SubscriptionHistoryResponse(BaseModel):
    userId: int
    activeProductIds: List[int]


class RecommendResponse(BaseModel):
    userId: int
    recommendedProducts: List[ApiProductResponse]
    basedOnCategory: Optional[ProductCategory] = None
    message: str


class ApiResponse(BaseModel):
    success: bool
    message: str
    data: Optional[dict] = None
