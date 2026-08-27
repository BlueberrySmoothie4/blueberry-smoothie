<template>
  <component
    :is="interactive ? 'button' : 'router-link'"
    v-bind="
      interactive ? { type: 'button' } : { to: `/products/${product.id}` }
    "
    class="api-card surface surface-hover"
    :class="{ selected }"
    @click="onClick"
  >
    <div class="api-card-head">
      <span class="cat-tile" :class="style.tone">{{ label.charAt(0) }}</span>
      <span class="badge" :class="style.badge">{{ label }}</span>
    </div>

    <div class="api-body">
      <h3 class="api-title">{{ product.title }}</h3>
      <p class="api-provider">{{ product.providerName || "제공사 미확인" }}</p>
    </div>

    <div class="api-foot">
      <span class="api-subs">
        <span class="tabular">{{ subscriberCount }}</span
        >개사 구독
      </span>
      <span class="api-price">
        ₩<span class="tabular">{{ price }}</span>
        <i>/월</i>
      </span>
    </div>
  </component>
</template>

<script setup>
import { computed } from "vue";
import { categoryLabel, categoryStyle } from "@/config/catalog.js";

const props = defineProps({
  product: { type: Object, required: true },
  /** true면 링크 이동 대신 select 이벤트를 보낸다 (목록 사이드 패널용) */
  interactive: { type: Boolean, default: false },
  selected: { type: Boolean, default: false },
});

const emit = defineEmits(["select"]);

const label = computed(() => categoryLabel(props.product.category));
const style = computed(() => categoryStyle(props.product.category));

const subscriberCount = computed(() =>
  Number(props.product.subscriberCount ?? 0).toLocaleString(),
);

const price = computed(() => Number(props.product.price ?? 0).toLocaleString());

function onClick() {
  if (props.interactive) {
    emit("select", props.product);
  }
}
</script>

<style scoped>
.api-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 22px;
  text-align: left;
  font: inherit;
  color: inherit;
  cursor: pointer;
}

button.api-card {
  appearance: none;
  border: none;
  background: var(--color-bg-primary);
}

.api-card.selected {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 1px var(--color-primary);
}

.api-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.api-body {
  flex: 1;
}

.api-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  line-height: 1.4;
  margin-bottom: 5px;
}

.api-provider {
  font-size: 13px;
  color: var(--color-text-muted);
}

.api-foot {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.api-subs {
  font-size: 12.5px;
  color: var(--color-text-muted);
}

.api-price {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  white-space: nowrap;
}

.api-price i {
  font-style: normal;
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
}
</style>
