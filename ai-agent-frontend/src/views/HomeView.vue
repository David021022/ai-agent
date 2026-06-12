<template>
  <main class="home-page">
    <section class="hero">
      <p class="eyebrow">AI Chat Suite</p>
      <h1>选择你要进入的应用</h1>
      <p class="summary">
        两个聊天入口共享同一套聊天体验，但调用不同的后端 SSE 接口。
      </p>
    </section>

    <section class="grid">
      <RouterLink class="entry-card love" to="/love">
        <span class="tag">页面 1</span>
        <h2>AI 恋爱大师</h2>
        <p>面向情感陪伴与聊天建议的实时对话页面。</p>
      </RouterLink>

      <RouterLink class="entry-card manus" to="/manus">
        <span class="tag">页面 2</span>
        <h2>AI 超级智能体</h2>
        <p>面向通用智能体能力的实时对话页面。</p>
      </RouterLink>
    </section>
  </main>
</template>

<style scoped>
.home-page {
  width: min(1120px, calc(100vw - 1.2rem));
  margin: 0 auto;
  padding: 2rem 0 2.5rem;
}

.hero {
  margin-bottom: 1.5rem;
}

.eyebrow {
  margin: 0 0 0.35rem;
  color: var(--text-soft);
  letter-spacing: 0.18em;
  text-transform: uppercase;
  font-size: 0.78rem;
}

h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.4rem);
  color: var(--text-strong);
}

.summary {
  max-width: 42rem;
  margin: 0.75rem 0 0;
  color: var(--text-soft);
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.entry-card {
  min-height: 16rem;
  padding: 1.5rem;
  border-radius: 1.4rem;
  border: 1px solid var(--panel-border);
  display: grid;
  align-content: start;
  gap: 0.85rem;
  box-shadow: var(--shadow);
  transition:
    transform 0.18s ease,
    border-color 0.18s ease;
}

.entry-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.18);
}

.love {
  background:
    radial-gradient(circle at top right, rgba(255, 139, 102, 0.34), transparent 36%),
    rgba(14, 20, 38, 0.88);
}

.manus {
  background:
    radial-gradient(circle at top right, rgba(127, 140, 255, 0.32), transparent 36%),
    rgba(14, 20, 38, 0.88);
}

.tag {
  width: fit-content;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: var(--text-soft);
  font-size: 0.8rem;
}

h2 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--text-strong);
}

p {
  margin: 0;
  color: var(--text-soft);
}

@media (max-width: 760px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
