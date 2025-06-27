<script lang="ts" setup>
import {defineProps} from "vue";
import {useStatusStore} from "@/store/useStatusStore";
import {type World} from "@/api/world/worldType";
const statusStore = useStatusStore()
const props = defineProps({
  world: {} as World
})
const running = ref(false)
const dialog = ref(false)
const handleConfirm = ()=>{

}
</script>
<template>
  <div class="world-list-item">
    <MinecraftButtonComponent>
      <div class="item-inner">
        <div class="icon">
          🔨
        </div>
        <div class="world-name">
          {{world.worldName}}
        </div>
        <div class="actions">
          <div class="action-item" v-if="statusStore.currentWorld.id!==world.id">
            <MinecraftButtonComponent @click="statusStore.setCurrentWorld(world)"><v-icon size="32">mdi-earth-arrow-left</v-icon></MinecraftButtonComponent>
          </div>
          <div class="action-item" v-else>
            <MinecraftButtonComponent >
              <v-icon size="32" v-if="running">mdi-play</v-icon>
              <v-icon size="32" v-else>mdi-pause</v-icon>
            </MinecraftButtonComponent>
          </div>
          <div class="action-item">
            <MinecraftButtonComponent @click="dialog = true"><v-icon size="32">mdi-delete</v-icon></MinecraftButtonComponent>
          </div>
        </div>
      </div>
    </MinecraftButtonComponent>
  </div>
<!--  <ConfirmComponent v-model="dialog" :title="'Delete World?'" @confirm="handleConfirm"></ConfirmComponent>-->
</template>
<style scoped lang="scss">
.world-list-item{
  position: relative;
  width: 90%;
  left: 50%;
  transform: translate(-50%);
  margin-top: 10px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  .item-inner{
    height: 80px;
    display: flex;
    width: 100%;
    align-items: center;
    gap: 24px;
    margin: 16px;
    .icon{
      font-size: 32px;
      height: 64px;
      width: 64px;
      border-radius: 6px;
      background-color: rgba(0,0,0,0.1);
      line-height: 64px;
      border: white 1px solid;
      box-shadow: white 0 0 8px 0px;
    }
    .world-name{
      font-size: 24px;
    }
    .actions{
      margin-left: auto;
      display: flex;
      .action-item{
        height: 32px;
        margin-left: 8px;
        //width: 32px;
      }
    }
  }
}

</style>
