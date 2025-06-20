<script lang="ts" setup>

const selectedLanguage = ref('java')
const editorOptions = ref({
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: false,
  scrollBeyondLastLine: false,
  find: {
    addExtraSpaceOnTop: false
  }
})
const code = ref<String>('package io.github.tml.controller;\n\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;\n\n@RestController\n@RequestMapping(\"/welsir\")\npublic class WelsirTestController {\n\n    @GetMapping(\"/v1\")\n    public String welsir() {\n\n        String params = \"\";\n        System.out.println(params);\n        return \"welsir\";\n    }\n\n}')


</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.settings')}}</h1>
  </div>
  <div class="editor-container">
    <MonacoEditor
        v-model="code"
        :language="selectedLanguage"
        :options="editorOptions"
    />
  </div>
</template>
<style scoped lang="scss">
.editor-container {
  position: relative;
  padding: 20px;
}

</style>
