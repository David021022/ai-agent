<template>
  <ChatShell
    title="AI 旅游大师"
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
//import { buildLoveChatUrl } from '@/api/ai';
import { buildLoveToolsChatUrl } from '@/api/ai';
import { useSseChat } from '@/composables/useSseChat';

const chatId = ref(globalThis.crypto?.randomUUID?.() ?? `love_${Date.now()}`);

const { messages, isStreaming, sendMessage, stop } = useSseChat({
  createStreamUrl: (message) => buildLoveToolsChatUrl(message, chatId.value),
});
</script>
