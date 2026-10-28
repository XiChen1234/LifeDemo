<script setup lang="ts">
import SideBar from './components/layout/SideBar.vue'
</script>

<template>
  <div class="app">
    <SideBar />
    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style scoped>
.app {
  display: flex;
  align-items: flex-start;
  min-height: 100vh;
}
.main {
  flex: 1;
  min-width: 0;
  padding: 22px 26px 48px;
  max-width: 1240px;
}
@media (max-width: 900px) {
  .main {
    padding: 18px 16px 40px;
  }
}
</style>
