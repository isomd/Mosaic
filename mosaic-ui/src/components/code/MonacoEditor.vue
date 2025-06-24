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

const emit = defineEmits(['update:modelValue', 'editor-mounted','clickAdd'])

const container = ref(null)
let editor


// watch(() => props.modelValue, (newValue) => {
//   if (editor.value && newValue !== editor.value.getValue()) {
//     editor.value.setValue(newValue)
//   }
// })
const lineCount = ref(0)
const containerHeight = computed(()=>{
  return container.value?(container.value.getBoundingClientRect().height)+'px':'0'
})
const containerWidth = computed(()=>{
  return container.value?(container.value.getBoundingClientRect().width)+'px':'0'
})
const translateY = computed(()=>{
  return `-${container.value?container.value.getBoundingClientRect().height:'0'}px`
})
const scrollTop = ref(0)
const scrollY = computed(()=>{
  return `-${scrollTop.value}px`
})
onMounted(() => {
  if (!container.value) return
  editor = monaco.editor.create(container.value, {
    value: props.modelValue,
    language: props.language,
    theme: props.theme,
    automaticLayout: true,  // 自适应容器大小
    minimap: {enabled: false},
    ...props.options,
  })
  editor.onDidChangeModelContent(() => {
    const value = editor.getValue()
    lineCount.value = editor.getModel().getLineCount()
    // emit('update:modelValue', value)
  })
  editor.onDidScrollChange((val)=>{
    scrollTop.value = val.scrollTop
    console.log(JSON.stringify(val))
  })
  lineCount.value = editor.getModel().getLineCount()
  emit('editor-mounted', editor)
})
onUnmounted(() => {
  if (editor) {
    editor.dispose()
  }
})
</script>
<template>
  <div style="overflow: hidden;position: relative">
    <div ref="container" class="monaco-editor">

    </div>
    <div class="layer">
      <div class="line" v-for="i in lineCount">
        <div class="add" @click="$emit('clickAdd',i)">＋</div>
        <div class="underline"></div>
      </div>
    </div>
  </div>
</template>



<style scoped lang="scss">
.monaco-editor {
  width: 100%;
  height: 600px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.layer{
  position: relative;
  height: v-bind(containerHeight);
  transform: translate(5px,v-bind(translateY));
  --line-number-width: 19px;
  width: var(--line-number-width);

  .line {
    position: relative;
    display: flex;
    width: v-bind(containerWidth);
    transform: translate(0,v-bind(scrollY));
    height: 0;
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
      width: calc(v-bind(containerWidth) - var(--line-number-width) - 5px);
    }
  }

}
.view-line {
  border-bottom: 2px solid #007fd4 !important;
}
</style>
