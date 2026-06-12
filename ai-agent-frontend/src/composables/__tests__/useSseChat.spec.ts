import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createSseChatSession } from '@/composables/useSseChat';

class MockEventSource {
  static instances: MockEventSource[] = [];

  url: string;
  closed = false;
  onmessage: ((event: MessageEvent<string>) => void) | null = null;
  onerror: ((event: Event) => void) | null = null;

  constructor(url: string) {
    this.url = url;
    MockEventSource.instances.push(this);
  }

  close() {
    this.closed = true;
  }

  emitMessage(data: string) {
    this.onmessage?.({ data } as MessageEvent<string>);
  }

  emitError() {
    this.onerror?.(new Event('error'));
  }
}

describe('createSseChatSession', () => {
  const originalEventSource = globalThis.EventSource;

  beforeEach(() => {
    MockEventSource.instances = [];
    globalThis.EventSource = MockEventSource as unknown as typeof EventSource;
  });

  afterEach(() => {
    globalThis.EventSource = originalEventSource;
    vi.restoreAllMocks();
  });

  it('creates user and assistant messages and appends streamed chunks', () => {
    const session = createSseChatSession({
      createStreamUrl: (message) => `http://example.com/chat?message=${encodeURIComponent(message)}`,
    });

    session.sendMessage('你好');

    expect(session.messages.value).toHaveLength(2);
    expect(session.messages.value[0]).toMatchObject({
      role: 'user',
      content: '你好',
    });
    expect(session.messages.value[1]).toMatchObject({
      role: 'assistant',
      content: '',
    });
    expect(MockEventSource.instances).toHaveLength(1);
    expect(MockEventSource.instances[0].url).toContain('message=%E4%BD%A0%E5%A5%BD');

    MockEventSource.instances[0].emitMessage('第一段');
    MockEventSource.instances[0].emitMessage('第二段');

    expect(session.messages.value[1].content).toBe('第一段第二段');
  });

  it('stops the stream when requested', () => {
    const session = createSseChatSession({
      createStreamUrl: () => 'http://example.com/chat',
    });

    session.sendMessage('hi');
    const source = MockEventSource.instances[0];

    session.stop();

    expect(source.closed).toBe(true);
    expect(session.isStreaming.value).toBe(false);
  });
});
