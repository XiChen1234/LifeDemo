<script setup lang="ts">
import type { Note } from '../../types'
import StatusPill from '../common/StatusPill.vue'

defineProps<{ note: Note }>()
</script>

<template>
  <div class="card preview-card">
    <div class="preview-head">
      <div class="preview-title">{{ note.title }}</div>
      <div class="preview-meta">
        <StatusPill :text="note.tag" :level="note.tagClass" />
        <span class="preview-time">{{ note.timeAgo }} · {{ note.words }} 字</span>
      </div>
    </div>
    <div class="preview-body">
      <template v-for="(block, i) in note.paragraphs" :key="i">
        <h4 v-if="block.type === 'h4'">{{ block.text }}</h4>
        <p v-else>{{ block.text }}</p>
      </template>
    </div>
    <div class="preview-foot">
      <StatusPill v-for="t in note.tags" :key="t.text" :text="t.text" :level="t.class" />
    </div>
  </div>
</template>

<style scoped>
.preview-card {
  padding: 26px 30px 24px;
  min-height: 520px;
  display: flex;
  flex-direction: column;
}
.preview-head {
  border-bottom: 1px solid var(--line);
  padding-bottom: 14px;
  margin-bottom: 18px;
}
.preview-title {
  font-size: var(--fs-2xl);
  font-weight: 600;
  letter-spacing: -0.3px;
  line-height: 1.35;
}
.preview-meta {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
  align-items: center;
}
.preview-time {
  font-size: var(--fs-sm);
  color: var(--text-muted);
}
.preview-body {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  line-height: 2;
  flex: 1;
}
.preview-body p {
  margin-bottom: 13px;
}
.preview-body p:last-child {
  margin-bottom: 0;
}
.preview-body h4 {
  font-size: var(--fs-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 4px 0 9px;
  padding-left: 9px;
  border-left: 2px solid var(--green);
}
.preview-foot {
  margin-top: 16px;
  padding-top: 13px;
  border-top: 1px dashed var(--line);
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
@media (max-width: 900px) {
  .preview-card {
    padding: 20px 18px;
    min-height: 0;
  }
}
</style>
