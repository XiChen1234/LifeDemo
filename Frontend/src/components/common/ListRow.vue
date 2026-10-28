<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string
    sub?: string
    clickable?: boolean
    active?: boolean
  }>(),
  { clickable: false, active: false }
)

const emit = defineEmits<{ (e: 'click'): void }>()
</script>

<template>
  <div class="list-row" :class="{ click: clickable, on: active }" @click="emit('click')">
    <slot name="leading" />
    <div class="list-main">
      <div class="list-title">{{ title }}</div>
      <div v-if="sub" class="list-sub">{{ sub }}</div>
    </div>
    <slot name="trailing" />
  </div>
</template>

<style scoped>
.list-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 0;
  border-bottom: 1px solid var(--line);
}
.list-row:last-child {
  border-bottom: 0;
  padding-bottom: 2px;
}
.list-main {
  flex: 1;
  min-width: 0;
}
.list-title {
  font-size: var(--fs-base);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.list-sub {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 2px;
}
.list-row.click {
  cursor: pointer;
  border-radius: 8px;
  padding: 8px;
  transition: background 0.16s ease;
}
.list-row.click:hover {
  background: var(--surface-2);
}
.list-row.click.on {
  background: var(--green-soft);
}
.list-row.click.on .list-title {
  color: var(--green-deep);
  font-weight: 600;
}
</style>
