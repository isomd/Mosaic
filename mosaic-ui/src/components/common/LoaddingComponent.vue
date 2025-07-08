<template>
  <v-dialog v-model="dialog" persistent style="z-index: 1000000">
    <div class="spinner-container">
      <div class="spinner-cube">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
      <div class="loading-text">LOADING<span v-for="i in count">.</span></div>
    </div>
  </v-dialog>
</template>
<script lang="ts" setup>
import {useStatusStore} from "@/store/useStatusStore";
const statusStore = useStatusStore()
const dialog = computed(()=>{
  return statusStore.getLoading()
})
let interval
let count = ref(0)
onMounted(()=>{
  interval = setInterval(()=>{
    count.value = (count.value+1)%4
  },500)
})
onUnmounted(()=>{
  clearInterval(interval)
})
</script>
<style scoped>
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.spinner-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  border-radius: 15px;
  padding: 20px;
  transition: all 0.3s ease;
}
.spinner-cube {
  width: 60px;
  height: 60px;
  position: relative;
  transform-style: preserve-3d;
  animation: spin 1.8s infinite ease-in-out;
}

.spinner-cube div {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #0575F2;
  border: 2px solid white;
}

.spinner-cube div:nth-child(1) {
  transform: translateZ(30px);
}

.spinner-cube div:nth-child(2) {
  transform: rotateY(90deg) translateZ(30px);
}

.spinner-cube div:nth-child(3) {
  transform: rotateY(180deg) translateZ(30px);
}

.spinner-cube div:nth-child(4) {
  transform: rotateY(-90deg) translateZ(30px);
}

.spinner-cube div:nth-child(5) {
  transform: rotateX(90deg) translateZ(30px);
}

.spinner-cube div:nth-child(6) {
  transform: rotateX(-90deg) translateZ(30px);
}
.loading-text {
  margin-top: 20px;
  font-size: 1.2rem;
  font-weight: 500;
  color: white;
  text-align: center;
  opacity: 1;
  text-shadow:
      1px 1px 3px rgba(0, 0, 0, 0.7),   /* 主阴影：右下偏移+模糊 */
      -1px -1px 3px rgba(0, 0, 0, 0.3); /* 补光阴影：左上偏移 */
}
</style>
