import { defineStore } from 'pinia'
import { apiWealth } from '../api'

interface WealthState {
  banner: { title: string; desc: string; chips: string[]; totalAssets?: number; balance?: number; savingsRate?: number }
  metrics: { key?: string; label: string; value: string; foot?: string; trend?: 'up' | 'down'; trendText?: string }[]
  monthCash: { labels: string[]; income: number[]; expense: number[] }
  expense6m: { labels: string[]; data: number[] }
  expenseCategory: { label: string; value: number }[]
  savingGoals: { name: string; percent: number; valueText: string; subText?: string; color: string }[]
  transactions: { id: string; category: string; categoryClass: string; title: string; time: string; amount: string; isIncome: boolean }[]
  budgets: { name: string; used: number; total: number; percent: number; color: string }[]
  loading: boolean
  error: string | null
}

export const useWealthStore = defineStore('wealth', {
  state: (): WealthState => ({
    banner: { title: '', desc: '', chips: [] },
    metrics: [],
    monthCash: { labels: [], income: [], expense: [] },
    expense6m: { labels: [], data: [] },
    expenseCategory: [],
    savingGoals: [],
    transactions: [],
    budgets: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiWealth.overview()
        this.banner = d.banner ?? { title: '', desc: '', chips: [] }
        this.metrics = d.metrics ?? []
        this.monthCash = d.monthCash ?? { labels: [], income: [], expense: [] }
        this.expense6m = d.expense6m ?? { labels: [], data: [] }
        this.expenseCategory = d.expenseCategory ?? []
        this.savingGoals = d.savingGoals ?? []
        this.transactions = d.transactions ?? []
        this.budgets = d.budgets ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '财富页加载失败'
      } finally {
        this.loading = false
      }
    },
  },
})
