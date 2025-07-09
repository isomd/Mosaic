<script lang="ts" setup>
import {defineProps} from "vue";
import {useStatusStore} from "@/store/useStatusStore";
import {useWorldStore} from "@/store/data/useWorldStore";

import type{ World,TraverseWorldForm,DeleteWorldForm} from "@/api/world/worldType";
import {traverseWorld,removeWorld} from "@/api/world/worldApi";
import router from "@/router";
const statusStore = useStatusStore()
const props = defineProps({
  world: {} as World
})
const worldStore = useWorldStore()
const dialog = ref(false)
const deleteWorldForm = ref<DeleteWorldForm>({})

const handleTraverse = ()=>{
  statusStore.setCurrentWorld(props.world)
}
const handleDelete = ()=>{
  removeWorld(deleteWorldForm.value).then((res:any)=>{
    if(res.code == 200){
      worldStore.getWorlds()
      router.go(0)
    }
  })
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
          {{world.name}}
        </div>
        <div class="actions">
          <div class="action-item" v-if="world&&statusStore.currentWorld.id.uuid!==world.id.uuid">
            <MinecraftButtonComponent @click="handleTraverse"><v-icon size="32">mdi-earth-arrow-left</v-icon></MinecraftButtonComponent>
          </div>
<!--          <div class="action-item" v-else>-->
<!--            <MinecraftButtonComponent >-->
<!--              <v-icon size="32" v-if="running">mdi-play</v-icon>-->
<!--              <v-icon size="32" v-else>mdi-pause</v-icon>-->
<!--            </MinecraftButtonComponent>-->
<!--          </div>-->
          <div class="action-item">
            <MinecraftButtonComponent @click="handleDelete"><v-icon size="32">mdi-delete</v-icon></MinecraftButtonComponent>
          </div>
        </div>
      </div>
    </MinecraftButtonComponent>
  </div>
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
