<script lang="ts" setup>
import {getClassStr} from "@/api/hotSwap/hotSwapApi";
import {getHotSwapPoints} from "@/api/hotSwap/hotSwapApi";
import {useStatusStore} from "@/store/useStatusStore";
import type{GetHotSwapPointsForm,PointVersion,ChangeRecord,MethodInfo} from "@/api/hotSwap/hotSwapType";
const statusStore = useStatusStore()
const editorOptions = ref({
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: false,
  scrollBeyondLastLine: false,
  find: {
    addExtraSpaceOnTop: false
  },
  folding: false,
  readOnly: false,
})
onMounted(()=>{
  searchInput.value = statusStore.getRecentClassName()
  if(searchInput.value != '')handleSearch()
})
const code = computed(()=>{
  return statusStore.classCode
})
watch(code, (newValue, oldValue) => {
  getHotSwapPointList()
})
const searchInput = ref('')
const handleSearch = () => {
  getHotSwapPointsForm.value.className = searchInput.value
  getClassCode()
}
const getClassCode = ()=>{
  statusStore.getClassCode(searchInput.value).then(getHotSwapPointList)
}
const dialog = ref(false)
const handleClickAdd = (line:number) => {
  dialog.value = true
  targetLine.value = line
}
const targetLine = ref(0)
const updateCode = (newCode:String)=>{
  getClassCode()
}

const methodInfoList = ref<MethodInfo[]>([

])
const getHotSwapPointsForm = ref<GetHotSwapPointsForm>({
  className: ''
})
const points = ref<PointVersion[]>([])
const pointMap = ref<Map<string,PointVersion>>(new Map())
const getMethodList = computed(()=>{
  let methodSet = new Set();
  for(let i in points.value){
    methodSet.add(points.value[i].changeRecord.methodName)
  }
  return methodSet
})
const getHotSwapPointList = ()=>{
  methodInfoList.value = []
  getHotSwapPoints(getHotSwapPointsForm.value).then((res:any)=>{
    if(res.code == 200){
      points.value = res.data
      let methods = getMethodList.value
      methods.forEach(method => {
        let changeRecord = findRecentVersion(method)
        if(!changeRecord)return
        calcMethodPosition(code.value,changeRecord)
      })
    }
  })
}
const findRecentVersion = (methodName:string):ChangeRecord | null => {
  let pointArray = points.value.reverse()
  for(let i in pointArray){
    if(pointArray[i].changeRecord.methodName === methodName&&pointArray[i].changeRecord.oldSourceCode!=pointArray[i].changeRecord.newSourceCode){
      return pointArray[i].changeRecord
    }
  }
  return null
}
const handleCode = (code:string)=>{
  return code.replaceAll('\r\n','\n').split(' ').join('')
}
const calcMethodPosition = (code:string,changeRecord:ChangeRecord)=> {
  let methodCode = handleCode(changeRecord.newSourceCode)
  let info:MethodInfo = {
    endLineNumber: 0, methodName: "", newSourceCode: "", oldSourceCode: "", startLineNumber: 0,differentLineNumber:0,className:""
  }
  let sourceCode = handleCode(code)
  let startLineNumberArr:number[] = []   // 与方法代码第一行相同的行数
  sourceCode.split('\n').forEach(((codeLine,index) => {
    if(codeLine == methodCode.split('\n')[0]){
      startLineNumberArr.push(index)
    }
  }))
  startLineNumberArr.forEach((value)=>{
    let codeLines = sourceCode.split('\n')
    let oldCodeLines = handleCode(changeRecord.oldSourceCode).split('\n')
    let methodLines = methodCode.split('\n')
    const methodLength = methodLines.length;

    if (value + methodLength > codeLines.length) {
      return;
    }
    for (let i = 0; i < methodLength; i++) {
      if (codeLines[value + i] !== methodLines[i]) {
        return
      }
    }
    for (let i = 0; i < methodLength; i++) {

      if (oldCodeLines[i] !== methodLines[i]) {
        info.differentLineNumber = value + i + 1
        break;
      }
    }
    info.className = searchInput.value
    info.methodName = changeRecord.methodName
    info.oldSourceCode = changeRecord.oldSourceCode
    info.newSourceCode = changeRecord.newSourceCode
    info.startLineNumber = value
    info.endLineNumber = value + methodLength
    methodInfoList.value.push(info)
  })
}
const currentMethodInfo = ref<MethodInfo>({})
const rollbackDialog = ref(false)

const handleClickRollback = (index) => {
  for(let i in methodInfoList.value){
    if(methodInfoList.value[i].differentLineNumber == index){
      currentMethodInfo.value = methodInfoList.value[i]
    }
  }
  rollbackDialog.value = true
}
const handleConfirmRollback = ()=>{
  getClassCode()
  rollbackDialog.value = false
}

</script>
<template>
  <div class="input-container">
    <MinecraftInputComponent v-model="searchInput" :placeholder="'io.github.tml.controller.UserController'" style="flex: 9" @keydown.enter="handleSearch"></MinecraftInputComponent>
    <MinecraftButtonComponent :disabled="searchInput===''" style="flex: 1" @click="handleSearch"><span class="mx-auto" >{{$t('common.search')}}</span></MinecraftButtonComponent>
  </div>
  <div class="editor-container" v-if="code!==''">
    <MonacoEditor
        :key="code"
        v-model="code"
        :language="'java'"
        :options="editorOptions"
        @clickAdd="handleClickAdd"
        @clickRollback="handleClickRollback"
        :methodInfoList="methodInfoList"
    />
  </div>
  <RollbackHotSwapPointDialog v-model="rollbackDialog" :methodInfo="currentMethodInfo" @confirm="handleConfirmRollback"></RollbackHotSwapPointDialog>
  <CreateHotSwapInfoDialog :key="targetLine" v-model="dialog" :targetLine="targetLine" :className="searchInput" @updateCode="updateCode"></CreateHotSwapInfoDialog>
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
  height: 70%;
}

</style>
