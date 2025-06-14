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
      <v-container fluid>

      <router-view></router-view>
      </v-container>
    </v-main>
    <footerComponent></footerComponent>
  </v-app>
</template>

<style scoped lang="scss">
.m-body{
  width: 100%;
  height: calc(100% - 64px);
  display: flex;
}
.routerView{
  width: 100%;
  height: 100%;
}

</style>
