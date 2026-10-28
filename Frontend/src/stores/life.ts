import { defineStore } from 'pinia'
import { apiLife } from '../api'

interface LifeState {
  banner: { title: string; desc: string; chips: string[] }
  metrics: { label: string; value: string; foot?: string; trend?: 'up' | 'down'; trendText?: string }[]
  activityDist: { label: string; value: number }[]
  mediaDist: { label: string; value: number }[]
  reading: { name: string; valueText: string; percent: number; color: string }[]
  watching: { name: string; valueText: string; percent: number; color: string }[]
  finishedMedia: { id: string; type: string; typeClass: string; title: string; meta: string; rating: number }[]
  loading: boolean
  error: string | null
}

export const useLifeStore = defineStore('life', {
  state: (): LifeState => ({
    banner: { title: '', desc: '', chips: [] },
    metrics: [],
    activityDist: [],
    mediaDist: [],
    reading: [],
    watching: [],
    finishedMedia: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiLife.overview()
        this.banner = d.banner ?? { title: '', desc: '', chips: [] }
        this.metrics = d.metrics ?? []
        this.activityDist = d.activityDist ?? []
        this.mediaDist = d.mediaDist ?? []
        this.reading = d.reading ?? []
        this.watching = d.watching ?? []
        this.finishedMedia = d.finishedMedia ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '生活页加载失败'
      } finally {
        this.loading = false
      }
    },
  },
})
