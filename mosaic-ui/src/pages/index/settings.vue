<script lang="ts" setup>


const config = ref({
  "test":{
    "test1":true,
    "test2":"haha"
  },
  "apiEndpoint": "https://api.siliconflow.cn/v1/chat/completions",
  "apiKey": "sk-qdahitijgevlgckknvypdmyvpveyhbrjqyylmhfkvuedmfsf",
  "model": "deepseek-ai/DeepSeek-R1",
  "maxTokens": 4096,
  "temperature": 0.7,

})
const val = ref({})

onMounted(()=>{
  val.value = config
})
</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.settings')}}</h1>
  </div>
  <div style="white-space: break-spaces">
    {{val.value}}
  </div>
  <ConfigFormComponent :configNode="config" v-model="val"></ConfigFormComponent>
</template>
<style scoped lang="scss">
.editor-container {
  position: relative;
  padding: 20px;
}

</style>
