<script lang="ts"setup>
import {useStatusStore} from '@/store/useStatusStore'
import {ref} from "vue";
import LoaddingComponent from "../components/common/LoaddingComponent.vue";
const statusStore = useStatusStore()

onMounted(()=>{
})
</script>
<template>
  <v-app>
    <headerComponent></headerComponent>
    <sidebarComponent></sidebarComponent>
    <v-main>
      <LoaddingComponent></LoaddingComponent>
      <FloatingBallComponent></FloatingBallComponent>
      <WorldPanelComponent></WorldPanelComponent>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-main>
    <footerComponent></footerComponent>
  </v-app>
</template>

<style scoped lang="scss">


</style>
