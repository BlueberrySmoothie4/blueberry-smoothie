<template>
  <div class="landing">
    <AppHeader />

    <!-- 히어로 섹션 -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-content fade-in-up">
          <span class="hero-badge">SK AI API 마켓플레이스</span>
          <h1 class="hero-title">필요한 AI를,<br>바로 연결하세요</h1>
          <p class="hero-desc">텍스트, 비전, 음성 등 검증된 AI API를 비교하고 구독해 우리 서비스에 바로 붙여보세요. 계약, 결제, Key 발급까지 한 곳에서 끝납니다.</p>
          <div class="hero-actions">
            <router-link to="/login" class="btn btn-primary btn-lg">무료로 시작하기</router-link>
            <router-link to="/courses" class="btn btn-outline btn-lg">API 둘러보기</router-link>
          </div>
          <div class="hero-stats">
            <div class="stat"><span class="stat-num">1,200+</span><span class="stat-label">API</span></div>
            <div class="stat"><span class="stat-num">340+</span><span class="stat-label">제공사</span></div>
            <div class="stat"><span class="stat-num">28,000+</span><span class="stat-label">구독 기업</span></div>
          </div>
        </div>

        <!-- API 스펙 카드 미리보기 -->
        <div class="hero-visual fade-in">
          <div class="spec-stack">
            <div class="spec-card spec-card-back">
              <div class="spec-card-top">
                <span class="badge badge-purple">데이터</span>
                <span class="spec-price">₩59,000<em>/월</em></span>
              </div>
              <p class="spec-title">데이터 인사이트 API</p>
              <div class="spec-meta"><span>구독 기업 412개사</span></div>
            </div>
            <div class="spec-card spec-card-front">
              <div class="spec-card-top">
                <span class="badge badge-teal">텍스트 / NLP</span>
                <span class="spec-price">₩89,000<em>/월</em></span>
              </div>
              <p class="spec-title">문서 요약 API</p>
              <div class="spec-meta">
                <span>구독 기업 1,204개사</span>
                <span class="spec-live">● 응답 지연 120ms</span>
              </div>
              <span class="spec-cta">구독하기 →</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 카테고리 -->
    <section class="category-section">
      <div class="section-inner">
        <div class="section-header stacked">
          <h2 class="section-title">카테고리로 바로 찾기</h2>
          <p class="section-desc">하나씩 붙여보지 않아도, 필요한 분야로 좁혀서 API를 비교하세요.</p>
        </div>
        <div class="category-grid">
          <router-link
            v-for="cat in categories"
            :key="cat.label"
            to="/login"
            class="category-tile"
            :class="cat.tile"
          >
            <span class="tile-icon">{{ cat.icon }}</span>
            <span class="tile-label">{{ cat.label }}</span>
            <span class="tile-desc">{{ cat.desc }}</span>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 인기 API -->
    <section class="popular-section">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title">인기 API</h2>
          <router-link to="/login" class="section-link">전체 보기 →</router-link>
        </div>
        <div class="course-grid">
          <div v-for="course in featuredCourses" :key="course.id" class="course-card-landing">
            <div class="card-thumb" :class="course.thumbBg">
              <img :src="course.thumbSrc" :alt="course.title" class="thumb-img" />
            </div>
            <div class="card-body">
              <span class="badge" :class="course.badgeClass">{{ course.category }}</span>
              <h3 class="card-title">{{ course.title }}</h3>
              <p class="card-provider">{{ course.instructor }}</p>
              <div class="card-meta">
                <span class="subscribers">구독 기업 {{ course.subscribers.toLocaleString() }}개사</span>
                <span class="price">{{ course.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 이용 흐름 -->
    <section class="steps-section">
      <div class="section-inner">
        <h2 class="section-title center">이렇게 연결됩니다</h2>
        <div class="steps-grid">
          <div class="step-card" v-for="s in steps" :key="s.no">
            <span class="step-no">{{ s.no }}</span>
            <h3 class="step-title">{{ s.title }}</h3>
            <p class="step-desc">{{ s.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 두 페르소나 -->
    <section class="persona-section">
      <div class="section-inner">
        <div class="persona-grid">
          <div class="persona-card">
            <span class="persona-eyebrow">구매 기업 담당자라면</span>
            <h3 class="persona-title">필요한 AI를 지금 바로 연동하세요</h3>
            <ul class="persona-list">
              <li>카테고리 · 가격 · 제약으로 API 비교</li>
              <li>크레딧 결제 한 번으로 구독 신청</li>
              <li>구독 즉시 API Key 발급</li>
            </ul>
            <router-link to="/login" class="btn btn-primary">API 둘러보기</router-link>
          </div>
          <div class="persona-card persona-card-alt">
            <span class="persona-eyebrow">API 제공사라면</span>
            <h3 class="persona-title">우리 모델을 B2B 시장에 올리세요</h3>
            <ul class="persona-list">
              <li>카테고리 · 요금 · 스펙으로 상품 등록</li>
              <li>구독 기업 수와 트래픽을 대시보드로 확인</li>
              <li>영업 조직 없이 판로 확보</li>
            </ul>
            <router-link to="/login" class="btn btn-outline btn-alt">제공사로 시작하기</router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="cta-inner">
        <h2>지금 바로 시작하세요</h2>
        <p>수천 개 기업이 APINexus와 함께 AI를 도입하고 있습니다.</p>
        <router-link to="/login" class="btn btn-primary btn-lg">무료로 시작하기</router-link>
      </div>
    </section>

    <!-- 푸터 -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-logo">
          <img src="@/assets/images/logo/main_logo.png" alt="APINexus" />
          <span>APINexus</span>
        </div>
        <p class="footer-copy">© 2026 APINexus. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'

import springImg   from '@/assets/images/courses/spring_boot.png'
import vueImg      from '@/assets/images/courses/vue_js.png'
import k8sImg      from '@/assets/images/courses/kubernetes.png'
import dockerImg   from '@/assets/images/courses/docker.png'
import pythonImg   from '@/assets/images/courses/python.png'
import genaiImg    from '@/assets/images/courses/generative_ai.png'

const featuredCourses = [
  { id:1, title:'문서 요약 API',          category:'텍스트/NLP', instructor:'브라이트AI',   price:'₩89,000', subscribers:1204, thumbSrc: springImg, thumbBg:'thumb-teal',   badgeClass:'badge-teal'   },
  { id:2, title:'제품 이미지 인식 API',    category:'비전',      instructor:'뷰테크',       price:'₩69,000', subscribers:856,  thumbSrc: vueImg,    thumbBg:'thumb-teal',   badgeClass:'badge-teal'   },
  { id:3, title:'대규모 추론 인프라 API',  category:'인프라',    instructor:'클러스터랩',    price:'₩99,000', subscribers:203,  thumbSrc: k8sImg,    thumbBg:'thumb-blue',   badgeClass:'badge-blue'   },
  { id:4, title:'경량 서빙 API',           category:'인프라',    instructor:'컨테이너웍스',  price:'₩79,000', subscribers:377,  thumbSrc: dockerImg, thumbBg:'thumb-blue',   badgeClass:'badge-blue'   },
  { id:5, title:'데이터 인사이트 API',     category:'데이터',    instructor:'파이런',       price:'₩59,000', subscribers:412,  thumbSrc: pythonImg, thumbBg:'thumb-purple', badgeClass:'badge-purple' },
  { id:6, title:'생성형 AI 카피라이팅 API',category:'생성형AI',  instructor:'제너에이아이', price:'₩75,000', subscribers:689,  thumbSrc: genaiImg,  thumbBg:'thumb-pink',   badgeClass:'badge-pink'   },
]

const categories = [
  { icon: '📝', label: '텍스트 / NLP', desc: '요약, 번역, 문서 분석', tile: 'tile-teal' },
  { icon: '👁️', label: '비전',        desc: '이미지 인식, OCR',    tile: 'tile-blue' },
  { icon: '🎙️', label: '음성',        desc: '음성 인식, 합성',     tile: 'tile-purple' },
  { icon: '📈', label: '데이터',       desc: '분석, 예측 모델',     tile: 'tile-pink' },
  { icon: '✨', label: '생성형 AI',    desc: '카피, 이미지 생성',   tile: 'tile-amber' },
  { icon: '🧱', label: '인프라',       desc: '추론 서빙, 오케스트레이션', tile: 'tile-gray' },
]

const steps = [
  { no: '01', title: '비교하기',   desc: '카테고리 · 성능 · 가격을 비교해 우리 데이터에 맞는 API를 고릅니다.' },
  { no: '02', title: '구독 신청',  desc: '크레딧으로 결제하면 구독 신청이 바로 처리됩니다.' },
  { no: '03', title: 'Key 발급',  desc: '결제가 완료되면 API Key가 즉시 발급됩니다.' },
  { no: '04', title: '연동하기',   desc: '발급받은 Key로 우리 서비스에 바로 연동합니다.' },
]
</script>

<style scoped>
.landing { background: var(--color-bg-secondary); }

/* 히어로 */
.hero {
  background: linear-gradient(135deg, #FBF6EE 0%, #F6EBDA 50%, #FAF3E7 100%);
  border-bottom: 1px solid var(--color-border);
  padding: 80px 0 96px;
}
.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 48px;
  align-items: center;
}
.hero-badge {
  display: inline-block;
  padding: 5px 14px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 16px;
}
.hero-title {
  font-family: var(--font-display);
  font-size: 42px;
  font-weight: 800;
  line-height: 1.25;
  letter-spacing: -0.5px;
  color: var(--color-text-primary);
  margin-bottom: 16px;
}
.hero-desc {
  font-size: 16px;
  color: var(--color-text-secondary);
  line-height: 1.7;
  max-width: 460px;
  margin-bottom: 28px;
}
.hero-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 40px;
}
.btn-lg { padding: 12px 28px; font-size: 15px; }
.hero-stats {
  display: flex;
  gap: 36px;
}
.stat { display: flex; flex-direction: column; gap: 2px; }
.stat-num { font-family: var(--font-display); font-size: 22px; font-weight: 800; color: var(--color-primary); font-variant-numeric: tabular-nums; }
.stat-label { font-size: 12px; color: var(--color-text-secondary); }

/* 히어로 스펙 카드 미리보기 */
.hero-visual {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 300px;
}
.spec-stack {
  position: relative;
  width: 260px;
  height: 220px;
}
.spec-card {
  position: absolute;
  left: 0;
  width: 260px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 18px;
  box-shadow: var(--shadow-lg);
}
.spec-card-back {
  top: 0;
  transform: rotate(-6deg);
  opacity: 0.85;
  filter: saturate(0.9);
}
.spec-card-front {
  top: 46px;
  transform: rotate(3deg);
}
.spec-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.spec-price {
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-primary);
  font-variant-numeric: tabular-nums;
}
.spec-price em { font-style: normal; color: var(--color-text-muted); font-weight: 500; }
.spec-title {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}
.spec-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 11.5px;
  color: var(--color-text-secondary);
}
.spec-live { color: var(--color-success); font-family: var(--font-mono); }
.spec-cta {
  display: inline-block;
  margin-top: 14px;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--color-primary);
}

/* 카테고리 */
.category-section { padding: 56px 0; background: var(--color-bg-primary); }
.section-header.stacked {
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 28px;
}
.section-desc { font-size: 14px; color: var(--color-text-secondary); margin-top: 6px; }
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.category-tile {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 18px 16px;
  border-radius: var(--radius-lg);
  border: 1px solid transparent;
  transition: var(--transition);
}
.category-tile:hover { transform: translateY(-3px); box-shadow: var(--shadow-md); }
.tile-icon { font-size: 22px; margin-bottom: 6px; }
.tile-label { font-family: var(--font-display); font-size: 14px; font-weight: 700; color: var(--color-text-primary); }
.tile-desc { font-size: 12px; color: var(--color-text-secondary); }
.tile-teal   { background: #E1F0EA; }
.tile-blue   { background: #E7EEF6; }
.tile-purple { background: #ECE8F4; }
.tile-pink   { background: #F5E8E6; }
.tile-amber  { background: #F4ECD2; }
.tile-gray   { background: #EFEBE2; }

/* 인기 API */
.popular-section { padding: 64px 0; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.section-title { font-family: var(--font-display); font-size: 22px; font-weight: 800; color: var(--color-text-primary); }
.section-title.center { text-align: center; margin-bottom: 40px; }
.section-link { font-size: 14px; color: var(--color-primary); font-weight: 500; }
.section-link:hover { text-decoration: underline; }

.course-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.course-card-landing {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: var(--transition);
}
.course-card-landing:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}
.card-thumb {
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.thumb-teal   { background: #E1F0EA; }
.thumb-blue   { background: #E7EEF6; }
.thumb-purple { background: #ECE8F4; }
.thumb-pink   { background: #F5E8E6; }
.thumb-img { width: 100%; height: 100%; object-fit: contain; padding: 14px; }
.card-body { padding: 14px 16px; display: flex; flex-direction: column; gap: 5px; }
.card-title { font-family: var(--font-display); font-size: 14px; font-weight: 700; color: var(--color-text-primary); line-height: 1.4; }
.card-provider { font-size: 12px; color: var(--color-text-secondary); }
.card-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 4px; padding-top: 8px; border-top: 1px solid var(--color-border); }
.subscribers { font-size: 11.5px; color: var(--color-text-muted); }
.price { font-family: var(--font-mono); font-size: 13px; font-weight: 600; color: var(--color-primary); }

/* 이용 흐름 */
.steps-section { padding: 64px 0; background: var(--color-bg-primary); }
.steps-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.step-card {
  padding: 24px 22px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}
.step-no {
  display: block;
  font-family: var(--font-mono);
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 10px;
}
.step-title { font-family: var(--font-display); font-size: 16px; font-weight: 700; margin-bottom: 8px; color: var(--color-text-primary); }
.step-desc { font-size: 13px; color: var(--color-text-secondary); line-height: 1.6; }

/* 두 페르소나 */
.persona-section { padding: 64px 0; }
.persona-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.persona-card {
  padding: 32px;
  border-radius: var(--radius-xl);
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}
.persona-eyebrow {
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: 0.04em;
  margin-bottom: 10px;
}
.persona-title {
  font-family: var(--font-display);
  font-size: 21px;
  font-weight: 800;
  line-height: 1.35;
  margin-bottom: 18px;
  color: var(--color-text-primary);
}
.persona-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 26px;
}
.persona-list li {
  font-size: 13.5px;
  color: var(--color-text-secondary);
  padding-left: 16px;
  position: relative;
}
.persona-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 7px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-primary);
}
.persona-card-alt {
  background: linear-gradient(160deg, #201C17 0%, #4A3419 55%, #A34E14 100%);
  border-color: transparent;
}
.persona-card-alt .persona-eyebrow { color: #F0D3AE; }
.persona-card-alt .persona-title { color: #fff; }
.persona-card-alt .persona-list li { color: rgba(255,255,255,0.75); }
.persona-card-alt .persona-list li::before { background: #F0D3AE; }
.btn-alt {
  background: transparent;
  color: #fff;
  border: 1.5px solid rgba(255,255,255,0.55);
}
.btn-alt:hover { background: rgba(255,255,255,0.12); }

/* CTA */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  text-align: center;
}
.cta-inner { max-width: 600px; margin: 0 auto; padding: 0 24px; }
.cta-inner h2 { font-family: var(--font-display); font-size: 32px; font-weight: 800; color: #fff; margin-bottom: 12px; }
.cta-inner p { font-size: 16px; color: rgba(255,255,255,0.8); margin-bottom: 32px; }
.cta-inner .btn-primary {
  background: #fff;
  color: var(--color-primary);
  border-color: #fff;
  font-weight: 600;
}
.cta-inner .btn-primary:hover { background: var(--color-primary-light); }

/* 푸터 */
.footer {
  background: var(--color-text-primary);
  padding: 32px 0;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 700;
}
.footer-logo img { width: 28px; height: 28px; border-radius: 6px; }
.footer-copy { font-size: 13px; color: rgba(255,255,255,0.5); }

@media (max-width: 992px) {
  .hero-inner { grid-template-columns: 1fr; }
  .hero-visual { width: 100%; justify-content: flex-start; margin-top: 24px; }
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .course-grid { grid-template-columns: repeat(2, 1fr); }
  .steps-grid { grid-template-columns: repeat(2, 1fr); }
  .persona-grid { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .category-grid { grid-template-columns: repeat(2, 1fr); }
  .course-grid { grid-template-columns: 1fr; }
  .steps-grid { grid-template-columns: 1fr; }
}
</style>
