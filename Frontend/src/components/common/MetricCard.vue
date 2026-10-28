<script setup lang="ts">
import type { Metric } from '../../types'

defineProps<{ metric: Metric }>()
</script>

<template>
  <div class="card metric">
    <div class="metric-label">{{ metric.label }}</div>
    <div class="metric-value">{{ metric.value }}</div>
    <div class="metric-foot">
      <span v-if="metric.trendText" :class="metric.trend === 'down' ? 'down' : 'up'">{{ metric.trendText }}</span>
      {{ metric.foot ? ' ' + metric.foot : '' }}
    </div>
  </div>
</template>

<style scoped>
.metric {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
}
.metric-label {
  font-size: var(--fs-md);
  color: var(--text-secondary);
}
.metric-value {
  font-size: var(--fs-2xl);
  font-weight: 600;
  letter-spacing: -0.4px;
  line-height: 1.15;
}
.metric-foot {
  font-size: var(--fs-sm);
  color: var(--text-muted);
}
</style>
