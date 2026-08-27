<template>
  <router-link
    :to="`/products/${product.id}`"
    class="api-card surface surface-hover"
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
  </router-link>
</template>

<script setup>
import { computed } from "vue";
import { categoryLabel, categoryStyle } from "@/config/catalog.js";

const props = defineProps({
  product: { type: Object, required: true },
});

const label = computed(() => categoryLabel(props.product.category));
const style = computed(() => categoryStyle(props.product.category));

const subscriberCount = computed(() =>
  Number(props.product.subscriberCount ?? 0).toLocaleString(),
);

const price = computed(() => Number(props.product.price ?? 0).toLocaleString());
</script>

<style scoped>
.api-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 22px;
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
