<script setup lang="ts">
import type { ProgressItem } from '../../types'
import ProgressBar from './ProgressBar.vue'

defineProps<{ item: ProgressItem }>()
</script>

<template>
  <div class="prow">
    <div class="prow-top">
      <span class="prow-name">{{ item.name }}</span>
      <span class="prow-val">{{ item.valueText }}</span>
    </div>
    <ProgressBar :percent="item.percent" :color="item.color" />
  </div>
</template>

<style scoped>
.prow {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 0;
}
.prow-top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-size: var(--fs-md);
}
.prow-name {
  color: var(--text-secondary);
}
.prow-val {
  color: var(--text-primary);
  font-weight: 600;
  font-size: var(--fs-base);
}
</style>
