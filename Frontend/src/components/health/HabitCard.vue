<script setup lang="ts">
import type { Habit } from '../../types'
import ProgressBar from '../common/ProgressBar.vue'

defineProps<{ habit: Habit }>()

const emit = defineEmits<{ (e: 'toggle', id: string): void }>()
</script>

<template>
  <div class="habit" :class="{ done: habit.done }" @click="emit('toggle', habit.id)">
    <div class="habit-top">
      <span class="habit-ico">{{ habit.icon }}</span>
      <span class="habit-check"></span>
    </div>
    <div class="habit-info">
      <div class="habit-name">{{ habit.name }}</div>
      <div class="habit-val">{{ habit.valueText }}</div>
    </div>
    <ProgressBar :percent="habit.percent" :color="habit.color" small />
  </div>
</template>

<style scoped>
.habit {
  background: var(--surface-2);
  border: 1px solid #eef4f1;
  border-radius: var(--radius-md);
  padding: 13px 14px;
  cursor: pointer;
  user-select: none;
  display: flex;
  flex-direction: column;
  gap: 9px;
  transition: background 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
}
.habit:hover {
  border-color: #d7ece4;
  box-shadow: var(--shadow-soft);
}
.habit.done {
  background: var(--green-soft);
  border-color: #cfe9df;
}
.habit-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.habit-ico {
  font-size: 16px;
  line-height: 1;
}
.habit-check {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1.5px solid #dfe8e3;
  position: relative;
  transition: all 0.18s ease;
  flex: none;
}
.habit.done .habit-check {
  background: var(--green);
  border-color: var(--green);
}
.habit.done .habit-check::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2.5px;
  width: 4px;
  height: 7px;
  border: 1.5px solid #fff;
  border-top: 0;
  border-left: 0;
  transform: rotate(42deg);
}
.habit-name {
  font-size: var(--fs-base);
  color: var(--text-primary);
}
.habit-val {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 2px;
}
</style>
