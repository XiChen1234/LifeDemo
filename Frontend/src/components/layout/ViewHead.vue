<script setup lang="ts">
defineProps<{
  title: string
  sub?: string
  tag?: string
}>()
</script>

<template>
  <div class="view-head">
    <div>
      <h1>{{ title }}</h1>
      <p v-if="sub">{{ sub }}</p>
    </div>
    <div v-if="tag" class="head-tag">{{ tag }}</div>
  </div>
</template>

<style scoped>
.view-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}
h1 {
  font-size: var(--fs-3xl);
  font-weight: 600;
  letter-spacing: -0.3px;
  line-height: 1.15;
}
p {
  font-size: var(--fs-md);
  color: var(--text-muted);
  margin-top: 6px;
}
.head-tag {
  font-size: var(--fs-sm);
  color: var(--green-deep);
  background: var(--green-soft);
  padding: 5px 11px;
  border-radius: 99px;
  white-space: nowrap;
}
</style>
