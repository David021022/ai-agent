<template>
  <ChatShell
    :title="'AI 恋爱大师'"
    subtitle="输入你的情感困惑，让 AI 以聊天室方式实时回应。"
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
import { buildLoveChatUrl } from '@/api/ai';
import { useSseChat } from '@/composables/useSseChat';

const chatId = ref(globalThis.crypto?.randomUUID?.() ?? `love_${Date.now()}`);

const { messages, isStreaming, sendMessage, stop } = useSseChat({
  createStreamUrl: (message) => buildLoveChatUrl(message, chatId.value),
});
</script>
