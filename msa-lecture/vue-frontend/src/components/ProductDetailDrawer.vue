<template>
  <Teleport to="body">
    <Transition name="drawer">
      <div v-if="open" class="drawer-root" @keydown.esc="close">
        <button
          class="drawer-backdrop"
          type="button"
          aria-label="닫기"
          @click="close"
        />

        <aside
          class="drawer-panel"
          role="dialog"
          aria-modal="true"
          :aria-label="title"
        >
          <header class="drawer-header">
            <div>
              <p class="drawer-eyebrow">API 상품 상세 스펙</p>
              <h2 class="drawer-title">{{ title }}</h2>
            </div>
            <button
              class="drawer-close"
              type="button"
              aria-label="닫기"
              @click="close"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </button>
          </header>

          <div class="drawer-body">
            <div v-if="loading" class="drawer-loading">
              <div class="spinner"></div>
            </div>

            <div v-else-if="error" class="empty-state drawer-empty">
              <p class="empty-state-title">스펙을 불러오지 못했습니다</p>
              <p>{{ error }}</p>
              <button class="btn btn-ghost" type="button" @click="reload">
                다시 시도
              </button>
            </div>

            <template v-else-if="product">
              <div class="spec-plate">
                <CategoryChip :category="product.category" size="lg" />

                <span class="plate-div" aria-hidden="true"></span>

                <div class="plate-field">
                  <span class="plate-label">운영 상태</span>
                  <span class="plate-value" :class="statusMeta.pill">
                    <span class="status-dot"></span>
                    {{ statusMeta.label }}
                  </span>
                </div>
              </div>

              <p class="drawer-desc">
                {{
                  product.description || "설명이 등록되지 않은 API 상품입니다."
                }}
              </p>

              <h3 class="block-title">스펙</h3>
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
                <div class="spec-row" v-if="providerId">
                  <dt>제공사 ID</dt>
                  <dd class="mono">#{{ providerId }}</dd>
                </div>
              </dl>
            </template>
          </div>

          <footer class="drawer-footer" v-if="product && !loading && !error">
            <div class="footer-price">
              <span class="footer-price-label">월 요금</span>
              <span class="footer-price-value tabular">₩{{ price }}</span>
            </div>
            <router-link
              :to="`/products/${product.id}`"
              class="btn btn-primary footer-btn"
              @click="close"
            >
              상세 페이지로 이동
            </router-link>
          </footer>
        </aside>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, ref, watch, onUnmounted } from "vue";
import CategoryChip from "@/components/CategoryChip.vue";
import { productApi } from "@/api/product.js";
import { categoryLabel, productStatus } from "@/config/catalog.js";

const props = defineProps({
  open: { type: Boolean, default: false },
  productId: { type: [Number, String], default: null },
});

const emit = defineEmits(["close"]);

const product = ref(null);
const loading = ref(false);
const error = ref("");

const title = computed(() => product.value?.title || "API 상품");
const categoryName = computed(() => categoryLabel(product.value?.category));
const statusMeta = computed(() => productStatus(product.value?.status));
const subscriberCount = computed(() =>
  Number(product.value?.subscriberCount ?? 0).toLocaleString(),
);
const price = computed(() =>
  Number(product.value?.price ?? 0).toLocaleString(),
);
const createdAt = computed(() => product.value?.createdAt?.slice(0, 10) || "");
const providerId = computed(() => product.value?.providerId ?? null);

function close() {
  emit("close");
}

function normalize(raw) {
  if (!raw || typeof raw !== "object") return raw;
  return {
    ...raw,
    category: categoryLabel(raw.category),
  };
}

async function loadDetail(id) {
  if (!id) return;

  loading.value = true;
  error.value = "";
  product.value = null;

  try {
    const res = await productApi.getById(id);
    const raw =
      res.data?.data && typeof res.data.data === "object"
        ? res.data.data
        : res.data;

    if (!raw?.id) {
      throw new Error("응답 형식이 올바르지 않습니다.");
    }

    product.value = normalize(raw);
  } catch (e) {
    console.error("[ProductDetailDrawer] getById failed:", e);
    error.value =
      e.response?.data?.message || e.message || "잠시 후 다시 시도해 주세요.";
  } finally {
    loading.value = false;
  }
}

function reload() {
  loadDetail(props.productId);
}

watch(
  () => [props.open, props.productId],
  ([isOpen, id]) => {
    if (isOpen && id != null) {
      document.body.style.overflow = "hidden";
      loadDetail(id);
    } else {
      document.body.style.overflow = "";
    }
  },
);

onUnmounted(() => {
  document.body.style.overflow = "";
});
</script>

<style scoped>
.drawer-root {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  justify-content: flex-end;
}

.drawer-backdrop {
  position: absolute;
  inset: 0;
  border: none;
  background: rgba(10, 11, 13, 0.42);
  cursor: pointer;
}

.drawer-panel {
  position: relative;
  z-index: 1;
  width: min(440px, 100vw);
  height: 100%;
  background: var(--color-bg-primary);
  border-left: 1px solid var(--color-border);
  box-shadow: -16px 0 48px rgba(16, 24, 40, 0.12);
  display: flex;
  flex-direction: column;
}

.drawer-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 28px 28px 20px;
  border-bottom: 1px solid var(--color-border);
}

.drawer-eyebrow {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.drawer-title {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.3;
  color: var(--color-text-primary);
}

.drawer-close {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: var(--transition);
}

.drawer-close svg {
  width: 18px;
  height: 18px;
}

.drawer-close:hover {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px 28px 32px;
}

.drawer-loading {
  display: flex;
  justify-content: center;
  padding: 72px 0;
}

.drawer-empty {
  border: none;
  background: transparent;
  padding: 48px 8px;
}

/* 카테고리와 상태를 한 판에 묶어 상세 헤더처럼 읽히게 한다 */
.spec-plate {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 18px;
  margin-bottom: 24px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: var(--color-bg-secondary);
}

.plate-div {
  align-self: stretch;
  width: 1px;
  background: var(--color-border);
}

.plate-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.plate-label {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.09em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.plate-value {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.plate-value.active {
  color: var(--color-success);
}
.plate-value.pending {
  color: var(--color-warning);
}
.plate-value.inactive {
  color: var(--color-text-muted);
}

.drawer-desc {
  font-size: 14.5px;
  line-height: 1.7;
  color: var(--color-text-secondary);
  margin-bottom: 28px;
}

.block-title {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.drawer-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 28px 24px;
  border-top: 1px solid var(--color-border);
  background: var(--color-bg-primary);
}

.footer-price-label {
  display: block;
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 2px;
}

.footer-price-value {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.footer-btn {
  flex-shrink: 0;
}

.drawer-enter-active,
.drawer-leave-active {
  transition: opacity 0.22s ease;
}

.drawer-enter-active .drawer-panel,
.drawer-leave-active .drawer-panel {
  transition: transform 0.28s cubic-bezier(0.16, 1, 0.3, 1);
}

.drawer-enter-from,
.drawer-leave-to {
  opacity: 0;
}

.drawer-enter-from .drawer-panel,
.drawer-leave-to .drawer-panel {
  transform: translateX(100%);
}

@media (max-width: 560px) {
  .drawer-panel {
    width: 100vw;
  }
  .drawer-header,
  .drawer-body,
  .drawer-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
  .drawer-footer {
    flex-direction: column;
    align-items: stretch;
  }
  .footer-btn {
    justify-content: center;
  }
}
</style>
