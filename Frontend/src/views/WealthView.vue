<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import PageBanner from '../components/common/PageBanner.vue'
import MetricCard from '../components/common/MetricCard.vue'
import GoalRow from '../components/common/GoalRow.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ProgressRow from '../components/common/ProgressRow.vue'
import ChartLegend from '../components/common/ChartLegend.vue'
import VChart from '../components/chart/VChart.vue'
import { PALETTE, chartTooltip, categoryAxis, valueAxis, chartGrid } from '../components/chart/theme'
import { useWealthStore } from '../stores/wealth'

const wealth = useWealthStore()

const cashLegend = [
  { label: '收入', color: PALETTE[0] },
  { label: '支出', color: PALETTE[3] },
]

const monthCashOption = computed<EChartsOption>(() => ({
  color: [PALETTE[0], PALETTE[3]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(wealth.monthCash.labels, true),
  yAxis: valueAxis(),
  series: [
    { name: '收入', type: 'bar', barWidth: 10, itemStyle: { borderRadius: [4, 4, 0, 0] }, data: wealth.monthCash.income },
    { name: '支出', type: 'bar', barWidth: 10, itemStyle: { borderRadius: [4, 4, 0, 0] }, data: wealth.monthCash.expense },
  ],
}))

const expense6mOption = computed<EChartsOption>(() => ({
  color: [PALETTE[0]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(wealth.expense6m.labels),
  yAxis: valueAxis(),
  series: [
    {
      name: '支出',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.12 },
      data: wealth.expense6m.data,
    },
  ],
}))

const categoryOption = computed<EChartsOption>(() => ({
  color: PALETTE,
  tooltip: { ...chartTooltip, trigger: 'item' },
  series: [
    {
      type: 'pie',
      radius: ['58%', '80%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: true,
      label: { show: false },
      itemStyle: { borderColor: '#fff', borderWidth: 2, borderRadius: 4 },
      data: wealth.expenseCategory.map((c) => ({ name: c.label, value: c.value })),
    },
  ],
}))

const categoryLegend = computed(() =>
  wealth.expenseCategory.map((c, i) => ({ label: c.label, color: PALETTE[i % PALETTE.length] }))
)
</script>

<template>
  <div>
    <ViewHead title="财富" sub="6 月记账 · 距月末还有 18 天" />

    <div class="stack">
      <PageBanner :title="wealth.banner.title" :desc="wealth.banner.desc" :chips="wealth.banner.chips" />

      <div class="grid g4">
        <MetricCard v-for="m in wealth.metrics" :key="m.label" :metric="m" />
      </div>

      <div class="grid g2">
        <AppCard title="本月收支对比" sub="按周统计">
          <template #head>
            <ChartLegend :items="cashLegend" />
          </template>
          <div class="chart-box"><VChart :option="monthCashOption" /></div>
        </AppCard>

        <AppCard title="近 6 月支出趋势" sub="每月总支出">
          <div class="chart-box"><VChart :option="expense6mOption" /></div>
        </AppCard>
      </div>

      <div class="grid g13">
        <AppCard title="支出分类占比" sub="本月 · 共 5 类">
          <template #head>
            <ChartLegend :items="categoryLegend" />
          </template>
          <div class="donut-wrap">
            <div class="chart-box donut"><VChart :option="categoryOption" /></div>
            <div class="donut-center">
              <b>¥8,426</b>
              <span>本月支出</span>
            </div>
          </div>
        </AppCard>

        <AppCard title="储蓄目标" sub="2025 年度">
          <GoalRow v-for="g in wealth.savingGoals" :key="g.name" :goal="g" name-width="56px" num-width="96px" />
        </AppCard>
      </div>

      <div class="grid g2">
        <AppCard title="最近交易" sub="近 3 天">
          <ListRow v-for="t in wealth.transactions" :key="t.id" :title="t.title" :sub="t.time">
            <template #leading>
              <StatusPill :text="t.category" :level="t.categoryClass" />
            </template>
            <template #trailing>
              <span class="list-amt" :class="{ up: t.isIncome }">{{ t.amount }}</span>
            </template>
          </ListRow>
        </AppCard>

        <AppCard title="预算使用情况" sub="本月预算 · 超支标红">
          <template #head>
            <span class="head-tag">预算使用 84%</span>
          </template>
          <ProgressRow
            v-for="b in wealth.budgets"
            :key="b.name"
            :item="{ name: b.name, valueText: `¥${b.used} / ¥${b.total}`, percent: b.percent, color: b.color }"
          />
        </AppCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chart-box {
  height: 180px;
}
.donut-wrap {
  position: relative;
}
.chart-box.donut {
  height: 190px;
}
.donut-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.donut-center b {
  font-size: var(--fs-xl);
  font-weight: 600;
  letter-spacing: -0.2px;
}
.donut-center span {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  margin-top: 2px;
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
