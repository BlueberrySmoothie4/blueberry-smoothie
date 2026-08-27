<template>
  <aside class="rail">
    <router-link to="/" class="rail-brand">
      <span class="rail-name">
        <span class="rail-sk">{{ BRAND.prefix }}</span
        ><span>{{ BRAND.suffix }}</span>
      </span>
      <span class="rail-mark">{{ BRAND.mark }}</span>
    </router-link>

    <nav class="rail-nav">
      <p class="rail-section">메뉴</p>

      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="rail-link"
        :class="{ active: item.isActive($route.path) }"
      >
        <svg
          class="rail-svg"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.7"
          stroke-linecap="round"
          stroke-linejoin="round"
          v-html="item.icon"
        ></svg>
        <span class="rail-label">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="rail-footer">
      <router-link to="/mypage" class="rail-user">
        <span class="rail-avatar">{{ auth.user?.name?.charAt(0) || "?" }}</span>
        <span class="rail-user-info">
          <span class="rail-user-name">{{ auth.user?.name || "사용자" }}</span>
          <span class="rail-user-role">
            {{ isProvider ? "API 제공사" : "구매 기업" }}
          </span>
        </span>
      </router-link>

      <button class="rail-logout" @click="handleLogout" title="로그아웃">
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.7"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
          <polyline points="16 17 21 12 16 7" />
          <line x1="21" y1="12" x2="9" y2="12" />
        </svg>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth.js";
import { BRAND } from "@/config/brand.js";

const auth = useAuthStore();
const router = useRouter();

const isProvider = computed(() => auth.user?.role === "PROVIDER");

const ICONS = {
  catalog: `<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>`,
  create: `<circle cx="12" cy="12" r="9"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>`,
  subscription: `<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M9 12l2 2 4-4"/>`,
  profile: `<circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/>`,
};

const navItems = computed(() => {
  const items = [
    {
      to: "/products",
      label: "API 카탈로그",
      icon: ICONS.catalog,
      isActive: (path) =>
        path.startsWith("/products") && path !== "/products/new",
    },
  ];

  if (isProvider.value) {
    items.push({
      to: "/products/new",
      label: "API 등록",
      icon: ICONS.create,
      isActive: (path) => path === "/products/new",
    });
  } else {
    items.push({
      to: "/subscriptions",
      label: "내 구독",
      icon: ICONS.subscription,
      isActive: (path) => path === "/subscriptions",
    });
  }

  items.push({
    to: "/mypage",
    label: "마이페이지",
    icon: ICONS.profile,
    isActive: (path) => path === "/mypage",
  });

  return items;
});

function handleLogout() {
  auth.logout();
  router.push("/");
}
</script>

<style scoped>
.rail {
  position: sticky;
  top: 0;
  height: 100vh;
  width: var(--sidebar-width);
  flex: none;
  background: var(--color-bg-primary);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  padding: 22px 14px;
}

.rail-brand {
  display: flex;
  align-items: center;
  padding: 6px 10px;
  margin-bottom: 28px;
}

.rail-name {
  display: inline-flex;
  align-items: baseline;
  gap: 3px;
  font-size: 16.5px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
}

.rail-sk {
  color: var(--sk-red);
}

.rail-mark {
  display: none;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
}

.rail-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.rail-section {
  font-family: var(--font-mono);
  font-size: 10.5px;
  font-weight: 500;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  padding: 0 10px;
  margin-bottom: 10px;
}

.rail-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: -0.02em;
  color: var(--color-text-secondary);
  transition: var(--transition);
}

.rail-svg {
  width: 19px;
  height: 19px;
  flex-shrink: 0;
  opacity: 0.75;
}

.rail-link:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

.rail-link.active {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  font-weight: 700;
}

.rail-link.active .rail-svg {
  opacity: 1;
  color: var(--color-primary);
}

/* 활성 항목 좌측 인디케이터 */
.rail-link.active::before {
  content: "";
  position: absolute;
  left: -14px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  border-radius: 0 3px 3px 0;
  background: var(--color-primary);
}

.rail-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
}

.rail-user {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
  padding: 7px 8px;
  border-radius: 10px;
  transition: var(--transition);
}

.rail-user:hover {
  background: var(--color-bg-secondary);
}

.rail-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--color-text-primary);
  color: #fff;
  font-size: 12.5px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.rail-user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.35;
}

.rail-user-name {
  font-size: 13px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rail-user-role {
  font-size: 11px;
  color: var(--color-text-muted);
}

.rail-logout {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  background: none;
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: var(--transition);
}

.rail-logout svg {
  width: 17px;
  height: 17px;
}

.rail-logout:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

@media (max-width: 900px) {
  .rail {
    width: 64px;
    padding: 18px 8px;
  }
  .rail-name,
  .rail-label,
  .rail-section,
  .rail-user-info {
    display: none;
  }
  .rail-mark {
    display: block;
  }
  .rail-brand {
    justify-content: center;
    padding: 6px 0;
  }
  .rail-link {
    justify-content: center;
    padding: 11px;
  }
  .rail-link.active::before {
    left: -8px;
  }
  .rail-user {
    justify-content: center;
    padding: 6px;
  }
  .rail-footer {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
