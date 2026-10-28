import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import { router } from './router'
import { useAppStore } from './stores/app'
import { useDashboardStore } from './stores/dashboard'
import { useHealthStore } from './stores/health'
import { useWealthStore } from './stores/wealth'
import { useWorkStore } from './stores/work'
import { useLifeStore } from './stores/life'
import { useNotesStore } from './stores/notes'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia).use(router)

async function bootstrap() {
  const stores = [
    useAppStore(),
    useDashboardStore(),
    useHealthStore(),
    useWealthStore(),
    useWorkStore(),
    useLifeStore(),
    useNotesStore(),
  ]
  // 并发加载，单个失败不阻塞其他
  await Promise.allSettled(stores.map((s) => {
    if ((s as any).fetchOverview) return (s as any).fetchOverview()
    return (s as any).fetch?.()
  }))
  app.mount('#app')
}

bootstrap()
