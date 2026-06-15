import { API_BASE_URL } from './http';

function buildUrl(path: string, params: Record<string, string>) {
  const url = new URL(path, API_BASE_URL.endsWith('/') ? API_BASE_URL : `${API_BASE_URL}/`);

  Object.entries(params).forEach(([key, value]) => {
    url.searchParams.set(key, value);
  });

  return url.toString();
}

export function buildLoveChatUrl(message: string, chatId: string) {
  return buildUrl('ai/love_app/chat/sse', { message, chatId });
}

export function buildManusChatUrl(message: string) {
  return buildUrl('ai/manus/chat', { message });
}




export function buildLoveToolsChatUrl(message: string, chatId: string) {
  return buildUrl('ai/love_app/chat/tools/sse', { message, chatId });
}