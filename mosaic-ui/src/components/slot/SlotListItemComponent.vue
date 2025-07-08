<script lang="ts" setup>
import {defineProps} from "vue";
import {type Slot} from '@/api/slot/slotType'
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
import {ExtensionPoint} from "@/api/plugin/pluginType";
import {deleteSlot,unSetup} from "@/api/slot/slotApi";
import router from "@/router";
import {useI18n} from 'vue-i18n'
const {t} = useI18n()
const dialog = ref(false)
const deleteDialog = ref(false)
const unSetupDialog = ref(false)
const cubeStore = useCubeStore()
const slotStore = useSlotStore()
const exPoint = ref<ExtensionPoint>({

})
const props = defineProps({
  slot: {

  } as Slot
})
onMounted(()=>{
  exPoint.value = cubeStore.getExPoint(props.slot.cubeId,props.slot.exPackageId,props.slot.exPointId)
})
const handleDelete = () => {
  deleteSlot(props.slot.slotId).then((res:any)=>{
    if(res.code == 200) {
      slotStore.getSlots()
    } else {
      //
    }
  })
}
const handleUnSetup = () => {
  unSetup(props.slot.slotId).then((res:any)=>{
    if(res.code == 200) {
      slotStore.getSlots()
    } else {
      //
    }
  })
}
</script>
<template>
  <v-card width="320" min-height="320" class="stone">
    <div class="slot-title">
      {{props.slot.slotId}}
    </div>
    <v-divider></v-divider>
    <div v-if="props.slot.setupFlag&&exPoint&&exPoint.id!==''" class="extension-point">
      <h5>{{$t('slot.resName')}}</h5>
      <v-chip x-small label class="mr-2">{{ props.slot.resName }} </v-chip>
      <h5>{{$t('slot.method')}}</h5>
      <v-chip x-small label class="mr-2">{{ exPoint.methodName }}()  </v-chip>
      <h5>{{$t('slot.resType')}}</h5>
      <v-chip x-small label color="indigo" text-color="white">
        {{ exPoint.returnType }}
      </v-chip>
      <span class="uninstall" @click="unSetupDialog = true">
        -
      </span>
    </div>
    <div v-else class="extension-point">
      <div class="setup" @click="dialog = true">
        {{$t('slot.setup')}}
      </div>
    </div>
    <div class="action">
      <v-icon @click="deleteDialog = true">mdi-trash-can</v-icon>
    </div>
  </v-card>
  <CreateSlotComponent v-model="dialog" :slot="props.slot"></CreateSlotComponent>
  <ConfirmComponent v-model="deleteDialog" @confirm="handleDelete" :title="t('slot.confirmDelete')"></ConfirmComponent>
  <ConfirmComponent v-model="unSetupDialog" @confirm="handleUnSetup" :title="t('slot.confirmUnSetup')"></ConfirmComponent>
</template>
<style lang="scss" scoped>
.v-card{
  padding: 16px !important;
  &:hover > .action .v-icon{
    visibility: visible;
  }
  .action{
    position: absolute;
    width: 100%;
    top:327px;
    left: 0;
    display: flex;
    justify-content: right;
    &:hover > .v-icon{
      visibility: visible;
    }
    .v-icon{
      cursor: pointer;
      visibility: hidden;
    }
  }
}
.v-chip{
  font-size: 10px !important;
}
.v-divider{
  margin: 12px 0 !important;
}
.slot-title{
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.extension-point{
  position: relative;
  border-radius: 16px;
  padding: 12px;
  transition: all 0.3s;
  cursor: pointer;
  background-color: rgba(0,0,0,0.1);
  &:hover{
    background-color: rgba(0,0,0,0.2);
  }
  &:hover > .uninstall {
    visibility: visible;
  }
  .uninstall{
    position: absolute;
    visibility: hidden;
    right: 8px;
    top: 8px;
    color: red;
    transition: all 0.3s;
    cursor: pointer;
    &:hover{
      transform: translate(0,-2px);
    }
  }
  .setup{
    text-align: center;
  }
}

</style>
