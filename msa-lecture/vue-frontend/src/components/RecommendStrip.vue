<template>
  <!--
    카탈로그 그리드와 다른 구역임을 바로 읽히게 한다.
    왼쪽은 잉크색 칼럼(추천 근거), 오른쪽은 흰 목차. 누를 곳은 없다.
  -->
  <section v-if="items.length" class="rec" aria-label="추천 API">
    <div class="rec-aside">
      <p class="rec-eyebrow">
        <span class="rec-rule" aria-hidden="true"></span>
        For you
      </p>
      <h2 class="rec-headline">{{ headline }}</h2>
      <p class="rec-sub">{{ subline }}</p>
      <p class="rec-count">
        <span class="tabular">{{ items.length }}</span
        >건
      </p>
    </div>

    <ol class="rec-list">
      <li v-for="(item, i) in items" :key="item.id" class="rec-row">
        <span class="rec-rank" aria-hidden="true">
          {{ String(i + 1).padStart(2, "0") }}
        </span>

        <div class="rec-body">
          <h3 class="rec-title">{{ item.title }}</h3>
          <p class="rec-desc">
            {{ item.description || "설명이 등록되지 않은 API입니다." }}
          </p>
        </div>

        <div class="rec-tail">
          <span class="rec-cat">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.6"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
              v-html="metaOf(item).icon"
            ></svg>
            {{ metaOf(item).label }}
          </span>
          <span class="rec-provider">
            {{ item.providerName || "제공사 미확인" }}
          </span>
        </div>
      </li>
    </ol>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { recommendApi } from "@/api/recommend.js";
import { useAuthStore } from "@/store/auth.js";
import { categoryMeta } from "@/config/catalog.js";

const MAX_ITEMS = 3;

const auth = useAuthStore();

const items = ref([]);
const basedOnCategory = ref(null);

const basedOnLabel = computed(() =>
  basedOnCategory.value ? categoryMeta(basedOnCategory.value).label : "",
);

const headline = computed(() =>
  basedOnLabel.value
    ? `${basedOnLabel.value} API를 쓰고 계시네요`
    : "지금 기업들이 가장 많이 쓰는 API",
);

const subline = computed(() =>
  basedOnLabel.value
    ? `구독 중인 API 가운데 ${basedOnLabel.value} 비중이 가장 높았습니다. 같은 영역에서 아직 안 쓰고 계신 API입니다.`
    : "아직 구독 이력이 없어 도입 기업이 많은 순서로 골랐습니다.",
);

function metaOf(item) {
  return categoryMeta(item.category);
}

onMounted(async () => {
  if (!auth.user?.id) return;

  try {
    // recommend-service는 공통 봉투 없이 바로 결과를 내려준다.
    const { data } = await recommendApi.getRecommendations(auth.user.id);

    items.value = Array.isArray(data?.recommendedProducts)
      ? data.recommendedProducts.slice(0, MAX_ITEMS)
      : [];
    basedOnCategory.value = data?.basedOnCategory ?? null;
  } catch (e) {
    // 추천은 보조 기능이라 실패해도 카탈로그를 방해하지 않는다.
    console.warn("[RecommendStrip] 추천 조회 실패:", e);
    items.value = [];
  }
});
</script>

<style scoped>
.rec {
  display: grid;
  grid-template-columns: minmax(0, 280px) minmax(0, 1fr);
  margin-bottom: 30px;
  overflow: hidden;
  border-radius: var(--radius-xl);
  background: #fff;
  box-shadow: 0 18px 40px -28px rgba(11, 11, 12, 0.45);
}

/* 왼쪽: 왜 이걸 추천하는지 — 잉크색이라 카탈로그와 바로 갈라진다 */
.rec-aside {
  position: relative;
  padding: 32px 28px 28px;
  background: var(--ink);
  color: #fff;
}

.rec-aside::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, var(--sk-red), var(--sk-orange));
}

.rec-eyebrow {
  display: flex;
  align-items: center;
  gap: 9px;
  font-family: var(--font-mono);
  font-size: 10.5px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.48);
  margin-bottom: 16px;
}

.rec-rule {
  width: 18px;
  height: 2px;
  background: linear-gradient(90deg, var(--sk-red), var(--sk-orange));
}

.rec-headline {
  font-family: var(--font-heading);
  font-size: 23px;
  font-weight: 700;
  letter-spacing: -0.04em;
  line-height: 1.35;
  color: #fff;
}

.rec-sub {
  margin-top: 12px;
  font-size: 13.5px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.58);
}

.rec-count {
  margin-top: 22px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--sk-orange);
}

/* 오른쪽: 흰 목차 */
.rec-list {
  list-style: none;
  padding: 8px 32px 8px 28px;
}

.rec-row {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  align-items: baseline;
  gap: 0 18px;
  padding: 18px 0;
  border-top: 1px solid var(--color-border);
}

.rec-row:first-child {
  border-top: none;
}

.rec-rank {
  font-family: var(--font-mono);
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.04em;
  color: var(--sk-red);
}

.rec-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.4;
  color: var(--color-text-primary);
}

.rec-desc {
  margin-top: 5px;
  font-size: 13px;
  line-height: 1.7;
  color: var(--color-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-tail {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
  text-align: right;
  white-space: nowrap;
}

.rec-cat {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: var(--color-text-primary);
}

.rec-cat svg {
  width: 15px;
  height: 15px;
  color: var(--color-text-muted);
}

.rec-provider {
  font-size: 12px;
  color: var(--color-text-muted);
}

@media (max-width: 1100px) {
  .rec-list {
    padding: 8px 24px;
  }
}

@media (max-width: 900px) {
  .rec {
    grid-template-columns: 1fr;
  }
  .rec-aside {
    padding: 26px 24px 22px;
  }
  .rec-aside::before {
    width: 100%;
    height: 3px;
  }
  .rec-count {
    margin-top: 16px;
  }
  .rec-list {
    padding: 4px 22px 10px;
  }
}

@media (max-width: 560px) {
  .rec-row {
    grid-template-columns: 36px minmax(0, 1fr);
    gap: 0 12px;
  }
  .rec-tail {
    grid-column: 2;
    flex-direction: row;
    align-items: center;
    gap: 10px;
    margin-top: 8px;
    text-align: left;
  }
}
</style>
