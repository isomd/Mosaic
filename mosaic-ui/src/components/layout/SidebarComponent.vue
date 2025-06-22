<script lang="ts" setup>
import {useStatusStore} from '@/store/useStatusStore'
import {useThemeStore} from '@/store/useThemeStore'
import {ref} from "vue";
import router from "@/router";
const statusStore = useStatusStore()
const themeStore = useThemeStore()
const fontColor = computed(() => themeStore.getTheme().fontColor)
const path = ref('')
const menuItems = ref([
  {title: 'menu.dashboard', icon: 'mdi-view-dashboard', route: '/dashboard', badge: null},
  {title: 'menu.plugins', icon: 'mdi-puzzle', route: '/plugins', badge: null, badgeColor: 'warning'},
  {title: 'menu.settings', icon: 'mdi-cog', route: '/settings', badge: null},
  {title: 'menu.slot',icon: 'mdi-shovel',route: '/slot',badge:null}
])
const navigateTo = (route: string) => {
  path.value = route
  router.push(route)
}
onMounted(()=>{
  path.value = router!.currentRoute.value.fullPath
})
</script>
<template>
  <v-navigation-drawer v-model="statusStore.showSidebar" app class="minecraft-drawer" width="320">
    <div class="minecraft-drawer-header">
      <div class="minecraft-logo">⛏️</div>
    </div>

    <v-list nav class="minecraft-nav-list">
      <v-list-item
          v-for="item in menuItems"
          :key="item.title"
          :class="{ 'v-list-item--active': path === item.route }"
          @click="navigateTo(item.route)"
          class="d-flex"
      >
        <template v-slot:prepend>
          <v-icon>{{ item.icon }}</v-icon>
        </template>
        <v-list-item-title>{{ $t(item.title) }}</v-list-item-title>
        <template v-slot:append v-if="item.badge">
          <v-badge :color="item.badgeColor" :content="item.badge"></v-badge>
        </template>
      </v-list-item>
    </v-list>

    <template v-slot:append>
      <div class="CraftingTable">
        <span>{{ $t('menu.craftingTable') }}</span>
      </div>
    </template>
  </v-navigation-drawer>
</template>
<style scoped lang="scss">

.SidebarComponent{
  position: relative;
  height: 100%;
  /*background-color: black;*/
  transition: width 0.3s;
  background-image: url("/stone.png");
  background-repeat: repeat;
}
.SidebarComponent-show{
  width: 15%;
}
.SidebarComponent-hidden {
  width: 0;
}
.CraftingTable{
  position: absolute;
  width: 100%;
  height: 320px;
  left: 0;
  bottom: -230px;
  background-image: url("/img.png");
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center center;
  cursor: pointer;
  animation: float 3s ease-in-out infinite;
  transition: all 0.3s;
  span {
    position: absolute;
    left: 50%;
    top: 100px;
    width: 100%;
    text-align: center;
    transform: translate(-50%);
    /*display: inline-block;*/
    color: v-bind(fontColor);
    font-size: 1rem;
    font-weight: 700;
  }
  &:hover{
    bottom: -150px;
  }
}
.minecraft-nav-list v-list-item{
  width: 100%;
  display: flex;
  justify-content: space-between;
}
</style>
