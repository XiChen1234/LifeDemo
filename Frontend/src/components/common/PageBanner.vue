<script setup lang="ts">
defineProps<{
  title: string
  desc: string
  chips: string[]
}>()
</script>

<template>
  <div class="page-banner">
    <div class="pb-main">
      <div class="pb-title">{{ title }}</div>
      <div class="pb-desc">{{ desc }}</div>
    </div>
    <div class="pb-chips">
      <span v-for="chip in chips" :key="chip" class="chip">{{ chip }}</span>
    </div>
  </div>
</template>

<style scoped>
.page-banner {
  position: relative;
  overflow: hidden;
  background: linear-gradient(115deg, #eaf7f2 0%, #f6fbf9 55%, #ffffff 100%);
  border: 1px solid #e9f4ef;
  border-radius: var(--radius-xl);
  padding: 18px 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  flex-wrap: wrap;
}
.page-banner::after {
  content: '';
  position: absolute;
  right: -46px;
  top: -58px;
  width: 190px;
  height: 190px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(85, 185, 154, 0.16), transparent 70%);
}
.pb-main {
  position: relative;
  min-width: 240px;
  flex: 1;
}
.pb-title {
  font-size: var(--fs-xl);
  font-weight: 600;
  letter-spacing: -0.2px;
}
.pb-desc {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  margin-top: 5px;
  line-height: 1.7;
  max-width: 560px;
}
.pb-chips {
  display: flex;
  gap: 8px;
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
@media (max-width: 900px) {
  .page-banner {
    padding: 16px 18px;
  }
}
</style>
