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
            class="sidebar-item active"
          >
            <span class="si-icon">✅</span> 내 구독 목록
          </router-link>

          <router-link to="/mypage" class="sidebar-item">
            <span class="si-icon">⭐</span> 마이페이지
          </router-link>
        </div>

        <div class="sidebar-section">
          <div class="sidebar-label">계정</div>
          <router-link to="/mypage" class="sidebar-item">
            <span class="si-icon">👤</span> 마이페이지
          </router-link>
          <button class="sidebar-item sidebar-btn" @click="handleLogout">
            <span class="si-icon">🚪</span> 로그아웃
          </button>
        </div>
      </aside>

      <main class="main-content">
        <h1 class="page-title">내 구독 목록</h1>

        <div v-if="loading" class="loading-center">
          <div class="spinner"></div>
        </div>

        <div v-else-if="subscriptions.length" class="enrollment-list fade-in">
          <div v-for="item in subscriptions" :key="item.id" class="enrollment-card">
            <div class="enroll-thumb" :class="getThumbBg(item.product?.category)">
              <img :src="getThumbSrc(item.product)" :alt="item.product?.title" />
            </div>

            <div class="enroll-info">
              <span class="badge" :class="getBadge(item.product?.category)">
                {{ item.product?.category }}
              </span>
              <h3 class="enroll-title">{{ item.product?.title }}</h3>
              <p class="enroll-instructor">제공사: {{ item.product?.providerName }}</p>
            </div>

            <div class="enroll-status">
              <span
                :class="[
                  'status-badge',
                  item.status === 'ACTIVE' ? 'status-active' : 'status-pending'
                ]"
              >
                {{ item.status === 'ACTIVE' ? '구독 중' : '대기 중' }}
              </span>
              <router-link :to="`/products/${item.productId}`" class="btn btn-ghost btn-sm">
                상품 보기
              </router-link>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <p class="empty-icon">📭</p>
          <p>구독 중인 API 상품이 없습니다.</p>
          <router-link to="/products" class="btn btn-primary" style="margin-top:16px;">
            API 상품 둘러보기
          </router-link>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import { subscriptionApi } from '@/api/subscription.js'
import { useAuthStore } from '@/store/auth.js'

const router = useRouter()
const auth = useAuthStore()

const subscriptions = ref([])
const loading = ref(true)

const isProvider = computed(() => auth.user?.role === 'PROVIDER')

const categoryConfig = {
  '비전': { bg: 'thumb-purple', badge: 'badge-purple', thumb: 'python' },
  '음성': { bg: 'thumb-teal', badge: 'badge-teal', thumb: 'vue_js' },
  '언어': { bg: 'thumb-teal', badge: 'badge-teal', thumb: 'spring_boot' },
  '문서': { bg: 'thumb-blue', badge: 'badge-blue', thumb: 'docker' },
  '생성형': { bg: 'thumb-pink', badge: 'badge-pink', thumb: 'generative_ai' },
  '예측': { bg: 'thumb-blue', badge: 'badge-blue', thumb: 'kubernetes' },
  '이상탐지': { bg: 'thumb-amber', badge: 'badge-amber', thumb: 'docker' },
}

function getThumbBg(cat) {
  return categoryConfig[cat]?.bg || 'thumb-gray'
}

function getBadge(cat) {
  return categoryConfig[cat]?.badge || 'badge-gray'
}

function getThumbSrc(product) {
  const key = product?.thumbnail || categoryConfig[product?.category]?.thumb
  if (!key) return ''
  try {
    return new URL(`../assets/images/courses/${key}.png`, import.meta.url).href
  } catch {
    return ''
  }
}

function handleLogout() {
  auth.logout()
  router.push('/')
}

onMounted(async () => {
  // 제공사는 이 페이지 접근 불가 → 마이페이지로 이동
  if (isProvider.value) {
    console.warn('[SubscriptionView] provider tried to access /subscriptions, redirect to /mypage')
    router.replace('/mypage')
    return
  }

  try {
    const res = await subscriptionApi.getMySubscriptions()
    console.log('[SubscriptionView] my subscriptions response:', res.data)

    if (Array.isArray(res.data?.data)) {
      subscriptions.value = res.data.data
    } else if (Array.isArray(res.data)) {
      subscriptions.value = res.data
    } else {
      subscriptions.value = []
    }
  } catch (error) {
    console.error('[SubscriptionView] failed to load subscriptions:', error)
    subscriptions.value = []
  } finally {
    loading.value = false
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
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
}

.enrollment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.enrollment-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 16px;
  transition: var(--transition);
}

.enrollment-card:hover {
  box-shadow: var(--shadow-sm);
}

.enroll-thumb {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.enroll-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 8px;
}

.thumb-teal {
  background: #E1F5EE;
}

.thumb-blue {
  background: #E6F1FB;
}

.thumb-purple {
  background: #EEEDFE;
}

.thumb-pink {
  background: #FBEAF0;
}

.thumb-gray {
  background: #F1EFE8;
}

.enroll-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.enroll-title {
  font-size: 15px;
  font-weight: 600;
}

.enroll-instructor {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.enroll-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background: #E1F5EE;
  color: #0F6E56;
}

.status-pending {
  background: #FAEEDA;
  color: #854F0B;
}

.btn-sm {
  padding: 7px 14px;
  font-size: 13px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: var(--color-text-muted);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.loading-center {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>