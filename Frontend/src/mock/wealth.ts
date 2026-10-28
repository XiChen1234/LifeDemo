import type { Metric, Transaction, Budget, Goal } from '../types'

export const mockWealthBanner = {
  title: '本月收支健康',
  desc: '本月结余 ¥9,774，储蓄率 53.7%，预算使用 84%，整体节奏好于上个月。',
  chips: ['总资产 ¥220,780', '结余 ¥9,774', '储蓄率 53.7%'],
}

export const mockWealthMetrics: Metric[] = [
  { label: '总资产', value: '¥220,780', trend: 'up', trendText: '+¥6,420', foot: '本月' },
  { label: '本月收入', value: '¥18,200', foot: '工资 + 副业' },
  { label: '本月支出', value: '¥8,426', trend: 'up', trendText: '-12.8%', foot: '较上月' },
  { label: '本月结余', value: '¥9,774', foot: '储蓄率 53.7%' },
]

export const mockMonthCash = {
  labels: ['第 1 周', '第 2 周', '第 3 周', '第 4 周'],
  income: [4200, 4600, 5100, 4200],
  expense: [2100, 2400, 1900, 2026],
}

export const mock6mExpense = {
  labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
  data: [9860, 11240, 10380, 12460, 10920, 8426],
}

export const mockExpenseCategory = [
  { label: '餐饮', value: 2860 },
  { label: '购物', value: 1420 },
  { label: '交通', value: 1320 },
  { label: '娱乐', value: 966 },
  { label: '其他', value: 1860 },
]

export const mockSavingGoals: Goal[] = [
  { name: '年度', percent: 38.6, valueText: '38.6%', subText: '¥19,300', color: 'green' },
  { name: '月度', percent: 76, valueText: '76%', subText: '¥3,200', color: 'yellow' },
  { name: '连续', percent: 83, valueText: '5 个月', subText: '达标', color: 'blue' },
]

export const mockTransactions: Transaction[] = [
  { id: 't1', category: '餐饮', categoryClass: 'low', title: '午餐 · 公司楼下', time: '今天 12:30', amount: '-¥38', isIncome: false },
  { id: 't2', category: '交通', categoryClass: 'blue', title: '地铁通勤', time: '今天 08:45', amount: '-¥6', isIncome: false },
  { id: 't3', category: '收入', categoryClass: 'low', title: '工资入账', time: '06-10', amount: '+¥18,200', isIncome: true },
  { id: 't4', category: '购物', categoryClass: 'mid', title: '独立书店 · 两本书', time: '06-09', amount: '-¥128', isIncome: false },
  { id: 't5', category: '餐饮', categoryClass: 'low', title: '咖啡', time: '06-09', amount: '-¥32', isIncome: false },
  { id: 't6', category: '娱乐', categoryClass: 'pink', title: '电影票', time: '06-08', amount: '-¥68', isIncome: false },
]

export const mockBudgets: Budget[] = [
  { name: '餐饮', used: 2860, total: 3000, percent: 95, color: 'danger' },
  { name: '购物', used: 1420, total: 2000, percent: 71, color: 'yellow' },
  { name: '交通', used: 1320, total: 1500, percent: 88, color: 'yellow' },
  { name: '娱乐', used: 966, total: 1500, percent: 64, color: 'green' },
  { name: '其他', used: 1860, total: 2500, percent: 74, color: 'blue' },
]
