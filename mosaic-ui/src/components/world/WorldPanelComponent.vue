<script lang="ts" setup>
import {useStatusStore} from "@/store/useStatusStore";
import {useWorldStore} from "@/store/data/useWorldStore";
import {type World,type CreateWorldForm} from "@/api/world/worldType";
import {getWorldList,createWorld,getCurrentWorld} from "@/api/world/worldApi";
const worldStore = useWorldStore()
const statusStore = useStatusStore()
const display = computed(()=>{
  return statusStore.showWorldPanel
})
const createWorldForm = ref<CreateWorldForm>({
  name: ''
})
const worldList = computed(()=>{
  return worldStore.worldList
})
onMounted(()=>{
  getCurrentWorld().then((res:any)=>{
    if(res.code == 200){
      statusStore.setCurrentWorld(res.data)
    }
  })
  worldStore.getWorlds()
})
const handleCreateWorld = ()=>{
  createWorld(createWorldForm.value).then((res:any)=>{
    if(res.code == 200) {
      worldStore.getWorlds()
    } else {
      //
    }
  })
  createWorldForm.value.name = ''
}


</script>
<template>
  <transition name="slide-up">
    <div v-if="display" class="page">
      <div class="background" @click="statusStore.setShowWorldPanel(false)">

      </div>
      <div class="world-list">
        <WorldListItemComponent v-for="world in worldList" :key="world.id.uuid" :world="world"></WorldListItemComponent>
      </div>
      <div class="actions">
        <MinecraftInputComponent v-model="createWorldForm.name" style="flex: 8" :placeholder="$t('world.worldName')"></MinecraftInputComponent>
        <MinecraftButtonComponent :disabled="createWorldForm.name === ''"  style="flex: 1" @click="handleCreateWorld">{{$t("world.createWorld")}}</MinecraftButtonComponent>
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
