<template>
  <router-link :to="`/products/${product.id}`" class="course-card">
    <!-- 썸네일 -->
    <div class="card-thumb" :class="thumbBg">
      <img v-if="thumbSrc" :src="thumbSrc" :alt="product.title" class="thumb-img" />
      <div v-else class="thumb-placeholder">{{ product.category?.charAt(0) }}</div>
    </div>

    <!-- 내용 -->
    <div class="card-body">
      <span class="badge" :class="badgeClass">{{ product.category }}</span>
      <h3 class="card-title">{{ product.title }}</h3>
      <div class="card-meta">
        <span class="instructor">{{ product.providerName }}</span>
        <span class="price">₩{{ Number(product.price).toLocaleString() }}/월</span>
      </div>
      <div class="card-footer">
        <span class="enrolled">구독 {{ product.subscriberCount?.toLocaleString() }}개 기업</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: { type: Object, required: true }
})

const categoryConfig = {
  '비전':     { bg: 'thumb-purple', badge: 'badge-purple', thumb: 'python' },
  '음성':     { bg: 'thumb-teal',   badge: 'badge-teal',   thumb: 'vue_js' },
  '언어':     { bg: 'thumb-teal',   badge: 'badge-teal',   thumb: 'spring_boot' },
  '문서':     { bg: 'thumb-blue',   badge: 'badge-blue',   thumb: 'docker' },
  '생성형':   { bg: 'thumb-pink',   badge: 'badge-pink',   thumb: 'generative_ai' },
  '예측':     { bg: 'thumb-blue',   badge: 'badge-blue',   thumb: 'kubernetes' },
  '이상탐지': { bg: 'thumb-amber',  badge: 'badge-amber',  thumb: 'docker' },
}

const config = computed(() => categoryConfig[props.product.category] || { bg: 'thumb-gray', badge: 'badge-gray' })
const thumbBg = computed(() => config.value.bg)
const badgeClass = computed(() => config.value.badge)

// 썸네일 이미지 동적 import
const thumbSrc = computed(() => {
  const key = props.product.thumbnail || config.value.thumb
  if (!key) return null
  try {
    return new URL(`../assets/images/courses/${key}.png`, import.meta.url).href
  } catch {
    return null
  }
})
</script>

<style scoped>
.course-card {
  display: flex;
  flex-direction: column;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: var(--transition);
  cursor: pointer;
}
.course-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-border-hover);
}
.card-thumb {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.thumb-teal   { background: #E1F5EE; }
.thumb-blue   { background: #E6F1FB; }
.thumb-amber  { background: #FAEEDA; }
.thumb-purple { background: #EEEDFE; }
.thumb-pink   { background: #FBEAF0; }
.thumb-gray   { background: #F1EFE8; }
.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 16px;
}
.thumb-placeholder {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text-muted);
}
.card-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
  line-height: 1.4;
}
.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.instructor {
  font-size: 12px;
  color: var(--color-text-secondary);
}
.price {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
}
.card-footer {
  margin-top: 2px;
}
.enrolled {
  font-size: 11px;
  color: var(--color-text-muted);
}
</style>
