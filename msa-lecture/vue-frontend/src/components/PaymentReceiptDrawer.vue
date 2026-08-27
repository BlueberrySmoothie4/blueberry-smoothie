<template>
  <Teleport to="body">
    <Transition name="drawer">
      <div v-if="open && payment" class="drawer-root" @keydown.esc="close">
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
          :aria-label="`영수증 ${payment.paymentId}`"
        >
          <header class="drawer-header">
            <div>
              <p class="drawer-eyebrow">Receipt · #{{ payment.paymentId }}</p>
              <h2 class="drawer-title">{{ payment.productTitle }}</h2>
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
            <div class="receipt-tags">
              <span class="badge" :class="categoryBadge">{{ categoryName }}</span>
            </div>

            <p class="receipt-provider">{{ payment.providerName }}</p>

            <!-- 파선 괘선 — 영수증 신호는 여기까지만 준다 -->
            <div class="receipt-perf" aria-hidden="true"></div>

            <dl class="spec-sheet">
              <div class="spec-row">
                <dt>결제일시</dt>
                <dd class="mono">{{ paidAt }}</dd>
              </div>
              <div class="spec-row">
                <dt>품목</dt>
                <dd>1개월 이용료</dd>
              </div>
              <div class="spec-row">
                <dt>상품 ID</dt>
                <dd class="mono">#{{ payment.productId }}</dd>
              </div>
              <div class="spec-row">
                <dt>결제 ID</dt>
                <dd class="mono">#{{ payment.paymentId }}</dd>
              </div>
            </dl>

            <div class="receipt-total">
              <span class="total-label">합계</span>
              <span class="total-value tabular">₩{{ amountText }}</span>
            </div>

            <div class="receipt-approval">
              <span class="approval-label">승인번호</span>
              <span class="approval-value mono">
                {{ payment.transactionId || "발급되지 않음" }}
              </span>
            </div>

            <p class="receipt-stamp" :class="statusMeta.pill">
              {{ statusMeta.label }}
            </p>
          </div>

          <footer class="drawer-footer">
            <router-link
              :to="`/products/${payment.productId}`"
              class="btn btn-ghost footer-btn"
              @click="close"
            >
              상품 보기
            </router-link>
          </footer>
        </aside>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, watch, onUnmounted } from "vue";
import {
  categoryLabel,
  categoryStyle,
  paymentStatus,
} from "@/config/catalog.js";

const props = defineProps({
  open: { type: Boolean, default: false },
  /** PaymentLedger 가 넘기는 상품 정보까지 붙은 결제 행 */
  payment: { type: Object, default: null },
});

const emit = defineEmits(["close"]);

const statusMeta = computed(() => paymentStatus(props.payment?.status));

/** 카테고리 표기는 ProductCard·ProductDetailDrawer 와 같은 .badge 를 쓴다. */
const categoryName = computed(() => categoryLabel(props.payment?.category));
const categoryBadge = computed(
  () => categoryStyle(props.payment?.category).badge,
);

const amountText = computed(() =>
  Number(props.payment?.amount ?? 0).toLocaleString(),
);

/** "2026-08-27T05:29:38.244044" → "2026-08-27 05:29" (로컬 타임존 해석을 피해 문자열로 자른다) */
const paidAt = computed(() => {
  const raw = props.payment?.createdAt;
  if (!raw) return "-";
  return `${raw.slice(0, 10)} ${raw.slice(11, 16)}`;
});

function close() {
  emit("close");
}

watch(
  () => props.open,
  (isOpen) => {
    document.body.style.overflow = isOpen ? "hidden" : "";
  },
);

onUnmounted(() => {
  document.body.style.overflow = "";
});
</script>

<style scoped>
/*
 * 드로어 셸(root/backdrop/panel/header/footer)은 ProductDetailDrawer 와 같은 규격이다.
 * 공용 AppDrawer 로 추출하는 편이 낫지만, 그쪽 컴포넌트를 건드리면 충돌 위험이 있어
 * 지금은 복제해 두고 후속 작업으로 남긴다.
 */
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
  width: min(400px, 100vw);
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
  font-size: 21px;
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

.drawer-footer {
  padding: 18px 28px 24px;
  border-top: 1px solid var(--color-border);
}

.footer-btn {
  width: 100%;
  justify-content: center;
}

/* ── 영수증 본문 ── */
.receipt-tags {
  margin-bottom: 12px;
}

.receipt-provider {
  font-size: 13.5px;
  color: var(--color-text-muted);
}

.receipt-perf {
  border-top: 1px dashed var(--color-border-hover);
  margin: 20px 0;
}

.receipt-total {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  margin-top: 22px;
}

.total-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.total-value {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.receipt-approval {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 22px;
}

.approval-label {
  font-size: 11.5px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.approval-value {
  font-size: 12.5px;
  color: var(--color-text-secondary);
  word-break: break-all;
  user-select: all;
}

/* 상태는 원장 행의 뱃지를 반복하지 않고 스탬프로 — 같은 표식을 두 번 쓰지 않는다 */
.receipt-stamp {
  display: inline-block;
  margin-top: 26px;
  padding: 7px 16px;
  border: 1.5px solid currentColor;
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 600;
  /* 한글이라 자간을 넓게 주면 어절이 끊겨 보인다. 스탬프 느낌만 남기는 선에서 멈춘다. */
  letter-spacing: 0.02em;
  opacity: 0.85;
}

.receipt-stamp.active {
  color: var(--color-success);
}
.receipt-stamp.pending {
  color: var(--color-warning);
}
.receipt-stamp.failed {
  color: var(--color-danger);
}
.receipt-stamp.inactive {
  color: var(--color-text-muted);
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
}
</style>
