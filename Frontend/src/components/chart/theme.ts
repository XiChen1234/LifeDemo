import type { EChartsOption } from 'echarts'

/** 统一图表主题：薄荷绿系调色板 + 深色 tooltip + 浅色轴线 */
export const PALETTE = ['#55b99a', '#78a9dc', '#f3c96a', '#e5a3b6', '#a9cbe8', '#cfd8d3']

export const chartTooltip: NonNullable<EChartsOption['tooltip']> = {
  trigger: 'axis',
  backgroundColor: '#34403b',
  borderWidth: 0,
  textStyle: { color: '#fff', fontSize: 10 },
  padding: [7, 10],
  extraCssText: 'border-radius:9px;box-shadow:0 6px 20px rgba(30,45,40,.22);',
}

const axisLabel = { color: '#a3ada8', fontSize: 10 }

export function categoryAxis(data: string[], boundaryGap = false) {
  return {
    type: 'category' as const,
    data,
    boundaryGap,
    axisLine: { lineStyle: { color: '#edf1ef' } },
    axisTick: { show: false },
    axisLabel,
  }
}

export function valueAxis() {
  return {
    type: 'value' as const,
    axisLabel,
    splitLine: { lineStyle: { color: '#edf1ef' } },
  }
}

export const chartGrid = { left: 6, right: 10, top: 16, bottom: 0, containLabel: true }
