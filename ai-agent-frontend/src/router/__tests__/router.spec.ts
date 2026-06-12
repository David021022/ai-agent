import { describe, expect, it } from 'vitest';
import router from '@/router';

describe('route config', () => {
  it('exposes home and two chat routes', () => {
    const paths = router.getRoutes().map((route) => route.path).sort();

    expect(paths).toEqual(['/', '/love', '/manus']);
  });
});
