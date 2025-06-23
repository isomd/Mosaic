<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";

const props = defineProps({
  modelValue: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    default: '......'
  }
})


defineEmits(['update:modelValue'])
</script>
<template>
    <input
        type="text"
        class="minecraft-input-field"
        :placeholder="props.placeholder"
        :value="props.modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
    >
</template>
<style scoped lang="scss">


.minecraft-input-field {
  width: 100%;
  padding: 18px 20px;
  font-family: 'Press Start 2P', cursive;
  font-size: 16px;
  background: #8b8b8b;
  border: 4px solid;
  border-color: #373737 #fff #fff #373737;
  color: #fff;
  outline: none;
  transition: all 0.1s ease;
  box-shadow: inset 4px 4px 0 #555,
  inset -4px -4px 0 #c6c6c6;
  letter-spacing: 1px;
}

.minecraft-input-field:focus {
  background: #9e9e9e;
}
</style>
