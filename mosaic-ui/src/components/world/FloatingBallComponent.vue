<script lang="ts" setup>
import {useStatusStore} from "@/store/useStatusStore";
const statusStore = useStatusStore()
const ball = ref()
onMounted(()=>{
  document.addEventListener("mousemove", handleMouseMove)
})
const dragging = ref(false)
const isDrag = ref(false)
const offsetX = ref(0)
const offsetY = ref(0)
const timeout = ref()
const handleMouseMove = (event)=>{
  if(!dragging.value)return
  isDrag.value = true
  ball.value.style.top = `${event.clientY-offsetY.value}px`
  ball.value.style.left = `${event.clientX-offsetX.value}px`
}
const handleMouseDown = (event) =>{
  offsetX.value = event.offsetX
  offsetY.value = event.offsetY
  dragging.value = true
}
const handleMouseUp = () => {
  dragging.value = false

}
const handleClick = () =>{
  if(isDrag.value){
    isDrag.value = false
    return
  }
  statusStore.setShowWorldPanel(true)
}
const handleMouseEnter = () => {

}
onUnmounted(()=>{
  document.removeEventListener("mousemove",handleMouseMove)
})
</script>
<template>
  <div class="ball-container" ref="ball" @mousedown="handleMouseDown" @mouseup="handleMouseUp" @mouseleave="handleMouseUp" @mouseenter="handleMouseEnter">
    <div class="world" @click="handleClick">

    </div>

  </div>
</template>
<style scoped lang="scss">
.ball-container{
  z-index: 10000;
  position: fixed;
  height: 64px;
  width: 64px;
  border-radius: 32px;
  right: 48px;
  bottom: 88px;
  box-shadow: white 0 0 30px 5px;
  user-select: none;
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
