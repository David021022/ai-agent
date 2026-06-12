import { describe, expect, it } from 'vitest';
import { buildLoveChatUrl, buildManusChatUrl } from '@/api/ai';

describe('ai api urls', () => {
  it('builds the love chat sse url with chatId', () => {
    expect(buildLoveChatUrl('hello', 'chat-1')).toContain('/ai/love_app/chat/sse');
    expect(buildLoveChatUrl('hello world', 'chat-1')).toContain('message=hello+world');
    expect(buildLoveChatUrl('hello', 'chat-1')).toContain('chatId=chat-1');
  });

  it('builds the manus chat url with message only', () => {
    expect(buildManusChatUrl('hello')).toContain('/ai/manus/chat');
    expect(buildManusChatUrl('hello')).toContain('message=hello');
  });
});
