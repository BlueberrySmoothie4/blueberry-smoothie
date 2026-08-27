<template>
  <div class="app-shell">
    <AppSideRail />

    <main class="shell-main">
      <div class="shell-content">
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

        <header class="page-header">
          <p class="page-eyebrow">New API</p>
          <h1 class="page-title">새 API 상품을 등록하세요</h1>
          <p class="page-subtitle">
            카테고리와 요금을 지정하면 카탈로그에 바로 노출됩니다.
          </p>
        </header>

        <div class="create-layout">
          <div class="form-card surface">
            <form class="course-form" @submit.prevent="handleSubmit">
              <div class="form-group">
                <label class="form-label" for="title">API 상품명</label>
                <input
                  id="title"
                  v-model.trim="form.title"
                  type="text"
                  class="form-input"
                  placeholder="예: 실시간 문서 요약 API"
                  maxlength="100"
                />
              </div>

              <div class="form-group">
                <label class="form-label" for="description">API 설명</label>
                <textarea
                  id="description"
                  v-model.trim="form.description"
                  class="form-textarea"
                  rows="6"
                  placeholder="API 소개, 성능/제약, 대상 고객 등을 입력해 주세요."
                ></textarea>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label" for="category">카테고리</label>
                  <select
                    id="category"
                    v-model="form.category"
                    class="form-select"
                  >
                    <option disabled value="">카테고리를 선택하세요</option>
                    <option
                      v-for="option in categoryOptions"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.label }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label" for="price">월 요금 (원)</label>
                  <input
                    id="price"
                    v-model.number="form.price"
                    type="number"
                    min="0"
                    step="1000"
                    class="form-input mono"
                    placeholder="예: 50000"
                  />
                </div>
              </div>

              <div v-if="validationError" class="error-box">
                {{ validationError }}
              </div>

              <div v-if="submitError" class="error-box">
                {{ submitError }}
              </div>

              <div v-if="submitSuccess" class="success-box">
                {{ submitSuccess }}
              </div>

              <div class="form-actions">
                <router-link to="/courses" class="btn btn-ghost">
                  취소
                </router-link>

                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="submitting"
                >
                  <span v-if="submitting">등록 중...</span>
                  <span v-else>API 등록</span>
                </button>
              </div>
            </form>
          </div>

          <!-- 미리보기 -->
          <aside class="preview-pane">
            <p class="preview-label">카탈로그 미리보기</p>
            <div class="preview-card surface">
              <div class="preview-top">
                <span class="badge" :class="previewBadgeClass">{{
                  previewCategoryLabel
                }}</span>
              </div>
              <h3 class="preview-title">{{ form.title || "API 상품명" }}</h3>
              <p class="preview-desc">
                {{ form.description || "설명을 입력하면 여기에 표시됩니다." }}
              </p>
              <div class="preview-meta">
                <span class="preview-subs">구독 기업 0개사</span>
                <span class="preview-price">{{ previewPrice }}</span>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import AppSideRail from "@/components/AppSideRail.vue";
import { courseApi } from "@/api/course.js";
import { useAuthStore } from "@/store/auth.js";

const router = useRouter();
const auth = useAuthStore();

const form = reactive({
  title: "",
  description: "",
  category: "",
  price: null,
});

const submitting = ref(false);
const validationError = ref("");
const submitError = ref("");
const submitSuccess = ref("");

const categoryOptions = [
  { label: "텍스트/NLP", value: "BACKEND", badge: "badge-teal" },
  { label: "비전", value: "FRONTEND", badge: "badge-teal" },
  { label: "인프라", value: "DEVOPS", badge: "badge-blue" },
  { label: "데이터 / 생성형AI", value: "DATA_SCIENCE", badge: "badge-purple" },
];

const previewCategoryLabel = computed(() => {
  return (
    categoryOptions.find((opt) => opt.value === form.category)?.label ||
    "카테고리 미선택"
  );
});

const previewBadgeClass = computed(() => {
  return (
    categoryOptions.find((opt) => opt.value === form.category)?.badge ||
    "badge-gray"
  );
});

const previewPrice = computed(() => {
  const value = Number(form.price);
  if (!form.price || Number.isNaN(value)) return "₩0";
  return `₩${value.toLocaleString()}`;
});

function validateForm() {
  validationError.value = "";

  if (!auth.user || auth.user.role !== "INSTRUCTOR") {
    validationError.value = "제공사 계정만 API를 등록할 수 있습니다.";
    return false;
  }

  if (!form.title) {
    validationError.value = "API 상품명을 입력해 주세요.";
    return false;
  }

  if (!form.description) {
    validationError.value = "API 설명을 입력해 주세요.";
    return false;
  }

  if (!form.category) {
    validationError.value = "카테고리를 선택해 주세요.";
    return false;
  }

  if (form.price === null || form.price === undefined || form.price === "") {
    validationError.value = "가격을 입력해 주세요.";
    return false;
  }

  const price = Number(form.price);
  if (Number.isNaN(price) || price < 0) {
    validationError.value = "가격은 0 이상의 숫자로 입력해 주세요.";
    return false;
  }

  return true;
}

async function handleSubmit() {
  submitError.value = "";
  submitSuccess.value = "";

  if (!validateForm()) return;

  submitting.value = true;

  try {
    const payload = {
      title: form.title,
      description: form.description,
      category: form.category,
      price: Number(form.price),
    };

    const res = await courseApi.create(payload);
    console.log("[CourseCreate] create response =", res.data);

    submitSuccess.value = "API가 성공적으로 등록되었습니다.";

    const createdCourseId = res.data?.data?.id ?? res.data?.id;

    if (createdCourseId) {
      setTimeout(() => {
        router.push(`/courses/${createdCourseId}`);
      }, 500);
    } else {
      setTimeout(() => {
        router.push("/courses");
      }, 500);
    }
  } catch (error) {
    console.error("[CourseCreate] create failed:", error);
    submitError.value =
      error.response?.data?.message || "API 등록에 실패했습니다.";
  } finally {
    submitting.value = false;
  }
}
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

.create-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 24px;
  align-items: start;
}

.form-card {
  padding: 28px;
}

.course-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-bg-primary);
  padding: 12px 14px;
  font-size: 14px;
  font-family: inherit;
  letter-spacing: -0.01em;
  color: var(--color-text-primary);
  outline: none;
  transition: var(--transition);
  box-sizing: border-box;
}

.form-input.mono {
  font-family: var(--font-mono);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(49, 130, 246, 0.14);
}

.form-textarea {
  resize: vertical;
  min-height: 140px;
  line-height: 1.5;
}

.error-box {
  background: var(--color-danger-light);
  color: var(--color-danger);
  border-radius: var(--radius-md);
  padding: 12px 14px;
  font-size: 13px;
}

.success-box {
  background: var(--color-success-light);
  color: var(--color-success);
  border-radius: var(--radius-md);
  padding: 12px 14px;
  font-size: 13px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 6px;
}

/* 미리보기 */
.preview-pane {
  position: sticky;
  top: 24px;
}

.preview-label {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.preview-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preview-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
}

.preview-desc {
  font-size: 12.5px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.preview-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid var(--color-border);
}

.preview-subs {
  font-size: 11px;
  color: var(--color-text-muted);
}

.preview-price {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: -0.03em;
  font-variant-numeric: tabular-nums;
  color: var(--color-text-primary);
}

@media (max-width: 992px) {
  .create-layout {
    grid-template-columns: 1fr;
  }
  .preview-pane {
    position: static;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
