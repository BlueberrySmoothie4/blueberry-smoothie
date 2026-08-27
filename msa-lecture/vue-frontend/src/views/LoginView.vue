<template>
  <div class="login-page">
    <div class="login-layout">
      <!-- 좌측 브랜딩 -->
      <div class="login-left">
        <router-link to="/" class="brand-name">{{ BRAND.name }}</router-link>
        <div class="brand-content">
          <h2>다시 만나서<br />반갑습니다</h2>
          <p>로그인하고 구독 중인 API를 바로 확인하세요.</p>
          <ul class="feature-list">
            <li v-for="f in features" :key="f">
              <span class="dot"></span>{{ f }}
            </li>
          </ul>
        </div>
      </div>

      <!-- 우측 -->
      <div class="login-right">
        <div class="login-box fade-in-up">
          <router-link to="/" class="back-link">← 홈으로</router-link>

          <!-- 로그인 영역 -->
          <div v-if="!showRegister" class="section">
            <h3 class="section-title">로그인</h3>
            <p class="section-desc">가입한 계정으로 로그인합니다.</p>
            <button class="btn btn-primary btn-full" @click="handleOAuth">
              로그인
            </button>
            <div class="switch-link">
              계정이 없으신가요?
              <button class="text-btn" @click="showRegister = true">
                회원가입
              </button>
            </div>
          </div>

          <!-- 회원가입 영역 -->
          <div v-else class="section">
            <h3 class="section-title">회원가입</h3>
            <form @submit.prevent="handleRegister" class="form">
              <div class="form-group">
                <label class="form-label">이름</label>
                <input
                  v-model="registerForm.name"
                  type="text"
                  class="form-input"
                  placeholder="홍길동"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">이메일</label>
                <input
                  v-model="registerForm.email"
                  type="email"
                  class="form-input"
                  placeholder="user@example.com"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">비밀번호</label>
                <input
                  v-model="registerForm.password"
                  type="password"
                  class="form-input"
                  placeholder="8자 이상"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">역할</label>
                <select v-model="registerForm.role" class="form-input">
                  <option value="BUYER">구매 기업 담당자</option>
                  <option value="PROVIDER">API 제공사</option>
                </select>
              </div>
              <div v-if="error" class="error-msg">{{ error }}</div>
              <div v-if="success" class="success-msg">{{ success }}</div>
              <button
                type="submit"
                class="btn btn-primary btn-full"
                :disabled="loading"
              >
                <span v-if="loading">가입 중...</span>
                <span v-else>회원가입</span>
              </button>
            </form>
            <div class="switch-link">
              이미 계정이 있으신가요?
              <button class="text-btn" @click="showRegister = false">
                로그인
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useAuthStore } from "@/store/auth.js";
import { authApi } from "@/api/auth.js";
import { BRAND } from "@/config/brand.js";

const auth = useAuthStore();

const showRegister = ref(false);
const loading = ref(false);
const error = ref("");
const success = ref("");

const registerForm = ref({
  name: "",
  email: "",
  password: "",
  role: "BUYER",
});

const features = [
  "구독 중인 API 상태 확인",
  "구독 이력 기반 API 추천",
  "결제·크레딧 내역 관리",
];

function handleOAuth() {
  auth.redirectToLogin();
}

async function handleRegister() {
  error.value = "";
  success.value = "";
  loading.value = true;
  try {
    await authApi.register(registerForm.value);
    success.value = "회원가입 완료! 로그인 페이지로 이동합니다.";
    registerForm.value = { name: "", email: "", password: "", role: "BUYER" };
    setTimeout(() => {
      showRegister.value = false;
      success.value = "";
    }, 2000);
  } catch (e) {
    error.value = e.response?.data?.message || "회원가입에 실패했습니다.";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: stretch;
}
.login-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 100%;
  min-height: 100vh;
}
.login-left {
  position: relative;
  overflow: hidden;
  background: var(--ink);
  padding: 48px;
  display: flex;
  flex-direction: column;
  gap: 48px;
}
/* 랜딩과 동일한 격자 + 블루 글로우 */
.login-left::before {
  content: "";
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.045) 1px, transparent 1px);
  background-size: 64px 64px;
  -webkit-mask-image: radial-gradient(
    ellipse 80% 60% at 30% 10%,
    #000 30%,
    transparent 100%
  );
  mask-image: radial-gradient(
    ellipse 80% 60% at 30% 10%,
    #000 30%,
    transparent 100%
  );
}
.login-left::after {
  content: "";
  position: absolute;
  top: -220px;
  left: -120px;
  width: 620px;
  height: 480px;
  background: radial-gradient(
    ellipse at center,
    rgba(49, 130, 246, 0.24),
    transparent 62%
  );
  filter: blur(20px);
}
.brand-name {
  position: relative;
  z-index: 1;
  display: inline-block;
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: #fff;
}
.brand-content {
  position: relative;
  z-index: 1;
}
.brand-content h2 {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: #fff;
  line-height: 1.3;
  margin-bottom: 14px;
}
.brand-content p {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.58);
  margin-bottom: 28px;
}
.feature-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.feature-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}
.dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #7dd3fc;
  flex-shrink: 0;
}

.login-right {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: var(--color-bg-primary);
}
.login-box {
  width: 100%;
  max-width: 400px;
}
.back-link {
  display: inline-block;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
  transition: var(--transition);
}
.back-link:hover {
  color: var(--color-primary);
}

.section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.section-title {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}
.section-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary);
}
.form-input {
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  font-family: var(--font-sans);
  letter-spacing: -0.01em;
  color: var(--color-text-primary);
  background: var(--color-bg-primary);
  transition: var(--transition);
  outline: none;
}
.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(49, 130, 246, 0.14);
}
.btn-full {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  justify-content: center;
  margin-top: 4px;
}

.switch-link {
  text-align: center;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}
.text-btn {
  background: none;
  border: none;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 0 2px;
  text-decoration: underline;
}
.error-msg {
  padding: 10px 14px;
  background: var(--color-danger-light);
  border: 1px solid #edcac6;
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--color-danger);
}
.success-msg {
  padding: 10px 14px;
  background: var(--color-success-light);
  border: 1px solid #c4e0d2;
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--color-success);
}
</style>
