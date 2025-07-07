<template>
  <v-dialog v-model="dialog" width="95%" class="hotSwap">
    <v-card>
      <v-card-title>{{ $t('hotSwap.rollback') }}</v-card-title>
      <v-card-item>
        <div class="rollback-dialog" v-if="methodInfo">
          <div class="rollback-dialog-editor" >
            <v-card-subtitle>{{$t('hotSwap.current')}}</v-card-subtitle>
            <MonacoEditor
                :key="methodInfo"
                v-model="methodInfo.newSourceCode"
                :language="'java'"
                :options="editorOptions"
            ></MonacoEditor>
          </div>
          <div class="rollback-dialog-editor">
            <v-card-subtitle>{{$t('hotSwap.afterApplying')}}</v-card-subtitle>
            <MonacoEditor
                :key="methodInfo"
                v-model="methodInfo.oldSourceCode"
                :language="'java'"
                :options="editorOptions"
            ></MonacoEditor>
          </div>
        </div>
      </v-card-item>
      <v-card-actions>
        <v-btn @click="dialog = false">
          {{$t('common.cancel')}}
        </v-btn>
        <v-btn @click="handleConfirmRollback">
          {{$t('common.confirm')}}
        </v-btn>
      </v-card-actions>
    </v-card>

  </v-dialog>
</template>

<script lang="ts" setup>
import {defineEmits, defineProps} from "vue";
import type {MethodInfo,RollBackHotSwapPointForm} from "@/api/hotSwap/hotSwapType";
import {rollBackHotSwapPoint} from '@/api/hotSwap/hotSwapApi'
import router from "@/router";
const props = defineProps({
  value: Boolean,
  methodInfo: {
  } as MethodInfo
})

const editorOptions = ref({
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: false,
  scrollBeyondLastLine: false,
  find: {
    addExtraSpaceOnTop: false
  },
  folding: false,
  readOnly: true
})
const emit = defineEmits(['update:modelValue', 'updateCode','confirm'])
const dialog = computed({
  get() {
    return props.value
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const rollBackHotSwapPointForm = ref<RollBackHotSwapPointForm>({})
const handleConfirmRollback = () =>{
  rollBackHotSwapPointForm.value.className = props.methodInfo.className
  rollBackHotSwapPointForm.value.method = props.methodInfo.methodName

  rollBackHotSwapPoint(rollBackHotSwapPointForm.value).then((res:any)=>{
    if(res.code == 200) {
      emit('confirm')
      dialog.value = false
    }
  })

}
</script>

<style scoped lang="scss">
.rollback-dialog{
  width: 100%;
  height: 400px;
  display: flex;
  gap: 8px;
  .rollback-dialog-editor{
    width: 50%;
  }
}
</style>
