<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <div class="mypage-grid">
          <!-- 본문: 구매기업은 결제 원장, 제공사는 등록 API -->
          <div class="mypage-main">
            <PaymentLedger
              v-if="!isProvider"
              :payments="paymentRows"
              :loading="paymentLoading"
              :error="paymentError"
            />

            <section v-else>
              <p class="section-label">Dashboard</p>
              <h2 class="section-heading">내가 등록한 API</h2>

              <div v-if="productsLoading" class="api-list">
                <div
                  v-for="i in 2"
                  :key="i"
                  class="skeleton-card surface"
                  style="padding: 24px"
                >
                  <div class="skeleton-block sk-line long"></div>
                  <div class="skeleton-block sk-line medium"></div>
                </div>
              </div>

              <div v-else-if="myProducts.length" class="api-list">
                <article
                  v-for="product in myProducts"
                  :key="product.id"
                  class="api-item surface"
                >
                  <div class="api-item-head">
                    <div>
                      <h3 class="api-item-title">{{ product.title }}</h3>
                      <p class="api-item-desc">
                        {{ product.description || "설명이 없습니다." }}
                      </p>
                    </div>
                    <span class="status-pill" :class="statusOf(product).pill">
                      <span class="status-dot"></span>
                      {{ statusOf(product).label }}
                    </span>
                  </div>

                  <dl class="spec-sheet">
                    <div class="spec-row">
                      <dt>카테고리</dt>
                      <dd>{{ product.category || "-" }}</dd>
                    </div>
                    <div class="spec-row">
                      <dt>월 요금</dt>
                      <dd class="tabular">₩{{ won(product.price) }}</dd>
                    </div>
                    <div class="spec-row">
                      <dt>구독 기업</dt>
                      <dd class="tabular">
                        {{ product.subscriberCount ?? 0 }}개사
                      </dd>
                    </div>
                    <div class="spec-row">
                      <dt>상품 ID</dt>
                      <dd class="mono">#{{ product.id }}</dd>
                    </div>
                  </dl>

                  <router-link
                    :to="`/products/${product.id}`"
                    class="btn btn-ghost api-item-btn"
                  >
                    API 보기
                  </router-link>
                </article>
              </div>

              <div v-else class="empty-state">
                <p class="empty-state-title">
                  {{
                    productsError
                      ? "API 정보를 불러오지 못했습니다"
                      : "아직 등록한 API가 없습니다"
                  }}
                </p>
                <p>{{ productsError || "첫 API 상품을 등록해 보세요." }}</p>
                <router-link
                  v-if="!productsError"
                  to="/products/new"
                  class="btn btn-primary empty-action"
                >
                  API 등록하기
                </router-link>
              </div>
            </section>
          </div>

          <!-- 요약: 신원 + 계정 지표 (기존 프로필 헤더와 KPI 타일을 대체) -->
          <aside class="mypage-side">
            <div class="side-id">
              <span class="side-avatar">{{ avatarInitial }}</span>
              <div class="side-id-text">
                <p class="side-name">{{ auth.user?.name || "사용자" }}</p>
                <p class="side-email">{{ auth.user?.email || "-" }}</p>
              </div>
            </div>

            <dl class="side-stats">
              <div v-for="stat in stats" :key="stat.label" class="side-stat">
                <dt>{{ stat.label }}</dt>
                <dd :class="{ tabular: stat.numeric }">{{ stat.value }}</dd>
              </div>
            </dl>
          </aside>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import AppSideRail from "@/components/AppSideRail.vue";
import PaymentLedger from "@/components/PaymentLedger.vue";
import { useAuthStore } from "@/store/auth.js";
import { useProductStore } from "@/store/product.js";
import { useSubscriptionStore } from "@/store/subscription.js";
import { paymentApi } from "@/api/payment.js";
import { productStatus } from "@/config/catalog.js";

const auth = useAuthStore();
const productStore = useProductStore();
const subStore = useSubscriptionStore();

const isProvider = computed(() => auth.user?.role === "PROVIDER");

const payments = ref([]);
const paymentLoading = ref(false);
const paymentError = ref("");

const productsLoading = ref(false);
const productsError = ref("");

function won(value) {
  return Number(value ?? 0).toLocaleString();
}

function statusOf(product) {
  return productStatus(product.status);
}

/**
 * 시드의 기업명은 "(주)테크이노베이션" 처럼 법인 접두어로 시작해서
 * 첫 글자를 그대로 쓰면 아바타에 "(" 가 찍힌다. 접두어를 걷어내고 첫 글자를 쓴다.
 */
const avatarInitial = computed(() => {
  const name = auth.user?.name ?? "";
  const stripped = name
    .replace(/^\s*(\(주\)|\(유\)|㈜|주식회사)\s*/, "")
    .replace(/^[^\p{L}\p{N}]+/u, "");

  return (stripped || name).charAt(0) || "?";
});

const productById = computed(
  () => new Map(productStore.products.map((p) => [Number(p.id), p])),
);

/** 제공사별 조회 엔드포인트가 없어 전체 목록에서 걸러낸다. */
const myProducts = computed(() => {
  const providerId = Number(auth.user?.id);
  return productStore.products.filter(
    (product) => Number(product.providerId) === providerId,
  );
});

/** 결제 응답에는 상품명이 없어 productId 로 상품 정보를 붙인다. */
const paymentRows = computed(() =>
  payments.value.map((payment) => {
    const product = productById.value.get(Number(payment.productId));
    return {
      ...payment,
      productTitle: product?.title || `상품 #${payment.productId}`,
      providerName: product?.providerName || "제공사 미확인",
      category: product?.category || "기타",
    };
  }),
);

/** 실제로 빠져나간 돈만 집계한다 (실패·취소 제외) */
const completedPayments = computed(() =>
  payments.value.filter((p) => p.status === "COMPLETED"),
);

const monthlySpend = computed(() => {
  // createdAt 은 타임존 없는 LocalDateTime 이라 로컬 연·월과 문자열로 맞춘다.
  // (toISOString 은 UTC 라 월말·월초에 한 달씩 어긋난다)
  const now = new Date();
  const thisMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;

  return completedPayments.value
    .filter((p) => String(p.createdAt ?? "").startsWith(thisMonth))
    .reduce((sum, p) => sum + Number(p.amount ?? 0), 0);
});

const totalSpend = computed(() =>
  completedPayments.value.reduce((sum, p) => sum + Number(p.amount ?? 0), 0),
);

const totalSubscriberCount = computed(() =>
  myProducts.value.reduce(
    (sum, product) => sum + Number(product.subscriberCount ?? 0),
    0,
  ),
);

/**
 * 회원 분류를 맨 앞에 둔다. 키 없이 떠 있으면 무슨 값인지 읽히지 않아서
 * 아래 지표들과 같은 키-값 리듬에 태운다.
 */
const stats = computed(() => {
  const role = {
    label: "회원 분류",
    value: isProvider.value ? "API 제공사" : "구매 기업",
  };

  return isProvider.value
    ? [
        role,
        {
          label: "등록 API",
          value: `${myProducts.value.length}개`,
          numeric: true,
        },
        {
          label: "총 구독 기업",
          value: `${won(totalSubscriberCount.value)}개사`,
          numeric: true,
        },
      ]
    : [
        role,
        {
          label: "이번 달 결제",
          value: `₩${won(monthlySpend.value)}`,
          numeric: true,
        },
        {
          label: "누적 결제",
          value: `₩${won(totalSpend.value)}`,
          numeric: true,
        },
        {
          label: "구독 중",
          value: `${subStore.activeCount}건`,
          numeric: true,
        },
      ];
});

async function loadProducts() {
  productsLoading.value = true;
  productsError.value = "";

  try {
    if (!productStore.products.length) await productStore.fetchProducts();
    if (productStore.error) productsError.value = productStore.error;
  } finally {
    productsLoading.value = false;
  }
}

async function loadPayments() {
  if (!auth.user?.id) {
    paymentError.value = "계정 정보를 확인할 수 없습니다.";
    return;
  }

  paymentLoading.value = true;
  paymentError.value = "";

  try {
    const res = await paymentApi.getByUser(auth.user.id);
    payments.value = Array.isArray(res.data?.data) ? res.data.data : [];
  } catch (e) {
    console.error("[MyPage] 결제 내역 조회 실패:", e);
    paymentError.value = "결제 내역을 불러오지 못했습니다.";
    payments.value = [];
  } finally {
    paymentLoading.value = false;
  }
}

onMounted(async () => {
  if (isProvider.value) {
    await loadProducts();
    return;
  }

  // 원장에 상품명을 붙여야 하므로 상품 목록도 함께 받는다.
  await Promise.all([
    loadPayments(),
    loadProducts(),
    subStore.fetchMySubscriptions(),
  ]);
});
</script>

<style scoped>
/* 본문(원장/목록)과 sticky 요약 컬럼의 비대칭 2단 */
.mypage-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 268px;
  gap: 40px;
  align-items: start;
}

.mypage-side {
  position: sticky;
  top: 48px;
  padding-left: 28px;
  border-left: 1px solid var(--color-border);
}

.side-id {
  display: flex;
  align-items: center;
  gap: 12px;
}

.side-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--color-text-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.side-id-text {
  min-width: 0;
}

.side-name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.side-email {
  font-size: 12.5px;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/*
 * 큰 숫자 KPI 타일 대신 명세형 정의 목록.
 * 값을 오른쪽에 세로로 정렬해 숫자끼리 자릿수가 맞도록 둔다.
 */
.side-stats {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px dashed var(--color-border-hover);
}

.side-stat {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 7px 0;
}

.side-stat dt {
  font-size: 12.5px;
  color: var(--color-text-muted);
}

.side-stat dd {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  white-space: nowrap;
}

/* ── 제공사: 등록 API 목록 ── */
.api-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sk-line {
  height: 13px;
}
.sk-line.medium {
  width: 55%;
}
.sk-line.long {
  width: 85%;
}

.api-item {
  padding: 24px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  grid-template-areas:
    "head head"
    "spec action";
  gap: 18px 20px;
  align-items: end;
}

.api-item-head {
  grid-area: head;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.api-item-title {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  margin-bottom: 5px;
}

.api-item-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.55;
}

.api-item .spec-sheet {
  grid-area: spec;
  max-width: 420px;
}

.api-item-btn {
  grid-area: action;
}

.empty-action {
  margin-top: 20px;
}

/* 요약 컬럼이 눌리기 전에 단으로 접는다 (사이드레일 축소는 900px) */
@media (max-width: 1024px) {
  .mypage-grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }
  .mypage-side {
    position: static;
    order: -1;
    padding-left: 0;
    padding-bottom: 24px;
    border-left: none;
    border-bottom: 1px solid var(--color-border);
  }
  .side-stats {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 32px;
  }
  .side-stat {
    gap: 8px;
  }
}

@media (max-width: 640px) {
  .api-item {
    grid-template-columns: 1fr;
    grid-template-areas:
      "head"
      "spec"
      "action";
  }
  .api-item-head {
    flex-direction: column;
  }
  .api-item .spec-sheet {
    max-width: none;
  }
  .api-item-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
