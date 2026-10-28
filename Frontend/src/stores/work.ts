import { defineStore } from 'pinia'
import { apiWork, apiTasks } from '../api'
import type { Priority } from '../types'

interface TaskRow {
  id: string
  text: string
  title: string
  area: string
  priority: Priority
  done: boolean
  scheduledDate?: string
  dueAt?: string
  completedAt?: string
}

interface WorkState {
  banner: { title: string; desc: string; chips: string[] }
  calendarMeta: { year: number; month: number; today: number; title: string; sub: string }
  calendarEvents: { day: number; text: string; level: Priority; isDdl?: boolean }[]
  todos: TaskRow[]
  deadlines: { title: string; time: string; priority: Priority }[]
  projects: { id?: string; name: string; percent: number; stage: string; color: string }[]
  loading: boolean
  error: string | null
}

export const useWorkStore = defineStore('work', {
  state: (): WorkState => ({
    banner: { title: '', desc: '', chips: [] },
    calendarMeta: { year: 0, month: 0, today: 0, title: '', sub: '' },
    calendarEvents: [],
    todos: [],
    deadlines: [],
    projects: [],
    loading: false,
    error: null,
  }),
  getters: {
    todoDoneCount: (s) => s.todos.filter((t) => t.done).length,
    todosByPriority: (s) => (p: Priority) => s.todos.filter((t) => t.priority === p),
  },
  actions: {
    async fetch() {
      this.loading = true
      this.error = null
      try {
        const d = await apiWork.overview()
        this.banner = d.banner ?? { title: '', desc: '', chips: [] }
        this.calendarMeta = d.calendar ?? d.calendarMeta ?? { year: 0, month: 0, today: 0, title: '', sub: '' }
        this.calendarEvents = d.calendarEvents ?? []
        this.todos = d.todos ?? []
        this.deadlines = d.deadlines ?? []
        this.projects = d.projects ?? []
      } catch (e: any) {
        this.error = e?.detail ?? '工作页加载失败'
      } finally {
        this.loading = false
      }
    },
    async toggleTodo(id: string) {
      const t = this.todos.find((x) => x.id === id)
      if (!t) return
      const prev = t.done
      t.done = !prev
      try {
        await apiTasks.patch(id, { done: t.done })
      } catch (e: any) {
        t.done = prev // rollback on failure
        alert('更新失败：' + (e?.detail ?? '请稍后重试'))
      }
    },
  },
})
