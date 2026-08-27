<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content" v-if="course">
        <router-link to="/courses" class="back-link">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="15 18 9 12 15 6" />
          </svg>
          API 카탈로그
        </router-link>

        <div class="detail-layout">
          <div class="detail-main">
            <div class="detail-head">
              <span class="badge" :class="badgeClass">{{
                displayCategory
              }}</span>
              <h1 class="detail-title">{{ course.title }}</h1>
              <p class="detail-desc">
                {{
                  course.description ||
                  "성능과 제약 조건을 확인하고 바로 연동할 수 있는 AI API입니다."
                }}
              </p>
            </div>

            <div class="block">
              <h2 class="block-title">스펙</h2>
              <dl class="spec-sheet">
                <div class="spec-row">
                  <dt>제공사</dt>
                  <dd>{{ displayInstructorName }}</dd>
                </div>
                <div class="spec-row">
                  <dt>카테고리</dt>
                  <dd>{{ displayCategory }}</dd>
                </div>
                <div class="spec-row">
                  <dt>구독 기업</dt>
                  <dd class="tabular">{{ displayEnrollmentCount }}개사</dd>
                </div>
                <div class="spec-row">
                  <dt>월 요금</dt>
                  <dd class="tabular">₩{{ displayPrice }}</dd>
                </div>
              </dl>
            </div>

            <div class="block">
              <h2 class="block-title">호출 예시</h2>
              <pre
                class="code-block"
              ><span class="c-com"># 발급된 Key를 헤더에 담아 호출합니다</span>
<span class="c-cmd">curl</span> https://{{ BRAND.apiHost }}/v1/{{ endpointSlug }} \
  -H <span class="c-str">"X-API-Key: $YOUR_API_KEY"</span></pre>
            </div>
          </div>

          <aside class="plan-panel surface">
            <div class="plan-thumb" :class="thumbBg">
              <img v-if="thumbSrc" :src="thumbSrc" :alt="course.title" />
            </div>

            <div class="plan-body">
              <div class="plan-price-block">
                <span class="plan-price tabular">₩{{ displayPrice }}</span>
                <span class="plan-period">/ 월</span>
              </div>

              <button
                class="btn btn-primary btn-full"
                @click="handlePrimaryAction"
                :disabled="buttonDisabled"
              >
                <span v-if="enrolling">처리 중...</span>
                <span v-else>{{ buttonLabel }}</span>
              </button>

              <p v-if="enrollError" class="error-msg">{{ enrollError }}</p>
              <p v-else-if="helperText" class="helper-text">{{ helperText }}</p>

              <ul class="plan-features">
                <li v-for="f in features" :key="f">
                  <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2.5"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                  {{ f }}
                </li>
              </ul>
            </div>
          </aside>
        </div>
      </div>

      <div v-else-if="loading" class="loading-center">
        <div class="spinner"></div>
      </div>

      <div v-else class="shell-content">
        <div class="empty-state">
          <div class="empty-state-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.7"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="12" cy="12" r="9" />
              <line x1="12" y1="8" x2="12" y2="13" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
          </div>
          <p class="empty-state-title">API 정보를 불러오지 못했습니다</p>
          <p>주소를 확인하거나 잠시 후 다시 시도해 주세요.</p>
          <router-link to="/courses" class="btn btn-ghost empty-action">
            카탈로그로 돌아가기
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import AppSideRail from "@/components/AppSideRail.vue";
import { useCourseStore } from "@/store/course.js";
import { enrollmentApi } from "@/api/enrollment.js";
import { useAuthStore } from "@/store/auth.js";
import { BRAND } from "@/config/brand.js";

const route = useRoute();
const router = useRouter();
const courseStore = useCourseStore();
const auth = useAuthStore();

const enrolling = ref(false);
const enrollError = ref("");
const enrollmentStatus = ref("NONE");

const features = [
  "결제 즉시 Key 발급",
  "언제든 재발급 가능",
  "사용량 대시보드 제공",
];

const course = computed(() => courseStore.selectedCourse);
const loading = computed(() => courseStore.loading);
const isInstructor = computed(() => auth.user?.role === "INSTRUCTOR");

const categoryConfig = {
  백엔드: { badge: "badge-teal", bg: "thumb-teal", thumb: "spring_boot" },
  프론트엔드: { badge: "badge-teal", bg: "thumb-teal", thumb: "vue_js" },
  DevOps: { badge: "badge-blue", bg: "thumb-blue", thumb: "kubernetes" },
  데이터: { badge: "badge-purple", bg: "thumb-purple", thumb: "python" },
  AI: { badge: "badge-pink", bg: "thumb-pink", thumb: "generative_ai" },
};

const config = computed(() => categoryConfig[course.value?.category] || {});
const badgeClass = computed(() => config.value.badge || "badge-gray");
const thumbBg = computed(() => config.value.bg || "thumb-gray");

const displayCategory = computed(() => course.value?.category || "-");

const endpointSlug = computed(() => `apis/${course.value?.id ?? 0}/invoke`);

const displayInstructorName = computed(
  () =>
    course.value?.instructorName ||
    course.value?.teacherName ||
    course.value?.instructor?.name ||
    course.value?.instructor_name ||
    course.value?.ownerName ||
    "제공사 정보 없음",
);

const displayEnrollmentCount = computed(() => {
  const value = Number(
    course.value?.enrollmentCount ?? course.value?.enrollment_count ?? 0,
  );
  return Number.isNaN(value) ? 0 : value.toLocaleString();
});

const displayPrice = computed(() => {
  const value = Number(course.value?.price ?? 0);
  return Number.isNaN(value) ? "0" : value.toLocaleString();
});

const thumbSrc = computed(() => {
  const key = course.value?.thumbnail || config.value.thumb;
  if (!key) return null;
  try {
    return new URL(`../assets/images/courses/${key}.png`, import.meta.url).href;
  } catch {
    return null;
  }
});

const buttonLabel = computed(() => {
  if (isInstructor.value) return "제공사 계정은 구독 불가";
  if (enrollmentStatus.value === "ACTIVE") return "내 구독 목록으로 이동";
  if (enrollmentStatus.value === "PENDING") return "신청 완료 · 결제 처리 중";
  return "결제하고 구독하기";
});

const buttonDisabled = computed(() => {
  if (enrolling.value) return true;
  if (isInstructor.value) return true;
  if (enrollmentStatus.value === "PENDING") return true;
  return false;
});

const helperText = computed(() => {
  if (isInstructor.value)
    return "제공사 계정은 본인 API를 구독 신청할 수 없습니다.";
  if (enrollmentStatus.value === "ACTIVE")
    return "이미 구독 중입니다. 내 구독 목록에서 Key를 확인하세요.";
  if (enrollmentStatus.value === "PENDING")
    return "구독 신청이 접수되었습니다. 결제 처리 후 Key가 발급됩니다.";
  return "결제를 진행하면 구독 신청이 함께 처리됩니다.";
});

async function loadEnrollmentStatus() {
  if (!auth.user?.id || !course.value?.id || isInstructor.value) {
    enrollmentStatus.value = "NONE";
    return;
  }

  try {
    const res = await enrollmentApi.getMyEnrollments();
    const enrollments = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : [];
    const matched = enrollments.find(
      (item) => Number(item.courseId) === Number(course.value.id),
    );
    enrollmentStatus.value = matched
      ? matched.status === "ACTIVE"
        ? "ACTIVE"
        : "PENDING"
      : "NONE";
  } catch {
    enrollmentStatus.value = "NONE";
  }
}

async function handlePrimaryAction() {
  enrollError.value = "";

  if (!course.value?.id) {
    enrollError.value = "API 정보가 올바르지 않습니다.";
    return;
  }
  if (isInstructor.value) {
    enrollError.value = "제공사 계정은 본인 API를 구독 신청할 수 없습니다.";
    return;
  }
  if (enrollmentStatus.value === "ACTIVE") {
    router.push("/enrollments");
    return;
  }
  if (enrollmentStatus.value === "PENDING") return;

  enrolling.value = true;
  try {
    await enrollmentApi.enroll(course.value.id);
    enrollmentStatus.value = "PENDING";
  } catch (e) {
    enrollError.value =
      e.response?.data?.message || "결제/구독 신청에 실패했습니다.";
  } finally {
    enrolling.value = false;
  }
}

onMounted(async () => {
  await courseStore.fetchCourse(route.params.id);
  await loadEnrollmentStatus();
});

watch(
  () => courseStore.selectedCourse,
  async (value) => {
    if (value?.id) await loadEnrollmentStatus();
  },
  { deep: true },
);
</script>

<style scoped>
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--color-text-muted);
  margin-bottom: 24px;
  transition: var(--transition);
}

.back-link svg {
  width: 17px;
  height: 17px;
}

.back-link:hover {
  color: var(--color-primary);
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 32px;
  align-items: start;
}

.detail-main {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.detail-head {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  padding-bottom: 4px;
}

.detail-title {
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.3;
  color: var(--color-text-primary);
}

.detail-desc {
  font-size: 15.5px;
  color: var(--color-text-secondary);
  line-height: 1.72;
  max-width: 56ch;
}

.block-title {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

/* 호출 예시 */
.code-block {
  padding: 18px 20px;
  border-radius: var(--radius-md);
  background: var(--ink);
  font-family: var(--font-mono);
  font-size: 12.5px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.82);
  white-space: pre-wrap;
  word-break: break-all;
}

.c-com {
  color: rgba(255, 255, 255, 0.35);
}
.c-cmd {
  color: #7dd3fc;
}
.c-str {
  color: #fcd34d;
}

/* 구독 패널 */
.plan-panel {
  position: sticky;
  top: 24px;
  overflow: hidden;
}

.plan-thumb {
  height: 116px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.plan-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 22px;
}

.thumb-teal {
  background: #e6faf5;
}
.thumb-blue {
  background: #e8f3ff;
}
.thumb-purple {
  background: #f3f0ff;
}
.thumb-pink {
  background: #fff0f6;
}
.thumb-gray {
  background: var(--color-bg-secondary);
}

.plan-body {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  border-top: 1px solid var(--color-border);
}

.plan-price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.plan-price {
  font-size: 27px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.plan-period {
  font-size: 14px;
  color: var(--color-text-muted);
}

.btn-full {
  width: 100%;
  padding: 13px;
  font-size: 15px;
  justify-content: center;
}

.plan-features {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 4px;
}

.plan-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.plan-features svg {
  width: 15px;
  height: 15px;
  color: var(--color-success);
  flex-shrink: 0;
}

.error-msg {
  font-size: 13px;
  color: var(--color-danger);
  padding: 10px 12px;
  background: var(--color-danger-light);
  border-radius: var(--radius-sm);
  line-height: 1.5;
}

.helper-text {
  font-size: 12.5px;
  color: var(--color-text-muted);
  line-height: 1.55;
}

.empty-action {
  margin-top: 20px;
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .plan-panel {
    position: static;
  }
  .detail-title {
    font-size: 25px;
  }
}
</style>
