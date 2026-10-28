/** 勾选/打卡状态的 localStorage 持久化辅助 */

export function loadDoneMap(key: string): Record<string, boolean> {
  try {
    const raw = localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as Record<string, boolean>) : {}
  } catch {
    return {}
  }
}

export function persistDone(key: string, items: { id: string; done: boolean }[]) {
  const map: Record<string, boolean> = {}
  items.forEach((i) => {
    map[i.id] = i.done
  })
  try {
    localStorage.setItem(key, JSON.stringify(map))
  } catch {
    /* ignore */
  }
}
