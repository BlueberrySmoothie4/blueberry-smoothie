import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth.js'

const routes = [
  {
    path: '/',
    name: 'Landing',
    component: () => import('@/views/LandingView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/callback',
    name: 'Callback',
    component: () => import('@/views/CallbackView.vue')
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/ProductListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/products/new',
    name: 'ProductCreate',
    component: () => import('@/views/ProductCreateView.vue'),
    meta: { requiresAuth: true, providerOnly: true }
  },
  {
    path: '/products/:id(\\d+)',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/subscriptions',
    name: 'Subscription',
    component: () => import('@/views/SubscriptionView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: () => import('@/views/MyPageView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 인증/권한 가드
router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'Login' }
  }

  if (to.meta.guestOnly && auth.isAuthenticated) {
    return { name: 'ProductList' }
  }

  if (to.meta.providerOnly && auth.user?.role !== 'PROVIDER') {
    return { name: 'ProductList' }
  }
})

export default router
