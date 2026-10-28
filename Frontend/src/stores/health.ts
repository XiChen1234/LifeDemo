import { defineStore } from 'pinia'
import { apiHealth } from '../api'

interface HealthState {
  hero: { title: string; desc: string; chips: string[]; sleepMinutes?: number; deepRatio?: number; steps?: number; weightKg?: number }
  mini: { label: string; value: string; percent: number; color: string }[]
  weekGoalDays: number
  sleepTrend: { labels: string[]; deep: number[]; light: number[]; rem: number[] }
  sportDuration: { labels: string[]; run: number[]; ride: number[]; gym: number[]; other: number[] }
  weightCurve: { labels: string[]; data: number[] }
  sleepRecords: { date: string; sleepTime: string; duration: string; deep: string; quality: string; qualityLevel: string }[]
  habits: { id: string; icon: string; name: string; valueText: string; percent: number; color: string; done: boolean }[]
  loading: boolean
  error: string | null
}

export const useHealthStore = defineStore('health', {
  state: (): HealthState => ({
    hero: { title: '', desc: '', chips: [] },
    mini: [],
    weekGoalDays: 0,
    sleepTrend: { labels: [], deep: [], light: [], rem: [] },
    sportDuration: { labels: [], run: [], ride: [], gym: [], other: [] },
    weightCurve: { labels: [], data: [] },
    sleepRecords: [],
    habits: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiHealth.overview()
        this.hero = d.hero ?? { title: '', desc: '', chips: [] }
        this.mini = d.mini ?? []
        this.weekGoalDays = d.weekGoalDays ?? 0
        this.sleepTrend = d.sleepTrend ?? { labels: [], deep: [], light: [], rem: [] }
        this.sportDuration = d.sportDuration ?? { labels: [], run: [], ride: [], gym: [], other: [] }
        this.weightCurve = d.weightCurve ?? { labels: [], data: [] }
        this.sleepRecords = d.sleepRecords ?? []
        this.habits = d.habits ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '健康页加载失败'
      } finally {
        this.loading = false
      }
    },
    toggleHabit(id: string) {
      const habit = this.habits.find((h) => h.id === id)
      if (habit) habit.done = !habit.done
    },
  },
})
