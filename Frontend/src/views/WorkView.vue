<script setup lang="ts">
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import PageBanner from '../components/common/PageBanner.vue'
import TaskItem from '../components/common/TaskItem.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import ChartLegend from '../components/common/ChartLegend.vue'
import WorkCalendar from '../components/work/WorkCalendar.vue'
import { PALETTE } from '../components/chart/theme'
import { useWorkStore } from '../stores/work'

const work = useWorkStore()

const calLegend = [
  { label: '高优先级', color: '#c9748a' },
  { label: '中优先级', color: PALETTE[2] },
  { label: '低优先级', color: PALETTE[0] },
  { label: 'DDL', color: '#d47474' },
]

const priorityGroups = [
  { key: 'high' as const, label: '高优先级' },
  { key: 'mid' as const, label: '中优先级' },
  { key: 'low' as const, label: '低优先级' },
]
</script>

<template>
  <div>
    <ViewHead title="工作" sub="今天有 3 项高优先级任务 · 专注 4h20m" tag="本周完成 18 项" />

    <div class="stack">
      <PageBanner :title="work.banner.title" :desc="work.banner.desc" :chips="work.banner.chips" />

      <!-- 日历 -->
      <AppCard :title="work.calendarMeta.title" :sub="work.calendarMeta.sub">
        <template #head>
          <ChartLegend :items="calLegend" />
        </template>
        <WorkCalendar
          :year="work.calendarMeta.year"
          :month="work.calendarMeta.month"
          :today="work.calendarMeta.today"
          :events="work.calendarEvents"
        />
      </AppCard>

      <!-- 待办 + 右列 -->
      <div class="grid g31">
        <AppCard title="今日待办" :sub="`已完成 ${work.todoDoneCount} / ${work.todos.length}`">
          <div v-for="group in priorityGroups" :key="group.key" class="todo-group">
            <div class="todo-group-label">{{ group.label }}</div>
            <TaskItem
              v-for="todo in work.todosByPriority(group.key)"
              :key="todo.id"
              :task="todo"
              :show-priority="false"
              @toggle="work.toggleTodo"
            />
          </div>
        </AppCard>

        <div class="side-stack">
          <AppCard title="即将到期" sub="按时间排序">
            <ListRow v-for="d in work.deadlines" :key="d.title" :title="d.title" :sub="d.time">
              <template #trailing>
                <StatusPill :text="d.priority === 'high' ? '紧急' : d.priority === 'mid' ? '本周' : '本月'" :level="d.priority" />
              </template>
            </ListRow>
          </AppCard>

          <AppCard title="项目进度" sub="3 个进行中">
            <div v-for="p in work.projects" :key="p.name" class="proj">
              <div class="proj-top">
                <span class="proj-name">{{ p.name }}</span>
                <span class="proj-stage">{{ p.stage }} · {{ p.percent }}%</span>
              </div>
              <ProgressBar :percent="p.percent" :color="p.color" />
            </div>
          </AppCard>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.todo-group + .todo-group {
  margin-top: 8px;
}
.todo-group-label {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  padding: 4px 0 2px;
  letter-spacing: 0.3px;
}
.side-stack {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}
.proj {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 0;
}
.proj-top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
}
.proj-name {
  font-size: var(--fs-md);
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.proj-stage {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  white-space: nowrap;
  flex: none;
}
</style>
