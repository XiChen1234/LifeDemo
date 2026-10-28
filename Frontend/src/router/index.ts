import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'dashboard', component: () => import('../views/DashboardView.vue'), meta: { title: '首页' } },
  { path: '/health', name: 'health', component: () => import('../views/HealthView.vue'), meta: { title: '健康' } },
  { path: '/wealth', name: 'wealth', component: () => import('../views/WealthView.vue'), meta: { title: '财富' } },
  { path: '/work', name: 'work', component: () => import('../views/WorkView.vue'), meta: { title: '工作' } },
  { path: '/life', name: 'life', component: () => import('../views/LifeView.vue'), meta: { title: '生活' } },
  { path: '/notes', name: 'notes', component: () => import('../views/NotesView.vue'), meta: { title: '笔记' } },
  { path: '/settings', name: 'settings', component: () => import('../views/SettingsView.vue'), meta: { title: '设置' } },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.afterEach((to) => {
  document.title = `${(to.meta.title as string) ?? ''} · 生活台`
})
