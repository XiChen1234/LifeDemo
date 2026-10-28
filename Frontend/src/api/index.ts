/**
 * 前端 ↔ 后端 接口映射。路径对齐后端 /api/v1/* 控制器。
 */
import { http } from './http'

// ---------- 页面聚合 ----------
export const apiDashboard = { overview: (date?: string) => http.get<any>(`/dashboard/overview${date ? `?date=${date}` : ''}`) }
export const apiHealth = { overview: (date?: string) => http.get<any>(`/health/overview${date ? `?date=${date}` : ''}`) }
export const apiWealth = { overview: () => http.get<any>('/wealth/overview') }
export const apiWork = { overview: () => http.get<any>('/work/overview') }
export const apiLife = { overview: () => http.get<any>('/life/overview') }
export const apiNotes = {
  overview: () => http.get<any>('/notes/overview'),
  tree: () => http.get<any>('/notes/tree'),
  tags: (limit = 8) => http.get<any>(`/notes/tags/stats?limit=${limit}`),
  list: (params: { folder?: string; q?: string; tag?: string; page?: number; size?: number }) => {
    const q = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => { if (v != null && v !== '') q.set(k, String(v)) })
    return http.get<any>(`/notes?${q.toString()}`)
  },
  content: (path: string) => http.get<any>(`/notes/content?path=${encodeURIComponent(path)}`),
  rescan: () => http.post<any>('/notes/rescan'),
}
export const apiMe = {
  profile: () => http.get<any>('/me'),
  integrations: () => http.get<any>('/me/integrations'),
  patch: (body: { displayName?: string; signature?: string; theme?: string; avatarChar?: string }) =>
    http.patch<any>('/me', body),
}

// ---------- 工作 / 任务 ----------
export const apiTasks = {
  list: (params?: { date?: string; page?: number; size?: number }) => {
    const q = new URLSearchParams()
    Object.entries(params ?? {}).forEach(([k, v]) => { if (v != null && v !== '') q.set(k, String(v)) })
    return http.get<any>(`/work/tasks?${q.toString()}`)
  },
  create: (body: { title: string; area?: string; priority?: string; scheduledDate?: string; dueAt?: string }) =>
    http.post<any>('/work/tasks', body),
  patch: (id: string, body: { done?: boolean; title?: string; priority?: string; area?: string }) =>
    http.patch<any>(`/work/tasks/${id}`, body),
  remove: (id: string) => http.delete<any>(`/work/tasks/${id}`),
}

// ---------- 财富 ----------
export const apiAccounts = { list: () => http.get<any>('/wealth/accounts') }
export const apiCategories = { list: () => http.get<any>('/wealth/categories') }
export const apiBudgets = { list: (month?: string) => http.get<any>(`/wealth/budgets${month ? `?budgetMonth=${month}` : ''}`) }
export const apiSavings = { plan: () => http.get<any>('/wealth/savings-plan') }
export const apiTransactions = {
  list: (params?: { page?: number; size?: number }) => {
    const q = new URLSearchParams()
    Object.entries(params ?? {}).forEach(([k, v]) => { if (v != null) q.set(k, String(v)) })
    return http.get<any>(`/wealth/transactions?${q.toString()}`)
  },
  create: (body: { type: 'income' | 'expense'; categoryId: number; title: string; amount: number; occurredAt: string; note?: string }) =>
    http.post<any>('/wealth/transactions', body),
  patch: (id: string, body: { title?: string; amount?: number; note?: string; categoryId?: number }) =>
    http.patch<any>(`/wealth/transactions/${id}`, body),
  remove: (id: string) => http.delete<any>(`/wealth/transactions/${id}`),
}

// ---------- 健康 ----------
export const apiHealthSleep = {
  upsert: (body: { sleepAt: string; wakeAt: string; deepMinutes?: number; lightMinutes?: number; remMinutes?: number; awakeMinutes?: number; qualityScore?: number; note?: string }) =>
    http.post<any>('/health/sleep-records', body),
}
export const apiHealthActivity = {
  upsert: (date: string, body: { steps: number; stepGoal?: number; distanceKm?: number; activeCaloriesKcal?: number }) =>
    http.put<any>(`/health/daily-activities/${date}`, body),
}
export const apiHealthWorkouts = {
  list: (from: string, to: string) => http.get<any>(`/health/workouts?from=${from}&to=${to}`),
  create: (body: { workoutType: string; startedAt: string; durationMinutes: number; distanceKm?: number; caloriesKcal?: number; note?: string }) =>
    http.post<any>('/health/workouts', body),
}
export const apiHealthBody = {
  upsert: (body: { measuredOn: string; weightKg: number; bodyFatPercent?: number; muscleKg?: number }) =>
    http.post<any>('/health/body-metrics', body),
}
export const apiHealthHabit = {
  checkIn: (habitId: string, date: string, done: boolean) =>
    http.put<any>(`/health/habits/${habitId}/check-ins/${date}`, { done }),
}

// ---------- 生活 ----------
export const apiLifeActivity = {
  list: (year: number, month?: number) => {
    const q = new URLSearchParams({ year: String(year) })
    if (month) q.set('month', String(month))
    return http.get<any>(`/life/activities?${q.toString()}`)
  },
  create: (body: { activityType: string; title: string; occurredAt: string; durationMinutes?: number; note?: string }) =>
    http.post<any>('/life/activities', body),
  remove: (id: string) => http.delete<any>(`/life/activities/${id}`),
}
export const apiLifeMedia = {
  list: () => http.get<any>('/life/media'),
  create: (body: { mediaType: string; title: string; creator?: string; status: string; progressCurrent?: number; progressTotal?: number; progressUnit?: string; rating?: number; finishedOn?: string }) =>
    http.post<any>('/life/media', body),
  patch: (id: string, body: { title?: string; status?: string; finishedOn?: string; progressCurrent?: number; progressTotal?: number; progressUnit?: string; rating?: number }) =>
    http.patch<any>(`/life/media/${id}`, body),
  remove: (id: string) => http.delete<any>(`/life/media/${id}`),
}
export const apiProjects = {
  list: () => http.get<any>('/work/projects'),
  create: (body: { name: string; stage?: string; progressPercent?: number; status?: string; startDate?: string; targetDate?: string; color?: string }) =>
    http.post<any>('/work/projects', body),
  update: (id: string, body: { name: string; stage?: string; progressPercent?: number; status?: string; startDate?: string; targetDate?: string; color?: string }) =>
    http.put<any>(`/work/projects/${id}`, body),
  remove: (id: string) => http.delete<any>(`/work/projects/${id}`),
}
