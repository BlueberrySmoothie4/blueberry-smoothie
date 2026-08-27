<template>
  <router-link
    :to="`/courses/${course.id}`"
    class="api-card surface surface-hover"
  >
    <div class="api-card-head">
      <div class="api-thumb" :class="thumbBg">
        <img
          v-if="thumbSrc"
          :src="thumbSrc"
          :alt="course.title"
          class="thumb-img"
        />
        <span v-else class="thumb-placeholder">{{
          course.category?.charAt(0)
        }}</span>
      </div>
      <span class="badge" :class="badgeClass">{{ course.category }}</span>
    </div>

    <div class="api-body">
      <h3 class="api-title">{{ course.title }}</h3>
      <p class="api-provider">{{ course.instructorName }}</p>
    </div>

    <div class="api-foot">
      <span class="api-subs">
        <span class="tabular">{{
          course.enrollmentCount?.toLocaleString() ?? 0
        }}</span
        >개사 구독
      </span>
      <span class="api-price">
        ₩<span class="tabular">{{
          Number(course.price).toLocaleString()
        }}</span>
        <i>/월</i>
      </span>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  course: { type: Object, required: true },
});

const categoryConfig = {
  백엔드: { bg: "thumb-teal", badge: "badge-teal", thumb: "spring_boot" },
  프론트엔드: { bg: "thumb-teal", badge: "badge-teal", thumb: "vue_js" },
  DevOps: { bg: "thumb-blue", badge: "badge-blue", thumb: "docker" },
  데이터: { bg: "thumb-purple", badge: "badge-purple", thumb: "python" },
  AI: { bg: "thumb-pink", badge: "badge-pink", thumb: "generative_ai" },
};

const config = computed(
  () =>
    categoryConfig[props.course.category] || {
      bg: "thumb-gray",
      badge: "badge-gray",
    },
);
const thumbBg = computed(() => config.value.bg);
const badgeClass = computed(() => config.value.badge);

const thumbSrc = computed(() => {
  const key = props.course.thumbnail || config.value.thumb;
  if (!key) return null;
  try {
    return new URL(`../assets/images/courses/${key}.png`, import.meta.url).href;
  } catch {
    return null;
  }
});
</script>

<style scoped>
.api-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 22px;
}

.api-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.api-thumb {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
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

.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 8px;
}

.thumb-placeholder {
  font-size: 17px;
  font-weight: 800;
  color: var(--color-text-muted);
}

.api-body {
  flex: 1;
}

.api-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  line-height: 1.4;
  margin-bottom: 5px;
}

.api-provider {
  font-size: 13px;
  color: var(--color-text-muted);
}

.api-foot {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.api-subs {
  font-size: 12.5px;
  color: var(--color-text-muted);
}

.api-price {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  white-space: nowrap;
}

.api-price i {
  font-style: normal;
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
}
</style>
