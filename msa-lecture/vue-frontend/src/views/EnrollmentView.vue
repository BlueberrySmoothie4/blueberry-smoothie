<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <header class="page-header">
          <p class="page-eyebrow">Subscriptions</p>
          <div class="page-header-row">
            <div>
              <h1 class="page-title">구독 중인 API</h1>
              <p class="page-subtitle">
                구독이 활성화되면 API Key가 발급돼 바로 호출할 수 있습니다.
              </p>
            </div>
            <span class="count-badge" v-if="!loading">
              <span class="tabular">{{ enrollments.length }}</span
              >건
            </span>
          </div>
        </header>

        <div v-if="loading" class="loading-center">
          <div class="spinner"></div>
        </div>

        <div v-else-if="enrollments.length" class="sub-list">
          <article
            v-for="item in enrollments"
            :key="item.id"
            class="sub-item surface"
          >
            <div class="sub-icon" :class="getIconTone(item.course?.category)">
              {{ item.course?.category?.charAt(0) || "A" }}
            </div>

            <div class="sub-info">
              <div class="sub-top">
                <span class="badge badge-gray">{{
                  item.course?.category
                }}</span>
                <span
                  class="status-pill"
                  :class="item.status === 'ACTIVE' ? 'active' : 'pending'"
                >
                  <span class="status-dot"></span>
                  {{ item.status === "ACTIVE" ? "구독 중" : "대기 중" }}
                </span>
              </div>
              <h3 class="sub-title">{{ item.course?.title }}</h3>
              <p class="sub-provider">{{ item.course?.instructorName }}</p>

              <div
                class="key-row"
                :class="{ issued: item.status === 'ACTIVE' }"
              >
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path
                    d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"
                  />
                </svg>
                <code v-if="item.status === 'ACTIVE'"
                  >nx_live_••••••••{{ keySuffix(item.id) }}</code
                >
                <span v-else>결제 처리 후 Key가 발급됩니다</span>
              </div>
            </div>

            <router-link :to="`/courses/${item.courseId}`" class="sub-link">
              API 보기
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </router-link>
          </article>
        </div>

        <div v-else class="empty-state">
          <div class="empty-state-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.7"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M22 12h-6l-2 3h-4l-2-3H2" />
              <path
                d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"
              />
            </svg>
          </div>
          <p class="empty-state-title">구독 중인 API가 없습니다</p>
          <p>카탈로그에서 필요한 API를 찾아보세요.</p>
          <router-link to="/courses" class="btn btn-primary empty-action">
            API 둘러보기
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import AppSideRail from "@/components/AppSideRail.vue";
import { enrollmentApi } from "@/api/enrollment.js";
import { useAuthStore } from "@/store/auth.js";

const router = useRouter();
const auth = useAuthStore();

const enrollments = ref([]);
const loading = ref(true);

const isInstructor = computed(() => auth.user?.role === "INSTRUCTOR");

const iconTones = {
  백엔드: "tone-teal",
  프론트엔드: "tone-teal",
  DevOps: "tone-blue",
  데이터: "tone-purple",
  AI: "tone-pink",
};

function getIconTone(cat) {
  return iconTones[cat] || "tone-gray";
}

/** 실제 Key가 내려오기 전까지 노출용 접미사 */
function keySuffix(id) {
  return String(id).padStart(3, "0").slice(-3);
}

onMounted(async () => {
  if (isInstructor.value) {
    router.replace("/mypage");
    return;
  }

  try {
    const res = await enrollmentApi.getMyEnrollments();
    if (Array.isArray(res.data?.data)) {
      enrollments.value = res.data.data;
    } else if (Array.isArray(res.data)) {
      enrollments.value = res.data;
    }
  } catch {
    enrollments.value = [];
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.count-badge {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
  padding: 5px 12px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: 999px;
  flex-shrink: 0;
}

.sub-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sub-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 22px 24px;
  transition:
    border-color 0.22s ease,
    box-shadow 0.22s ease;
}

.sub-item:hover {
  border-color: var(--color-border-hover);
  box-shadow: var(--shadow-sm);
}

.sub-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  font-weight: 800;
  flex-shrink: 0;
}

.tone-teal {
  background: #e6faf5;
  color: #0ca678;
}
.tone-blue {
  background: #e8f3ff;
  color: #1b64da;
}
.tone-purple {
  background: #f3f0ff;
  color: #7048e8;
}
.tone-pink {
  background: #fff0f6;
  color: #d6336c;
}
.tone-gray {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

.sub-info {
  flex: 1;
  min-width: 0;
}

.sub-top {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 8px;
}

.sub-title {
  font-size: 16.5px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  line-height: 1.4;
}

.sub-provider {
  font-size: 13px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.key-row {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-top: 14px;
  padding: 9px 12px;
  border-radius: 9px;
  background: var(--color-bg-secondary);
  font-size: 12.5px;
  color: var(--color-text-muted);
}

.key-row svg {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.key-row code {
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 0.02em;
}

.key-row.issued {
  background: var(--color-success-light);
  color: var(--color-success);
}

.sub-link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 13.5px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--color-text-secondary);
  flex-shrink: 0;
  transition: var(--transition);
}

.sub-link svg {
  width: 15px;
  height: 15px;
}

.sub-link:hover {
  color: var(--color-primary);
}

.empty-action {
  margin-top: 20px;
}

@media (max-width: 640px) {
  .sub-item {
    flex-wrap: wrap;
  }
  .sub-link {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
