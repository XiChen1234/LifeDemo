/**
 * 统一 fetch 封装。开发态通过 Vite proxy 转发到 Spring Boot；
 * 生产可通过 VITE_API_BASE_URL 覆盖（默认相对路径 /api/v1）。
 */

export interface ApiError {
  status: number
  title?: string
  detail?: string
  errors?: { field: string; message: string }[]
}

const BASE = (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? '/api/v1'

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const url = path.startsWith('http') ? path : BASE + path
  const resp = await fetch(url, {
    ...init,
    headers: {
      Accept: 'application/json, application/problem+json',
      'Content-Type': 'application/json; charset=utf-8',
      ...(init?.headers ?? {}),
    },
  })
  if (!resp.ok) {
    const err = await parseError(resp)
    throw err
  }
  if (resp.status === 204 || resp.status === 304) {
    return undefined as unknown as T
  }
  const ct = resp.headers.get('content-type') ?? ''
  if (ct.includes('application/json') || ct.includes('problem+json')) {
    return (await resp.json()) as T
  }
  return (await resp.text()) as unknown as T
}

async function parseError(resp: Response): Promise<ApiError> {
  try {
    const body = await resp.json()
    return {
      status: resp.status,
      title: body.title,
      detail: body.detail,
      errors: body.errors,
    }
  } catch {
    return { status: resp.status, detail: resp.statusText }
  }
}

export const http = {
  get: <T>(path: string) => api<T>(path, { method: 'GET' }),
  post: <T>(path: string, body?: unknown) =>
    api<T>(path, { method: 'POST', body: body == null ? undefined : JSON.stringify(body) }),
  patch: <T>(path: string, body?: unknown) =>
    api<T>(path, { method: 'PATCH', body: body == null ? undefined : JSON.stringify(body) }),
  put: <T>(path: string, body?: unknown) =>
    api<T>(path, { method: 'PUT', body: body == null ? undefined : JSON.stringify(body) }),
  delete: <T>(path: string, body?: unknown) =>
    api<T>(path, { method: 'DELETE', body: body == null ? undefined : JSON.stringify(body) }),
}

export function displayError(err: unknown): string {
  const e = err as ApiError
  if (e?.detail) return e.detail
  if (typeof err === 'string') return err
  return '请求失败'
}
