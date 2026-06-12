<template>
  <article :class="bubbleClass">
    <div class="bubble-label">{{ label }}</div>
    <div class="bubble-content">{{ message.content }}</div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { ChatMessage } from '@/types/chat';

const props = defineProps<{
  message: ChatMessage;
}>();

const bubbleClass = computed(() => [
  'message-bubble',
  props.message.role === 'user' ? 'is-user' : 'is-assistant',
]);

const label = computed(() => (props.message.role === 'user' ? '你' : 'AI'));
</script>

<style scoped>
.message-bubble {
  max-width: min(78%, 42rem);
  padding: 0.9rem 1rem;
  border-radius: 1.1rem;
  border: 1px solid var(--panel-border);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  word-break: break-word;
  white-space: pre-wrap;
}

.is-user {
  margin-left: auto;
  background: linear-gradient(160deg, rgba(95, 114, 255, 0.92), rgba(127, 140, 255, 0.78));
  color: var(--text-strong);
  border-bottom-right-radius: 0.35rem;
}

.is-assistant {
  margin-right: auto;
  background: rgba(17, 25, 46, 0.9);
  color: var(--text-strong);
  border-bottom-left-radius: 0.35rem;
}

.bubble-label {
  margin-bottom: 0.35rem;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.58);
}

.bubble-content {
  font-size: 0.98rem;
  line-height: 1.7;
}
</style>
