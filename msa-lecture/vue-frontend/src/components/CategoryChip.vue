<template>
  <span class="cat-chip" :class="[meta.tone, { dark, compact }]">
    <span class="cat-mark">
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
        stroke-linecap="round"
        stroke-linejoin="round"
        v-html="meta.icon"
      ></svg>
    </span>

    <span v-if="!compact" class="cat-text">
      <span class="cat-name">{{ meta.label }}</span>
      <span class="cat-slug">{{ meta.slug }}</span>
    </span>
  </span>
</template>

<script setup>
import { computed } from "vue";
import { categoryMeta } from "@/config/catalog.js";

const props = defineProps({
  category: { type: String, default: "" },
  /** 어두운 배경 위에서 쓸 때 */
  dark: { type: Boolean, default: false },
  /** 글리프만 노출 */
  compact: { type: Boolean, default: false },
});

const meta = computed(() => categoryMeta(props.category));
</script>

<style scoped>
.cat-chip {
  display: inline-flex;
  align-items: center;
  gap: 11px;
  min-width: 0;
}

.cat-mark {
  position: relative;
  flex: none;
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: var(--cat-bg);
  color: var(--cat-ink);
  overflow: hidden;
}

.cat-mark svg {
  position: relative;
  z-index: 1;
  width: 20px;
  height: 20px;
}

/*
 * 단색 채움 대신 미세한 사선 해칭을 깔아 질감을 만든다.
 * 부모 카드가 --cat-shift를 바꾸면 결이 스르륵 밀린다.
 */
.cat-mark::after {
  content: "";
  position: absolute;
  inset: -6px;
  background-image: repeating-linear-gradient(
    45deg,
    currentColor 0 1px,
    transparent 1px 5px
  );
  background-position: var(--cat-shift, 0px) 0;
  opacity: 0.16;
  transition: background-position 0.55s cubic-bezier(0.16, 1, 0.3, 1);
}

.cat-text {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.cat-name {
  font-size: 13.5px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--cat-name-color);
  line-height: 1.3;
}

/* enum 값을 그대로 노출해 API 상품이라는 성격을 드러낸다 */
.cat-slug {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.09em;
  text-transform: uppercase;
  color: var(--cat-slug-color);
  line-height: 1.3;
}

/* ── 라이트 배경 ── */
.cat-chip {
  --cat-name-color: var(--color-text-primary);
  --cat-slug-color: var(--color-text-muted);
}

.tone-violet {
  --cat-bg: #efebf3;
  --cat-ink: #5b4571;
}
.tone-blue {
  --cat-bg: #eceff4;
  --cat-ink: #34506e;
}
.tone-teal {
  --cat-bg: #e7f1ee;
  --cat-ink: #1f6b57;
}
.tone-amber {
  --cat-bg: #f7efe0;
  --cat-ink: #8a5a16;
}
.tone-pink {
  --cat-bg: #f9ebec;
  --cat-ink: #a8323f;
}
.tone-gray {
  --cat-bg: var(--color-bg-tertiary);
  --cat-ink: var(--color-text-secondary);
}

/* ── 어두운 배경 ── */
.dark {
  --cat-bg: rgba(255, 255, 255, 0.055);
  --cat-name-color: #fff;
  --cat-slug-color: rgba(255, 255, 255, 0.38);
}

.dark.tone-violet {
  --cat-ink: #c3aede;
}
.dark.tone-blue {
  --cat-ink: #a8bdd6;
}
.dark.tone-teal {
  --cat-ink: #8ecfba;
}
.dark.tone-amber {
  --cat-ink: #e0b878;
}
.dark.tone-pink {
  --cat-ink: #eda3ad;
}
.dark.tone-gray {
  --cat-ink: rgba(255, 255, 255, 0.62);
}
</style>
