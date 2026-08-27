<template>
  <section class="ledger">
    <div class="ledger-head">
      <div>
        <p class="section-label">Receipts</p>
        <h2 class="section-heading">결제 내역</h2>
      </div>
      <p v-if="!loading && !error && rows.length" class="ledger-sum">
        <span class="tabular">{{ rows.length }}</span
        >건 · 합계
        <span class="tabular">₩{{ won(paidTotal) }}</span>
      </p>
    </div>

    <div v-if="loading" class="ledger-skeleton">
      <div v-for="i in 3" :key="i" class="skeleton-row">
        <span class="skeleton-block sk-date"></span>
        <span class="skeleton-block sk-title"></span>
        <span class="skeleton-block sk-amount"></span>
      </div>
    </div>

    <p v-else-if="error" class="ledger-note">{{ error }}</p>

    <div v-else-if="!rows.length" class="ledger-note">
      <span>아직 결제 내역이 없습니다.</span>
      <router-link to="/products" class="ledger-link">
        API 카탈로그 둘러보기 →
      </router-link>
    </div>

    <template v-else>
      <section v-for="group in groups" :key="group.key" class="ledger-group">
        <header class="group-head">
          <h3 class="group-label">{{ group.label }}</h3>
          <span class="group-sum tabular">₩{{ won(group.total) }}</span>
        </header>

        <ol class="ledger-rows">
          <li v-for="row in group.items" :key="row.paymentId">
            <button type="button" class="ledger-row" @click="select(row)">
              <span class="row-date mono">{{ row.dateShort }}</span>

              <span class="row-main">
                <span class="row-title">{{ row.productTitle }}</span>
                <span class="row-meta">
                  {{ row.providerName }} · {{ row.category }}
                </span>
              </span>

              <span class="row-amount tabular" :class="{ voided: row.voided }">
                ₩{{ won(row.amount) }}
              </span>

              <!--
                정상 건에는 표식을 달지 않는다. 장부에서 특기사항만 표기하는 관습이자,
                모든 행에 같은 뱃지가 반복되는 획일성을 피하기 위한 선택.
              -->
              <span class="row-flag">
                <span
                  v-if="row.flagged"
                  class="status-pill"
                  :class="row.statusMeta.pill"
                >
                  <span class="status-dot"></span>
                  {{ row.statusMeta.label }}
                </span>
              </span>

              <svg
                class="row-chevron"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </button>
          </li>
        </ol>
      </section>
    </template>

    <PaymentReceiptDrawer
      :open="drawerOpen"
      :payment="selected"
      @close="drawerOpen = false"
    />
  </section>
</template>

<script setup>
import { computed, ref } from "vue";
import PaymentReceiptDrawer from "@/components/PaymentReceiptDrawer.vue";
import { paymentStatus } from "@/config/catalog.js";

const props = defineProps({
  /** 상품 정보(productTitle/providerName/category)까지 붙은 결제 목록 */
  payments: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: "" },
});

const drawerOpen = ref(false);
const selected = ref(null);

function won(value) {
  return Number(value ?? 0).toLocaleString();
}

/** 합계에는 실제로 빠져나간 돈만 넣는다 (실패·취소 제외) */
function paidAmount(payment) {
  return payment.status === "COMPLETED" ? Number(payment.amount ?? 0) : 0;
}

const rows = computed(() =>
  [...props.payments]
    .sort((a, b) =>
      String(b.createdAt ?? "").localeCompare(String(a.createdAt ?? "")),
    )
    .map((payment) => ({
      ...payment,
      statusMeta: paymentStatus(payment.status),
      // "2026-08-27T05:29:38" → "08-27" (연도는 월 그룹 헤더가 알려준다)
      dateShort: payment.createdAt ? payment.createdAt.slice(5, 10) : "--/--",
      voided: payment.status === "FAILED" || payment.status === "CANCELLED",
      flagged: payment.status !== "COMPLETED",
    })),
);

const paidTotal = computed(() => rows.value.reduce((sum, r) => sum + paidAmount(r), 0));

const groups = computed(() => {
  const buckets = new Map();

  for (const row of rows.value) {
    const key = row.createdAt ? row.createdAt.slice(0, 7) : "unknown";

    if (!buckets.has(key)) {
      const [year, month] = key.split("-");
      buckets.set(key, {
        key,
        label: year && month ? `${year}년 ${Number(month)}월` : "날짜 미상",
        total: 0,
        items: [],
      });
    }

    const bucket = buckets.get(key);
    bucket.items.push(row);
    bucket.total += paidAmount(row);
  }

  return [...buckets.values()];
});

function select(row) {
  selected.value = row;
  drawerOpen.value = true;
}
</script>

<style scoped>
.ledger-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 8px;
}

/* .section-heading 의 하단 여백은 헤더 행에서 정렬을 깨서 제거한다 */
.ledger-head .section-heading {
  margin-bottom: 0;
}

.ledger-sum {
  font-size: 13px;
  color: var(--color-text-muted);
  white-space: nowrap;
}

/* ── 월 그룹 ── */
.group-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 2px 8px;
}

.group-label {
  font-size: 12.5px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--color-text-secondary);
}

.group-sum {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--color-text-muted);
}

.ledger-rows {
  list-style: none;
  border-top: 1px solid var(--color-border);
}

/*
 * 카드가 아니라 괘선으로만 구분한다 — surface/radius/shadow 를 쓰지 않는 것이
 * 이 화면에서 획일성을 막는 핵심이다.
 */
.ledger-row {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) auto auto 14px;
  align-items: center;
  gap: 14px;
  width: 100%;
  padding: 14px 10px;
  border: none;
  border-bottom: 1px solid var(--color-border);
  background: none;
  text-align: left;
  font: inherit;
  color: inherit;
  transition: background 0.15s ease;
}

/* 카드가 떠오르는 hover 대신 배경 틴트 */
.ledger-row:hover {
  background: var(--color-bg-secondary);
}

.ledger-row:hover .row-chevron {
  color: var(--color-text-secondary);
  transform: translateX(2px);
}

.row-date {
  font-size: 12px;
  color: var(--color-text-muted);
}

.row-main {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.row-title {
  font-size: 14.5px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.row-meta {
  font-size: 12px;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.row-amount {
  font-size: 14.5px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  white-space: nowrap;
}

/* 실패·취소 건은 취소선 — 뱃지를 읽기 전에 눈에 들어온다 */
.row-amount.voided {
  color: var(--color-text-muted);
  text-decoration: line-through;
  text-decoration-thickness: 1px;
}

.row-flag {
  display: inline-flex;
}

/* global.css 의 .status-pill 에 없는 변형. 결제 뱃지는 이 컴포넌트에서만 렌더된다. */
.status-pill.failed {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.row-chevron {
  width: 14px;
  height: 14px;
  color: var(--color-border-hover);
  transition: var(--transition);
}

/* ── 빈 상태 / 오류 ── */
/*
 * 가운데 정렬 + 둥근 사각형 아이콘(.empty-state)을 쓰지 않는다.
 * 원장 흐름을 끊지 않는 좌측 정렬 한 줄로 끝낸다.
 */
.ledger-note {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 10px;
  margin-top: 20px;
  padding: 18px 2px;
  border-top: 1px solid var(--color-border);
  font-size: 14px;
  color: var(--color-text-muted);
}

.ledger-link {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--color-primary);
}

.ledger-link:hover {
  text-decoration: underline;
}

/* ── 로딩 ── */
.ledger-skeleton {
  margin-top: 20px;
  border-top: 1px solid var(--color-border);
}

.skeleton-row {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) 72px;
  align-items: center;
  gap: 14px;
  padding: 18px 10px;
  border-bottom: 1px solid var(--color-border);
}

.skeleton-row span {
  height: 12px;
}

.sk-title {
  width: 70%;
}

@media (max-width: 620px) {
  .ledger-row {
    grid-template-columns: minmax(0, 1fr) auto;
    grid-template-areas:
      "main amount"
      "meta flag";
    gap: 6px 12px;
  }
  .row-date {
    display: none;
  }
  .row-main {
    grid-area: main;
  }
  .row-amount {
    grid-area: amount;
    text-align: right;
  }
  .row-flag {
    grid-area: flag;
    justify-content: flex-end;
  }
  .row-chevron {
    display: none;
  }
}
</style>
