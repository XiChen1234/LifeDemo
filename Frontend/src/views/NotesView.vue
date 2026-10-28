<script setup lang="ts">
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import PageBanner from '../components/common/PageBanner.vue'
import MetricSlim from '../components/common/MetricSlim.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import NoteTree from '../components/notes/NoteTree.vue'
import NotePreview from '../components/notes/NotePreview.vue'
import { useNotesStore } from '../stores/notes'

const notes = useNotesStore()
</script>

<template>
  <div>
    <ViewHead title="笔记" sub="428 篇 · Obsidian 同步正常" tag="最后同步 08:30" />

    <div class="stack">
      <PageBanner :title="notes.banner.title" :desc="notes.banner.desc" :chips="notes.banner.chips" />

      <div class="metric-row">
        <MetricSlim v-for="m in notes.metrics" :key="m.label" :label="m.label" :value="m.value" :unit="m.unit" />
      </div>

      <div class="notes-layout">
        <!-- 目录树 -->
        <div class="card tree-card">
          <NoteTree :folders="notes.folders" :active-id="notes.activeFolderId" @select="notes.selectFolder" />
        </div>

        <!-- 预览 -->
        <NotePreview v-if="notes.activeNote" :note="notes.activeNote" />

        <!-- 侧栏 -->
        <div class="side-col">
          <AppCard title="最近笔记">
            <ListRow
              v-for="n in notes.visibleNotes"
              :key="n.id"
              :title="n.title"
              :sub="n.timeAgo"
              clickable
              :active="n.id === notes.activeNoteId"
              @click="notes.selectNote(n.id)"
            >
              <template #leading>
                <StatusPill :text="n.tag" :level="n.tagClass" />
              </template>
            </ListRow>
          </AppCard>

          <AppCard title="标签分布" sub="Top 8">
            <div class="tagbars">
              <div v-for="t in notes.tagStats" :key="t.name" class="tagbar">
                <span class="tag-name">{{ t.name }}</span>
                <ProgressBar :percent="t.percent" :color="t.color" small />
                <span class="tag-cnt">{{ t.count }}</span>
              </div>
            </div>
          </AppCard>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.metric-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.notes-layout {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr) 232px;
  gap: 14px;
  align-items: start;
}
.tree-card {
  padding: 14px 12px;
}
.side-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}
.side-col :deep(.card) {
  padding: 14px 13px 12px;
}
.side-col :deep(.list-row) {
  padding: 7px 6px;
}
.side-col :deep(.list-title) {
  font-size: var(--fs-md);
}
.side-col :deep(.list-sub) {
  font-size: var(--fs-xs);
}
.tagbars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.tagbar {
  display: grid;
  grid-template-columns: 34px 1fr 26px;
  align-items: center;
  gap: 8px;
}
.tag-name {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}
.tag-cnt {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  text-align: right;
}
@media (max-width: 1180px) {
  .notes-layout {
    grid-template-columns: 150px minmax(0, 1fr);
  }
  .side-col {
    grid-column: 1 / -1;
    flex-direction: row;
  }
  .side-col > :deep(.card) {
    flex: 1;
    min-width: 0;
  }
  .metric-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 900px) {
  .notes-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .side-col {
    flex-direction: column;
  }
}
@media (max-width: 620px) {
  .metric-row {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
