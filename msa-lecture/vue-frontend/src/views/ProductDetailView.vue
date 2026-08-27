<template>
  <div class="page-wrapper">
    <AppHeader />

    <div class="detail-layout" v-if="product">
      <div class="detail-hero">
        <div class="detail-hero-inner">
          <!-- 좌측 상세 정보 -->
          <div class="detail-info fade-in-up">
            <span class="badge" :class="badgeClass">{{ displayCategory }}</span>
            <h1 class="detail-title">{{ product.title }}</h1>
            <p class="detail-desc">
              {{ product.description || '엔드포인트 하나로 바로 연동할 수 있는 상용 AI API입니다.' }}
            </p>

            <div class="detail-meta">
              <span>제공사: {{ displayProviderName }}</span>
              <span>구독 기업: {{ displaySubscriberCount }}개</span>
            </div>
          </div>

          <!-- 우측 결제/구독 카드 -->
          <div class="enroll-card fade-in">
            <div class="enroll-thumb" :class="thumbBg">
              <img v-if="thumbSrc" :src="thumbSrc" :alt="product.title" />
            </div>

            <div class="enroll-body">
              <div class="enroll-price">₩{{ displayPrice }}<span class="price-unit">/월</span></div>

              <button
                class="btn btn-primary btn-full"
                @click="handlePrimaryAction"
                :disabled="buttonDisabled"
                :class="{ 'btn-disabled': buttonDisabled }"
              >
                <span v-if="subscribing">처리 중...</span>
                <span v-else>{{ buttonLabel }}</span>
              </button>

              <div v-if="subscribeError" class="error-msg">{{ subscribeError }}</div>

              <p class="helper-text" v-if="helperText">
                {{ helperText }}
              </p>

              <ul class="enroll-info-list">
                <li>✅ 결제 즉시 호출 권한 활성화</li>
                <li>✅ 월 구독 정액제 (호출 수 제한 없음)</li>
                <li>✅ 사용 내역 조회 제공</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loading" class="loading-center">
      <div class="spinner"></div>
    </div>

    <div v-else class="loading-center">
      <p class="empty-text">API 상품 정보를 불러오지 못했습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import { useProductStore } from '@/store/product.js'
import { subscriptionApi } from '@/api/subscription.js'
import { useAuthStore } from '@/store/auth.js'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const auth = useAuthStore()

const subscribing = ref(false)
const subscribeError = ref('')
const subscriptionStatus = ref('NONE') // NONE | PENDING | ACTIVE

const product = computed(() => productStore.selectedProduct)
const loading = computed(() => productStore.loading)
const isProvider = computed(() => auth.user?.role === 'PROVIDER')

const categoryConfig = {
  '비전': { badge: 'badge-purple', bg: 'thumb-purple', thumb: 'python' },
  '음성': { badge: 'badge-teal', bg: 'thumb-teal', thumb: 'vue_js' },
  '언어': { badge: 'badge-teal', bg: 'thumb-teal', thumb: 'spring_boot' },
  '문서': { badge: 'badge-blue', bg: 'thumb-blue', thumb: 'docker' },
  '생성형': { badge: 'badge-pink', bg: 'thumb-pink', thumb: 'generative_ai' },
  '예측': { badge: 'badge-blue', bg: 'thumb-blue', thumb: 'kubernetes' },
  '이상탐지': { badge: 'badge-amber', bg: 'thumb-amber', thumb: 'docker' },
}

const config = computed(() => categoryConfig[product.value?.category] || {})
const badgeClass = computed(() => config.value.badge || 'badge-gray')
const thumbBg = computed(() => config.value.bg || 'thumb-gray')

const displayCategory = computed(() => product.value?.category || '-')

const displayProviderName = computed(() => {
  return (
    product.value?.providerName ||
    product.value?.vendorName ||
    product.value?.provider?.name ||
    product.value?.provider_name ||
    product.value?.ownerName ||
    '제공사 정보 없음'
  )
})

const displaySubscriberCount = computed(() => {
  const value = Number(
    product.value?.subscriberCount ??
    product.value?.subscriber_count ??
    0
  )
  return Number.isNaN(value) ? 0 : value.toLocaleString()
})

const displayPrice = computed(() => {
  const value = Number(product.value?.price ?? 0)
  return Number.isNaN(value) ? '0' : value.toLocaleString()
})

const thumbSrc = computed(() => {
  const key = product.value?.thumbnail || config.value.thumb
  if (!key) return null

  try {
    return new URL(`../assets/images/courses/${key}.png`, import.meta.url).href
  } catch {
    return null
  }
})

const buttonLabel = computed(() => {
  if (isProvider.value) return '제공사 계정은 구독 불가'
  if (subscriptionStatus.value === 'ACTIVE') return '내 구독 목록으로 이동'
  if (subscriptionStatus.value === 'PENDING') return '신청 완료 · 결제 처리 중'
  return '구독하고 API 사용하기'
})

const buttonDisabled = computed(() => {
  if (subscribing.value) return true
  if (isProvider.value) return true
  if (subscriptionStatus.value === 'PENDING') return true
  return false
})

const helperText = computed(() => {
  if (isProvider.value) {
    return '제공사 계정은 자사 API 상품을 구독할 수 없습니다.'
  }

  if (subscriptionStatus.value === 'ACTIVE') {
    return '이미 구독 중인 API입니다. 내 구독 목록에서 연동 정보를 확인할 수 있습니다.'
  }

  if (subscriptionStatus.value === 'PENDING') {
    return '구독 신청이 접수되었습니다. 결제가 완료되면 내 구독 목록에서 ACTIVE로 전환됩니다.'
  }

  return '결제를 진행하면 구독 신청이 함께 처리됩니다.'
})

async function loadSubscriptionStatus() {
  if (!auth.user?.id || !product.value?.id || isProvider.value) {
    subscriptionStatus.value = 'NONE'
    return
  }

  try {
    const res = await subscriptionApi.getMySubscriptions()
    console.log('[ProductDetail] my subscriptions response =', res.data)

    const subscriptions = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : []

    const matched = subscriptions.find(item => Number(item.productId) === Number(product.value.id))

    if (!matched) {
      subscriptionStatus.value = 'NONE'
      return
    }

    subscriptionStatus.value = matched.status === 'ACTIVE' ? 'ACTIVE' : 'PENDING'
  } catch (e) {
    console.error('[ProductDetail] failed to load subscription status:', e)
    subscriptionStatus.value = 'NONE'
  }
}

async function handlePrimaryAction() {
  subscribeError.value = ''

  if (!product.value?.id) {
    subscribeError.value = 'API 상품 정보가 올바르지 않습니다.'
    return
  }

  if (isProvider.value) {
    subscribeError.value = '제공사 계정은 자사 API 상품을 구독할 수 없습니다.'
    return
  }

  if (subscriptionStatus.value === 'ACTIVE') {
    router.push('/subscriptions')
    return
  }

  if (subscriptionStatus.value === 'PENDING') {
    return
  }

  subscribing.value = true

  try {
    await subscriptionApi.subscribe(product.value.id)
    subscriptionStatus.value = 'PENDING'
  } catch (e) {
    console.error('[ProductDetail] subscribe failed:', e)
    subscribeError.value = e.response?.data?.message || '결제/구독 신청에 실패했습니다.'
  } finally {
    subscribing.value = false
  }
}

onMounted(async () => {
  await productStore.fetchProduct(route.params.id)
  console.log('[ProductDetail] selectedProduct =', productStore.selectedProduct)
  await loadSubscriptionStatus()
})

watch(
  () => productStore.selectedProduct,
  async (value) => {
    console.log('[ProductDetail] selectedProduct changed =', value)
    if (value?.id) {
      await loadSubscriptionStatus()
    }
  },
  { deep: true }
)
</script>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

.detail-hero {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-bottom: 1px solid var(--color-border);
  padding: 48px 0;
}

.detail-hero-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 48px;
  align-items: start;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-title {
  font-size: 30px;
  font-weight: 700;
  line-height: 1.3;
}

.detail-desc {
  font-size: 15px;
  color: var(--color-text-secondary);
  line-height: 1.7;
}

.detail-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
  flex-wrap: wrap;
}

.enroll-card {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.enroll-thumb {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.enroll-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
}

.thumb-teal { background: #E1F5EE; }
.thumb-blue { background: #E6F1FB; }
.thumb-purple { background: #EEEDFE; }
.thumb-pink { background: #FBEAF0; }
.thumb-gray { background: #F1EFE8; }

.enroll-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.price-unit {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
}
.enroll-price {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-primary);
}

.btn-full {
  width: 100%;
  padding: 13px;
  font-size: 15px;
  justify-content: center;
}

.btn-disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.enroll-info-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.enroll-info-list li {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.error-msg {
  font-size: 13px;
  color: #dc2626;
  padding: 8px 12px;
  background: #fef2f2;
  border-radius: var(--radius-sm);
}

.helper-text {
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.5;
}

.empty-text {
  font-size: 14px;
  color: var(--color-text-muted);
}

.loading-center {
  display: flex;
  justify-content: center;
  padding: 100px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.badge-gray {
  background: #f3f4f6;
  color: #6b7280;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 900px) {
  .detail-hero-inner {
    grid-template-columns: 1fr;
  }
}
</style>