<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import PageBanner from '../components/common/PageBanner.vue'
import MetricCard from '../components/common/MetricCard.vue'
import ProgressRow from '../components/common/ProgressRow.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ChartLegend from '../components/common/ChartLegend.vue'
import VChart from '../components/chart/VChart.vue'
import { PALETTE, chartTooltip } from '../components/chart/theme'
import { useLifeStore } from '../stores/life'

const life = useLifeStore()

const donutOption = (data: { label: string; value: number }[]): EChartsOption => ({
  color: PALETTE,
  tooltip: { ...chartTooltip, trigger: 'item' },
  series: [
    {
      type: 'pie',
      radius: ['56%', '78%'],
      center: ['50%', '50%'],
      label: { show: false },
      itemStyle: { borderColor: '#fff', borderWidth: 2, borderRadius: 4 },
      data: data.map((d) => ({ name: d.label, value: d.value })),
    },
  ],
})

const activityOption = computed<EChartsOption>(() => donutOption(life.activityDist))
const mediaOption = computed<EChartsOption>(() => donutOption(life.mediaDist))

const activityLegend = computed(() =>
  life.activityDist.map((d, i) => ({ label: `${d.label} ${d.value}`, color: PALETTE[i % PALETTE.length] }))
)
const mediaLegend = computed(() =>
  life.mediaDist.map((d, i) => ({ label: `${d.label} ${d.value}`, color: PALETTE[i % PALETTE.length] }))
)

const activityTotal = computed(() =>
  life.activityDist.reduce((sum, item) => sum + item.value, 0),
)

const mediaTotal = computed(() =>
  life.mediaDist.reduce((sum, item) => sum + item.value, 0),
)

const stars = (n: number) => '★'.repeat(n) + '☆'.repeat(5 - n)
</script>

<template>
  <div>
    <ViewHead title="生活与媒体" sub="6 月的第 12 天 · 慢一点也没关系" tag="本月记录 30 条" />

    <div class="stack">
      <PageBanner :title="life.banner.title" :desc="life.banner.desc" :chips="life.banner.chips" />

      <div class="grid g4">
        <MetricCard v-for="m in life.metrics" :key="m.label" :metric="m" />
      </div>

      <div class="grid g2">
        <AppCard title="月度活动分布" sub="本月 30 次">
          <template #head>
            <ChartLegend :items="activityLegend" />
          </template>
          <div class="donut-wrap">
            <div class="chart-box"><VChart :option="activityOption" /></div>
            <div class="donut-center">
              <b>{{ activityTotal }}</b>
              <span>本月活动</span>
            </div>
          </div>
        </AppCard>

        <AppCard title="内容类型占比" sub="观影与阅读 · 累计">
          <template #head>
            <ChartLegend :items="mediaLegend" />
          </template>
          <div class="donut-wrap">
            <div class="chart-box"><VChart :option="mediaOption" /></div>
            <div class="donut-center">
              <b>{{ mediaTotal }}</b>
              <span>内容条目</span>
            </div>
          </div>
        </AppCard>
      </div>

      <div class="grid g2">
        <AppCard title="阅读进度" sub="4 本在读 / 已读">
          <ProgressRow v-for="r in life.reading" :key="r.name" :item="r" />
        </AppCard>

        <AppCard title="观影进度" sub="4 部在追 / 已看完">
          <ProgressRow v-for="w in life.watching" :key="w.name" :item="w" />
        </AppCard>
      </div>

      <AppCard title="最近完播与读完" sub="近两周">
        <div class="list-cols">
          <ListRow v-for="m in life.finishedMedia" :key="m.id" :title="m.title" :sub="m.meta">
            <template #leading>
              <StatusPill :text="m.type" :level="m.typeClass" />
            </template>
            <template #trailing>
              <span class="stars">{{ stars(m.rating) }}</span>
            </template>
          </ListRow>
        </div>
      </AppCard>
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
.list-cols {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 26px;
}
.stars {
  font-size: var(--fs-sm);
  color: var(--yellow);
  letter-spacing: 1px;
  white-space: nowrap;
  flex: none;
}
@media (max-width: 900px) {
  .list-cols {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
