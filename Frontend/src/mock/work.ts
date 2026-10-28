import type { Task, Deadline, Project, CalendarEvent } from '../types'

export const mockWorkBanner = {
  title: '今天节奏不错',
  desc: '3 项高优先级任务，2 个 DDL 落在本周，项目整体推进顺利。上午适合先攻复盘文档。',
  chips: ['今日 3 高优', '本周 2 DDL'],
}

export const mockCalendarMeta = {
  year: 2025,
  month: 6, // 1-based
  today: 12,
  title: '六月日历',
  sub: '任务与 DDL 已标记 · 今天 6 月 12 日',
}

export const mockCalendarEvents: CalendarEvent[] = [
  { day: 3, text: '用户访谈整理', level: 'mid' },
  { day: 5, text: '周报草稿', level: 'low' },
  { day: 9, text: 'A/B 实验启动', level: 'mid' },
  { day: 10, text: '知识库同步', level: 'low' },
  { day: 11, text: '设计稿评审', level: 'mid' },
  { day: 12, text: '产品复盘文档', level: 'high' },
  { day: 12, text: '交互稿对齐', level: 'high' },
  { day: 12, text: '18:00 复盘截止', level: 'low', isDdl: true },
  { day: 13, text: '10:00 交互稿评审', level: 'low', isDdl: true },
  { day: 16, text: '月度数据报告', level: 'low', isDdl: true },
  { day: 18, text: '团队分享', level: 'low' },
  { day: 20, text: '季度 OKR 复盘', level: 'low', isDdl: true },
  { day: 24, text: '用户增长复盘', level: 'mid' },
  { day: 26, text: '知识库整理', level: 'low' },
]

export const mockWorkTodos: Task[] = [
  { id: 'w1', text: '写 Q2 产品复盘文档', done: false, priority: 'high' },
  { id: 'w2', text: '和设计对齐新版本交互稿', done: false, priority: 'high' },
  { id: 'w3', text: '提交本周周报', done: true, priority: 'high' },
  { id: 'w4', text: '整理 6 份用户访谈记录', done: false, priority: 'mid' },
  { id: 'w5', text: '更新项目周报看板', done: false, priority: 'mid' },
  { id: 'w6', text: '整理桌面文件夹', done: false, priority: 'low' },
  { id: 'w7', text: '看一集设计课', done: false, priority: 'low' },
  { id: 'w8', text: '回复 3 封邮件', done: true, priority: 'low' },
]

export const mockDeadlines: Deadline[] = [
  { title: '产品复盘文档', time: '今天 18:00', priority: 'high' },
  { title: '交互稿评审', time: '明天 10:00', priority: 'mid' },
  { title: '月度数据报告', time: '06-16', priority: 'mid' },
  { title: '季度 OKR 复盘', time: '06-20', priority: 'low' },
]

export const mockProjects: Project[] = [
  { name: '「生活台」v2 改版', percent: 68, stage: '交互稿', color: 'green' },
  { name: '用户增长实验', percent: 42, stage: 'A/B 中', color: 'blue' },
  { name: '知识库整理计划', percent: 25, stage: '42/168', color: 'yellow' },
]
