<script lang="ts"setup>
import {useStatusStore} from '@/store/useStatusStore'
import {ref} from "vue";
const statusStore = useStatusStore()

onMounted(()=>{
})
</script>
<template>
  <v-app>
    <headerComponent></headerComponent>
    <sidebarComponent></sidebarComponent>
    <v-main>
      <router-view></router-view>
    </v-main>
    <footerComponent></footerComponent>
  </v-app>
</template>

<style scoped lang="scss">


</style>
