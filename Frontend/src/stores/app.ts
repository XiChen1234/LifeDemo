import { defineStore } from 'pinia'
import { mockUser, mockAppMeta, mockDataSources } from '../mock/app'

type Theme = 'light' | 'dark' | 'system'

export const useAppStore = defineStore('app', {
  state: () => ({
    user: { ...mockUser },
    meta: { ...mockAppMeta },
    dataSources: mockDataSources.map((s) => ({ ...s })),
  }),
  actions: {
    setTheme(theme: Theme) {
      if (theme !== 'light') return // 当前仅实现亮色
      this.meta.theme = theme
    },
    updateUser(patch: Partial<{ name: string; targetYear: number; signature: string }>) {
      Object.assign(this.user, patch)
    },
  },
})
