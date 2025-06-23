<script lang="ts" setup>
import {defineEmits, defineProps} from "vue";
import {getSlotList} from "@/api/slot/slotApi";
import {type Slot} from '@/api/slot/slotType'
import {createPoint} from "@/api/hotSwap/hotSwapApi";
import {type CreatePointForm} from "@/api/hotSwap/hotSwapType";
const emit = defineEmits(['update:modelValue','updateCode'])
const props = defineProps({
  value: Boolean,
  targetLine: Number,
  className: String
})
let slotList = ref<Slot[]>([])
const dialog = computed({
  get() { return props.value },
  set(val) { emit('update:modelValue', val) }
})
const createPointForm = ref<CreatePointForm>({
  slotId: '',
  cubeId: "system.log.cube",
  exPackageId: "system.text",
  exPointId: "text.get",
  resName: "threadId",
  className: '',
  targetLine:0,
  changeType: 'INSERT_AFTER',
  args:[""]
})
onMounted(()=>{
  createPointForm.value.targetLine = props.targetLine
  createPointForm.value.className = props.className
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
const handelSave = () => {
  createPoint(createPointForm.value).then((res:any)=>{
    if(res.code == 200){
      emit('updateCode',res.data.code)
    } else {
      //
    }
  })
}
</script>
<template>
  <v-dialog class="hotSwap" v-model="dialog" max-width="480">
    <v-card>
      <v-list>
        <v-list-item>
          <v-list-item-title>
            {{$t('hotSwap.lineNumber')}}
          </v-list-item-title>
          <br>
          <v-list-item-subtitle>
            {{ props.targetLine }}
          </v-list-item-subtitle>
        </v-list-item>
        <br>
        <v-list-item>
          <v-list-item-title>
            {{$t('hotSwap.slot')}}
          </v-list-item-title>
          <br>
          <v-list-item-subtitle>
            <v-select :items="slotList" item-title="slotId"
                      variant="outlined" item-value="slotId" v-model="createPointForm.slotId">
            </v-select>
          </v-list-item-subtitle>
        </v-list-item>
<!--        <v-list-item>-->
<!--          <v-list-item-title>-->
<!--            {{$t('hotSwap.className')}}-->
<!--          </v-list-item-title>-->
<!--          <br>-->
<!--          <v-list-item-subtitle>-->
<!--            <v-text-field-->
<!--                v-model="createHotSwapInfoForm.className"-->
<!--                outlined-->
<!--                dense-->
<!--                class="mb-4"-->
<!--                variant="outlined"-->
<!--            ></v-text-field>-->
<!--          </v-list-item-subtitle>-->
<!--        </v-list-item>-->
      </v-list>
      <v-card-actions>
        <v-btn @click="emit('update:modelValue', false)">
          关闭
        </v-btn>
        <v-btn @click="handelSave">
          保存
        </v-btn>
      </v-card-actions>
    </v-card>

  </v-dialog>
</template>
<style lang="scss" scoped>
.v-list{
  background-color: transparent;
}

</style>
