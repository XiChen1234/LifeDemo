import { defineStore } from 'pinia'
import {
  mockHealthHero,
  mockHealthMini,
  mockSleepTrend,
  mockSportDuration,
  mockWeightCurve,
  mockSleepRecords,
  mockHabits,
} from '../mock/health'
import { loadDoneMap, persistDone } from './persist'

export const useHealthStore = defineStore('health', {
  state: () => {
    const doneMap = loadDoneMap('life-habits')
    return {
      hero: { ...mockHealthHero },
      mini: mockHealthMini.map((m) => ({ ...m })),
      sleepTrend: mockSleepTrend,
      sportDuration: mockSportDuration,
      weightCurve: mockWeightCurve,
      sleepRecords: mockSleepRecords.map((r) => ({ ...r })),
      habits: mockHabits.map((h) => ({ ...h, done: doneMap[h.id] ?? h.done })),
    }
  },
  actions: {
    toggleHabit(id: string) {
      const habit = this.habits.find((h) => h.id === id)
      if (!habit) return
      habit.done = !habit.done
      persistDone('life-habits', this.habits)
    },
  },
})
