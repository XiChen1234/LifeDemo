<script setup lang="ts">
import { useAppStore } from '../../stores/app'

const app = useAppStore()

const navItems = [
  { to: '/dashboard', label: '首页' },
  { to: '/health', label: '健康' },
  { to: '/wealth', label: '财富' },
  { to: '/work', label: '工作' },
  { to: '/life', label: '生活' },
  { to: '/notes', label: '笔记' },
]
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">L</div>
      <div>
        <div class="brand-name">生活台</div>
        <div class="brand-sub">LIFE CONSOLE</div>
      </div>
    </div>

    <nav class="nav">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="nav-item"
        active-class="active"
      >
        <span class="nav-dot"></span>{{ item.label }}
      </router-link>
    </nav>

    <div class="nav-foot">
      <router-link to="/settings" class="nav-item" active-class="active">
        <span class="nav-dot"></span>设置
      </router-link>
      <div class="side-note">数据更新于 {{ app.meta.dataUpdatedAt }}</div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 196px;
  flex: none;
  position: sticky;
  top: 0;
  height: 100vh;
  background: var(--card);
  border-right: 1px solid var(--line);
  padding: 20px 12px 18px;
  display: flex;
  flex-direction: column;
}
.brand {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 0 8px 20px;
}
.brand-mark {
  width: 28px;
  height: 28px;
  border-radius: 10px;
  flex: none;
  background: linear-gradient(140deg, var(--green), #8fd6c0);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.4px;
  box-shadow: 0 4px 10px rgba(85, 185, 154, 0.28);
}
.brand-name {
  font-size: var(--fs-lg);
  font-weight: 600;
  letter-spacing: 0.3px;
}
.brand-sub {
  font-size: var(--fs-xs);
  color: var(--text-muted);
  letter-spacing: 0.7px;
}
.nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: var(--fs-base);
  cursor: pointer;
  user-select: none;
  text-decoration: none;
  transition: background 0.18s ease, color 0.18s ease;
}
.nav-item:hover {
  background: var(--surface-2);
  color: var(--text-primary);
}
.nav-item.active {
  background: var(--green-soft);
  color: var(--green-deep);
  font-weight: 600;
}
.nav-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.3;
  flex: none;
  transition: opacity 0.18s ease;
}
.nav-item.active .nav-dot {
  opacity: 1;
}
.nav-foot {
  margin-top: auto;
  padding-top: 14px;
}
.nav-foot .nav-item {
  color: var(--text-muted);
}
.side-note {
  font-size: var(--fs-xs);
  color: var(--text-muted);
  padding: 10px 10px 0;
  line-height: 1.7;
}
@media (max-width: 900px) {
  .sidebar {
    width: 150px;
  }
}
</style>
