<script setup lang="ts">
import type { Goal } from '../../types'
import ProgressBar from './ProgressBar.vue'

defineProps<{
  goal: Goal
  nameWidth?: string
  numWidth?: string
}>()
</script>

<template>
  <div
    class="goal-row"
    :style="nameWidth || numWidth ? { gridTemplateColumns: `${nameWidth ?? '88px'} 1fr ${numWidth ?? '118px'}` } : undefined"
  >
    <span class="goal-name">{{ goal.name }}</span>
    <ProgressBar :percent="goal.percent" :color="goal.color" />
    <span class="goal-num">
      {{ goal.valueText }}<em v-if="goal.subText">{{ goal.subText }}</em>
    </span>
  </div>
</template>

<style scoped>
.goal-row {
  display: grid;
  grid-template-columns: 88px 1fr 118px;
  align-items: center;
  gap: 16px;
  padding: 11px 0;
  border-bottom: 1px solid var(--line);
}
.goal-row:last-child {
  border-bottom: 0;
  padding-bottom: 2px;
}
.goal-name {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.goal-num {
  font-size: var(--fs-base);
  font-weight: 600;
  text-align: right;
  white-space: nowrap;
}
.goal-num em {
  font-style: normal;
  font-weight: 400;
  color: var(--text-muted);
  font-size: var(--fs-sm);
  margin-left: 5px;
}
@media (max-width: 900px) {
  .goal-row {
    grid-template-columns: 72px 1fr 100px !important;
    gap: 10px;
  }
}
</style>
