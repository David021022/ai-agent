<template>
  <main class="home-page">
    <section class="hero-panel">
      <div class="hero-badge">AI 智能体</div>
      <h1>选择一个入口开始体验</h1>
    </section>

    <section class="cards">
      <RouterLink class="entry-card love" to="/love">
        <div class="card-top">
          <span class="card-icon">♥</span>
        </div>
        <div class="card-body">
          <h2>AI 旅游大师</h2>
          <p>面向旅游指南和行程规划的智能助手。</p>
        </div>
        <div class="card-action">立即体验</div>
      </RouterLink>

      <RouterLink class="entry-card manus" to="/manus">
        <div class="card-top">
          <span class="card-icon">✦</span>
        </div>
        <div class="card-body">
          <h2>AI 超级智能体</h2>
          <p>面向通用智能体能力的实时对话入口。</p>
        </div>
        <div class="card-action">立即体验</div>
      </RouterLink>
    </section>
  </main>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 2rem;
  color: #111827;
  background:
    radial-gradient(circle at top, rgba(124, 58, 237, 0.1), transparent 28%),
    radial-gradient(circle at 85% 20%, rgba(59, 130, 246, 0.08), transparent 24%),
    linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%);
}

.hero-panel {
  max-width: 920px;
  margin: 0 auto 2rem;
  padding: 1.4rem 1.6rem;
  text-align: center;
}

.hero-badge {
  width: fit-content;
  margin: 0 auto 0.9rem;
  padding: 0.45rem 0.9rem;
  border: 1px solid rgba(17, 24, 39, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  font-size: 0.8rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(17, 24, 39, 0.6);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.6rem);
  line-height: 1.08;
  letter-spacing: -0.04em;
  color: #0f172a;
}

.cards {
  max-width: 1040px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.5rem;
}

.entry-card {
  position: relative;
  overflow: hidden;
  min-height: 28rem;
  padding: 1.4rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 1.8rem;
  background: rgba(255, 255, 255, 0.76);
  backdrop-filter: blur(18px);
  box-shadow: 0 18px 60px rgba(15, 23, 42, 0.08);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.entry-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 72px rgba(15, 23, 42, 0.12);
  border-color: rgba(15, 23, 42, 0.14);
}

.entry-card::before {
  content: '';
  position: absolute;
  inset: auto -12% -18% auto;
  width: 14rem;
  height: 14rem;
  border-radius: 999px;
  filter: blur(8px);
  opacity: 0.95;
}

.love::before {
  background: radial-gradient(circle, rgba(251, 113, 133, 0.28) 0%, rgba(251, 113, 133, 0) 72%);
}

.manus::before {
  background: radial-gradient(circle, rgba(96, 165, 250, 0.28) 0%, rgba(96, 165, 250, 0) 72%);
}

.card-top {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-icon {
  width: 2.6rem;
  height: 2.6rem;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.04);
  color: rgba(15, 23, 42, 0.68);
}

.card-body {
  display: grid;
  gap: 0.8rem;
  margin-top: 5rem;
  justify-items: center;
  text-align: center;
}

.card-body h2 {
  margin: 0;
  font-size: 1.8rem;
  line-height: 1.15;
  letter-spacing: -0.03em;
  color: #0f172a;
}

.card-body p {
  max-width: 22rem;
  margin: 0;
  font-size: 1rem;
  line-height: 1.7;
  color: rgba(15, 23, 42, 0.68);
}

.card-action {
  position: absolute;
  left: 1.4rem;
  right: 1.4rem;
  bottom: 1.4rem;
  height: 3.1rem;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: #0f172a;
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.02em;
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.18);
}

@media (max-width: 820px) {
  .home-page {
    padding: 1rem;
  }

  .cards {
    grid-template-columns: 1fr;
  }

  .entry-card {
    min-height: 24rem;
  }
}
</style>
