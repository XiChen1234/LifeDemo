import type { SleepRecord, Habit } from '../types'

export const mockHealthHero = {
  title: '今天的状态不错',
  desc: '昨晚睡了 7 小时 32 分，深睡比例 23%。今天适合做一次轻度有氧。',
  chips: ['睡眠 7h32m', '步数 8,420', '体重 57.4kg'],
}

export const mockHealthMini = [
  { label: '今日睡眠', value: '7h 32m', percent: 82, color: 'blue' as const },
  { label: '今日步数', value: '8,420', percent: 70, color: 'green' as const },
]

export const mockSleepTrend = {
  labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  deep: [1.7, 1.5, 1.9, 1.6, 1.8, 2.1, 1.9],
  light: [4.0, 3.8, 4.3, 4.1, 4.4, 4.8, 4.2],
  rem: [1.3, 1.2, 1.4, 1.3, 1.5, 1.7, 1.4],
}

export const mockSportDuration = {
  labels: ['第 1 周', '第 2 周', '第 3 周', '第 4 周'],
  run: [2.0, 2.4, 1.8, 2.2],
  ride: [0.6, 0.5, 0.9, 0.7],
  gym: [0.8, 1.1, 0.9, 1.0],
  other: [0.3, 0.3, 0.2, 0.3],
}

export const mockWeightCurve = {
  labels: ['5/13', '5/20', '5/27', '6/03', '6/10', '6/12'],
  data: [58.6, 58.2, 57.9, 58.1, 57.6, 57.4],
}

export const mockSleepRecords: SleepRecord[] = [
  { date: '06-12', sleepTime: '23:40 入睡', duration: '7h32m', deep: '1.7h', quality: '良好', qualityLevel: 'low' },
  { date: '06-11', sleepTime: '00:10 入睡', duration: '6h05m', deep: '1.4h', quality: '一般', qualityLevel: 'mid' },
  { date: '06-10', sleepTime: '23:20 入睡', duration: '7h48m', deep: '2.0h', quality: '优秀', qualityLevel: 'low' },
  { date: '06-09', sleepTime: '23:55 入睡', duration: '6h40m', deep: '1.6h', quality: '良好', qualityLevel: 'low' },
]

export const mockHabits: Habit[] = [
  { id: 'h1', icon: '💧', name: '喝水', valueText: '6 / 8 杯', percent: 75, color: 'blue', done: true },
  { id: 'h2', icon: '🧘', name: '冥想', valueText: '15 / 20 分钟', percent: 75, color: 'green', done: true },
  { id: 'h3', icon: '🤸', name: '拉伸', valueText: '1 / 2 次', percent: 50, color: 'yellow', done: false },
  { id: 'h4', icon: '🌙', name: '早睡', valueText: '4 / 7 天', percent: 57, color: 'pink', done: false },
]
