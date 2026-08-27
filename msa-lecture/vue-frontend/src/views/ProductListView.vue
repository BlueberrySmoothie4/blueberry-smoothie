<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
        <header class="page-header">
          <div class="page-header-row">
            <div>
              <p class="page-eyebrow">Catalog</p>
              <h1 class="page-title">필요한 AI API를 찾아보세요</h1>
              <p class="page-subtitle">
                {{
                  isInstructor
                    ? "등록된 API를 관리하고 새 상품을 추가하세요."
                    : "카테고리·요금·구독 기업 수를 비교해 맞는 API를 고르세요."
                }}
              </p>
            </div>
            <router-link
              v-if="isInstructor"
              to="/courses/new"
              class="btn btn-primary"
            >
              API 등록
            </router-link>
          </div>
        </header>

        <div class="toolbar">
          <div class="filter-bar">
            <button
              v-for="cat in categories"
              :key="cat"
              :class="['filter-chip', { active: selectedCategory === cat }]"
              @click="selectCategory(cat)"
            >
              {{ cat }}
            </button>
          </div>
          <span class="result-count" v-if="!loading">
            <span class="tabular">{{ filteredCourses.length }}</span
            >개
          </span>
        </div>

        <div v-if="loading" class="course-grid">
          <div v-for="i in 6" :key="i" class="skeleton-card surface">
            <div class="skeleton-block sk-thumb"></div>
            <div class="skeleton-block sk-line long"></div>
            <div class="skeleton-block sk-line medium"></div>
          </div>
        </div>

        <div v-else-if="filteredCourses.length" class="course-grid">
          <CourseCard
            v-for="course in filteredCourses"
            :key="course.id"
            :course="course"
          />
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
                d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"
              />
              <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
              <line x1="12" y1="22.08" x2="12" y2="12" />
            </svg>
          </div>
          <p class="empty-state-title">해당 카테고리의 API가 없습니다</p>
          <p>다른 카테고리를 선택해 보세요.</p>
          <router-link
            v-if="isInstructor"
            to="/courses/new"
            class="btn btn-primary empty-action"
          >
            첫 API 등록하기
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from "vue";
import AppSideRail from "@/components/AppSideRail.vue";
import CourseCard from "@/components/CourseCard.vue";
import { useCourseStore } from "@/store/course.js";
import { useAuthStore } from "@/store/auth.js";

const courseStore = useCourseStore();
const auth = useAuthStore();

const { categories } = courseStore;

const loading = computed(() => courseStore.loading);
const selectedCategory = computed(() => courseStore.selectedCategory);
const isInstructor = computed(() => auth.user?.role === "INSTRUCTOR");

const filteredCourses = computed(() => {
  if (!Array.isArray(courseStore.courses)) return [];
  if (selectedCategory.value === "전체") return courseStore.courses;
  return courseStore.courses.filter(
    (c) => c.category === selectedCategory.value,
  );
});

function selectCategory(cat) {
  courseStore.setCategory(cat);
}

onMounted(() => {
  courseStore.fetchCourses();
});
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
}

.result-count {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.course-grid {
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

.empty-action {
  margin-top: 20px;
}

@media (max-width: 992px) {
  .course-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .course-grid {
    grid-template-columns: 1fr;
  }
  .page-header-row {
    flex-direction: column;
    align-items: flex-start;
  }
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
