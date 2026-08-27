<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content" v-if="product">
        <router-link to="/products" class="back-link">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="15 18 9 12 15 6" />
          </svg>
          API 카탈로그
        </router-link>

        <div class="detail-layout">
          <div class="detail-main">
            <div class="detail-head">
              <div class="head-tags">
                <span class="badge" :class="style.badge">{{
                  categoryName
                }}</span>
                <span class="status-pill" :class="statusMeta.pill">
                  <span class="status-dot"></span>
                  {{ statusMeta.label }}
                </span>
              </div>
              <h1 class="detail-title">{{ product.title }}</h1>
              <p class="detail-desc">
                {{
                  product.description || "설명이 등록되지 않은 API 상품입니다."
                }}
              </p>
            </div>

            <div class="block">
              <h2 class="block-title">스펙</h2>
              <dl class="spec-sheet">
                <div class="spec-row">
                  <dt>제공사</dt>
                  <dd>{{ product.providerName || "제공사 미확인" }}</dd>
                </div>
                <div class="spec-row">
                  <dt>카테고리</dt>
                  <dd>{{ categoryName }}</dd>
                </div>
                <div class="spec-row">
                  <dt>구독 기업</dt>
                  <dd class="tabular">{{ subscriberCount }}개사</dd>
                </div>
                <div class="spec-row">
                  <dt>월 요금</dt>
                  <dd class="tabular">₩{{ price }}</dd>
                </div>
                <div class="spec-row">
                  <dt>상품 ID</dt>
                  <dd class="mono">#{{ product.id }}</dd>
                </div>
                <div class="spec-row" v-if="createdAt">
                  <dt>등록일</dt>
                  <dd class="mono">{{ createdAt }}</dd>
                </div>
              </dl>
            </div>

            <div class="block">
              <h2 class="block-title">구독 신청 요청</h2>
              <pre
                class="code-block"
              ><span class="c-com"># 아래 버튼과 동일한 요청입니다</span>
<span class="c-cmd">curl</span> -X POST {{ apiBase }}/api/subscriptions \
  -H <span class="c-str">"Authorization: Bearer $ACCESS_TOKEN"</span> \
  -H <span class="c-str">"Content-Type: application/json"</span> \
  -d <span class="c-str">'{ "productId": {{ product.id }} }'</span></pre>
              <p class="code-note">
                신청 직후 상태는 <code>PENDING</code>이고, 결제가 승인되면
                <code>ACTIVE</code>로 전환됩니다.
              </p>
            </div>
          </div>

          <aside class="plan-panel surface">
            <div class="plan-head" :class="style.tone">
              <span class="plan-mark">{{ categoryName.charAt(0) }}</span>
              <span class="plan-cat">{{ categoryName }}</span>
            </div>

            <div class="plan-body">
              <div class="plan-price-block">
                <span class="plan-price tabular">₩{{ price }}</span>
                <span class="plan-period">/ 월</span>
              </div>

              <button
                class="btn btn-primary btn-full"
                @click="handlePrimaryAction"
                :disabled="buttonDisabled"
              >
                <span v-if="subscribing">처리 중...</span>
                <span v-else>{{ buttonLabel }}</span>
              </button>

              <p v-if="subscribeError" class="error-msg">
                {{ subscribeError }}
              </p>
              <p v-else-if="helperText" class="helper-text">{{ helperText }}</p>

              <ul class="plan-features">
                <li v-for="f in features" :key="f">
                  <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2.5"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                  {{ f }}
                </li>
              </ul>
            </div>
          </aside>
        </div>
      </div>

      <div v-else-if="loading" class="loading-center">
        <div class="spinner"></div>
      </div>

      <div v-else class="shell-content">
        <div class="empty-state">
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
              <line x1="12" y1="8" x2="12" y2="13" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
          </div>
          <p class="empty-state-title">API 상품 정보를 불러오지 못했습니다</p>
          <p>주소를 확인하거나 잠시 후 다시 시도해 주세요.</p>
          <router-link to="/products" class="btn btn-ghost empty-action">
            카탈로그로 돌아가기
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import AppSideRail from "@/components/AppSideRail.vue";
import { useProductStore } from "@/store/product.js";
import { subscriptionApi } from "@/api/subscription.js";
import { useAuthStore } from "@/store/auth.js";
import { categoryStyle, productStatus } from "@/config/catalog.js";

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();
const auth = useAuthStore();

const apiBase = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const subscribing = ref(false);
const subscribeError = ref("");
const subscriptionState = ref("NONE");

const features = [
  "신청과 동시에 결제 요청 처리",
  "결제 승인 시 구독 자동 활성화",
  "내 구독 목록에서 상태 확인",
];

const product = computed(() => productStore.selectedProduct);
const loading = computed(() => productStore.loading);
const isProvider = computed(() => auth.user?.role === "PROVIDER");
const isOwnProduct = computed(
  () =>
    !!product.value?.providerId &&
    Number(product.value.providerId) === Number(auth.user?.id),
);

const categoryName = computed(() => product.value?.category || "기타");
const style = computed(() => categoryStyle(product.value?.category));
const statusMeta = computed(() => productStatus(product.value?.status));

const subscriberCount = computed(() =>
  Number(product.value?.subscriberCount ?? 0).toLocaleString(),
);

const price = computed(() =>
  Number(product.value?.price ?? 0).toLocaleString(),
);

const createdAt = computed(() => product.value?.createdAt?.slice(0, 10) || "");

const buttonLabel = computed(() => {
  if (isOwnProduct.value) return "내가 등록한 API";
  if (isProvider.value) return "제공사 계정은 구독 불가";
  if (subscriptionState.value === "ACTIVE") return "내 구독 목록으로 이동";
  if (subscriptionState.value === "PENDING") return "신청 완료 · 결제 처리 중";
  return "결제하고 구독하기";
});

const buttonDisabled = computed(
  () =>
    subscribing.value ||
    isProvider.value ||
    subscriptionState.value === "PENDING",
);

const helperText = computed(() => {
  if (isOwnProduct.value) return "내가 등록한 API 상품입니다.";
  if (isProvider.value) return "제공사 계정으로는 구독을 신청할 수 없습니다.";
  if (subscriptionState.value === "ACTIVE") return "이미 구독 중인 API입니다.";
  if (subscriptionState.value === "PENDING")
    return "결제가 승인되면 구독이 활성화됩니다.";
  return "신청하면 보유 크레딧에서 결제가 함께 처리됩니다.";
});

async function loadSubscriptionState() {
  if (!auth.user?.id || !product.value?.id || isProvider.value) {
    subscriptionState.value = "NONE";
    return;
  }

  try {
    const res = await subscriptionApi.getMySubscriptions();
    const list = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : [];

    const matched = list.find(
      (item) => Number(item.productId) === Number(product.value.id),
    );
    subscriptionState.value = matched ? matched.status : "NONE";
  } catch {
    subscriptionState.value = "NONE";
  }
}

async function handlePrimaryAction() {
  subscribeError.value = "";

  if (!product.value?.id) {
    subscribeError.value = "API 상품 정보가 올바르지 않습니다.";
    return;
  }
  if (isProvider.value) return;
  if (subscriptionState.value === "ACTIVE") {
    router.push("/subscriptions");
    return;
  }
  if (subscriptionState.value === "PENDING") return;

  subscribing.value = true;
  try {
    const res = await subscriptionApi.subscribe(product.value.id);
    subscriptionState.value = res.data?.data?.status ?? "PENDING";
  } catch (e) {
    subscribeError.value =
      e.response?.data?.message || "구독 신청에 실패했습니다.";
  } finally {
    subscribing.value = false;
  }
}

async function loadProduct(id) {
  await productStore.fetchProduct(id);
  await loadSubscriptionState();
}

onMounted(() => loadProduct(route.params.id));

watch(
  () => route.params.id,
  (id) => {
    if (id) loadProduct(id);
  },
);
</script>

<style scoped>
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--color-text-muted);
  margin-bottom: 24px;
  transition: var(--transition);
}

.back-link svg {
  width: 17px;
  height: 17px;
}

.back-link:hover {
  color: var(--color-primary);
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 32px;
  align-items: start;
}

.detail-main {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.detail-head {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}

.head-tags {
  display: flex;
  align-items: center;
  gap: 7px;
}

.detail-title {
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.3;
  color: var(--color-text-primary);
}

.detail-desc {
  font-size: 15.5px;
  color: var(--color-text-secondary);
  line-height: 1.72;
  max-width: 56ch;
}

.block-title {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.code-block {
  padding: 18px 20px;
  border-radius: var(--radius-md);
  background: var(--ink);
  font-family: var(--font-mono);
  font-size: 12.5px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.82);
  white-space: pre-wrap;
  word-break: break-all;
}

.c-com {
  color: rgba(255, 255, 255, 0.35);
}
.c-cmd {
  color: #7dd3fc;
}
.c-str {
  color: #fcd34d;
}

.code-note {
  margin-top: 10px;
  font-size: 12.5px;
  color: var(--color-text-muted);
  line-height: 1.6;
}

.code-note code {
  font-family: var(--font-mono);
  font-size: 11.5px;
  padding: 1px 5px;
  border-radius: 5px;
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
}

/* 구독 패널 */
.plan-panel {
  position: sticky;
  top: 24px;
  overflow: hidden;
}

.plan-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 22px;
}

.plan-mark {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.plan-cat {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.plan-body {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  border-top: 1px solid var(--color-border);
}

.plan-price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.plan-price {
  font-size: 27px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.plan-period {
  font-size: 14px;
  color: var(--color-text-muted);
}

.btn-full {
  width: 100%;
  padding: 13px;
  font-size: 15px;
  justify-content: center;
}

.plan-features {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 4px;
}

.plan-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.plan-features svg {
  width: 15px;
  height: 15px;
  color: var(--color-success);
  flex-shrink: 0;
}

.error-msg {
  font-size: 13px;
  color: var(--color-danger);
  padding: 10px 12px;
  background: var(--color-danger-light);
  border-radius: var(--radius-sm);
  line-height: 1.5;
}

.helper-text {
  font-size: 12.5px;
  color: var(--color-text-muted);
  line-height: 1.55;
}

.empty-action {
  margin-top: 20px;
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .plan-panel {
    position: static;
  }
  .detail-title {
    font-size: 25px;
  }
}
</style>
