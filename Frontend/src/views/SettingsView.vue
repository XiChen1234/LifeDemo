<script setup lang="ts">
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import PageBanner from '../components/common/PageBanner.vue'
import StatusPill from '../components/common/StatusPill.vue'
import { useAppStore } from '../stores/app'

const app = useAppStore()

const banner = {
  title: '一切运行正常',
  desc: '数据已本地保存，主题与偏好即时生效。数据源将在后续版本接入。',
  chips: [`版本 ${app.meta.version}`, '亮色主题', '本地存储'],
}

const themeOptions = [
  { key: 'light' as const, label: '亮色', enabled: true },
  { key: 'dark' as const, label: '暗色', enabled: false },
  { key: 'system' as const, label: '跟随系统', enabled: false },
]

const aboutLines = [
  { label: '版本', value: app.meta.version },
  { label: '技术栈', value: app.meta.techStack },
  { label: '图表方案', value: 'ECharts 6' },
  { label: '数据更新', value: app.meta.lastUpdate },
]
</script>

<template>
  <div>
    <ViewHead title="设置" sub="偏好与数据源" :tag="app.meta.version" />

    <div class="stack">
      <PageBanner :title="banner.title" :desc="banner.desc" :chips="banner.chips" />

      <div class="grid g2">
        <!-- 个人信息 -->
        <AppCard title="个人信息" sub="本地资料">
          <div class="profile-head">
            <div class="avatar">{{ app.user.avatarChar }}</div>
            <div>
              <div class="profile-name">{{ app.user.name }}</div>
              <div class="profile-sub">已使用 {{ app.user.usedDays }} 天</div>
            </div>
          </div>
          <div class="field">
            <label>昵称</label>
            <input
              class="input"
              :value="app.user.name"
              @input="app.updateUser({ name: ($event.target as HTMLInputElement).value })"
            />
          </div>
          <div class="field">
            <label>目标年份</label>
            <input
              class="input"
              type="number"
              :value="app.user.targetYear"
              @input="app.updateUser({ targetYear: Number(($event.target as HTMLInputElement).value) })"
            />
          </div>
          <div class="field">
            <label>签名</label>
            <input
              class="input"
              :value="app.user.signature"
              @input="app.updateUser({ signature: ($event.target as HTMLInputElement).value })"
            />
          </div>
        </AppCard>

        <!-- 主题 + 关于 -->
        <div class="col-stack">
          <AppCard title="主题偏好" sub="当前仅支持亮色">
            <div class="opt-row">
              <span
                v-for="opt in themeOptions"
                :key="opt.key"
                class="opt"
                :class="{ on: app.meta.theme === opt.key, disabled: !opt.enabled }"
                :title="opt.enabled ? undefined : '后续版本开放'"
                @click="opt.enabled && app.setTheme(opt.key)"
              >{{ opt.label }}</span>
            </div>
          </AppCard>

          <AppCard title="关于" sub="生活台 · Life Console">
            <div v-for="line in aboutLines" :key="line.label" class="about-line">
              <span>{{ line.label }}</span>
              <span>{{ line.value }}</span>
            </div>
          </AppCard>
        </div>
      </div>

      <!-- 数据源 -->
      <AppCard title="数据源配置" sub="接入后自动替换 Mock 数据">
        <div v-for="src in app.dataSources" :key="src.name" class="src">
          <span class="src-ico" :style="{ background: src.iconBg, color: src.iconColor }">{{ src.icon }}</span>
          <div class="src-main">
            <div class="src-name">{{ src.name }}</div>
            <div class="src-desc">{{ src.desc }}</div>
          </div>
          <StatusPill :text="src.status" :level="src.status === '只读' ? 'mid' : 'low'" />
        </div>
      </AppCard>
    </div>
  </div>
</template>

<style scoped>
.profile-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  flex: none;
  background: linear-gradient(140deg, var(--green), #8fd6c0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--fs-xl);
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(85, 185, 154, 0.25);
}
.profile-name {
  font-size: var(--fs-lg);
  font-weight: 600;
}
.profile-sub {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 2px;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}
.field:last-child {
  margin-bottom: 0;
}
.field label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}
.input {
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  padding: 8px 11px;
  font-size: var(--fs-base);
  color: var(--text-primary);
  background: var(--surface-2);
  font-family: inherit;
  outline: none;
  transition: border-color 0.16s ease;
}
.input:focus {
  border-color: var(--green);
  background: #fff;
}
.col-stack {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}
.opt-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.opt {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  border: 1px solid var(--line);
  border-radius: 99px;
  padding: 6px 14px;
  cursor: pointer;
  transition: all 0.16s ease;
  user-select: none;
}
.opt:hover {
  border-color: #cfe9df;
}
.opt.on {
  background: var(--green-soft);
  border-color: #cfe9df;
  color: var(--green-deep);
  font-weight: 600;
}
.opt.disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.about-line {
  display: flex;
  justify-content: space-between;
  font-size: var(--fs-md);
  color: var(--text-secondary);
  padding: 7px 0;
  border-bottom: 1px dashed var(--line);
}
.about-line:last-child {
  border-bottom: 0;
}
.about-line span:last-child {
  color: var(--text-primary);
  font-weight: 500;
}
.src {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  background: var(--surface-2);
  margin-bottom: 8px;
}
.src:last-child {
  margin-bottom: 0;
}
.src-ico {
  width: 26px;
  height: 26px;
  border-radius: 9px;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--fs-md);
  font-weight: 600;
}
.src-main {
  flex: 1;
  min-width: 0;
}
.src-name {
  font-size: var(--fs-base);
}
.src-desc {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 2px;
}
</style>
