<script setup lang="ts">
import { computed } from 'vue'
import type { CalendarEvent } from '../../types'

const props = defineProps<{
  year: number
  month: number // 1-based
  today: number
  events: CalendarEvent[]
}>()

const weekDays = ['一', '二', '三', '四', '五', '六', '日']
const MAX_EVT = 2

interface CalCell {
  key: string
  day: number
  other: boolean
  isToday: boolean
  evts: CalendarEvent[]
}

const cells = computed<CalCell[]>(() => {
  const { year, month, today, events } = props
  const firstDay = new Date(year, month - 1, 1)
  const offset = (firstDay.getDay() + 6) % 7 // Monday-first
  const daysInMonth = new Date(year, month, 0).getDate()
  const daysInPrev = new Date(year, month - 1, 0).getDate()
  const total = Math.ceil((offset + daysInMonth) / 7) * 7

  const list: CalCell[] = []
  for (let i = 0; i < total; i++) {
    const d = i - offset + 1
    if (d < 1) {
      list.push({ key: `p-${i}`, day: daysInPrev + d, other: true, isToday: false, evts: [] })
    } else if (d > daysInMonth) {
      list.push({ key: `n-${i}`, day: d - daysInMonth, other: true, isToday: false, evts: [] })
    } else {
      list.push({
        key: `c-${d}`,
        day: d,
        other: false,
        isToday: d === today,
        evts: events.filter((e) => e.day === d),
      })
    }
  }
  return list
})
</script>

<template>
  <div class="calendar">
    <div class="cal-wd-row">
      <span v-for="wd in weekDays" :key="wd" class="cal-wd">{{ wd }}</span>
    </div>
    <div class="cal-grid">
      <div
        v-for="cell in cells"
        :key="cell.key"
        class="cal-cell"
        :class="{ other: cell.other, today: cell.isToday }"
      >
        <span class="cal-day">{{ cell.day }}</span>
        <div v-if="cell.evts.length" class="cal-evts">
          <span
            v-for="(evt, i) in cell.evts.slice(0, MAX_EVT)"
            :key="i"
            class="cal-evt"
            :class="[evt.level, { ddl: evt.isDdl }]"
            :title="evt.text"
          >{{ evt.text }}</span>
          <span v-if="cell.evts.length > MAX_EVT" class="cal-more">+{{ cell.evts.length - MAX_EVT }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.calendar {
  width: 100%;
}
.cal-wd-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
  margin-bottom: 6px;
}
.cal-wd {
  font-size: var(--fs-sm);
  color: var(--text-muted);
  text-align: center;
  padding: 2px 0;
  letter-spacing: 0.5px;
}
.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}
.cal-cell {
  background: var(--surface-2);
  border: 1px solid #eef4f1;
  border-radius: var(--radius-sm);
  min-height: 82px;
  padding: 6px 7px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: border-color 0.16s ease, box-shadow 0.16s ease;
  min-width: 0;
}
.cal-cell:hover {
  border-color: #d7ece4;
  box-shadow: var(--shadow-soft);
}
.cal-cell.other {
  opacity: 0.35;
  background: transparent;
  border-color: transparent;
  pointer-events: none;
}
.cal-cell.today {
  background: var(--green-soft);
  border-color: #cfe9df;
}
.cal-day {
  font-size: var(--fs-md);
  font-weight: 600;
  color: var(--text-secondary);
  line-height: 1;
}
.cal-cell.today .cal-day {
  color: var(--green-deep);
}
.cal-evts {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.cal-evt {
  font-size: 8px;
  line-height: 1.35;
  padding: 1px 5px;
  border-radius: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  max-width: 100%;
}
.cal-evt.high {
  background: var(--pink-soft);
  color: #c9748a;
}
.cal-evt.mid {
  background: var(--yellow-soft);
  color: #b98b2e;
}
.cal-evt.low {
  background: var(--green-soft);
  color: var(--green-deep);
}
.cal-evt.ddl {
  border-left: 2px solid var(--danger);
}
.cal-more {
  font-size: 8px;
  color: var(--text-muted);
  padding-left: 4px;
}
@media (max-width: 900px) {
  .cal-cell {
    min-height: 60px;
    padding: 4px 5px;
  }
  .cal-evt {
    font-size: 7px;
  }
  .cal-wd {
    font-size: 9px;
  }
}
@media (max-width: 620px) {
  .cal-cell {
    min-height: 50px;
  }
  .cal-evt {
    font-size: 6.5px;
    padding: 1px 3px;
  }
}
</style>
