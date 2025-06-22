<script lang="ts" setup>
import {getSlotList} from "@/api/slot/slotApi";
import {type Slot} from '@/api/slot/slotType'
const dialog = ref(false)
let slotList = ref<Slot[]>([])
onMounted(()=>{
  getSlotListFunction()
})
const getSlotListFunction = () => {
  getSlotList().then((res:any)=>{
    if(res.code == 200){
      slotList.value = res.data.slotList
    } else {
      //
    }
  })
}
</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.slot')}}</h1>
  </div>
  <div class="slot-list">
    <div class="add-slot" @click="dialog = true">
      <div class="add-slot__center">
        <div class="icon">
          <v-icon size="64">mdi-shovel</v-icon>
        </div>
        <br>
        <span class="add-slot-text">{{$t('slot.add')}}</span>
      </div>
    </div>
    <SlotListItemComponent v-for="slot in slotList" :key="slot.slotId" :slot="slot"></SlotListItemComponent>
  </div>
  <CreateSlotComponent v-model="dialog"></CreateSlotComponent>
</template>
<style scoped lang="scss">
.slot-list{
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  padding: 12px;
  gap: 24px;
  row-gap: 24px;
  .add-slot{
    position: relative;
    height: 320px;
    width: 320px;
    //border: grey 1px dashed;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.3s;
    .add-slot__center{
      position: relative;
      left: 50%;
      top: 50%;
      transform: translate(-50%,-50%);
      .icon{
        display: flex;
        justify-content: center;
      }
      .add-slot-text{
        text-align: center;
        display: block;
        font-size: 14px;
      }
    }
    &:hover{
      scale: 0.99;
      background-color: rgba(0,0,0,0.1);
    }
  }

}
</style>
