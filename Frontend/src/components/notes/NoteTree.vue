<script setup lang="ts">
import { computed } from 'vue'
import type { NoteFolder } from '../../types'

const props = defineProps<{
  folders: NoteFolder[]
  activeId: string
}>()

const emit = defineEmits<{ (e: 'select', id: string): void }>()

const topLevel = computed(() => props.folders.filter((f) => !f.parentId))
const childrenOf = (id: string) => props.folders.filter((f) => f.parentId === id)
</script>

<template>
  <div class="tree">
    <template v-for="folder in topLevel" :key="folder.id">
      <div
        class="tree-item"
        :class="{ on: folder.id === activeId }"
        @click="emit('select', folder.id)"
      >
        <span class="tree-ico">{{ folder.icon }}</span>
        <span class="tree-name">{{ folder.name }}</span>
        <span class="cnt">{{ folder.count }}</span>
      </div>
      <div
        v-for="child in childrenOf(folder.id)"
        :key="child.id"
        class="tree-item tree-child"
        :class="{ on: child.id === activeId }"
        @click="emit('select', child.id)"
      >
        <span class="tree-ico">{{ child.icon }}</span>
        <span class="tree-name">{{ child.name }}</span>
        <span class="cnt">{{ child.count }}</span>
      </div>
    </template>
  </div>
</template>

<style scoped>
.tree {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.tree-item {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 6px 9px;
  border-radius: 8px;
  font-size: var(--fs-base);
  color: var(--text-secondary);
  cursor: pointer;
  transition: background 0.16s ease;
}
.tree-item:hover {
  background: var(--surface-2);
}
.tree-item.on {
  background: var(--green-soft);
  color: var(--green-deep);
  font-weight: 600;
}
.tree-ico {
  font-size: 11px;
  line-height: 1;
  flex: none;
}
.tree-name {
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.cnt {
  margin-left: auto;
  font-size: var(--fs-sm);
  color: var(--text-muted);
  font-weight: 400;
  flex: none;
}
.tree-child {
  padding-left: 20px;
}
</style>
