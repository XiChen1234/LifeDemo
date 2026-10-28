<script setup lang="ts">
export interface LegendItem {
  label: string
  color: string
}

defineProps<{ items: LegendItem[] }>()
</script>

<template>
  <div class="legend">
    <span v-for="item in items" :key="item.label" class="lg">
      <i :style="{ background: item.color }"></i>{{ item.label }}
    </span>
  </div>
</template>

<style scoped>
.legend {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.lg {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}
.lg i {
  width: 8px;
  height: 8px;
  border-radius: 3px;
  display: block;
  flex: none;
}
</style>
