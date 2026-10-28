import type { Metric, ProgressItem, MediaItem } from '../types'

export const mockLifeBanner = {
  title: '六月过得挺充实',
  desc: '30 次活动，读了 6 本书，看了 8 部影视。运动和独处占了大多数，节奏刚刚好。',
  chips: ['运动 12 次', '阅读 6 本', '观影 8 部'],
}

export const mockLifeMetrics: Metric[] = [
  { label: '本周游戏时长', value: '6.5h', trend: 'up', trendText: '+1.2h', foot: '较上周' },
  { label: '本月观影数量', value: '8 部', foot: '电影 5 · 剧集 3' },
  { label: '本月阅读数量', value: '6 本', foot: '已读完 2 本' },
  { label: '本月活动次数', value: '30 次', foot: '运动 12 · 独处 9' },
]

export const mockActivityDist = [
  { label: '运动', value: 12 },
  { label: '独处', value: 9 },
  { label: '聚餐', value: 6 },
  { label: '旅行', value: 3 },
]

export const mockMediaDist = [
  { label: '电影', value: 32 },
  { label: '剧集', value: 28 },
  { label: '纪录片', value: 22 },
  { label: '动画', value: 18 },
]

export const mockReading: ProgressItem[] = [
  { name: '《慢思考》', valueText: '62%', percent: 62, color: 'green' },
  { name: '《置身事内》', valueText: '28%', percent: 28, color: 'blue' },
  { name: '《人类简史》', valueText: '12%', percent: 12, color: 'yellow' },
  { name: '《小径分岔的花园》', valueText: '100%', percent: 100, color: 'pink' },
]

export const mockWatching: ProgressItem[] = [
  { name: '《重启人生》', valueText: '4 / 10 集', percent: 40, color: 'pink' },
  { name: '《宇宙时空之旅》', valueText: '2 / 13 集', percent: 15, color: 'yellow' },
  { name: '《漫长的季节》', valueText: '6 / 12 集', percent: 50, color: 'blue' },
  { name: '《小森林》', valueText: '100%', percent: 100, color: 'green' },
]

export const mockFinishedMedia: MediaItem[] = [
  { id: 'm1', type: '电影', typeClass: 'low', title: '《小森林》', meta: '06-11 · 夏秋篇', rating: 4 },
  { id: 'm2', type: '剧集', typeClass: 'blue', title: '《重启人生》', meta: '06-08 · 第 4 集', rating: 5 },
  { id: 'm3', type: '纪录片', typeClass: 'mid', title: '《宇宙时空之旅》', meta: '06-05 · 第 2 集', rating: 4 },
  { id: 'm4', type: '图书', typeClass: 'pink', title: '《百年孤独》', meta: '06-02 · 已读完', rating: 4 },
  { id: 'm5', type: '图书', typeClass: 'pink', title: '《小径分岔的花园》', meta: '05-30 · 已读完', rating: 5 },
  { id: 'm6', type: '电影', typeClass: 'low', title: '《海街日记》', meta: '05-28', rating: 4 },
]
