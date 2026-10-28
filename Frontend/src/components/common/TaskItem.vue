<script setup lang="ts">
import type { Task } from '../../types'
import StatusPill from './StatusPill.vue'

withDefaults(defineProps<{ task: Task; showPriority?: boolean }>(), { showPriority: true })

const emit = defineEmits<{ (e: 'toggle', id: string): void }>()

const priorityText: Record<Task['priority'], string> = { high: '高', mid: '中', low: '低' }
</script>

<template>
  <div class="task" :class="{ done: task.done }">
    <span class="task-check" :class="{ done: task.done }" @click="emit('toggle', task.id)"></span>
    <span class="task-text">{{ task.text }}</span>
    <StatusPill v-if="showPriority" :text="priorityText[task.priority]" :level="task.priority" />
  </div>
</template>

<style scoped>
.task {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 7px 0;
}
.task-check {
  width: 15px;
  height: 15px;
  border-radius: 5px;
  flex: none;
  position: relative;
  border: 1.5px solid #dfe8e3;
  cursor: pointer;
  transition: all 0.16s ease;
}
.task-check:hover {
  border-color: var(--green);
}
.task-check.done {
  background: var(--green);
  border-color: var(--green);
}
.task-check.done::after {
  content: '';
  position: absolute;
  left: 4.5px;
  top: 1.5px;
  width: 4px;
  height: 8px;
  border: 1.5px solid #fff;
  border-top: 0;
  border-left: 0;
  transform: rotate(42deg);
}
.task-text {
  flex: 1;
  font-size: var(--fs-base);
  min-width: 0;
}
.task.done .task-text {
  color: var(--text-muted);
  text-decoration: line-through;
}
</style>
