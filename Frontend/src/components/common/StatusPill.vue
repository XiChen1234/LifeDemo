<script setup lang="ts">
withDefaults(
  defineProps<{
    text: string
    level?: string
  }>(),
  { level: 'low' }
)
</script>

<template>
  <span class="pill" :class="level">{{ text }}</span>
</template>

<style scoped>
.pill {
  font-size: var(--fs-xs);
  padding: 2px 7px;
  border-radius: 99px;
  font-weight: 500;
  white-space: nowrap;
  flex: none;
}
.pill.high { background: var(--pink-soft); color: #c9748a; }
.pill.mid { background: var(--yellow-soft); color: #b98b2e; }
.pill.low { background: var(--green-soft); color: var(--green-deep); }
.pill.blue { background: var(--blue-soft); color: #5b8fc4; }
.pill.pink { background: var(--pink-soft); color: #c9748a; }
</style>
