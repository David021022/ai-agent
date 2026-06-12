import { onUnmounted, ref } from 'vue';
import type { ChatMessage } from '@/types/chat';

interface CreateSseChatSessionOptions {
  createStreamUrl: (message: string) => string;
}

function createId() {
  return globalThis.crypto?.randomUUID?.() ?? `msg_${Date.now()}_${Math.random().toString(16).slice(2)}`;
}

export function createSseChatSession(options: CreateSseChatSessionOptions) {
  const messages = ref<ChatMessage[]>([]);
  const isStreaming = ref(false);
  let activeSource: EventSource | null = null;

  const stop = () => {
    if (activeSource) {
      activeSource.close();
      activeSource = null;
    }

    isStreaming.value = false;
  };

  const sendMessage = (rawMessage: string) => {
    const message = rawMessage.trim();

    if (!message || isStreaming.value) {
      return;
    }

    stop();

    const assistantId = createId();
    messages.value = [
      ...messages.value,
      {
        id: createId(),
        role: 'user',
        content: message,
      },
      {
        id: assistantId,
        role: 'assistant',
        content: '',
      },
    ];

    const source = new EventSource(options.createStreamUrl(message));
    activeSource = source;
    isStreaming.value = true;

    source.onmessage = (event) => {
      if (activeSource !== source) {
        return;
      }

      const chunk = event.data ?? '';
      const index = messages.value.findIndex((item) => item.id === assistantId);

      if (index >= 0) {
        const current = messages.value[index];
        messages.value[index] = {
          ...current,
          content: `${current.content}${chunk}`,
        };
      }
    };

    source.onerror = () => {
      if (activeSource === source) {
        stop();
      }
    };
  };

  return {
    messages,
    isStreaming,
    sendMessage,
    stop,
  };
}

export function useSseChat(options: CreateSseChatSessionOptions) {
  const session = createSseChatSession(options);

  onUnmounted(() => {
    session.stop();
  });

  return session;
}
