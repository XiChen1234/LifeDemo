import type { Metric, Goal, ProgressItem, RecentNote } from '../types'

export const mockDashHero = {
  greeting: '早安，Lin',
  quote: '今天也慢慢来，把最重要的那件事先做完。',
  chips: ['已连续记录 128 天', '本周完成度 72%', '目标 2025'],
  dateMain: '06 / 12',
  dateSub: '星期四 · 六月',
}

export const mockDashMetrics: Metric[] = [
  { label: '睡眠', value: '7h 32m', trend: 'up', trendText: '+0.4h', foot: '较上周' },
  { label: '本月支出', value: '¥8,426', trend: 'up', trendText: '-12.8%', foot: '较上月' },
  { label: '今日步数', value: '8,420', foot: '目标 10,000 · 84%' },
  { label: '连续记录', value: '128 天', trend: 'up', trendText: '+1', foot: '今日已记录' },
]

export const mockYearGoals: Goal[] = [
  { name: '读书', percent: 50, valueText: '12 / 24', subText: '50%', color: 'green' },
  { name: '跑步', percent: 45, valueText: '268 / 600 km', subText: '45%', color: 'blue' },
  { name: '储蓄', percent: 39, valueText: '¥19,300 / ¥50,000', subText: '39%', color: 'yellow' },
  { name: '旅行', percent: 50, valueText: '2 / 4 次', subText: '50%', color: 'pink' },
]

export const mockCashTrend = {
  labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
  income: [18200, 17600, 19800, 18500, 21000, 19400],
  expense: [11200, 9800, 13400, 10600, 12100, 8426],
}

export const mockExpenseStructure: ProgressItem[] = [
  { name: '餐饮', valueText: '¥2,860', percent: 100, color: 'green' },
  { name: '购物', valueText: '¥1,420', percent: 50, color: 'yellow' },
  { name: '交通', valueText: '¥1,320', percent: 46, color: 'blue' },
  { name: '娱乐', valueText: '¥966', percent: 34, color: 'pink' },
]

export const mockMediaProgress: ProgressItem[] = [
  { name: '《慢思考》', valueText: '62%', percent: 62, color: 'green' },
  { name: '《置身事内》', valueText: '28%', percent: 28, color: 'blue' },
  { name: '《重启人生》', valueText: '4 / 10 集', percent: 40, color: 'pink' },
  { name: '《宇宙时空之旅》', valueText: '2 / 13 集', percent: 15, color: 'yellow' },
]

export const mockRecentNotes: RecentNote[] = [
  { id: 'n1', title: '《慢思考》读书笔记', tag: '读书', timeAgo: '2 小时前' },
  { id: 'n2', title: '关于「生活台」的信息架构', tag: '产品', timeAgo: '昨天' },
  { id: 'n3', title: '六月的三件小事', tag: '生活', timeAgo: '2 天前' },
  { id: 'n4', title: '月度预算复盘', tag: '财务', timeAgo: '3 天前' },
]
