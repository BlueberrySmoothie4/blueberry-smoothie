/**
 * 백엔드 enum과 화면 표기를 잇는 단일 소스.
 * - ApiProduct.Category (course-service)
 * - ApiProduct.Status
 * - Subscription.Status (enrollment-service)
 * - Payment.Status (payment-service)
 * enum 값이 바뀌면 이 파일만 수정한다.
 */

/**
 * 카테고리 글리프.
 * 첫 글자를 사각형에 박는 대신 카테고리가 실제로 하는 일을 그린다.
 * stroke 기반이라 색은 currentColor를 따른다.
 */
const GLYPHS = {
  // 초점 프레임 안의 피사체 — 인식/검출
  VISION: `<circle cx="12" cy="12" r="3.2"/><path d="M3 8.5V5.6A2.6 2.6 0 0 1 5.6 3H8.5"/><path d="M15.5 3h2.9A2.6 2.6 0 0 1 21 5.6v2.9"/><path d="M21 15.5v2.9a2.6 2.6 0 0 1-2.6 2.6h-2.9"/><path d="M8.5 21H5.6A2.6 2.6 0 0 1 3 18.4v-2.9"/>`,
  // 파형
  SPEECH: `<path d="M3 10.2v3.6"/><path d="M7.5 6.6v10.8"/><path d="M12 3.4v17.2"/><path d="M16.5 7.6v8.8"/><path d="M21 10.6v2.8"/>`,
  // 번역
  LANGUAGE: `<path d="M3.4 6.6h9.2"/><path d="M8 4.1v2.5c0 4.1-1.9 7.2-4.6 8.7"/><path d="M6.1 11.6c1.4 2.3 3.3 3.9 5.5 4.7"/><path d="M12.4 20.6l4.3-10.2 4.3 10.2"/><path d="M14.1 17h5.2"/>`,
  // 문서
  DOCUMENT: `<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5"/><path d="M9 13.2h6"/><path d="M9 17h4"/>`,
  // 스파크
  GENERATIVE: `<path d="M11.2 3.4l1.7 4.5 4.5 1.7-4.5 1.7-1.7 4.5-1.7-4.5L5 9.6l4.5-1.7z"/><path d="M17.8 15.6l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z"/>`,
  // 추세선
  FORECAST: `<path d="M3 17.6l5.6-5.6 3.5 3.5L21 6.6"/><path d="M15.4 6.6H21v5.6"/>`,
  // 정상 구간을 뚫고 나가는 스파이크
  ANOMALY: `<path d="M2.5 13.2h4l2.6-7.4 3.6 12.4 2.5-5H21.5"/>`,
  // 모듈
  OTHER: `<path d="M12 3l7.4 4.3v8.6L12 21l-7.4-5.1V7.3z"/><circle cx="12" cy="12" r="2.6"/>`,
};

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
].map((c) => ({ ...c, slug: c.value.toLowerCase(), icon: GLYPHS[c.value] }));

export const CATEGORY_LABELS = CATEGORIES.map((c) => c.label);

const FALLBACK_STYLE = {
  value: "OTHER",
  label: "기타",
  slug: "other",
  tone: "tone-gray",
  badge: "badge-gray",
  icon: GLYPHS.OTHER,
};

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

/** 화면 라벨(비전) → 백엔드 enum(VISION) */
export function categoryValue(labelOrValue) {
  return BY_KEY[labelOrValue]?.value || labelOrValue;
}

export function categoryStyle(category) {
  return BY_KEY[category] || FALLBACK_STYLE;
}

/** 라벨·슬러그·글리프까지 포함한 전체 메타 (CategoryChip 전용) */
export function categoryMeta(category) {
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

/**
 * Payment.Status — 구독 신청 시 enrollment-service 가 결제를 호출해 생성된다.
 * pill 값 failed 는 global.css 에 없고 PaymentLedger/PaymentReceiptDrawer 의
 * scoped 스타일에서만 정의한다. 결제 뱃지는 그 두 곳에서만 렌더된다.
 */
export const PAYMENT_STATUS = {
  PENDING: { label: "결제 대기", pill: "pending" },
  COMPLETED: { label: "결제 완료", pill: "active" },
  FAILED: { label: "결제 실패", pill: "failed" },
  CANCELLED: { label: "취소됨", pill: "inactive" },
};

export function paymentStatus(status) {
  return (
    PAYMENT_STATUS[status] || {
      label: status || "알 수 없음",
      pill: "inactive",
    }
  );
}
