<template>
  <header class="app-header" :class="{ overlay, scrolled: isScrolled }">
    <div class="header-inner">
      <router-link to="/" class="brand">{{ BRAND.name }}</router-link>

      <nav class="nav-links" v-if="auth.isAuthenticated">
        <router-link
          to="/courses"
          class="nav-link"
          :class="{ active: $route.path.startsWith('/courses') }"
          >API 카탈로그</router-link
        >
        <router-link
          to="/enrollments"
          class="nav-link"
          :class="{ active: $route.path === '/enrollments' }"
          >내 구독</router-link
        >
      </nav>

      <div class="header-actions">
        <template v-if="auth.isAuthenticated">
          <router-link
            to="/mypage"
            class="user-avatar"
            :title="auth.user?.name"
          >
            {{ auth.user?.name?.charAt(0) || "?" }}
          </router-link>
          <button class="header-link" @click="handleLogout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/login" class="header-link">로그인</router-link>
          <router-link to="/login" class="header-cta">시작하기</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useAuthStore } from "@/store/auth.js";
import { useRouter } from "vue-router";
import { BRAND } from "@/config/brand.js";

defineProps({
  overlay: { type: Boolean, default: false },
});

const auth = useAuthStore();
const router = useRouter();
const isScrolled = ref(false);

function onScroll() {
  isScrolled.value = window.scrollY > 8;
}

function handleLogout() {
  auth.logout();
  router.push("/");
}

onMounted(() => {
  onScroll();
  window.addEventListener("scroll", onScroll, { passive: true });
});
onUnmounted(() => window.removeEventListener("scroll", onScroll));
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(16px);
  border-bottom: 1px solid transparent;
  transition:
    background 0.3s ease,
    border-color 0.3s ease;
}

.app-header.scrolled {
  border-bottom-color: var(--color-border);
}

/* 다크 페이지 위에 얹히는 상태 */
.app-header.overlay {
  background: transparent;
  backdrop-filter: none;
}

.app-header.overlay.scrolled {
  background: rgba(10, 11, 13, 0.72);
  backdrop-filter: saturate(180%) blur(16px);
  border-bottom-color: rgba(255, 255, 255, 0.09);
}

.header-inner {
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 28px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
  flex-shrink: 0;
  transition: color 0.3s ease;
}

.nav-links {
  display: flex;
  gap: 2px;
  flex: 1;
}

.nav-link {
  padding: 7px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  transition: var(--transition);
}

.nav-link:hover,
.nav-link.active {
  color: var(--color-text-primary);
  background: var(--color-bg-secondary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
}

.header-link {
  padding: 8px 12px;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  transition: var(--transition);
}

.header-link:hover {
  color: var(--color-text-primary);
}

.header-cta {
  padding: 9px 18px;
  border-radius: 10px;
  background: var(--ink);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: -0.01em;
  transition: var(--transition);
}

.header-cta:hover {
  background: #24262b;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.user-avatar:hover {
  background: var(--color-primary);
  color: #fff;
}

/* overlay 상태의 밝은 텍스트 */
.app-header.overlay .nav-link,
.app-header.overlay .header-link {
  color: rgba(255, 255, 255, 0.72);
}

.app-header.overlay .brand {
  color: #fff;
}

.app-header.overlay .nav-link:hover,
.app-header.overlay .header-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.app-header.overlay .header-cta {
  background: #fff;
  color: var(--ink);
}

.app-header.overlay .header-cta:hover {
  background: rgba(255, 255, 255, 0.88);
}

@media (max-width: 640px) {
  .header-inner {
    padding: 0 20px;
    gap: 16px;
  }
  .nav-links {
    display: none;
  }
}
</style>
