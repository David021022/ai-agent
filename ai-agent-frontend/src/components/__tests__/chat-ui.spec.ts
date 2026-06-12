import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import MessageBubble from '@/components/MessageBubble.vue';
import ChatInput from '@/components/ChatInput.vue';

describe('chat ui basics', () => {
  it('renders user messages on the right and assistant messages on the left', () => {
    const userBubble = mount(MessageBubble, {
      props: {
        message: {
          id: '1',
          role: 'user',
          content: '你好',
        },
      },
    });

    const assistantBubble = mount(MessageBubble, {
      props: {
        message: {
          id: '2',
          role: 'assistant',
          content: '你好，我是 AI。',
        },
      },
    });

    expect(userBubble.classes()).toContain('is-user');
    expect(assistantBubble.classes()).toContain('is-assistant');
    expect(userBubble.text()).toContain('你好');
    expect(assistantBubble.text()).toContain('你好，我是 AI。');
  });

  it('emits send when enter is pressed', async () => {
    const wrapper = mount(ChatInput, {
      props: {
        modelValue: 'hello',
        'onUpdate:modelValue': () => undefined,
      },
    });

    await wrapper.find('textarea').trigger('keydown.enter');

    expect(wrapper.emitted('send')).toHaveLength(1);
  });
});
