<script setup lang="ts">
defineProps<{
  title?: string
  sub?: string
}>()
</script>

<template>
  <div class="card">
    <div v-if="title || $slots.head" class="card-head">
      <div>
        <div v-if="title" class="card-title">{{ title }}</div>
        <div v-if="sub" class="card-sub">{{ sub }}</div>
      </div>
      <slot name="head" />
    </div>
    <slot />
  </div>
</template>

<style scoped>
.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.card-title {
  font-size: var(--fs-lg);
  font-weight: 600;
  letter-spacing: 0.1px;
}
.card-sub {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 3px;
}
</style>
