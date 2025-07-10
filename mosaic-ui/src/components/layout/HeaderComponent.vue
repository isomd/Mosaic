<script lang="ts" setup>
import {useStatusStore} from '@/store/useStatusStore'
import {useLocaleStore} from "@/store/useLocaleStore";
import {useWorldStore} from "@/store/data/useWorldStore";
import router from "@/router";
const worldStore = useWorldStore()
const statusStore = useStatusStore()
const local = useLocaleStore()
const language = ref<boolean>(false)
const showSidebar = ref<boolean>(false)
const changeLanguage = () => {
  if(language.value == true) {
    local.setLocale("en")
  } else {
    local.setLocale("zh")
  }
  language.value=!language.value
}
const worldList = computed(()=>{
  return worldStore.worldList
})
const handleClickSidebar = () => {
  showSidebar.value = !showSidebar.value
  statusStore.setShowSidebar(showSidebar.value)
}
onMounted(()=>{
  showSidebar.value = statusStore.showSidebar
  language.value = (local.currentLocale == 'zh')
})
</script>
<template>
  <v-app-bar app elevation="0" class="minecraft-app-bar">
    <v-app-bar-nav-icon @click="handleClickSidebar">
      <v-icon>mdi-menu</v-icon>
    </v-app-bar-nav-icon>
    <v-toolbar-title>Mosaic</v-toolbar-title>
    <v-spacer></v-spacer>
<!--    <v-btn icon @click="toggleTheme">-->
<!--      <v-icon>mdi-brightness-6</v-icon>-->
<!--    </v-btn>-->

<!--    <v-btn icon>-->
<!--      <v-icon>mdi-bell</v-icon>-->
<!--      <v-badge color="error" content="3" floating></v-badge>-->
<!--    </v-btn>-->
    <v-btn
        color="primary"
        small
        @click="changeLanguage"
    >
      <v-icon>mdi-translate</v-icon>
    </v-btn>
    <v-menu>
      <template v-slot:activator="{ props }">
        <v-btn v-bind="props" class="minecraft-user-btn">
          <v-icon start>mdi-earth-arrow-left</v-icon>
          <span class="ml-2 d-none d-sm-inline">{{ statusStore.currentWorld?.name }}</span>
          <v-icon end>mdi-chevron-down</v-icon>
        </v-btn>
      </template>

      <v-list class="minecraft-user-menu">
        <v-list-item v-for="world in worldList" @click="statusStore.setCurrentWorld(world)">
          <template v-slot:prepend>
            <v-icon>mdi-earth-arrow-left</v-icon>
          </template>
          <v-list-item-title>{{world.name}}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>

</template>
<style scoped lang="scss">

</style>
