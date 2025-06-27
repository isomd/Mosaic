<script lang="ts" setup>
import {useStatusStore} from "@/store/useStatusStore";
const statusStore = useStatusStore()
</script>
<template>
  <div class="ball-container">
    <div class="world" @click="statusStore.setShowWorldPanel(true)"></div>

  </div>
</template>
<style scoped lang="scss">
.ball-container{
  z-index: 1000;
  position: fixed;
  height: 64px;
  width: 64px;
  border-radius: 32px;
  right: 48px;
  bottom: 88px;
  box-shadow: white 0 0 30px 5px;
  &:hover{
    box-shadow: white 0 0 40px 10px;
  }
  .world{
    position: relative;
    z-index: 1000;
    cursor: pointer;
    width: 100px;
    height: 100px;
    background-image: url("/world.png");
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center center;
    border-radius: 50px;
    transition: all 0.3s;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    &:hover{
      transform: translate(-50%,-52%);
    }

  }

}
</style>
