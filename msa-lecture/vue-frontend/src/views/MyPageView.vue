<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <!-- 프로필 -->
        <header class="page-header profile-header">
          <div class="profile">
            <span class="profile-avatar">
              {{ auth.user?.name?.charAt(0) || "?" }}
            </span>
            <div class="profile-info">
              <h1 class="profile-name">{{ auth.user?.name || "사용자" }}</h1>
              <p class="profile-email">{{ auth.user?.email || "-" }}</p>
            </div>
          </div>
          <span class="role-badge" :class="isProvider ? 'provider' : 'buyer'">
            {{ isProvider ? "API 제공사" : "구매 기업 담당자" }}
          </span>
        </header>

        <!-- 구매 기업: 구독 요약 (추천은 카탈로그 상단으로 옮겼다) -->
        <section v-if="!isProvider">
          <p class="section-label">Overview</p>
          <h2 class="section-heading">내 구독 현황</h2>

          <div class="kpi-row">
            <div class="kpi surface">
              <span class="kpi-label">구독 중</span>
              <span class="kpi-value tabular">{{ activeCount }}</span>
            </div>
            <div class="kpi surface">
              <span class="kpi-label">결제 처리 중</span>
              <span class="kpi-value tabular">{{ pendingCount }}</span>
            </div>
            <div class="kpi surface kpi-accent">
              <span class="kpi-label">월 예상 요금</span>
              <span class="kpi-value tabular">
                ₩{{ monthlyTotal.toLocaleString() }}
              </span>
            </div>
          </div>

          <div class="quick-links">
            <router-link to="/subscriptions" class="btn btn-ghost">
              내 구독 보기
            </router-link>
            <router-link to="/products" class="btn btn-primary">
              카탈로그에서 추천 받기
            </router-link>
          </div>
        </section>

        <!-- 제공사: 대시보드 -->
        <section v-else>
          <p class="section-label">Dashboard</p>
          <h2 class="section-heading">내가 등록한 API</h2>

          <div class="kpi-row">
            <div class="kpi surface">
              <span class="kpi-label">등록 API 수</span>
              <span class="kpi-value tabular">{{ myProducts.length }}</span>
            </div>
            <div class="kpi surface kpi-accent">
              <span class="kpi-label">총 구독 기업 수</span>
              <span class="kpi-value tabular">
                {{ totalSubscriberCount.toLocaleString() }}
              </span>
            </div>
          </div>

          <div v-if="providerLoading" class="api-list">
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
                  <dd class="tabular">{{ formatPrice(product.price) }}</dd>
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
            <div class="empty-state-icon">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.7"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <circle cx="12" cy="12" r="9" />
                <line x1="12" y1="8" x2="12" y2="16" />
                <line x1="8" y1="12" x2="16" y2="12" />
              </svg>
            </div>
            <p class="empty-state-title">
              {{
                providerError
                  ? "API 정보를 불러오지 못했습니다"
                  : "아직 등록한 API가 없습니다"
              }}
            </p>
            <p>{{ providerError || "첫 API 상품을 등록해 보세요." }}</p>
            <router-link
              v-if="!providerError"
              to="/products/new"
              class="btn btn-primary empty-action"
            >
              API 등록하기
            </router-link>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import AppSideRail from "@/components/AppSideRail.vue";
import { useAuthStore } from "@/store/auth.js";
import { useProductStore } from "@/store/product.js";
import { subscriptionApi } from "@/api/subscription.js";
import { productApi } from "@/api/product.js";
import { productStatus } from "@/config/catalog.js";

const auth = useAuthStore();
const productStore = useProductStore();

const isProvider = computed(() => auth.user?.role === "PROVIDER");

const subscriptions = ref([]);

const myProducts = ref([]);
const providerLoading = ref(true);
const providerError = ref("");

const activeCount = computed(
  () => subscriptions.value.filter((s) => s.status === "ACTIVE").length,
);

const pendingCount = computed(
  () => subscriptions.value.filter((s) => s.status === "PENDING").length,
);

/** 활성 구독만 합산한다. 결제 대기 중인 건은 아직 청구되지 않는다. */
const monthlyTotal = computed(() =>
  subscriptions.value
    .filter((s) => s.status === "ACTIVE")
    .reduce((sum, s) => sum + Number(s.product?.price ?? 0), 0),
);

const totalSubscriberCount = computed(() =>
  myProducts.value.reduce((sum, product) => {
    const count = Number(product.subscriberCount ?? 0);
    return sum + (Number.isNaN(count) ? 0 : count);
  }, 0),
);

function formatPrice(price) {
  const value = Number(price ?? 0);
  if (Number.isNaN(value)) return "-";
  return `₩${value.toLocaleString()}`;
}

function statusOf(product) {
  return productStatus(product.status);
}

async function loadSubscriptions() {
  try {
    const res = await subscriptionApi.getMySubscriptions();
    subscriptions.value = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : [];
  } catch (e) {
    console.warn("[MyPage] 구독 현황 조회 실패:", e);
    subscriptions.value = [];
  }
}

async function loadMyProducts() {
  try {
    if (!auth.user?.id) {
      providerError.value = "계정 정보를 확인할 수 없습니다.";
      return;
    }

    // 제공사별 조회 엔드포인트가 없어 전체 목록에서 걸러낸다.
    const res = await productApi.getAll();
    const products = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : [];

    const providerId = Number(auth.user.id);
    myProducts.value = products
      .filter((product) => Number(product.providerId) === providerId)
      .map(productStore.normalizeProduct);
  } catch {
    providerError.value = "잠시 후 다시 시도해 주세요.";
  } finally {
    providerLoading.value = false;
  }
}

onMounted(async () => {
  if (isProvider.value) {
    await loadMyProducts();
  } else {
    providerLoading.value = false;
    await loadSubscriptions();
  }
});
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: var(--color-text-primary);
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-name {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.profile-email {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.role-badge {
  padding: 6px 13px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 600;
  letter-spacing: -0.01em;
  flex-shrink: 0;
}

.role-badge.buyer {
  background: var(--color-primary-light);
  color: var(--color-primary-dark);
}

.role-badge.provider {
  background: var(--color-accent-light);
  color: var(--color-accent);
}

.quick-links {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.skeleton-card {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sk-thumb {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  margin-bottom: 8px;
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

/* KPI */
/* 구매 기업은 3칸, 제공사는 2칸이라 개수에 맞춰 늘어나게 둔다 */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
}

.kpi {
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.kpi-label {
  font-size: 13px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.kpi-value {
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.kpi-accent {
  background: var(--color-text-primary);
  border-color: var(--color-text-primary);
}

.kpi-accent .kpi-label {
  color: rgba(255, 255, 255, 0.6);
}

.kpi-accent .kpi-value {
  color: #fff;
}

/* 등록 API 목록 */
.api-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
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

@media (max-width: 992px) {
  .quick-links .btn {
    flex: 1;
    justify-content: center;
  }
}

@media (max-width: 640px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .kpi-row {
    grid-template-columns: 1fr;
  }
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
