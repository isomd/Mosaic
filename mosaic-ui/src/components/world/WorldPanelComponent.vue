<script lang="ts" setup>
import {useStatusStore} from "@/store/useStatusStore";

import {type World,type CreateWorldForm} from "@/api/world/worldType";
const statusStore = useStatusStore()
const display = computed(()=>{
  return statusStore.showWorldPanel
})
const createWorldForm = ref<CreateWorldForm>({

})
const worldList = ref<World[]>([
  {
    id:0,
    worldName:'world0'
  },
  {
    id:1,
    worldName:'world1'
  },
  {
    id:2,
    worldName:'world2'
  },
  {
    id:3,
    worldName:'world3'
  },
  {
    id:4,
    worldName:'world4'
  },
  {
    id:5,
    worldName:'world5'
  },
  {
    id:6,
    worldName:'world6'
  },
])
onMounted(()=>{
  statusStore.setCurrentWorld(worldList.value[0])
})
const handleCreateWorld = ()=>{
  createWorldForm.value.id = worldList.length
  worldList.value.push(createWorldForm.value)
  createWorldForm.value.worldName = ''
}
</script>
<template>
  <transition name="slide-up">
    <div v-if="display" class="page">
      <div class="background" @click="statusStore.setShowWorldPanel(false)">

      </div>
      <div class="world-list">
        <WorldListItemComponent v-for="world in worldList" :key="world.id" :world="world"></WorldListItemComponent>
      </div>
      <div class="actions">
        <MinecraftInputComponent v-model="createWorldForm.worldName" style="flex: 8" :placeholder="$t('world.worldName')"></MinecraftInputComponent>
        <MinecraftButtonComponent :disabled="createWorldForm.worldName === ''"  style="flex: 1" @click="handleCreateWorld">{{$t("world.createWorld")}}</MinecraftButtonComponent>
      </div>
    </div>
  </transition>


</template>
<style scoped lang="scss">
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
}

.slide-up-enter-to,
.slide-up-leave-from {
  transform: translateY(0);
}
.page{
  height: 100vh;
  width: 100vw;
  z-index: 10000;
  position: fixed;
  top: 0;
  left: 0;
  padding: 0 10%;
  .background {
    position: fixed;
    height: 100vh;
    width: 100vw;
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
    backdrop-filter: blur(5px);
  }
  .world-list{
    position: relative;
    z-index: 100;
    height: calc(100vh - 100px);
    width: 1280px;
    top: 0;
    left: 50%;
    transform: translate(-50%);
    overflow-y: scroll;
  }
  .actions{
    z-index: 100;
    position: relative;
    height: 100px;
    width: 1280px;
    padding: 12px 0;
    bottom: 0;
    left: 50%;
    transform: translate(-50%);
    display: flex;
    gap: 24px;
  }
}

</style>
