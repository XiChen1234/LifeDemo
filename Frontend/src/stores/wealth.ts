import { defineStore } from 'pinia'
import {
  mockWealthBanner,
  mockWealthMetrics,
  mockMonthCash,
  mock6mExpense,
  mockExpenseCategory,
  mockSavingGoals,
  mockTransactions,
  mockBudgets,
} from '../mock/wealth'

export const useWealthStore = defineStore('wealth', {
  state: () => ({
    banner: { ...mockWealthBanner },
    metrics: mockWealthMetrics.map((m) => ({ ...m })),
    monthCash: mockMonthCash,
    expense6m: mock6mExpense,
    expenseCategory: mockExpenseCategory.map((c) => ({ ...c })),
    savingGoals: mockSavingGoals.map((g) => ({ ...g })),
    transactions: mockTransactions.map((t) => ({ ...t })),
    budgets: mockBudgets.map((b) => ({ ...b })),
  }),
})
