import { defineStore } from 'pinia'
import { apiDashboard } from '../api'

type Metric = { key?: string; label: string; value: string; foot?: string; trend?: 'up' | 'down'; trendText?: string }
type Goal = { name: string; percent: number; valueText: string; subText?: string; color: string }
type ProgressItem = { name: string; valueText: string; percent: number; color: string }

interface DashboardState {
  date: string
  hero: { greeting: string; quote: string; chips: string[]; dateMain: string; dateSub: string; displayName?: string }
  metrics: Metric[]
  yearGoals: Goal[]
  cashTrend: { labels: string[]; income: number[]; expense: number[] }
  expenseStructure: ProgressItem[]
  mediaProgress: ProgressItem[]
  recentNotes: { id: string; title: string; tag: string; timeAgo: string; path?: string }[]
  loading: boolean
  error: string | null
}

export const useDashboardStore = defineStore('dashboard', {
  state: (): DashboardState => ({
    date: '',
    hero: { greeting: '', quote: '', chips: [], dateMain: '', dateSub: '' },
    metrics: [],
    yearGoals: [],
    cashTrend: { labels: [], income: [], expense: [] },
    expenseStructure: [],
    mediaProgress: [],
    recentNotes: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiDashboard.overview()
        this.date = d.date ?? ''
        this.hero = d.hero
        this.metrics = d.metrics ?? []
        this.yearGoals = d.yearGoals ?? []
        this.cashTrend = d.cashTrend ?? { labels: [], income: [], expense: [] }
        this.expenseStructure = d.expenseStructure ?? []
        this.mediaProgress = d.mediaProgress ?? []
        this.recentNotes = d.recentNotes ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '首页加载失败'
      } finally {
        this.loading = false
      }
    },
  },
})
