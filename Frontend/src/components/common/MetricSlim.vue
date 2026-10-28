<script setup lang="ts">
defineProps<{
  label: string
  value: string
  unit?: string
}>()
</script>

<template>
  <div class="metric-slim">
    <span class="ms-label">{{ label }}</span>
    <span class="ms-value">{{ value }}<span v-if="unit">{{ unit }}</span></span>
  </div>
</template>

<style scoped>
.metric-slim {
  background: var(--card);
  border: 1px solid #f2f6f4;
  border-radius: var(--radius-md);
  padding: 10px 13px;
  box-shadow: var(--shadow-soft);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-width: 0;
}
.ms-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ms-value {
  font-size: var(--fs-xl);
  font-weight: 600;
  letter-spacing: -0.3px;
  white-space: nowrap;
}
.ms-value span {
  font-size: var(--fs-sm);
  font-weight: 500;
  color: var(--text-muted);
  margin-left: 1px;
}
</style>
