/**
 * 백엔드 enum과 화면 표기를 잇는 단일 소스.
 * - ApiProduct.Category (course-service)
 * - ApiProduct.Status
 * - Subscription.Status (enrollment-service)
 * enum 값이 바뀌면 이 파일만 수정한다.
 */

/** ApiProduct.Category — 백엔드 enum 값과 1:1 */
export const CATEGORIES = [
  {
    value: "VISION",
    label: "비전",
    tone: "tone-violet",
    badge: "badge-purple",
  },
  { value: "SPEECH", label: "음성", tone: "tone-blue", badge: "badge-blue" },
  { value: "LANGUAGE", label: "언어", tone: "tone-teal", badge: "badge-teal" },
  {
    value: "DOCUMENT",
    label: "문서",
    tone: "tone-amber",
    badge: "badge-amber",
  },
  {
    value: "GENERATIVE",
    label: "생성형",
    tone: "tone-pink",
    badge: "badge-pink",
  },
  { value: "FORECAST", label: "예측", tone: "tone-blue", badge: "badge-blue" },
  {
    value: "ANOMALY",
    label: "이상탐지",
    tone: "tone-violet",
    badge: "badge-purple",
  },
  { value: "OTHER", label: "기타", tone: "tone-gray", badge: "badge-gray" },
];

export const CATEGORY_LABELS = CATEGORIES.map((c) => c.label);

const FALLBACK_STYLE = { tone: "tone-gray", badge: "badge-gray" };

/**
 * enum 값(VISION)과 한글 라벨(비전) 양쪽으로 찾을 수 있어야 한다.
 * 상품 조회 API는 enum을, 구독 목록의 product 요약은 한글 라벨을 내려준다.
 */
const BY_KEY = CATEGORIES.reduce((acc, c) => {
  acc[c.value] = c;
  acc[c.label] = c;
  return acc;
}, {});

export function categoryLabel(category) {
  return BY_KEY[category]?.label || category || "기타";
}

export function categoryStyle(category) {
  return BY_KEY[category] || FALLBACK_STYLE;
}

/** ApiProduct.Status */
export const PRODUCT_STATUS = {
  PUBLISHED: { label: "운영 중", pill: "active" },
  DEPRECATED: { label: "중단됨", pill: "inactive" },
};

export function productStatus(status) {
  return (
    PRODUCT_STATUS[status] || {
      label: status || "알 수 없음",
      pill: "inactive",
    }
  );
}

/** Subscription.Status — 결제 완료 Kafka 이벤트를 받으면 PENDING → ACTIVE */
export const SUBSCRIPTION_STATUS = {
  PENDING: { label: "결제 처리 중", pill: "pending" },
  ACTIVE: { label: "구독 중", pill: "active" },
  CANCELLED: { label: "해지됨", pill: "inactive" },
};

export function subscriptionStatus(status) {
  return (
    SUBSCRIPTION_STATUS[status] || {
      label: status || "알 수 없음",
      pill: "inactive",
    }
  );
}
