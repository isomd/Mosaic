<script lang="ts" setup>
import {useStatusStore} from '@/store/useStatusStore'
import {useLocaleStore} from "@/store/useLocaleStore";
import router from "@/router";
const statusStore = useStatusStore()
const local = useLocaleStore()
const language = ref<boolean>(false)
const showSidebar = ref<boolean>(false)
const changeLanguage = () => {
  console.log((language.value))
  if(language.value == true) {
    local.setLocale("en")
    router.go(0)
  } else {
    local.setLocale("zh")
    router.go(0)
  }
}
const handleClickSidebar = () => {
  console.log(showSidebar.value)
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
          <v-avatar size="32">
            <v-img src="https://mc-heads.net/avatar/steve" alt="Steve"></v-img>
          </v-avatar>
          <span class="ml-2 d-none d-sm-inline">Steve</span>
          <v-icon end>mdi-chevron-down</v-icon>
        </v-btn>
      </template>

      <v-list class="minecraft-user-menu">
        <v-list-item @click="()=>{}">
          <template v-slot:prepend>
            <v-icon>mdi-account</v-icon>
          </template>
          <v-list-item-title>Profile</v-list-item-title>
        </v-list-item>
        <v-list-item @click="()=>{}">
          <template v-slot:prepend>
            <v-icon>mdi-cog</v-icon>
          </template>
          <v-list-item-title>Settings</v-list-item-title>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item @click="()=>{}">
          <template v-slot:prepend>
            <v-icon>mdi-logout</v-icon>
          </template>
          <v-list-item-title>Logout</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>

</template>
<style scoped lang="scss">

</style>
