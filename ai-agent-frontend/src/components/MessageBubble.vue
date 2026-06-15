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
  padding: 1rem 1.1rem;
  border-radius: 1.2rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 40px rgba(15, 23, 42, 0.06);
  word-break: break-word;
  white-space: pre-wrap;
}

.is-user {
  margin-left: auto;
  background: linear-gradient(160deg, #0f172a, #1e293b);
  color: #f8fafc;
  border-bottom-right-radius: 0.35rem;
}

.is-assistant {
  margin-right: auto;
  background: rgba(255, 255, 255, 0.95);
  color: #0f172a;
  border-bottom-left-radius: 0.35rem;
}

.bubble-label {
  margin-bottom: 0.35rem;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: inherit;
  opacity: 0.58;
}

.bubble-content {
  font-size: 0.98rem;
  line-height: 1.75;
}
</style>
