<template>
  <section class="chat-page">
    <header class="hero">
      <RouterLink class="back-button" to="/">返回</RouterLink>
      <h1>{{ title }}</h1>
      <button v-if="isStreaming" class="stop" type="button" @click="$emit('stop')">
        停止生成
      </button>
    </header>

    <section class="chat-panel">
      <div class="chat-body">
        <MessageList :messages="messages" />
      </div>

      <footer class="chat-footer">
        <ChatInput
          v-model="draft"
          :disabled="isStreaming"
          :is-streaming="isStreaming"
          @send="submit"
          @stop="$emit('stop')"
        />
      </footer>
    </section>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import type { ChatMessage } from '@/types/chat';
import ChatInput from './ChatInput.vue';
import MessageList from './MessageList.vue';

defineProps<{
  title: string;
  chatId: string;
  messages: ChatMessage[];
  isStreaming: boolean;
}>();

const emit = defineEmits<{
  (event: 'send', value: string): void;
  (event: 'stop'): void;
}>();

const draft = ref('');

const submit = () => {
  const value = draft.value.trim();
  if (!value) {
    return;
  }

  emit('send', value);
  draft.value = '';
};
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  padding: 2rem;
  background:
    radial-gradient(circle at top, rgba(124, 58, 237, 0.08), transparent 30%),
    radial-gradient(circle at 82% 10%, rgba(59, 130, 246, 0.08), transparent 24%),
    linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%);
}

.hero {
  width: min(1080px, 100%);
  position: relative;
  margin: 0 auto 1rem;
  padding: 1rem 0.2rem 0;
  text-align: center;
}

.back-button {
  position: absolute;
  left: 0;
  top: 0.1rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 5rem;
  height: 2.6rem;
  padding: 0 1rem;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.78);
  color: rgba(15, 23, 42, 0.72);
  font-size: 0.92rem;
  text-decoration: none;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.back-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.08);
}

h1 {
  margin: 0;
  font-size: clamp(2rem, 3.8vw, 3.4rem);
  line-height: 1.08;
  letter-spacing: -0.04em;
  color: #0f172a;
}

.stop {
  position: absolute;
  right: 0;
  top: 0.1rem;
  border: 0;
  border-radius: 999px;
  padding: 0.55rem 1rem;
  color: #fff;
  cursor: pointer;
  background: #0f172a;
  box-shadow: 0 12px 26px rgba(15, 23, 42, 0.16);
}

.chat-panel {
  width: min(1080px, 100%);
  min-height: min(74vh, 900px);
  margin: 0 auto;
  display: grid;
  grid-template-rows: 1fr auto;
  gap: 1rem;
}

.chat-body {
  min-height: 0;
  overflow: hidden;
  border-radius: 1.8rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 18px 60px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(18px);
}

.chat-footer {
  padding-top: 0.2rem;
}

@media (max-width: 760px) {
  .chat-page {
    padding: 1rem;
  }

  .back-button,
  .stop {
    position: static;
  }

  .hero {
    display: grid;
    gap: 0.8rem;
    justify-items: center;
  }

  .chat-panel {
    min-height: auto;
  }
}
</style>
