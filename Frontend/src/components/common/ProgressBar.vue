<script setup lang="ts">
import type { BarColor } from '../../types'

const props = withDefaults(
  defineProps<{
    percent: number
    color?: BarColor
    small?: boolean
  }>(),
  { color: 'green', small: false }
)

const colorMap: Record<BarColor, string> = {
  green: 'var(--green)',
  blue: 'var(--blue)',
  yellow: 'var(--yellow)',
  pink: 'var(--pink)',
  danger: 'var(--danger)',
  gray: '#cfd8d3',
}

const barColor = () => colorMap[props.color]
</script>

<template>
  <div class="bar" :class="{ sm: small }">
    <i :style="{ width: Math.min(percent, 100) + '%', background: barColor() }"></i>
  </div>
</template>
