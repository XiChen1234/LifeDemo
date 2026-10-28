import { defineStore } from 'pinia'
import {
  mockWorkBanner,
  mockCalendarMeta,
  mockCalendarEvents,
  mockWorkTodos,
  mockDeadlines,
  mockProjects,
} from '../mock/work'
import { loadDoneMap, persistDone } from './persist'
import type { Priority } from '../types'

export const useWorkStore = defineStore('work', {
  state: () => {
    const doneMap = loadDoneMap('life-work-todos')
    return {
      banner: { ...mockWorkBanner },
      calendarMeta: { ...mockCalendarMeta },
      calendarEvents: mockCalendarEvents.map((e) => ({ ...e })),
      todos: mockWorkTodos.map((t) => ({ ...t, done: doneMap[t.id] ?? t.done })),
      deadlines: mockDeadlines.map((d) => ({ ...d })),
      projects: mockProjects.map((p) => ({ ...p })),
    }
  },
  getters: {
    todoDoneCount: (s) => s.todos.filter((t) => t.done).length,
    todosByPriority: (s) => (p: Priority) => s.todos.filter((t) => t.priority === p),
  },
  actions: {
    toggleTodo(id: string) {
      const todo = this.todos.find((t) => t.id === id)
      if (!todo) return
      todo.done = !todo.done
      persistDone('life-work-todos', this.todos)
    },
  },
})
