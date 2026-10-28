/** 全局领域模型类型定义。后端为事实来源，以下类型放宽 literal union。 */

// ---------- 通用 ----------
export type Priority = 'high' | 'mid' | 'low' | string
export type BarColor = string // 'green' | 'blue' | 'yellow' | 'pink' | 'danger' | 'gray'
export type Level = 'low' | 'mid' | 'high' | 'blue' | 'pink' | string

export interface Metric {
  key?: string
  label: string
  value: string
  foot?: string
  trend?: 'up' | 'down'
  trendText?: string
}

export interface Task {
  id: string
  text: string
  done: boolean
  priority: Priority
  title?: string
  area?: string
}

export interface Goal {
  name: string
  percent: number
  valueText: string
  subText?: string
  color: BarColor
}

export interface ProgressItem {
  name: string
  valueText: string
  percent: number
  color: BarColor
}

// ---------- 首页 ----------
export interface RecentNote {
  id: string
  title: string
  tag: string
  timeAgo: string
}

// ---------- 健康 ----------
export interface SleepRecord {
  date: string
  sleepTime: string
  duration: string
  deep: string
  quality: string
  qualityLevel: Level
}

export interface Habit {
  id: string
  icon: string
  name: string
  valueText: string
  percent: number
  color: BarColor
  done: boolean
}

// ---------- 财富 ----------
export interface Transaction {
  id: string
  category: string
  categoryClass: Level
  title: string
  time: string
  amount: string
  isIncome: boolean
}

export interface Budget {
  name: string
  used: number
  total: number
  percent: number
  color: BarColor
}

// ---------- 工作 ----------
export interface CalendarEvent {
  day: number
  text: string
  level: Priority
  isDdl?: boolean
}

export interface Deadline {
  title: string
  time: string
  priority: Priority
}

export interface Project {
  id?: string
  name: string
  percent: number
  stage: string
  color: BarColor
}

// ---------- 生活 ----------
export interface MediaItem {
  id: string
  type: string
  typeClass: Level
  title: string
  meta: string
  rating: number
}

// ---------- 笔记 ----------
export interface NoteFolder {
  id: string
  icon: string
  name: string
  count: number
  parentId?: string
}

export interface Note {
  id: string
  folderId: string
  title: string
  tag: string
  tagClass: Level
  timeAgo: string
  words: number
  paragraphs: NoteBlock[]
  tags: { text: string; class: Level }[]
}

export type NoteBlock =
  | { type: 'p'; text: string }
  | { type: 'h4'; text: string }

export interface TagStat {
  name: string
  count: number
  percent: number
  color: BarColor
}

// ---------- 设置 ----------
export interface DataSource {
  icon: string
  iconBg: string
  iconColor: string
  name: string
  desc: string
  status: string
}
