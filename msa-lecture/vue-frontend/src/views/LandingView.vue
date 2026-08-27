<template>
  <div class="landing">
    <!-- 스크롤 진행 바 -->
    <div class="scroll-progress" aria-hidden="true">
      <i :style="{ transform: `scaleX(${scrollProgress})` }"></i>
    </div>

    <AppHeader overlay />

    <!-- ── Hero ── -->
    <section class="hero">
      <div class="hero-grid" aria-hidden="true"></div>
      <div class="hero-glow" aria-hidden="true"></div>

      <div class="hero-inner">
        <div class="hero-copy">
          <p class="eyebrow enter" style="--d: 0ms">
            <span class="eyebrow-rule"></span>
            AI 컴포넌트 원스톱 마켓플레이스
          </p>

          <h1 class="hero-title enter" style="--d: 80ms">
            기업이 필요한 AI 기능,<br />
            <span class="hero-title-dim">한 곳에서 찾아보세요</span>
          </h1>

          <p class="hero-desc enter" style="--d: 160ms">
            텍스트·비전·음성 API를 성능과 요금으로 비교하고, 구독 즉시 발급되는
            Key로 바로 연동하세요.
          </p>

          <div class="hero-actions enter" style="--d: 240ms">
            <router-link to="/login" class="cta-solid">
              무료로 시작하기
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <line x1="5" y1="12" x2="19" y2="12" />
                <polyline points="12 5 19 12 12 19" />
              </svg>
            </router-link>
            <router-link to="/login" class="cta-quiet"
              >API 카탈로그 둘러보기</router-link
            >
          </div>
        </div>

        <!-- 콘솔 목업 -->
        <div class="console-wrap enter" style="--d: 320ms">
          <div
            class="console"
            :style="{ transform: `translate3d(0, ${heroParallax}px, 0)` }"
          >
            <div class="console-bar">
              <span class="console-dots"><i></i><i></i><i></i></span>
              <span class="console-file">subscribe.sh</span>
              <span class="console-status"
                ><span class="pulse"></span>200 OK · 82ms</span
              >
            </div>

            <pre
              class="console-body"
            ><span class="c-com"># 카탈로그에서 고른 API를 구독 신청합니다</span>
<span class="c-cmd">curl</span> -X POST https://{{ BRAND.apiHost }}/api/subscriptions \
  -H <span class="c-str">"Authorization: Bearer $ACCESS_TOKEN"</span> \
  -d <span class="c-str">'{ "productId": 12 }'</span>
</pre>

            <div class="console-divider"></div>

            <pre class="console-body console-res">{
  <span class="c-key">"success"</span>: <span class="c-ok">true</span>,
  <span class="c-key">"data"</span>: {
    <span class="c-key">"id"</span>:        <span class="c-num">1</span>,
    <span class="c-key">"productId"</span>: <span class="c-num">12</span>,
    <span class="c-key">"status"</span>:    <span class="c-str">"PENDING"</span>
  }
}</pre>
          </div>
        </div>
      </div>

      <!-- 지표 -->
      <div class="stats" ref="statsEl">
        <div class="stat" v-for="(s, i) in stats" :key="s.label">
          <span class="stat-value"
            >{{ formatStat(i) }}<i>{{ s.unit }}</i></span
          >
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </section>

    <!-- ── 인기 API ── -->
    <section class="section catalog">
      <div class="wrap">
        <div class="section-head reveal">
          <div>
            <p class="section-label">Catalog</p>
            <h2 class="section-title">지금 많이 쓰는 API</h2>
          </div>
          <router-link to="/login" class="section-link">
            전체 보기
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
        </div>

        <div class="catalog-grid">
          <div
            v-for="(api, i) in featuredApis"
            :key="api.id"
            class="reveal"
            :style="{ '--d': `${i * 90}ms` }"
          >
            <router-link to="/login" class="api-card">
              <div class="api-card-head">
                <span class="api-thumb" :class="api.tone">
                  {{ api.category.charAt(0) }}
                </span>
                <span class="api-cat">{{ api.category }}</span>
              </div>

              <h3 class="api-title">{{ api.title }}</h3>
              <p class="api-desc">{{ api.desc }}</p>

              <div class="api-foot">
                <span class="api-subs">{{ api.subs }}개사 구독</span>
                <span class="api-price">{{ api.price }}<i>/월</i></span>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- ── 스크롤 스토리 ── -->
    <section class="story">
      <div class="wrap story-head reveal">
        <p class="section-label">How it works</p>
        <h2 class="section-title">연동까지 3단계면 충분합니다</h2>
      </div>

      <div class="wrap story-grid">
        <!-- 스크롤 중 고정되는 시각 영역 -->
        <div class="story-visual">
          <div class="stage">
            <!-- 01 카탈로그 -->
            <div class="panel" :class="{ on: activeStep === 0 }">
              <div class="panel-bar">
                <span class="panel-title">API 카탈로그</span>
                <span class="panel-meta">1,248개</span>
              </div>
              <div class="panel-chips">
                <span
                  v-for="(c, i) in ['문서', '비전', '음성', '언어']"
                  :key="c"
                  class="chip"
                  :class="{ on: i === 0 }"
                  >{{ c }}</span
                >
              </div>
              <ul class="panel-rows">
                <li
                  v-for="(r, i) in catalogRows"
                  :key="r.name"
                  :style="{ '--d': `${i * 90}ms` }"
                >
                  <span class="row-name">{{ r.name }}</span>
                  <span class="row-price">{{ r.price }}</span>
                </li>
              </ul>
            </div>

            <!-- 02 결제 -->
            <div class="panel" :class="{ on: activeStep === 1 }">
              <div class="panel-bar">
                <span class="panel-title">구독 결제</span>
                <span class="panel-meta">crd_8f2a</span>
              </div>
              <div class="pay">
                <div class="pay-row">
                  <span>문서 요약 API · 월 구독</span>
                  <b>₩89,000</b>
                </div>
                <div class="pay-row dim">
                  <span>보유 크레딧</span>
                  <b>₩1,200,000</b>
                </div>
                <div class="pay-bar">
                  <i :class="{ on: activeStep === 1 }"></i>
                </div>
                <div class="pay-row total">
                  <span>결제 후 잔액</span>
                  <b>₩1,111,000</b>
                </div>
                <div class="pay-done">
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
                  결제 완료 · 구독 상태 ACTIVE
                </div>
              </div>
            </div>

            <!-- 03 Key 발급 -->
            <div class="panel" :class="{ on: activeStep === 2 }">
              <div class="panel-bar">
                <span class="panel-title">구독 상태</span>
                <span class="panel-meta ok"
                  ><span class="pulse"></span>ACTIVE</span
                >
              </div>
              <div class="statebox">
                <span class="state-step done">PENDING</span>
                <span class="state-arrow">→</span>
                <span class="state-step now">ACTIVE</span>
              </div>
              <pre
                class="panel-code"
              ><span class="c-cmd">GET</span> {{ BRAND.apiHost }}/api/subscriptions/my

{ <span class="c-key">"productId"</span>: <span class="c-num">12</span>,
  <span class="c-key">"status"</span>: <span class="c-str">"ACTIVE"</span>,
  <span class="c-key">"product"</span>: { <span class="c-key">"title"</span>: <span class="c-str">"계약서 요약 API"</span> } }</pre>
            </div>
          </div>
        </div>

        <!-- 스크롤되는 설명 영역 -->
        <ol class="story-steps">
          <li
            v-for="(s, i) in steps"
            :key="s.title"
            class="story-step"
            :class="{ on: activeStep === i }"
            :data-step="i"
            ref="stepEls"
          >
            <span class="step-num">{{ String(i + 1).padStart(2, "0") }}</span>
            <h3>{{ s.title }}</h3>
            <p>{{ s.desc }}</p>
            <ul class="step-points">
              <li v-for="p in s.points" :key="p">{{ p }}</li>
            </ul>
          </li>
        </ol>
      </div>
    </section>

    <!-- ── 역할별 ── -->
    <section class="section roles">
      <div class="wrap roles-grid">
        <article class="role-card reveal">
          <p class="role-tag">구매 기업</p>
          <h3>필요한 기능만 골라 씁니다</h3>
          <ul>
            <li v-for="t in buyerPoints" :key="t">{{ t }}</li>
          </ul>
          <router-link to="/login" class="role-link">
            구매 기업으로 시작
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </router-link>
        </article>

        <article class="role-card role-accent reveal" style="--d: 100ms">
          <p class="role-tag">API 제공사</p>
          <h3>만든 모델을 매출로 연결합니다</h3>
          <ul>
            <li v-for="t in providerPoints" :key="t">{{ t }}</li>
          </ul>
          <router-link to="/login" class="role-link">
            제공사로 시작
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </router-link>
        </article>
      </div>
    </section>

    <!-- ── CTA ── -->
    <section class="cta-band">
      <div class="cta-glow" aria-hidden="true"></div>
      <div class="wrap cta-inner reveal">
        <h2>오늘 바로 첫 API를 붙여보세요</h2>
        <p>가입은 무료이고, 구독한 만큼만 크레딧이 차감됩니다.</p>
        <router-link to="/login" class="cta-solid cta-solid-light">
          무료로 시작하기
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="5" y1="12" x2="19" y2="12" />
            <polyline points="12 5 19 12 12 19" />
          </svg>
        </router-link>
      </div>
    </section>

    <footer class="footer">
      <div class="wrap footer-inner">
        <span class="footer-brand">{{ BRAND.name }}</span>
        <p class="footer-copy">© 2026 {{ BRAND.name }}. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import AppHeader from "@/components/AppHeader.vue";
import { BRAND } from "@/config/brand.js";

const stats = [
  { target: 1248, decimals: 0, unit: "", label: "등록된 API" },
  { target: 340, decimals: 0, unit: "", label: "API 제공사" },
  { target: 82, decimals: 0, unit: "ms", label: "평균 응답 속도" },
  { target: 99.98, decimals: 2, unit: "%", label: "게이트웨이 가동률" },
];

// 카테고리는 course-service의 ApiProduct.Category 라벨을 그대로 쓴다.
const featuredApis = [
  {
    id: 1,
    title: "계약서 요약 API",
    category: "문서",
    desc: "계약서·리포트를 핵심 문단으로 압축합니다.",
    subs: "1,204",
    price: "₩89,000",
    tone: "tone-amber",
  },
  {
    id: 2,
    title: "제품 이미지 인식 API",
    category: "비전",
    desc: "상품 사진에서 카테고리와 속성을 추출합니다.",
    subs: "862",
    price: "₩69,000",
    tone: "tone-violet",
  },
  {
    id: 3,
    title: "마케팅 카피 생성 API",
    category: "생성형",
    desc: "브랜드 톤에 맞춘 마케팅 문구를 생성합니다.",
    subs: "733",
    price: "₩75,000",
    tone: "tone-pink",
  },
];

const catalogRows = [
  { name: "계약서 요약 API", price: "₩89,000" },
  { name: "감정 분석 API", price: "₩54,000" },
  { name: "문서 분류 API", price: "₩61,000" },
  { name: "개체명 추출 API", price: "₩47,000" },
];

const steps = [
  {
    title: "카탈로그에서 비교",
    desc: "카테고리·요금·구독 기업 수를 나란히 놓고 필요한 API를 고릅니다.",
    points: [
      "비전·음성·언어·문서 등 8개 카테고리",
      "요금과 인기도를 한 화면에서 확인",
    ],
  },
  {
    title: "크레딧으로 구독 결제",
    desc: "별도 계약 없이 보유 크레딧을 차감해 구독을 시작합니다.",
    points: ["신청과 동시에 결제 요청 생성", "기업별 결제 내역 자동 기록"],
  },
  {
    title: "결제 승인되면 바로 활성화",
    desc: "결제가 승인되는 순간 구독 상태가 ACTIVE로 바뀌어 사용할 수 있습니다.",
    points: [
      "PENDING에서 ACTIVE로 자동 전환",
      "내 구독 목록에서 상태를 바로 확인",
    ],
  },
];

const buyerPoints = [
  "카테고리별 스펙·요금 비교",
  "구독 이력 기반 추천 API",
  "구독 현황과 결제 상태 확인",
];

const providerPoints = [
  "카테고리·요금·스펙을 지정해 상품 등록",
  "구독 기업 수를 인기도 지표로 확인",
  "정산까지 이어지는 결제 파이프라인",
];

const reduceMotion =
  typeof window !== "undefined" &&
  window.matchMedia("(prefers-reduced-motion: reduce)").matches;

/* ── 스크롤 진행도 + 히어로 패럴랙스 ── */
const scrollProgress = ref(0);
const heroParallax = ref(0);
let ticking = false;

function readScroll() {
  const doc = document.documentElement;
  const max = doc.scrollHeight - window.innerHeight;
  scrollProgress.value = max > 0 ? Math.min(window.scrollY / max, 1) : 0;

  if (!reduceMotion) {
    heroParallax.value = Math.min(window.scrollY * 0.08, 48);
  }
  ticking = false;
}

function onScroll() {
  if (ticking) return;
  ticking = true;
  requestAnimationFrame(readScroll);
}

/* ── 지표 카운트업 ── */
const counts = ref(stats.map(() => 0));
const statsEl = ref(null);

function formatStat(i) {
  const { decimals } = stats[i];
  return counts.value[i].toLocaleString("ko-KR", {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  });
}

function runCountUp() {
  if (reduceMotion) {
    counts.value = stats.map((s) => s.target);
    return;
  }

  const duration = 1400;
  const start = performance.now();

  const tick = (now) => {
    const t = Math.min((now - start) / duration, 1);
    const eased = t === 1 ? 1 : 1 - Math.pow(2, -10 * t);
    counts.value = stats.map((s) => s.target * eased);
    if (t < 1) requestAnimationFrame(tick);
  };

  requestAnimationFrame(tick);
}

/* ── 스크롤 스토리 활성 단계 ── */
const activeStep = ref(0);
const stepEls = ref([]);

let revealObserver = null;
let statsObserver = null;
let stepObserver = null;

onMounted(() => {
  readScroll();
  window.addEventListener("scroll", onScroll, { passive: true });
  window.addEventListener("resize", onScroll, { passive: true });

  const revealTargets = document.querySelectorAll(".reveal");

  if (!("IntersectionObserver" in window)) {
    revealTargets.forEach((el) => el.classList.add("in"));
    runCountUp();
    return;
  }

  revealObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add("in");
        revealObserver.unobserve(entry.target);
      });
    },
    { threshold: 0.15, rootMargin: "0px 0px -60px 0px" },
  );
  revealTargets.forEach((el) => revealObserver.observe(el));

  statsObserver = new IntersectionObserver(
    (entries) => {
      if (!entries[0].isIntersecting) return;
      runCountUp();
      statsObserver.disconnect();
    },
    { threshold: 0.4 },
  );
  if (statsEl.value) statsObserver.observe(statsEl.value);

  // 화면 중앙에 걸린 단계를 활성으로 표시
  stepObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        activeStep.value = Number(entry.target.dataset.step);
      });
    },
    { rootMargin: "-45% 0px -45% 0px", threshold: 0 },
  );
  if (Array.isArray(stepEls.value)) {
    stepEls.value.forEach((el) => el && stepObserver.observe(el));
  }
});

onUnmounted(() => {
  window.removeEventListener("scroll", onScroll);
  window.removeEventListener("resize", onScroll);
  revealObserver?.disconnect();
  statsObserver?.disconnect();
  stepObserver?.disconnect();
});
</script>

<style scoped>
.landing {
  /* 다크 팔레트 */
  --d-bg: #0a0b0d;
  --d-bg-alt: #101217;
  --d-line: rgba(255, 255, 255, 0.09);
  --d-line-2: rgba(255, 255, 255, 0.16);
  --d-surface: rgba(255, 255, 255, 0.035);
  --d-surface-2: rgba(255, 255, 255, 0.06);
  --d-text: #ffffff;
  --d-text-2: rgba(255, 255, 255, 0.58);
  --d-text-3: rgba(255, 255, 255, 0.4);
  --d-cyan: #7dd3fc;

  background: var(--d-bg);
  color: var(--d-text);
  letter-spacing: -0.01em;
}

.wrap {
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 28px;
}

/* ── 스크롤 진행 바 ── */
.scroll-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  z-index: 60;
  pointer-events: none;
}

.scroll-progress i {
  display: block;
  height: 100%;
  background: var(--color-primary);
  transform-origin: 0 50%;
  transform: scaleX(0);
}

/* ── 등장 애니메이션 ── */
.enter {
  animation: enter 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--d, 0ms);
}

@keyframes enter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.reveal {
  opacity: 0;
  transform: translateY(26px);
  transition:
    opacity 0.75s cubic-bezier(0.16, 1, 0.3, 1),
    transform 0.75s cubic-bezier(0.16, 1, 0.3, 1);
  transition-delay: var(--d, 0ms);
}

.reveal.in {
  opacity: 1;
  transform: none;
}

@media (prefers-reduced-motion: reduce) {
  .enter {
    animation: none;
  }
  .reveal {
    opacity: 1;
    transform: none;
    transition: none;
  }
}

/* ── Hero ── */
.hero {
  position: relative;
  overflow: hidden;
  padding: 96px 0 0;
  margin-top: -64px;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.045) 1px, transparent 1px);
  background-size: 72px 72px;
  -webkit-mask-image: radial-gradient(
    ellipse 90% 55% at 50% 0%,
    #000 30%,
    transparent 100%
  );
  mask-image: radial-gradient(
    ellipse 90% 55% at 50% 0%,
    #000 30%,
    transparent 100%
  );
}

.hero-glow {
  position: absolute;
  top: -280px;
  left: 50%;
  width: 1000px;
  height: 620px;
  transform: translateX(-52%);
  background: radial-gradient(
    ellipse at center,
    rgba(49, 130, 246, 0.22),
    transparent 62%
  );
  filter: blur(20px);
}

.hero-inner {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
  padding: 72px 28px 80px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.05fr);
  gap: 56px;
  align-items: center;
}

.eyebrow {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 24px;
}

.eyebrow-rule {
  width: 28px;
  height: 1px;
  background: var(--d-line-2);
}

.hero-title {
  font-size: 52px;
  font-weight: 800;
  line-height: 1.24;
  letter-spacing: -0.04em;
  color: var(--d-text);
  margin-bottom: 22px;
}

.hero-title-dim {
  color: rgba(255, 255, 255, 0.44);
}

.hero-desc {
  font-size: 17px;
  line-height: 1.72;
  color: var(--d-text-2);
  max-width: 30em;
  margin-bottom: 36px;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.cta-solid {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 15px 26px;
  border-radius: 12px;
  background: #fff;
  color: var(--d-bg);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.02em;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
}

.cta-solid svg {
  width: 17px;
  height: 17px;
  transition: transform 0.2s ease;
}

.cta-solid:hover {
  transform: translateY(-1px);
}
.cta-solid:hover svg {
  transform: translateX(3px);
}

.cta-quiet {
  display: inline-flex;
  align-items: center;
  padding: 15px 22px;
  border-radius: 12px;
  border: 1px solid var(--d-line-2);
  color: rgba(255, 255, 255, 0.82);
  font-size: 15px;
  font-weight: 600;
  transition: var(--transition);
}

.cta-quiet:hover {
  background: var(--d-surface-2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

/* 콘솔 */
.console-wrap {
  perspective: 1000px;
}

.console {
  border-radius: 16px;
  background: rgba(20, 22, 26, 0.86);
  border: 1px solid rgba(255, 255, 255, 0.09);
  backdrop-filter: blur(12px);
  box-shadow: 0 32px 64px -24px rgba(0, 0, 0, 0.75);
  overflow: hidden;
  will-change: transform;
}

.console-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.console-dots {
  display: flex;
  gap: 6px;
}

.console-dots i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}

.console-file {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--d-text-3);
}

.console-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
  font-family: var(--font-mono);
  font-size: 11.5px;
  color: #4ade80;
}

.pulse {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ade80;
  animation: pulse 2.4s ease-out infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(74, 222, 128, 0.5);
  }
  70% {
    box-shadow: 0 0 0 7px rgba(74, 222, 128, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(74, 222, 128, 0);
  }
}

.console-body {
  padding: 18px 20px;
  font-family: var(--font-mono);
  font-size: 12.5px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.78);
  white-space: pre;
  overflow-x: auto;
}

.console-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.07);
  margin: 0 20px;
}

.console-res {
  color: var(--d-text-2);
}

.c-com {
  color: rgba(255, 255, 255, 0.32);
}
.c-cmd {
  color: var(--d-cyan);
}
.c-str {
  color: #fcd34d;
}
.c-key {
  color: #a5b4fc;
}
.c-num {
  color: #f0abfc;
}
.c-ok {
  color: #4ade80;
}

/* 지표 */
.stats {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 28px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  border-top: 1px solid var(--d-line);
}

.stat {
  padding: 30px 4px 40px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat + .stat {
  border-left: 1px solid var(--d-line);
  padding-left: 28px;
}

.stat-value {
  font-family: var(--font-mono);
  font-size: 27px;
  font-weight: 600;
  color: #fff;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.03em;
}

.stat-value i {
  font-style: normal;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.45);
  margin-left: 2px;
}

.stat-label {
  font-size: 13px;
  color: var(--d-text-3);
}

/* ── 공통 섹션 ── */
.section {
  padding: 104px 0;
}

.section-label {
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--d-cyan);
  margin-bottom: 10px;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.035em;
  color: var(--d-text);
  line-height: 1.35;
}

.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 40px;
}

.section-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 14px;
  font-weight: 600;
  color: var(--d-text-3);
  transition: var(--transition);
  flex-shrink: 0;
}

.section-link svg {
  width: 16px;
  height: 16px;
}

.section-link:hover {
  color: #fff;
}

/* ── 카탈로그 ── */
.catalog {
  background: var(--d-bg-alt);
  border-top: 1px solid var(--d-line);
  border-bottom: 1px solid var(--d-line);
}

.catalog-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.api-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 26px;
  border-radius: 18px;
  border: 1px solid var(--d-line);
  background: var(--d-surface);
  transition:
    transform 0.25s cubic-bezier(0.16, 1, 0.3, 1),
    background 0.25s ease,
    border-color 0.25s ease;
}

.api-card:hover {
  transform: translateY(-4px);
  background: var(--d-surface-2);
  border-color: var(--d-line-2);
}

.api-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.api-thumb {
  width: 46px;
  height: 46px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.tone-amber {
  background: #fff4d6;
  color: #b45309;
}
.tone-violet {
  background: #f1eeff;
  color: #6d3fe0;
}
.tone-pink {
  background: #ffe9f2;
  color: #c72c62;
}

.api-cat {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.03em;
  color: var(--d-text-3);
  text-transform: uppercase;
}

.api-title {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: -0.025em;
  color: var(--d-text);
  margin-bottom: 7px;
}

.api-desc {
  font-size: 14px;
  line-height: 1.6;
  color: var(--d-text-2);
  flex: 1;
  margin-bottom: 22px;
}

.api-foot {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding-top: 18px;
  border-top: 1px solid var(--d-line);
}

.api-subs {
  font-size: 12.5px;
  color: var(--d-text-3);
}

.api-price {
  font-family: var(--font-mono);
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  font-variant-numeric: tabular-nums;
}

.api-price i {
  font-style: normal;
  font-size: 11.5px;
  color: var(--d-text-3);
}

/* ── 스크롤 스토리 ── */
.story {
  padding: 112px 0 0;
}

.story-head {
  margin-bottom: 8px;
}

.story-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);
  gap: 64px;
}

/* 그리드 아이템은 늘어나고 내부 stage가 고정된다 */
.story-visual {
  position: relative;
}

.stage {
  position: sticky;
  top: 96px;
  height: 420px;
}

.panel {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  border-radius: 18px;
  background: var(--d-surface);
  border: 1px solid var(--d-line);
  backdrop-filter: blur(10px);
  overflow: hidden;
  opacity: 0;
  transform: translateY(16px) scale(0.985);
  pointer-events: none;
  transition:
    opacity 0.6s cubic-bezier(0.16, 1, 0.3, 1),
    transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.panel.on {
  opacity: 1;
  transform: none;
  pointer-events: auto;
}

.panel-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 18px;
  border-bottom: 1px solid var(--d-line);
}

.panel-title {
  font-size: 13.5px;
  font-weight: 700;
  color: #fff;
}

.panel-meta {
  font-family: var(--font-mono);
  font-size: 11.5px;
  color: var(--d-text-3);
}

.panel-meta.ok {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #4ade80;
}

/* 01 카탈로그 */
.panel-chips {
  display: flex;
  gap: 7px;
  padding: 16px 18px 4px;
  flex-wrap: wrap;
}

.chip {
  padding: 6px 13px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.chip.on {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
}

.panel-rows {
  list-style: none;
  padding: 8px 18px 18px;
}

.panel-rows li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 13px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  opacity: 0;
}

.panel.on .panel-rows li {
  animation: rowIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--d);
}

@keyframes rowIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.row-name {
  font-size: 13.5px;
  color: rgba(255, 255, 255, 0.8);
}

.row-price {
  font-family: var(--font-mono);
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.45);
}

/* 02 결제 */
.pay {
  padding: 22px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.pay-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13.5px;
  color: rgba(255, 255, 255, 0.78);
}

.pay-row b {
  font-family: var(--font-mono);
  font-weight: 600;
  color: #fff;
  font-variant-numeric: tabular-nums;
}

.pay-row.dim,
.pay-row.dim b {
  color: rgba(255, 255, 255, 0.42);
}

.pay-bar {
  height: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.pay-bar i {
  display: block;
  height: 100%;
  width: 0;
  border-radius: 999px;
  background: linear-gradient(90deg, #3182f6, #7dd3fc);
  transition: width 1.1s cubic-bezier(0.16, 1, 0.3, 1) 0.2s;
}

.pay-bar i.on {
  width: 92.6%;
}

.pay-row.total {
  padding-top: 14px;
  border-top: 1px solid var(--d-line);
  font-weight: 600;
}

.pay-done {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
  padding: 12px 14px;
  border-radius: 11px;
  background: rgba(74, 222, 128, 0.1);
  border: 1px solid rgba(74, 222, 128, 0.22);
  font-size: 13px;
  font-weight: 600;
  color: #4ade80;
}

.pay-done svg {
  width: 15px;
  height: 15px;
  flex-shrink: 0;
}

/* 03 구독 상태 전환 */
.statebox {
  margin: 18px 18px 0;
  padding: 18px 16px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.28);
  border: 1px dashed var(--d-line-2);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
}

.state-step {
  font-family: var(--font-mono);
  font-size: 13px;
  padding: 7px 14px;
  border-radius: 999px;
  border: 1px solid transparent;
}

.state-step.done {
  color: var(--d-text-3);
  border-color: var(--d-line-2);
  text-decoration: line-through;
}

.state-step.now {
  color: #4ade80;
  background: rgba(74, 222, 128, 0.1);
  border-color: rgba(74, 222, 128, 0.3);
}

.state-arrow {
  color: var(--d-text-3);
  font-size: 14px;
}

.panel-code {
  margin: 16px 18px 18px;
  font-family: var(--font-mono);
  font-size: 12px;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.7);
  white-space: pre-wrap;
  word-break: break-word;
}

/* 설명 영역 */
.story-steps {
  list-style: none;
}

.story-step {
  min-height: 74vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px 0 24px 26px;
  border-left: 2px solid var(--d-line);
  opacity: 0.32;
  transition:
    opacity 0.45s ease,
    border-color 0.45s ease;
}

.story-step.on {
  opacity: 1;
  border-left-color: var(--color-primary);
}

.step-num {
  font-family: var(--font-mono);
  font-size: 12.5px;
  color: var(--d-cyan);
  margin-bottom: 14px;
}

.story-step h3 {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.035em;
  color: #fff;
  margin-bottom: 12px;
}

.story-step > p {
  font-size: 15.5px;
  line-height: 1.75;
  color: var(--d-text-2);
  margin-bottom: 20px;
}

.step-points {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 9px;
}

.step-points li {
  position: relative;
  padding-left: 18px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.step-points li::before {
  content: "";
  position: absolute;
  left: 0;
  top: 9px;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
}

.story-step.on .step-points li::before {
  background: var(--d-cyan);
}

/* ── 역할별 ── */
.roles {
  padding-top: 104px;
  background: var(--d-bg-alt);
  border-top: 1px solid var(--d-line);
}

.roles-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.role-card {
  padding: 40px;
  border-radius: 22px;
  display: flex;
  flex-direction: column;
  background: var(--d-surface);
  border: 1px solid var(--d-line);
}

.role-accent {
  background: linear-gradient(
    160deg,
    rgba(49, 130, 246, 0.16) 0%,
    rgba(255, 255, 255, 0.03) 55%
  );
  border-color: rgba(49, 130, 246, 0.28);
}

.role-tag {
  font-family: var(--font-mono);
  font-size: 11.5px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--d-cyan);
  margin-bottom: 14px;
}

.role-card h3 {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.035em;
  line-height: 1.4;
  color: #fff;
  margin-bottom: 24px;
}

.role-card ul {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  margin-bottom: 30px;
}

.role-card li {
  position: relative;
  padding-left: 20px;
  font-size: 14.5px;
  line-height: 1.6;
  color: var(--d-text-2);
}

.role-card li::before {
  content: "";
  position: absolute;
  left: 2px;
  top: 9px;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.32);
}

.role-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #fff;
  transition: var(--transition);
}

.role-link svg {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
}

.role-link:hover svg {
  transform: translateX(3px);
}

/* ── CTA ── */
.cta-band {
  position: relative;
  overflow: hidden;
  padding: 108px 0;
  text-align: center;
  border-top: 1px solid var(--d-line);
}

.cta-glow {
  position: absolute;
  bottom: -320px;
  left: 50%;
  width: 900px;
  height: 560px;
  transform: translateX(-50%);
  background: radial-gradient(
    ellipse at center,
    rgba(49, 130, 246, 0.26),
    transparent 62%
  );
  filter: blur(20px);
}

.cta-inner {
  position: relative;
  z-index: 1;
}

.cta-inner h2 {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: -0.035em;
  color: #fff;
  margin-bottom: 12px;
}

.cta-inner p {
  font-size: 16px;
  color: var(--d-text-2);
  margin-bottom: 32px;
}

.cta-solid-light {
  box-shadow: 0 12px 36px -12px rgba(255, 255, 255, 0.35);
}

/* ── Footer ── */
.footer {
  padding: 34px 0;
  border-top: 1px solid var(--d-line);
}

.footer-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-brand {
  font-size: 15px;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: #fff;
}

.footer-copy {
  font-size: 13px;
  color: var(--d-text-3);
}

/* ── 반응형 ── */
@media (max-width: 1000px) {
  .hero-inner {
    grid-template-columns: 1fr;
    gap: 48px;
    padding-bottom: 64px;
  }
  .hero-title {
    font-size: 44px;
  }
  .catalog-grid {
    grid-template-columns: 1fr;
  }
  .roles-grid {
    grid-template-columns: 1fr;
  }

  /* 모바일에서는 블록 흐름으로 두고 시각 영역 자체를 고정 */
  .story-grid {
    display: block;
  }
  .story-visual {
    position: sticky;
    top: 76px;
    height: 320px;
    z-index: 2;
    margin-bottom: 8px;
  }
  .stage {
    position: relative;
    top: auto;
    height: 100%;
  }
  .story-steps {
    position: relative;
    z-index: 1;
  }
  .story-step {
    min-height: 58vh;
  }
}

@media (max-width: 720px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }
  .stat {
    padding: 22px 4px 26px;
  }
  .stat + .stat {
    border-left: none;
    padding-left: 4px;
  }
  .stat:nth-child(even) {
    border-left: 1px solid var(--d-line);
    padding-left: 24px;
  }
  .stat:nth-child(n + 3) {
    border-top: 1px solid var(--d-line);
  }
}

@media (max-width: 560px) {
  .wrap,
  .hero-inner,
  .stats {
    padding-left: 20px;
    padding-right: 20px;
  }
  .hero {
    padding-top: 88px;
  }
  .hero-title {
    font-size: 34px;
  }
  .hero-desc {
    font-size: 15.5px;
  }
  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }
  .cta-solid,
  .cta-quiet {
    justify-content: center;
  }
  .section {
    padding: 72px 0;
  }
  .section-title {
    font-size: 25px;
  }
  .story {
    padding-top: 72px;
  }
  .story-step h3 {
    font-size: 21px;
  }
  .cta-inner h2 {
    font-size: 26px;
  }
  .role-card {
    padding: 30px 24px;
  }
  .role-card h3 {
    font-size: 20px;
  }
  .footer-inner {
    flex-direction: column;
    gap: 10px;
  }
  .console-body {
    font-size: 11.5px;
  }
}
</style>
