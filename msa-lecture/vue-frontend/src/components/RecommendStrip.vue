<template>
  <!--
    카탈로그 흰 카드와 갈라지게, 패널 전체에 옅은 그라데이션을 깐다.
    목록은 반투명 행이고 누를 곳은 없다.
  -->
  <section
    v-if="loading || items.length"
    class="rec"
    aria-label="추천 API"
    :aria-busy="loading"
  >
    <span class="rec-orb rec-orb-a" aria-hidden="true"></span>
    <span class="rec-orb rec-orb-b" aria-hidden="true"></span>

    <header class="rec-head">
      <p class="rec-eyebrow">추천</p>
      <template v-if="loading">
        <div class="skeleton-block rec-sk-title"></div>
        <div class="skeleton-block rec-sk-sub"></div>
      </template>
      <template v-else>
        <h2 class="rec-headline">{{ headline }}</h2>
        <p class="rec-sub">{{ subline }}</p>
      </template>
    </header>

    <ol v-if="loading" class="rec-sheet">
      <li v-for="i in MAX_ITEMS" :key="i" class="rec-row">
        <span class="skeleton-block rec-sk-icon" aria-hidden="true"></span>
        <div class="rec-body">
          <div class="skeleton-block rec-sk-line"></div>
          <div class="skeleton-block rec-sk-line short"></div>
        </div>
        <span class="skeleton-block rec-sk-cat" aria-hidden="true"></span>
      </li>
    </ol>

    <ol v-else class="rec-sheet">
      <li v-for="item in items" :key="item.id" class="rec-row">
        <span class="rec-icon" :class="metaOf(item).tone" aria-hidden="true">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.7"
            stroke-linecap="round"
            stroke-linejoin="round"
            v-html="metaOf(item).icon"
          ></svg>
        </span>

        <div class="rec-body">
          <h3 class="rec-title">{{ item.title }}</h3>
          <p class="rec-desc">
            {{ item.description || "설명이 아직 없는 API예요." }}
          </p>
        </div>

        <span class="rec-cat">{{ metaOf(item).label }}</span>
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
const loading = ref(true);

const basedOnLabel = computed(() =>
  basedOnCategory.value ? categoryMeta(basedOnCategory.value).label : "",
);

const headline = computed(() =>
  basedOnLabel.value
    ? `${basedOnLabel.value}에 맞춰 API를 추천해요`
    : "이런 API를 추천해요",
);

const subline = computed(() =>
  basedOnLabel.value
    ? `${basedOnLabel.value}에서 현재 이런 API도 많이 사용 중이에요. 관심 기반으로 추천드릴게요.`
    : "아직 구독이 없어서, 기업들이 많이 쓰는 순으로 추천했어요",
);

function metaOf(item) {
  return categoryMeta(item.category);
}

onMounted(async () => {
  if (!auth.user?.id) {
    loading.value = false;
    return;
  }

  try {
    // recommend-service는 공통 봉투 없이 바로 결과를 내려준다.
    const { data } = await recommendApi.getRecommendations(auth.user.id);

    items.value = Array.isArray(data?.recommendedProducts)
      ? data.recommendedProducts.slice(0, MAX_ITEMS)
      : [];
    basedOnCategory.value = data?.basedOnCategory ?? null;
  } catch (e) {
    console.warn("[RecommendStrip] 추천 조회 실패:", e);
    items.value = [];
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.rec {
  position: relative;
  overflow: hidden;
  margin-bottom: 36px;
  padding: 28px 28px 22px;
  border-radius: 24px;
  isolation: isolate;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.08) 0%, transparent 42%),
    linear-gradient(135deg, #fff7f2 0%, #ffe8dc 38%, #fff0f3 72%, #fffaf7 100%);
}

.rec-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(42px);
  pointer-events: none;
  z-index: 0;
}

.rec-orb-a {
  top: -90px;
  right: -40px;
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, rgba(234, 0, 44, 0.22), transparent 68%);
}

.rec-orb-b {
  bottom: -120px;
  left: -60px;
  width: 320px;
  height: 260px;
  background: radial-gradient(
    circle,
    rgba(244, 119, 37, 0.28),
    transparent 70%
  );
}

.rec-head,
.rec-sheet {
  position: relative;
  z-index: 1;
}

.rec-head {
  margin-bottom: 18px;
}

.rec-eyebrow {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--sk-red);
  margin-bottom: 8px;
}

.rec-headline {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.35;
  color: var(--color-text-primary);
}

.rec-sub {
  margin-top: 6px;
  font-size: 15px;
  line-height: 1.55;
  color: var(--color-text-secondary);
  max-width: 100%;
}

.rec-sheet {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rec-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.82),
    rgba(255, 255, 255, 0.62)
  );
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.rec-icon {
  flex: none;
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--cat-bg);
  color: var(--cat-ink);
}

.rec-icon svg {
  width: 20px;
  height: 20px;
}

.rec-body {
  min-width: 0;
  flex: 1;
}

.rec-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.35;
  color: var(--color-text-primary);
}

.rec-desc {
  margin-top: 3px;
  font-size: 14px;
  line-height: 1.45;
  color: var(--color-text-muted);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.rec-cat {
  flex: none;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
}

.rec .skeleton-block {
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.45) 25%,
    rgba(255, 255, 255, 0.85) 50%,
    rgba(255, 255, 255, 0.45) 75%
  );
  background-size: 200% 100%;
}

.rec-sk-title {
  width: min(280px, 70%);
  height: 26px;
  border-radius: 8px;
}

.rec-sk-sub {
  width: min(520px, 92%);
  height: 16px;
  margin-top: 10px;
  border-radius: 6px;
}

.rec-sk-icon {
  flex: none;
  width: 42px;
  height: 42px;
  border-radius: 50%;
}

.rec-sk-line {
  width: 62%;
  height: 14px;
}

.rec-sk-line.short {
  width: 84%;
  height: 12px;
  margin-top: 8px;
}

.rec-sk-cat {
  flex: none;
  width: 36px;
  height: 12px;
}

@media (max-width: 560px) {
  .rec {
    padding: 22px 18px 16px;
  }
  .rec-headline {
    font-size: 20px;
  }
  .rec-cat,
  .rec-sk-cat {
    display: none;
  }
}
</style>
