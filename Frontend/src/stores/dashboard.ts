import { defineStore } from 'pinia'
import {
  mockDashHero,
  mockDashMetrics,
  mockYearGoals,
  mockCashTrend,
  mockTodayTasks,
  mockExpenseStructure,
  mockMediaProgress,
  mockRecentNotes,
} from '../mock/dashboard'
import { loadDoneMap, persistDone } from './persist'

export const useDashboardStore = defineStore('dashboard', {
  state: () => {
    const doneMap = loadDoneMap('life-dash-tasks')
    return {
      hero: { ...mockDashHero },
      metrics: mockDashMetrics.map((m) => ({ ...m })),
      yearGoals: mockYearGoals.map((g) => ({ ...g })),
      cashTrend: mockCashTrend,
      todayTasks: mockTodayTasks.map((t) => ({ ...t, done: doneMap[t.id] ?? t.done })),
      expenseStructure: mockExpenseStructure.map((p) => ({ ...p })),
      mediaProgress: mockMediaProgress.map((p) => ({ ...p })),
      recentNotes: mockRecentNotes.map((n) => ({ ...n })),
    }
  },
  getters: {
    taskDoneCount: (s) => s.todayTasks.filter((t) => t.done).length,
  },
  actions: {
    toggleTask(id: string) {
      const task = this.todayTasks.find((t) => t.id === id)
      if (!task) return
      task.done = !task.done
      persistDone('life-dash-tasks', this.todayTasks)
    },
  },
})
