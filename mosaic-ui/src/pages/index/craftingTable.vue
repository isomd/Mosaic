<script lang="ts" setup>
import {getClassStr} from "@/api/hotSwap/hotSwapApi";

const editorOptions = ref({
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: false,
  scrollBeyondLastLine: false,
  find: {
    addExtraSpaceOnTop: false
  },
  folding: false
})
const code = ref('')
const searchInput = ref('')
const handleSearch = () => {
  getClassStr(searchInput.value).then((res:any)=>{
    if(res.code == 200) {
      code.value = res.message
    } else {
      //
    }
  })
}
const dialog = ref(false)
const handleClickAdd = (line:number) => {
  dialog.value = true
  targetLine.value = line
}
const targetLine = ref(0)
const updateCode = (newCode:String)=>{
  code.value = newCode
}
</script>
<template>
  <div class="input-container">
    <MinecraftInputComponent v-model="searchInput" :placeholder="'io.github.tml.controller.UserController'" style="flex: 9" @keydown.enter="handleSearch"></MinecraftInputComponent>
    <MinecraftButtonComponent style="flex: 1" @click="handleSearch"><span>{{$t('common.search')}}</span></MinecraftButtonComponent>
  </div>
  <div class="editor-container">
    <MonacoEditor
        :key="code"
        v-model="code"
        :language="'java'"
        :options="editorOptions"
        @clickAdd="handleClickAdd"
    />
  </div>
  <CreateHotSwapInfoDialog :key="targetLine" v-model="dialog" :targetLine="targetLine" :className="searchInput" @updateCode=""></CreateHotSwapInfoDialog>
</template>
<style scoped lang="scss">
.input-container{
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
}
.editor-container {
  position: relative;
  padding: 20px;
}

</style>
