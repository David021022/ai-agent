<template>
  <ChatShell
    :title="'AI 超级智能体'"
    subtitle="输入需求，体验智能体实时推理与回复。"
    :chat-id="chatId"
    :messages="messages"
    :is-streaming="isStreaming"
    @send="sendMessage"
    @stop="stop"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ChatShell from '@/components/ChatShell.vue';
import { buildManusChatUrl } from '@/api/ai';
import { useSseChat } from '@/composables/useSseChat';

const chatId = ref(globalThis.crypto?.randomUUID?.() ?? `manus_${Date.now()}`);

const { messages, isStreaming, sendMessage, stop } = useSseChat({
  createStreamUrl: (message) => buildManusChatUrl(message),
});
</script>
