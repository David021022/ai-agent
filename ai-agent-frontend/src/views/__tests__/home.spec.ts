import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import HomeView from '@/views/HomeView.vue';

describe('home navigation', () => {
  it('shows two entry cards for the two apps', () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>',
          },
        },
      },
    });

    expect(wrapper.text()).toContain('AI 旅游大师');
    expect(wrapper.text()).toContain('AI 超级智能体');
  });
});
