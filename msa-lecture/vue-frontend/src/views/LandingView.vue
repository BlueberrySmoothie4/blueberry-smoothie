<template>
  <div class="landing">
    <AppHeader />

    <!-- 히어로 섹션 -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-content fade-in-up">
          <span class="hero-badge">MSA 기반 AI API 마켓플레이스</span>
          <h1 class="hero-title">필요한 AI 기능을,<br>엔드포인트 하나로</h1>
          <p class="hero-desc">비전·음성·언어·문서 등 검증된 AI API를 비교하고 월 구독으로 바로 연동하세요.</p>
          <div class="hero-actions">
            <router-link to="/login" class="btn btn-primary btn-lg">지금 시작하기</router-link>
            <router-link to="/products" class="btn btn-outline btn-lg">API 둘러보기</router-link>
          </div>
          <div class="hero-stats">
            <div class="stat"><span class="stat-num">1,200+</span><span class="stat-label">API 상품</span></div>
            <div class="stat"><span class="stat-num">340+</span><span class="stat-label">제공사</span></div>
            <div class="stat"><span class="stat-num">28,000+</span><span class="stat-label">구독 기업</span></div>
          </div>
        </div>
        <div class="hero-visual fade-in">
          <img src="@/assets/images/logo/main_logo.png" alt="Blueberry Smoothie" class="hero-logo" />
        </div>
      </div>
    </section>

    <!-- 인기 API 상품 -->
    <section class="popular-section">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title">인기 API 상품</h2>
          <router-link to="/login" class="section-link">전체 보기 →</router-link>
        </div>
        <div class="course-grid">
          <div v-for="product in featuredProducts" :key="product.id" class="course-card-landing">
            <div class="card-thumb" :class="product.thumbBg">
              <img :src="product.thumbSrc" :alt="product.title" class="thumb-img" />
            </div>
            <div class="card-body">
              <span class="badge" :class="product.badgeClass">{{ product.category }}</span>
              <h3 class="card-title">{{ product.title }}</h3>
              <div class="card-meta">
                <span class="instructor">{{ product.provider }}</span>
                <span class="price">{{ product.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 특징 섹션 -->
    <section class="features-section">
      <div class="section-inner">
        <h2 class="section-title center">왜 Blueberry Smoothie인가요?</h2>
        <div class="features-grid">
          <div v-for="f in features" :key="f.title" class="feature-card">
            <div class="feature-icon">{{ f.icon }}</div>
            <h3 class="feature-title">{{ f.title }}</h3>
            <p class="feature-desc">{{ f.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="cta-inner">
        <h2>지금 바로 연동하세요</h2>
        <p>수천 개의 팀이 Blueberry Smoothie에서 필요한 AI 기능을 찾고 있습니다.</p>
        <router-link to="/login" class="btn btn-primary btn-lg">지금 시작하기</router-link>
      </div>
    </section>

    <!-- 푸터 -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-logo">
          <img src="@/assets/images/logo/main_logo.png" alt="Blueberry Smoothie" />
          <span>Blueberry Smoothie</span>
        </div>
        <p class="footer-copy">© 2026 Blueberry Smoothie. All rights reserved.</p>
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

const featuredProducts = [
  { id:1, title:'제조 설비 불량 검출 API', category:'비전',     provider:'비전랩',      price:'₩89,000/월', thumbSrc: pythonImg, thumbBg:'thumb-purple', badgeClass:'badge-purple' },
  { id:2, title:'한국어 회의록 STT API',   category:'음성',     provider:'보이스에이아이', price:'₩69,000/월', thumbSrc: vueImg,    thumbBg:'thumb-teal',   badgeClass:'badge-teal'   },
  { id:3, title:'다국어 번역·요약 API',    category:'언어',     provider:'링구아',       price:'₩99,000/월', thumbSrc: springImg, thumbBg:'thumb-teal',   badgeClass:'badge-teal'   },
  { id:4, title:'계약서 조항 추출 API',    category:'문서',     provider:'독스코어',     price:'₩79,000/월', thumbSrc: dockerImg, thumbBg:'thumb-blue',   badgeClass:'badge-blue'   },
  { id:5, title:'수요 예측 시계열 API',    category:'예측',     provider:'포캐스트웍스',  price:'₩59,000/월', thumbSrc: k8sImg,    thumbBg:'thumb-blue',   badgeClass:'badge-blue'   },
  { id:6, title:'상품 설명 생성 API',      category:'생성형',   provider:'젠텍스트',     price:'₩75,000/월', thumbSrc: genaiImg,  thumbBg:'thumb-pink',   badgeClass:'badge-pink'   },
]

const features = [
  { icon:'🔌', title:'즉시 연동', desc:'구독 결제가 끝나는 순간 호출 권한이 활성화됩니다. 별도 심사 대기가 없습니다.' },
  { icon:'🎯', title:'맞춤 API 추천', desc:'추천 서비스가 구독 이력을 분석해 같은 기능군의 대체·보완 API를 제안합니다.' },
  { icon:'💳', title:'월 구독 정액제', desc:'호출당 과금 없이 예측 가능한 비용으로 운영하세요.' },
  { icon:'🧩', title:'기능 기준 탐색', desc:'비전·음성·언어·문서 등 필요한 기능에서 바로 멀티벤더 비교를 시작하세요.' },
]
</script>

<style scoped>
.landing { background: var(--color-bg-secondary); }

/* 히어로 */
.hero {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 50%, #f0f9ff 100%);
  border-bottom: 1px solid var(--color-border);
  padding: 80px 0 64px;
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
  font-size: 42px;
  font-weight: 700;
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
.stat-num { font-size: 22px; font-weight: 700; color: var(--color-primary); }
.stat-label { font-size: 12px; color: var(--color-text-secondary); }
.hero-visual {
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero-logo {
  width: 200px;
  height: 200px;
  object-fit: contain;
  border-radius: 24px;
  box-shadow: var(--shadow-lg);
}

/* API 상품 섹션 */
.popular-section { padding: 64px 0; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.section-title { font-size: 22px; font-weight: 700; color: var(--color-text-primary); }
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
.thumb-teal   { background: #E1F5EE; }
.thumb-blue   { background: #E6F1FB; }
.thumb-purple { background: #EEEDFE; }
.thumb-pink   { background: #FBEAF0; }
.thumb-img { width: 100%; height: 100%; object-fit: contain; padding: 14px; }
.card-body { padding: 14px 16px; display: flex; flex-direction: column; gap: 6px; }
.card-title { font-size: 14px; font-weight: 600; color: var(--color-text-primary); line-height: 1.4; }
.card-meta { display: flex; justify-content: space-between; align-items: center; }
.instructor { font-size: 12px; color: var(--color-text-secondary); }
.price { font-size: 14px; font-weight: 600; color: var(--color-primary); }

/* 특징 */
.features-section { padding: 64px 0; background: var(--color-bg-primary); }
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.feature-card {
  padding: 28px 24px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  text-align: center;
  transition: var(--transition);
}
.feature-card:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.feature-icon { font-size: 32px; margin-bottom: 12px; }
.feature-title { font-size: 15px; font-weight: 600; margin-bottom: 8px; }
.feature-desc { font-size: 13px; color: var(--color-text-secondary); line-height: 1.6; }

/* CTA */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  text-align: center;
}
.cta-inner { max-width: 600px; margin: 0 auto; padding: 0 24px; }
.cta-inner h2 { font-size: 32px; font-weight: 700; color: #fff; margin-bottom: 12px; }
.cta-inner p { font-size: 16px; color: rgba(255,255,255,0.8); margin-bottom: 32px; }
.cta-inner .btn-primary {
  background: #fff;
  color: var(--color-primary);
  border-color: #fff;
  font-weight: 600;
}
.cta-inner .btn-primary:hover { background: #f0f7ff; }

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
  font-size: 15px;
  font-weight: 600;
}
.footer-logo img { width: 28px; height: 28px; border-radius: 6px; }
.footer-copy { font-size: 13px; color: rgba(255,255,255,0.5); }
</style>
