<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{ option: echarts.EChartsOption }>()

const el = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null
let observer: ResizeObserver | null = null

function init() {
  if (!el.value || chart) return
  chart = echarts.init(el.value)
  chart.setOption(props.option)
  observer = new ResizeObserver(() => chart?.resize())
  observer.observe(el.value)
}

onMounted(() => {
  // 布局尚未完成时容器可能为 0 宽，延迟一帧初始化避免 ECharts 警告
  if (el.value && el.value.clientWidth === 0) {
    requestAnimationFrame(init)
  } else {
    init()
  }
})

watch(
  () => props.option,
  (opt) => {
    chart?.setOption(opt, { notMerge: true })
  },
  { deep: true }
)

onBeforeUnmount(() => {
  observer?.disconnect()
  chart?.dispose()
  chart = null
})
</script>

<template>
  <div ref="el" class="vchart"></div>
</template>

<style scoped>
.vchart {
  width: 100%;
  height: 100%;
  min-height: 0;
}
</style>
