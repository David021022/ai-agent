<template>
  <section ref="container" class="message-list" aria-label="聊天记录">
    <MessageBubble v-for="message in messages" :key="message.id" :message="message" />
  </section>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from 'vue';
import type { ChatMessage } from '@/types/chat';
import MessageBubble from './MessageBubble.vue';

const props = defineProps<{
  messages: ChatMessage[];
}>();

const container = ref<HTMLElement | null>(null);

watch(
  () => props.messages.map((message) => message.content).join('\n'),
  async () => {
    await nextTick();
    if (container.value) {
      container.value.scrollTop = container.value.scrollHeight;
    }
  },
  { flush: 'post' },
);
</script>

<style scoped>
.message-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  overflow: auto;
  padding: 1rem;
  min-height: 0;
}
</style>
