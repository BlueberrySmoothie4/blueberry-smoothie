<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-layout">
      <aside class="sidebar">
        <div class="sidebar-section">
          <div class="sidebar-label">메뉴</div>

          <router-link to="/products" class="sidebar-item">
            <span class="si-icon">🧩</span> API 상품 목록
          </router-link>

          <router-link
            v-if="!isProvider"
            to="/subscriptions"
            class="sidebar-item"
          >
            <span class="si-icon">✅</span> 내 구독 목록
          </router-link>

          <router-link to="/mypage" class="sidebar-item active">
            <span class="si-icon">⭐</span> 마이페이지
          </router-link>
        </div>

        <div class="sidebar-section">
          <div class="sidebar-label">계정</div>
          <button class="sidebar-item sidebar-btn" @click="handleLogout">
            <span class="si-icon">🚪</span> 로그아웃
          </button>
        </div>
      </aside>

      <main class="main-content">
        <!-- 프로필 카드 -->
        <div class="profile-card fade-in-up">
          <div class="profile-avatar">{{ auth.user?.name?.charAt(0) || '?' }}</div>
          <div class="profile-info">
            <h2 class="profile-name">{{ auth.user?.name || '사용자' }}</h2>
            <p class="profile-email">{{ auth.user?.email || '-' }}</p>
            <span class="badge" :class="isProvider ? 'badge-amber' : 'badge-blue'">
              {{ isProvider ? 'API 제공사' : '구매 기업' }}
            </span>
          </div>
        </div>

        <!-- 구매 기업 화면 -->
        <section v-if="!isProvider" class="recommend-section">
          <h3 class="section-title">추천 API 상품</h3>

          <p v-if="recommendMessage" class="recommend-message">
            {{ recommendMessage }}
          </p>

          <div v-if="recommendLoading" class="loading-row">
            <div v-for="i in 3" :key="i" class="skeleton-card">
              <div class="skeleton-thumb"></div>
              <div class="skeleton-body">
                <div class="skeleton-line short"></div>
                <div class="skeleton-line"></div>
              </div>
            </div>
          </div>

          <div v-else-if="recommendations.length" class="recommend-grid fade-in">
            <ProductCard v-for="p in recommendations" :key="p.id" :product="p" />
          </div>

          <p v-else-if="recommendError" class="empty-text">
            {{ recommendError }}
          </p>

          <p v-else class="empty-text">
            아직 추천할 API 상품이 없습니다.
          </p>
        </section>

        <!-- API 제공사 화면 -->
        <section v-else class="instructor-section">
          <div class="section-head">
            <h3 class="section-title">내가 등록한 API 상품</h3>
            <span class="section-subtitle">등록한 상품과 상품별 구독 기업 수를 확인할 수 있습니다.</span>
          </div>

          <div class="summary-cards">
            <div class="summary-card">
              <div class="summary-label">등록 상품 수</div>
              <div class="summary-value">{{ myProducts.length }}</div>
            </div>
            <div class="summary-card">
              <div class="summary-label">총 구독 기업 수</div>
              <div class="summary-value">{{ totalSubscriberCount }}</div>
            </div>
          </div>

          <div v-if="providerLoading" class="loading-row instructor-loading">
            <div v-for="i in 3" :key="i" class="skeleton-card">
              <div class="skeleton-thumb"></div>
              <div class="skeleton-body">
                <div class="skeleton-line short"></div>
                <div class="skeleton-line"></div>
              </div>
            </div>
          </div>

          <div v-else-if="myProducts.length" class="instructor-course-list fade-in">
            <div
              v-for="product in myProducts"
              :key="product.id"
              class="instructor-course-card"
            >
              <div class="course-card-top">
                <div>
                  <h4 class="course-title">{{ product.title }}</h4>
                  <p class="course-desc">{{ product.description || '설명이 없습니다.' }}</p>
                </div>
                <span
                  class="status-badge"
                  :class="product.status === 'PUBLISHED' ? 'status-active' : 'status-inactive'"
                >
                  {{ product.status || 'UNKNOWN' }}
                </span>
              </div>

              <div class="course-meta-grid">
                <div class="meta-box">
                  <div class="meta-label">카테고리</div>
                  <div class="meta-value">{{ product.category || '-' }}</div>
                </div>
                <div class="meta-box">
                  <div class="meta-label">월 구독료</div>
                  <div class="meta-value">{{ formatPrice(product.price) }}</div>
                </div>
                <div class="meta-box">
                  <div class="meta-label">구독 기업 수</div>
                  <div class="meta-value">
                    {{ product.subscriber_count ?? product.subscriberCount ?? 0 }}개
                  </div>
                </div>
                <div class="meta-box">
                  <div class="meta-label">상품 ID</div>
                  <div class="meta-value">#{{ product.id }}</div>
                </div>
              </div>

              <div class="course-card-actions">
                <router-link :to="`/products/${product.id}`" class="action-btn action-primary">
                  상품 보기
                </router-link>
              </div>
            </div>
          </div>

          <p v-else-if="providerError" class="empty-text">
            {{ providerError }}
          </p>

          <p v-else class="empty-text">
            아직 등록한 API 상품이 없습니다.
          </p>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import ProductCard from '@/components/ProductCard.vue'
import { useAuthStore } from '@/store/auth.js'
import { useProductStore } from '@/store/product.js'
import { subscriptionApi } from '@/api/subscription.js'
import { productApi } from '@/api/product.js'

const router = useRouter()
const auth = useAuthStore()
const productStore = useProductStore()

const isProvider = computed(() => auth.user?.role === 'PROVIDER')

/* 구매 기업용 */
const recommendations = ref([])
const recommendLoading = ref(true)
const recommendError = ref('')
const recommendMessage = ref('')

/* API 제공사용 */
const myProducts = ref([])
const providerLoading = ref(true)
const providerError = ref('')

const totalSubscriberCount = computed(() =>
  myProducts.value.reduce((sum, product) => {
    const count = Number(product.subscriber_count ?? product.subscriberCount ?? 0)
    return sum + (Number.isNaN(count) ? 0 : count)
  }, 0)
)

function handleLogout() {
  auth.logout()
  router.push('/')
}

function formatPrice(price) {
  const value = Number(price ?? 0)
  if (Number.isNaN(value)) return '-'
  return `${value.toLocaleString()}원/월`
}

/**
 * product 객체에서 제공사 식별자 추출
 */
function getProductProviderId(product) {
  return (
    product.providerId ??
    product.provider_id ??
    product.provider ??
    product.vendorId ??
    product.vendor_id ??
    null
  )
}

async function loadBuyerRecommendations() {
  try {
    if (!auth.user) {
      console.warn('[MyPage] auth.user is missing')
      recommendError.value = '추천 API 상품을 준비 중입니다.'
      return
    }

    if (!auth.user.id) {
      console.warn('[MyPage] auth.user.id is missing:', auth.user)
      recommendError.value = '추천 API 상품을 준비 중입니다.'
      return
    }

    const res = await subscriptionApi.getRecommendations(auth.user.id)
    console.log('[MyPage] recommendation response:', res.data)

    const payload = res.data

    // 추천 응답은 백엔드 enum(VISION 등)을 그대로 담고 있으므로
    // 목록 화면과 동일하게 카테고리 라벨을 씌워준다.
    if (Array.isArray(payload?.recommendedProducts)) {
      recommendations.value = payload.recommendedProducts.map(productStore.normalizeProduct)
      recommendMessage.value = payload.message ?? ''
    } else if (Array.isArray(payload?.data)) {
      recommendations.value = payload.data.map(productStore.normalizeProduct)
      recommendMessage.value = payload.message ?? ''
    } else if (Array.isArray(payload)) {
      recommendations.value = payload.map(productStore.normalizeProduct)
      recommendMessage.value = ''
    } else {
      console.warn('[MyPage] unexpected recommendation response shape:', payload)
      recommendations.value = []
      recommendMessage.value = ''
    }
  } catch (error) {
    console.error('[MyPage] failed to load recommendations:', error)
    recommendError.value = '현재 추천 API 상품을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    recommendLoading.value = false
  }
}

async function loadProviderProducts() {
  try {
    if (!auth.user) {
      console.warn('[MyPage] provider auth.user is missing')
      providerError.value = 'API 상품 정보를 불러오지 못했습니다.'
      return
    }

    if (!auth.user.id) {
      console.warn('[MyPage] provider auth.user.id is missing:', auth.user)
      providerError.value = 'API 상품 정보를 불러오지 못했습니다.'
      return
    }

    const res = await productApi.getProducts()
    console.log('[MyPage] product list response:', res.data)

    let products = []

    if (Array.isArray(res.data?.data)) {
      products = res.data.data
    } else if (Array.isArray(res.data)) {
      products = res.data
    } else {
      console.warn('[MyPage] unexpected product response shape:', res.data)
    }

    const providerId = Number(auth.user.id)

    myProducts.value = products.filter(product => {
      const productProviderId = Number(getProductProviderId(product))
      return !Number.isNaN(productProviderId) && productProviderId === providerId
    })

    console.log('[MyPage] filtered myProducts =', myProducts.value)
  } catch (error) {
    console.error('[MyPage] failed to load provider products:', error)
    providerError.value = '현재 API 상품 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    providerLoading.value = false
  }
}

onMounted(async () => {
  if (isProvider.value) {
    recommendLoading.value = false
    await loadProviderProducts()
  } else {
    providerLoading.value = false
    await loadBuyerRecommendations()
  }
})
</script>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

.page-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 28px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 8px;
}

.sidebar-label {
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--color-text-muted);
  padding: 8px 12px 4px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--color-text-secondary);
  transition: var(--transition);
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  font-family: var(--font-sans);
  text-decoration: none;
}

.sidebar-item:hover {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.sidebar-item.active {
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-weight: 500;
}

.si-icon {
  font-size: 15px;
}

.main-content {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 28px;
  box-shadow: var(--shadow-sm);
}

.profile-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.profile-name {
  font-size: 20px;
  font-weight: 700;
}

.profile-email {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.badge-blue {
  background: #e8f1ff;
  color: #2563eb;
}

.badge-amber {
  background: #f7edd8;
  color: #9a6700;
}

.section-head {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
}

.section-subtitle {
  font-size: 13px;
  color: var(--color-text-muted);
}

.recommend-message {
  margin-bottom: 14px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.loading-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.instructor-loading {
  margin-bottom: 20px;
}

.skeleton-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--color-border);
}

.skeleton-thumb {
  height: 110px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}

.skeleton-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-line {
  height: 12px;
  border-radius: 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}

.skeleton-line.short {
  width: 40%;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(160px, 220px));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 18px 20px;
  box-shadow: var(--shadow-sm);
}

.summary-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.instructor-course-list {
  display: grid;
  gap: 18px;
}

.instructor-course-card {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 22px;
  box-shadow: var(--shadow-sm);
}

.course-card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.course-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
}

.course-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  white-space: pre-line;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 600;
}

.status-active {
  background: #eaf8ef;
  color: #0f8a3b;
}

.status-inactive {
  background: #f3f4f6;
  color: #6b7280;
}

.course-meta-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 18px;
}

.meta-box {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: 14px;
}

.meta-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 6px;
}

.meta-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.course-card-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  border-radius: var(--radius-md);
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 600;
  transition: var(--transition);
}

.action-primary {
  background: var(--color-primary);
  color: white;
}

.action-primary:hover {
  opacity: 0.92;
}

.empty-text {
  color: var(--color-text-muted);
  font-size: 14px;
}

@keyframes shimmer {
  to {
    background-position: -200% 0;
  }
}

@media (max-width: 992px) {
  .page-layout {
    grid-template-columns: 1fr;
  }

  .recommend-grid,
  .loading-row,
  .course-meta-grid {
    grid-template-columns: 1fr;
  }

  .summary-cards {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .profile-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .course-card-top {
    flex-direction: column;
  }

  .summary-cards {
    grid-template-columns: 1fr;
  }
}
</style>