<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <!-- 프로필 -->
        <header class="page-header profile-header">
          <div class="profile">
            <span class="profile-avatar">
              {{ auth.user?.name?.charAt(0) || "?" }}
            </span>
            <div class="profile-info">
              <h1 class="profile-name">{{ auth.user?.name || "사용자" }}</h1>
              <p class="profile-email">{{ auth.user?.email || "-" }}</p>
            </div>
          </div>
          <span class="role-badge" :class="isInstructor ? 'provider' : 'buyer'">
            {{ isInstructor ? "API 제공사" : "구매 기업 담당자" }}
          </span>
        </header>

        <!-- 구매 기업: 추천 -->
        <section v-if="!isInstructor">
          <p class="section-label">Recommended</p>
          <h2 class="section-heading">함께 쓰면 좋은 API</h2>

          <p v-if="recommendMessage" class="recommend-msg">
            {{ recommendMessage }}
          </p>

          <div v-if="recommendLoading" class="card-grid">
            <div v-for="i in 3" :key="i" class="skeleton-card surface">
              <div class="skeleton-block sk-thumb"></div>
              <div class="skeleton-block sk-line long"></div>
              <div class="skeleton-block sk-line medium"></div>
            </div>
          </div>

          <div v-else-if="recommendations.length" class="card-grid">
            <CourseCard v-for="c in recommendations" :key="c.id" :course="c" />
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
                <path
                  d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                />
              </svg>
            </div>
            <p class="empty-state-title">
              {{
                recommendError
                  ? "추천을 불러오지 못했습니다"
                  : "아직 추천할 API가 없습니다"
              }}
            </p>
            <p>
              {{
                recommendError ||
                "API를 구독하면 사용 패턴에 맞는 추천이 표시됩니다."
              }}
            </p>
          </div>
        </section>

        <!-- 제공사: 대시보드 -->
        <section v-else>
          <p class="section-label">Dashboard</p>
          <h2 class="section-heading">내가 등록한 API</h2>

          <div class="kpi-row">
            <div class="kpi surface">
              <span class="kpi-label">등록 API 수</span>
              <span class="kpi-value tabular">{{ myCourses.length }}</span>
            </div>
            <div class="kpi surface kpi-accent">
              <span class="kpi-label">총 구독 기업 수</span>
              <span class="kpi-value tabular">
                {{ totalEnrollmentCount.toLocaleString() }}
              </span>
            </div>
          </div>

          <div v-if="instructorLoading" class="api-list">
            <div
              v-for="i in 2"
              :key="i"
              class="skeleton-card surface"
              style="padding: 24px"
            >
              <div class="skeleton-block sk-line long"></div>
              <div class="skeleton-block sk-line medium"></div>
            </div>
          </div>

          <div v-else-if="myCourses.length" class="api-list">
            <article
              v-for="course in myCourses"
              :key="course.id"
              class="api-item surface"
            >
              <div class="api-item-head">
                <div>
                  <h3 class="api-item-title">{{ course.title }}</h3>
                  <p class="api-item-desc">
                    {{ course.description || "설명이 없습니다." }}
                  </p>
                </div>
                <span
                  class="status-pill"
                  :class="course.status === 'ACTIVE' ? 'active' : 'inactive'"
                >
                  <span class="status-dot"></span>
                  {{ course.status || "UNKNOWN" }}
                </span>
              </div>

              <dl class="spec-sheet">
                <div class="spec-row">
                  <dt>카테고리</dt>
                  <dd>{{ course.category || "-" }}</dd>
                </div>
                <div class="spec-row">
                  <dt>월 요금</dt>
                  <dd class="tabular">{{ formatPrice(course.price) }}</dd>
                </div>
                <div class="spec-row">
                  <dt>구독 기업</dt>
                  <dd class="tabular">
                    {{
                      course.enrollment_count ?? course.enrollmentCount ?? 0
                    }}개사
                  </dd>
                </div>
                <div class="spec-row">
                  <dt>API ID</dt>
                  <dd class="mono">#{{ course.id }}</dd>
                </div>
              </dl>

              <router-link
                :to="`/courses/${course.id}`"
                class="btn btn-ghost api-item-btn"
              >
                API 보기
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
                <circle cx="12" cy="12" r="9" />
                <line x1="12" y1="8" x2="12" y2="16" />
                <line x1="8" y1="12" x2="16" y2="12" />
              </svg>
            </div>
            <p class="empty-state-title">
              {{
                instructorError
                  ? "API 정보를 불러오지 못했습니다"
                  : "아직 등록한 API가 없습니다"
              }}
            </p>
            <p>{{ instructorError || "첫 API 상품을 등록해 보세요." }}</p>
            <router-link
              v-if="!instructorError"
              to="/courses/new"
              class="btn btn-primary empty-action"
            >
              API 등록하기
            </router-link>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import AppSideRail from "@/components/AppSideRail.vue";
import CourseCard from "@/components/CourseCard.vue";
import { useAuthStore } from "@/store/auth.js";
import { enrollmentApi } from "@/api/enrollment.js";
import { courseApi } from "@/api/course.js";

const auth = useAuthStore();

const isInstructor = computed(() => auth.user?.role === "INSTRUCTOR");

const recommendations = ref([]);
const recommendLoading = ref(true);
const recommendError = ref("");
const recommendMessage = ref("");

const myCourses = ref([]);
const instructorLoading = ref(true);
const instructorError = ref("");

const totalEnrollmentCount = computed(() =>
  myCourses.value.reduce((sum, course) => {
    const count = Number(
      course.enrollment_count ?? course.enrollmentCount ?? 0,
    );
    return sum + (Number.isNaN(count) ? 0 : count);
  }, 0),
);

function formatPrice(price) {
  const value = Number(price ?? 0);
  if (Number.isNaN(value)) return "-";
  return `₩${value.toLocaleString()}`;
}

function getCourseInstructorId(course) {
  return (
    course.instructorId ??
    course.instructor_id ??
    course.instructor ??
    course.teacherId ??
    course.teacher_id ??
    null
  );
}

async function loadStudentRecommendations() {
  try {
    if (!auth.user?.id) {
      recommendError.value = "추천 API를 준비 중입니다.";
      return;
    }

    const res = await enrollmentApi.getRecommendations(auth.user.id);
    const payload = res.data;

    if (Array.isArray(payload?.recommendedCourses)) {
      recommendations.value = payload.recommendedCourses;
      recommendMessage.value = payload.message ?? "";
    } else if (Array.isArray(payload?.data)) {
      recommendations.value = payload.data;
      recommendMessage.value = payload.message ?? "";
    } else if (Array.isArray(payload)) {
      recommendations.value = payload;
    }
  } catch {
    recommendError.value = "잠시 후 다시 시도해 주세요.";
  } finally {
    recommendLoading.value = false;
  }
}

async function loadInstructorCourses() {
  try {
    if (!auth.user?.id) {
      instructorError.value = "계정 정보를 확인할 수 없습니다.";
      return;
    }

    const res = await courseApi.getCourses();
    const courses = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
        ? res.data
        : [];

    const instructorId = Number(auth.user.id);
    myCourses.value = courses.filter((course) => {
      const courseInstructorId = Number(getCourseInstructorId(course));
      return (
        !Number.isNaN(courseInstructorId) && courseInstructorId === instructorId
      );
    });
  } catch {
    instructorError.value = "잠시 후 다시 시도해 주세요.";
  } finally {
    instructorLoading.value = false;
  }
}

onMounted(async () => {
  if (isInstructor.value) {
    recommendLoading.value = false;
    await loadInstructorCourses();
  } else {
    instructorLoading.value = false;
    await loadStudentRecommendations();
  }
});
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: var(--color-text-primary);
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-name {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.profile-email {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.role-badge {
  padding: 6px 13px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 600;
  letter-spacing: -0.01em;
  flex-shrink: 0;
}

.role-badge.buyer {
  background: var(--color-primary-light);
  color: var(--color-primary-dark);
}

.role-badge.provider {
  background: var(--color-accent-light);
  color: var(--color-accent);
}

.recommend-msg {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: -12px 0 20px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.skeleton-card {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sk-thumb {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  margin-bottom: 8px;
}
.sk-line {
  height: 13px;
}
.sk-line.medium {
  width: 55%;
}
.sk-line.long {
  width: 85%;
}

/* KPI */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.kpi {
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.kpi-label {
  font-size: 13px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.kpi-value {
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
}

.kpi-accent {
  background: var(--color-text-primary);
  border-color: var(--color-text-primary);
}

.kpi-accent .kpi-label {
  color: rgba(255, 255, 255, 0.6);
}

.kpi-accent .kpi-value {
  color: #fff;
}

/* 등록 API 목록 */
.api-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.api-item {
  padding: 24px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  grid-template-areas:
    "head head"
    "spec action";
  gap: 18px 20px;
  align-items: end;
}

.api-item-head {
  grid-area: head;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.api-item-title {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  margin-bottom: 5px;
}

.api-item-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.55;
}

.api-item .spec-sheet {
  grid-area: spec;
  max-width: 420px;
}

.api-item-btn {
  grid-area: action;
}

.empty-action {
  margin-top: 20px;
}

@media (max-width: 992px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .card-grid {
    grid-template-columns: 1fr;
  }
  .kpi-row {
    grid-template-columns: 1fr;
  }
  .api-item {
    grid-template-columns: 1fr;
    grid-template-areas:
      "head"
      "spec"
      "action";
  }
  .api-item-head {
    flex-direction: column;
  }
  .api-item .spec-sheet {
    max-width: none;
  }
  .api-item-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
