/** 全局 / 设置 mock */

export const mockUser = {
  name: 'Lin',
  avatarChar: 'L',
  usedDays: 128,
  targetYear: 2025,
  signature: '慢慢来，比较快。',
}

export const mockAppMeta = {
  version: 'v0.4.2',
  theme: 'light' as 'light' | 'dark' | 'system',
  lastUpdate: '2025-06-12',
  dataUpdatedAt: '06-12 08:30',
  techStack: 'Vue 3 / TS / Pinia / ECharts',
}

export const mockDataSources = [
  {
    icon: '¥',
    iconBg: 'var(--green-soft)',
    iconColor: 'var(--green-deep)',
    name: '财务 API',
    desc: '用于同步账单与账户余额，需配置授权 Token',
    status: '未连接',
  },
  {
    icon: '♥',
    iconBg: 'var(--blue-soft)',
    iconColor: '#5b8fc4',
    name: '健康 API',
    desc: '同步睡眠、步数、运动与体重数据',
    status: '未连接',
  },
  {
    icon: '✎',
    iconBg: 'var(--yellow-soft)',
    iconColor: '#b98b2e',
    name: 'Obsidian 路径',
    desc: '本地 Vault 目录：~/Documents/Obsidian/Life',
    status: '只读',
  },
]
