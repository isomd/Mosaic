<script setup lang="ts">
import * as monaco from 'monaco-editor'
import {defineProps,defineEmits} from "vue";

const props = defineProps({
  modelValue: String,
  language: {
    type: String,
    default: 'java'
  },
  theme: {
    type: String,
    default: 'vs-dark'
  },
  options: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'editor-mounted'])

const container = ref(null)
let editor = ref(null)


watch(() => props.modelValue, (newValue) => {
  if (editor.value && newValue !== editor.value.getValue()) {
    editor.value.setValue(newValue)
  }
})
const lineCount = ref(0)
const offsetHeight = computed(()=>{
  return `translate(5px,-${editor.value?(editor.value._domElement.clientHeight + 1):'0'}px)`
})
onMounted(() => {
  if (!container.value) return
  editor.value = monaco.editor.create(container.value, {
    value: props.modelValue,
    language: props.language,
    theme: props.theme,
    automaticLayout: true,  // 自适应容器大小
    minimap: {enabled: false},
    ...props.options,
  })
  editor.value.onDidChangeModelContent(() => {
    const value = editor.value.getValue()
    lineCount.value = editor.value.getModel().getLineCount()
    emit('update:modelValue', value)
  })
  lineCount.value = editor.value.getModel().getLineCount()
  emit('editor-mounted', editor.value)
})
// onUnmounted(() => {
//   if (editor.value) {
//     editor.value.dispose()
//   }
// })
</script>
<template>
  <div ref="container" class="monaco-editor">

  </div>
  <div class="line" v-for="i in lineCount">
    <div class="add" @click="console.log(i)">＋</div>
    <div class="underline"></div>
  </div>
</template>



<style scoped lang="scss">
.monaco-editor {
  width: 100%;
  height: 600px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.line {
  position: relative;
  transform: v-bind(offsetHeight);
  display: flex;
  width: 100%;
  height: 0;
  --line-number-width: 19px;
  margin-bottom: 19px;

  .add {
    height: 19px;
    width: var(--line-number-width);
    font-size: 0.6rem;
    border: 2px solid #282828;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 100;
    color: #282828;
    cursor: pointer;
    opacity: 0;

    &:hover {
      opacity: 1;
      border: 2px solid #007fd4;
      color: #007fd4;
    }

    &:hover + .underline {
      border-bottom: 2px solid #007fd4;
    }
  }

  .underline {
    position: relative;
    height: 2px;
    top: 17px;
    bottom: 0;
    width: calc(100% - var(--line-number-width) - 5px);
  }
}

.view-line {
  border-bottom: 2px solid #007fd4 !important;
}
</style>
