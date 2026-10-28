<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import MetricCard from '../components/common/MetricCard.vue'
import GoalRow from '../components/common/GoalRow.vue'
import TaskItem from '../components/common/TaskItem.vue'
import ProgressRow from '../components/common/ProgressRow.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ChartLegend from '../components/common/ChartLegend.vue'
import VChart from '../components/chart/VChart.vue'
import { PALETTE, chartTooltip, categoryAxis, valueAxis, chartGrid } from '../components/chart/theme'
import { useDashboardStore } from '../stores/dashboard'
import { useWorkStore } from '../stores/work'

const dash = useDashboardStore()
const work = useWorkStore()

const cashLegend = [
  { label: '收入', color: PALETTE[0] },
  { label: '支出', color: PALETTE[3] },
]

const cashOption = computed<EChartsOption>(() => ({
  color: [PALETTE[0], PALETTE[3]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(dash.cashTrend.labels),
  yAxis: valueAxis(),
  series: [
    {
      name: '收入',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.1 },
      data: dash.cashTrend.income,
    },
    {
      name: '支出',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.1 },
      data: dash.cashTrend.expense,
    },
  ],
}))
</script>

<template>
  <div>
    <ViewHead title="首页" sub="2025 年 6 月 12 日 · 星期四 · 晴 26°" tag="本周第 4 天" />

    <div class="stack">
      <!-- Hero -->
      <div class="hero-banner">
        <div class="hero-main">
          <div class="hero-hello">{{ dash.hero.greeting }}</div>
          <div class="hero-quote">{{ dash.hero.quote }}</div>
          <div class="hero-meta">
            <span v-for="chip in dash.hero.chips" :key="chip" class="chip">{{ chip }}</span>
          </div>
        </div>
        <div class="hero-side">
          <div class="hero-date">{{ dash.hero.dateMain }}</div>
          <div class="hero-wd">{{ dash.hero.dateSub }}</div>
        </div>
      </div>

      <!-- 指标卡 -->
      <div class="grid g4">
        <MetricCard v-for="m in dash.metrics" :key="m.label" :metric="m" />
      </div>

      <!-- 年度目标 -->
      <AppCard title="今年的小目标" sub="2025 · 已过半程">
        <GoalRow v-for="g in dash.yearGoals" :key="g.name" :goal="g" />
      </AppCard>

      <!-- 收支趋势 + 今天要做 -->
      <div class="grid g13">
        <AppCard title="收支趋势" sub="近 6 个月">
          <template #head>
            <ChartLegend :items="cashLegend" />
          </template>
          <div class="chart-box"><VChart :option="cashOption" /></div>
        </AppCard>

        <AppCard title="今天要做" :sub="`已完成 ${work.todoDoneCount} / ${work.todos.length}`">
          <TaskItem
            v-for="task in work.todos"
            :key="task.id"
            :task="task"
            :show-priority="false"
            @toggle="work.toggleTodo"
          />
        </AppCard>
      </div>

      <!-- 生活切片三栏 -->
      <div class="grid g3">
        <AppCard title="支出结构" sub="本月 Top 4">
          <ProgressRow v-for="p in dash.expenseStructure" :key="p.name" :item="p" />
        </AppCard>

        <AppCard title="阅读与观影" sub="进行中">
          <ProgressRow v-for="p in dash.mediaProgress" :key="p.name" :item="p" />
        </AppCard>

        <AppCard title="最近笔记" sub="同步自 Obsidian">
          <ListRow v-for="n in dash.recentNotes" :key="n.id" :title="n.title" :sub="n.timeAgo">
            <template #leading>
              <StatusPill :text="n.tag" level="low" />
            </template>
          </ListRow>
        </AppCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.hero-banner {
  position: relative;
  overflow: hidden;
  background: linear-gradient(115deg, #eaf7f2 0%, #f6fbf9 55%, #ffffff 100%);
  border: 1px solid #e9f4ef;
  border-radius: var(--radius-xl);
  padding: 24px 26px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.hero-banner::after {
  content: '';
  position: absolute;
  right: -46px;
  top: -58px;
  width: 190px;
  height: 190px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(85, 185, 154, 0.16), transparent 70%);
}
.hero-main {
  position: relative;
  min-width: 0;
}
.hero-hello {
  font-size: var(--fs-2xl);
  font-weight: 600;
  letter-spacing: -0.3px;
}
.hero-quote {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  margin-top: 8px;
  max-width: 420px;
  line-height: 1.75;
}
.hero-meta {
  display: flex;
  gap: 8px;
  margin-top: 14px;
  flex-wrap: wrap;
}
.chip {
  font-size: var(--fs-sm);
  color: var(--green-deep);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid #dcf0e8;
  padding: 4px 10px;
  border-radius: 99px;
  white-space: nowrap;
}
.hero-side {
  text-align: right;
  flex: none;
  position: relative;
}
.hero-date {
  font-size: var(--fs-3xl);
  font-weight: 600;
  letter-spacing: -0.8px;
  color: var(--green-deep);
  line-height: 1;
}
.hero-wd {
  font-size: var(--fs-md);
  color: var(--text-secondary);
  margin-top: 7px;
}
.chart-box {
  height: 190px;
}
@media (max-width: 900px) {
  .hero-banner {
    flex-direction: column;
    align-items: flex-start;
  }
  .hero-side {
    text-align: left;
  }
}
</style>
