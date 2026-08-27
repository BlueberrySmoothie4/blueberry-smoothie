<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <header class="page-header">
          <p class="page-eyebrow">Subscriptions</p>
          <div class="page-header-row">
            <div>
              <h1 class="page-title">구독 중인 API</h1>
              <p class="page-subtitle">
                신청한 API는 결제가 승인되면 구독 중으로 전환됩니다.
              </p>
            </div>
            <span class="count-badge" v-if="loaded">
              <span class="tabular">{{ subscriptions.length }}</span
              >건
            </span>
          </div>
        </header>

        <div v-if="loading || !loaded" class="loading-center">
          <div class="spinner"></div>
        </div>

        <div v-else-if="subscriptions.length" class="sub-list">
          <article
            v-for="item in subscriptions"
            :key="item.id"
            class="sub-item surface"
          >
            <span class="cat-tile" :class="styleOf(item).tone">
              {{ categoryOf(item).charAt(0) }}
            </span>

            <div class="sub-info">
              <div class="sub-top">
                <span class="badge" :class="styleOf(item).badge">
                  {{ categoryOf(item) }}
                </span>
                <span class="status-pill" :class="statusOf(item).pill">
                  <span class="status-dot"></span>
                  {{ statusOf(item).label }}
                </span>
              </div>

              <h3 class="sub-title">
                {{ item.product?.title || `상품 #${item.productId}` }}
              </h3>
              <p class="sub-provider">
                {{ item.product?.providerName || "제공사 미확인" }}
              </p>

              <dl class="sub-meta">
                <div>
                  <dt>구독 번호</dt>
                  <dd class="mono">#{{ item.id }}</dd>
                </div>
                <div>
                  <dt>신청일</dt>
                  <dd class="mono">{{ formatDate(item.createdAt) }}</dd>
                </div>
                <div>
                  <dt>월 요금</dt>
                  <dd class="tabular">
                    ₩{{ formatPrice(item.product?.price) }}
                  </dd>
                </div>
              </dl>
            </div>

            <router-link :to="`/products/${item.productId}`" class="sub-link">
              상품 보기
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </router-link>
          </article>
        </div>

        <div v-else-if="error" class="empty-state">
          <p class="empty-state-title">{{ error }}</p>
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
              <rect x="3" y="4" width="18" height="16" rx="2" />
              <path d="M9 12l2 2 4-4" />
            </svg>
          </div>
          <p class="empty-state-title">구독 중인 API가 없습니다</p>
          <p>카탈로그에서 필요한 API를 찾아보세요.</p>
          <router-link to="/products" class="btn btn-primary empty-action">
            API 둘러보기
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import AppSideRail from "@/components/AppSideRail.vue";
import { storeToRefs } from "pinia";
import { useSubscriptionStore } from "@/store/subscription.js";
import { useAuthStore } from "@/store/auth.js";
import { categoryStyle, subscriptionStatus } from "@/config/catalog.js";

const router = useRouter();
const auth = useAuthStore();

const subStore = useSubscriptionStore();
const { subscriptions, loading, error, loaded } = storeToRefs(subStore);

const isProvider = computed(() => auth.user?.role === "PROVIDER");

function categoryOf(item) {
  return item.product?.category || "기타";
}

function styleOf(item) {
  return categoryStyle(categoryOf(item));
}

function statusOf(item) {
  return subscriptionStatus(item.status);
}

function formatDate(value) {
  return value ? value.slice(0, 10) : "-";
}

function formatPrice(value) {
  return Number(value ?? 0).toLocaleString();
}

onMounted(async () => {
  if (isProvider.value) {
    router.replace("/mypage");
    return;
  }
  await subStore.fetchMySubscriptions();
});
</script>

<style scoped>
.count-badge {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
  padding: 5px 12px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: 999px;
  flex-shrink: 0;
}

.sub-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sub-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 22px 24px;
  transition:
    border-color 0.22s ease,
    box-shadow 0.22s ease;
}

.sub-item:hover {
  border-color: var(--color-border-hover);
  box-shadow: var(--shadow-sm);
}

.sub-info {
  flex: 1;
  min-width: 0;
}

.sub-top {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 8px;
}

.sub-title {
  font-size: 16.5px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  line-height: 1.4;
}

.sub-provider {
  font-size: 13px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.sub-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 28px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
}

.sub-meta div {
  display: flex;
  align-items: baseline;
  gap: 7px;
}

.sub-meta dt {
  font-size: 12px;
  color: var(--color-text-muted);
}

.sub-meta dd {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.sub-link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 13.5px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--color-text-secondary);
  flex-shrink: 0;
  transition: var(--transition);
}

.sub-link svg {
  width: 15px;
  height: 15px;
}

.sub-link:hover {
  color: var(--color-primary);
}

.empty-action {
  margin-top: 20px;
}

@media (max-width: 640px) {
  .sub-item {
    flex-wrap: wrap;
  }
  .sub-link {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
