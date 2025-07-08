<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";

const emit = defineEmits(['click'])

const props = defineProps({
  disabled: {
    default: false
  }
})
function handleClick() {
  if (!props.disabled) {
    emit('click')
  }

}
</script>
<template>
  <div class="minecraft-button" :class="props.disabled?'disabled':''" @click="handleClick">
    <slot></slot>
  </div>
</template>
<style lang="scss" scoped>

.minecraft-button {
  flex: 1;
  height: auto;
  //min-width: 180px;
  //padding: 16px;
  font-size: 14px;
  background: #727272;
  border: 4px solid;
  border-color: #fff #5a5a5a #5a5a5a #fff;
  color: #fff;
  cursor: pointer;
  position: relative;
  text-align: center;
  transition: all 0.1s ease;
  text-shadow: 2px 2px 0 #333;
  box-shadow: inset -4px -4px 0 #5a5a5a;
  user-select: none;
  display: flex;
  align-items: center;

}

.minecraft-button:hover {
  background: #828282;
  transform: translateY(-2px);
}

.minecraft-button:active {
  border-color: #5a5a5a #fff #fff #5a5a5a;
  box-shadow: inset 4px 4px 0 #5a5a5a;
  transform: translateY(2px);
}
.disabled {
  cursor: not-allowed;
}
</style>
