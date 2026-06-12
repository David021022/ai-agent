<template>
  <section class="chat-shell">
    <header class="chat-header">
      <div>
        <p class="eyebrow">AI 应用</p>
        <h1>{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
      </div>

      <div class="meta">
        <span class="chat-id">Chat ID: {{ chatId }}</span>
        <button v-if="isStreaming" class="stop" type="button" @click="$emit('stop')">
          结束生成
        </button>
      </div>
    </header>

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
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { ChatMessage } from '@/types/chat';
import ChatInput from './ChatInput.vue';
import MessageList from './MessageList.vue';

defineProps<{
  title: string;
  subtitle: string;
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
.chat-shell {
  width: min(1120px, calc(100vw - 1.2rem));
  min-height: min(920px, calc(100vh - 1.2rem));
  margin: 0.6rem auto;
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: 1rem;
  padding: 1.2rem;
  border: 1px solid var(--panel-border);
  border-radius: 1.5rem;
  background: var(--panel);
  box-shadow: var(--shadow);
  backdrop-filter: blur(22px);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
}

.eyebrow {
  margin: 0 0 0.25rem;
  font-size: 0.78rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--text-soft);
}

h1 {
  margin: 0;
  font-size: clamp(1.6rem, 2.4vw, 2.2rem);
  color: var(--text-strong);
}

.subtitle {
  margin: 0.35rem 0 0;
  color: var(--text-soft);
}

.meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.6rem;
}

.chat-id {
  padding: 0.4rem 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: var(--text-soft);
  font-size: 0.85rem;
}

.stop {
  border: 0;
  border-radius: 999px;
  padding: 0.65rem 1rem;
  color: #fff;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.12);
}

.chat-body {
  min-height: 0;
  border-radius: 1.2rem;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(6, 10, 20, 0.42);
}

.chat-footer {
  padding-top: 0.2rem;
}

@media (max-width: 760px) {
  .chat-shell {
    width: min(100vw - 1rem, 1120px);
    min-height: calc(100vh - 1rem);
    margin: 0.5rem auto;
    padding: 0.9rem;
    border-radius: 1rem;
  }

  .chat-header {
    flex-direction: column;
  }

  .meta {
    align-items: flex-start;
  }
}
</style>
