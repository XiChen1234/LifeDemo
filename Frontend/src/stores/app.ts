import { defineStore } from 'pinia'
import { apiMe } from '../api'

export type Theme = 'light' | 'dark' | 'system'

interface AppState {
  user: { name: string; avatarChar: string; usedDays: number; targetYear: number; signature: string }
  meta: { version: string; theme: Theme; lastUpdate: string; dataUpdatedAt: string; techStack: string }
  dataSources: { id: string; icon: string; iconBg: string; iconColor: string; name: string; desc: string; status: string }[]
  loading: boolean
  error: string | null
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    user: { name: '', avatarChar: '', usedDays: 0, targetYear: 0, signature: '' },
    meta: { version: '', theme: 'light', lastUpdate: '', dataUpdatedAt: '', techStack: '' },
    dataSources: [],
    loading: false,
    error: null,
  }),
  actions: {
    setTheme(theme: Theme) {
      if (theme !== 'light') return
      this.meta.theme = theme
    },
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiMe.profile()
        this.user = d.user ?? this.user
        this.meta = d.meta ?? this.meta
        this.dataSources = d.dataSources ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '个人信息加载失败'
      } finally {
        this.loading = false
      }
    },
    updateUser(patch: Partial<{ name: string; targetYear: number; signature: string }>) {
      Object.assign(this.user, patch)
    },
    async patch(patch: Partial<{ name: string; signature: string; theme: Theme; avatarChar: string }>) {
      try {
        const resp = await apiMe.patch({
          displayName: patch.name,
          signature: patch.signature,
          theme: patch.theme,
          avatarChar: patch.avatarChar,
        })
        Object.assign(this.user, resp ?? {})
      } catch (e: any) {
        this.error = e?.detail ?? '个人资料更新失败'
      }
    },
  },
})
