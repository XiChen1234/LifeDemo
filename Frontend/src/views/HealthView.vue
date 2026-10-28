<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import ViewHead from '../components/layout/ViewHead.vue'
import AppCard from '../components/common/AppCard.vue'
import ListRow from '../components/common/ListRow.vue'
import StatusPill from '../components/common/StatusPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import ChartLegend from '../components/common/ChartLegend.vue'
import VChart from '../components/chart/VChart.vue'
import HabitCard from '../components/health/HabitCard.vue'
import { PALETTE, chartTooltip, categoryAxis, valueAxis, chartGrid } from '../components/chart/theme'
import { useHealthStore } from '../stores/health'

const health = useHealthStore()

const sleepLegend = [
  { label: '深睡', color: PALETTE[1] },
  { label: '浅睡', color: PALETTE[0] },
  { label: 'REM', color: PALETTE[2] },
]

const sleepOption = computed<EChartsOption>(() => ({
  color: [PALETTE[1], PALETTE[0], PALETTE[2]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(health.sleepTrend.labels),
  yAxis: valueAxis(),
  series: [
    { name: '深睡', type: 'line', stack: 'sleep', smooth: true, symbol: 'none', lineStyle: { width: 1.5 }, areaStyle: { opacity: 0.55 }, data: health.sleepTrend.deep },
    { name: '浅睡', type: 'line', stack: 'sleep', smooth: true, symbol: 'none', lineStyle: { width: 1.5 }, areaStyle: { opacity: 0.35 }, data: health.sleepTrend.light },
    { name: 'REM', type: 'line', stack: 'sleep', smooth: true, symbol: 'none', lineStyle: { width: 1.5 }, areaStyle: { opacity: 0.4 }, data: health.sleepTrend.rem },
  ],
}))

const sportLegend = [
  { label: '跑步', color: PALETTE[0] },
  { label: '骑行', color: PALETTE[1] },
  { label: '健身', color: PALETTE[2] },
  { label: '其他', color: PALETTE[5] },
]

const sportOption = computed<EChartsOption>(() => ({
  color: [PALETTE[0], PALETTE[1], PALETTE[2], PALETTE[5]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(health.sportDuration.labels, true),
  yAxis: valueAxis(),
  series: [
    { name: '跑步', type: 'bar', stack: 'sport', barWidth: 18, itemStyle: { borderRadius: [0, 0, 0, 0] }, data: health.sportDuration.run },
    { name: '骑行', type: 'bar', stack: 'sport', data: health.sportDuration.ride },
    { name: '健身', type: 'bar', stack: 'sport', data: health.sportDuration.gym },
    { name: '其他', type: 'bar', stack: 'sport', itemStyle: { borderRadius: [4, 4, 0, 0] }, data: health.sportDuration.other },
  ],
}))

const weightOption = computed<EChartsOption>(() => ({
  color: [PALETTE[3]],
  tooltip: chartTooltip,
  grid: chartGrid,
  xAxis: categoryAxis(health.weightCurve.labels),
  yAxis: { ...valueAxis(), min: 56, max: 60 },
  series: [
    {
      name: '体重',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.1 },
      data: health.weightCurve.data,
    },
  ],
}))
</script>

<template>
  <div>
    <ViewHead title="健康" sub="身体是一切的地基 · 本周数据已同步" tag="本周达标 5 / 7 天" />

    <div class="stack">
      <!-- Hero 组合 -->
      <div class="hero">
        <div class="hero-card">
          <div class="hc-title">{{ health.hero.title }}</div>
          <div class="hc-desc">{{ health.hero.desc }}</div>
          <div class="hc-chips">
            <span v-for="chip in health.hero.chips" :key="chip" class="chip">{{ chip }}</span>
          </div>
        </div>
        <div class="hero-mini">
          <div v-for="m in health.mini" :key="m.label" class="mini-card">
            <span class="mini-label">{{ m.label }}</span>
            <span class="mini-value">{{ m.value }}</span>
            <ProgressBar :percent="m.percent" :color="m.color" small />
          </div>
        </div>
      </div>

      <!-- 睡眠 + 运动 -->
      <div class="grid g2">
        <AppCard title="本周睡眠趋势" sub="深睡 / 浅睡 / REM 结构">
          <template #head>
            <ChartLegend :items="sleepLegend" />
          </template>
          <div class="chart-box"><VChart :option="sleepOption" /></div>
        </AppCard>

        <AppCard title="本月运动时长" sub="按周统计（小时）">
          <template #head>
            <ChartLegend :items="sportLegend" />
          </template>
          <div class="chart-box"><VChart :option="sportOption" /></div>
        </AppCard>
      </div>

      <!-- 体重 + 睡眠记录 -->
      <div class="grid g13">
        <AppCard title="体重曲线" sub="近 30 天 · kg">
          <div class="chart-box"><VChart :option="weightOption" /></div>
        </AppCard>

        <AppCard title="最近睡眠记录" sub="近 4 天">
          <ListRow
            v-for="r in health.sleepRecords"
            :key="r.date"
            :title="`${r.date} · ${r.sleepTime}`"
            :sub="`时长 ${r.duration} · 深睡 ${r.deep}`"
          >
            <template #trailing>
              <StatusPill :text="r.quality" :level="r.qualityLevel" />
            </template>
          </ListRow>
        </AppCard>
      </div>

      <!-- 习惯打卡 -->
      <AppCard title="习惯打卡" sub="点击卡片完成打卡">
        <div class="habits">
          <HabitCard v-for="h in health.habits" :key="h.id" :habit="h" @toggle="health.toggleHabit" />
        </div>
      </AppCard>
    </div>
  </div>
</template>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 268px;
  gap: 14px;
}
.hero-card {
  position: relative;
  overflow: hidden;
  background: linear-gradient(115deg, #eaf7f2 0%, #f6fbf9 55%, #ffffff 100%);
  border: 1px solid #e9f4ef;
  border-radius: var(--radius-xl);
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.hero-card::after {
  content: '';
  position: absolute;
  right: -46px;
  top: -58px;
  width: 190px;
  height: 190px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(85, 185, 154, 0.16), transparent 70%);
}
.hc-title {
  font-size: var(--fs-xl);
  font-weight: 600;
  letter-spacing: -0.2px;
  position: relative;
}
.hc-desc {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  margin-top: 6px;
  line-height: 1.7;
  max-width: 460px;
  position: relative;
}
.hc-chips {
  display: flex;
  gap: 8px;
  margin-top: 14px;
  flex-wrap: wrap;
  position: relative;
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
.hero-mini {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.mini-card {
  background: var(--card);
  border-radius: var(--radius-lg);
  padding: 14px;
  border: 1px solid #f2f6f4;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}
.mini-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}
.mini-value {
  font-size: var(--fs-2xl);
  font-weight: 600;
  letter-spacing: -0.4px;
  line-height: 1.1;
}
.chart-box {
  height: 180px;
}
.habits {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
@media (max-width: 1120px) {
  .hero {
    grid-template-columns: minmax(0, 1fr);
  }
  .hero-mini {
    grid-template-columns: 1fr 1fr;
  }
}
@media (max-width: 900px) {
  .habits {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 620px) {
  .habits {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
