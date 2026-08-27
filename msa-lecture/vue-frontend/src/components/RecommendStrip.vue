<template>
  <!--
    카탈로그 그리드와 완전히 다른 톤으로 간다.
    카드가 아니라 읽히는 목록이고, 링크·버튼 없이 추천만 한다.
  -->
  <section v-if="items.length" class="rec" aria-label="추천 API">
    <div class="rec-head">
      <p class="rec-eyebrow">
        <span class="rec-rule" aria-hidden="true"></span>
        Recommended for you
      </p>
      <h2 class="rec-headline">{{ headline }}</h2>
      <p class="rec-sub">{{ subline }}</p>
    </div>

    <ol class="rec-list">
      <li v-for="(item, i) in items" :key="item.id" class="rec-item">
        <span class="rec-rank" aria-hidden="true">
          {{ String(i + 1).padStart(2, "0") }}
        </span>

        <svg
          class="rec-glyph"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
          v-html="metaOf(item).icon"
        ></svg>

        <h3 class="rec-title">{{ item.title }}</h3>

        <p class="rec-desc">
          {{ item.description || "설명이 등록되지 않은 API입니다." }}
        </p>

        <p class="rec-meta">
          <span class="rec-cat">{{ metaOf(item).label }}</span>
          <span class="rec-sep" aria-hidden="true"></span>
          <span>{{ item.providerName || "제공사 미확인" }}</span>
        </p>
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
    ? `구독 중인 API 가운데 ${basedOnLabel.value} 비중이 가장 높았습니다. 같은 영역에서 아직 안 쓰고 계신 API를 골랐습니다.`
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
  padding: 30px 32px 32px;
  margin-bottom: 28px;
  border-radius: var(--radius-xl);
  background: var(--ink);
  color: #fff;
}

.rec-eyebrow {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.09em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.45);
  margin-bottom: 14px;
}

.rec-rule {
  width: 22px;
  height: 2px;
  background: linear-gradient(90deg, var(--sk-red), var(--sk-orange));
}

.rec-headline {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.035em;
  line-height: 1.35;
  color: #fff;
}

.rec-sub {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.55);
  max-width: 62ch;
}

.rec-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0;
  list-style: none;
  margin-top: 28px;
  padding-top: 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* 카드가 아니라 세로 괘선으로만 나눈다 */
.rec-item {
  padding: 22px 24px 4px;
  border-left: 1px solid rgba(255, 255, 255, 0.1);
}

.rec-item:first-child {
  padding-left: 0;
  border-left: none;
}

.rec-rank {
  font-family: var(--font-mono);
  font-size: 11.5px;
  letter-spacing: 0.08em;
  color: var(--sk-orange);
}

.rec-glyph {
  display: block;
  width: 22px;
  height: 22px;
  margin: 14px 0 12px;
  color: rgba(255, 255, 255, 0.75);
}

.rec-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.4;
  color: #fff;
  margin-bottom: 7px;
}

.rec-desc {
  font-size: 13.5px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.55);
  margin-bottom: 14px;
}

.rec-meta {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.4);
}

.rec-cat {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
}

.rec-sep {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.28);
}

@media (max-width: 900px) {
  .rec {
    padding: 26px 22px 24px;
  }
  .rec-list {
    grid-template-columns: 1fr;
  }
  .rec-item {
    padding: 20px 0;
    border-left: none;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
  .rec-item:first-child {
    border-top: none;
  }
}
</style>
