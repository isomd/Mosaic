<script setup lang="ts">
import { ref, watch } from 'vue'
import { type AlertInfo, newAlert } from '@/utils/message'

const alertMap = ref<Map<string, AlertInfo>>(new Map)

watch(newAlert.value, () => {
  alertMap.value.set(newAlert.value.id, { ...newAlert.value })
  deleteAlert(newAlert.value.id)
})

const deleteAlert = (id: string) => {
  setTimeout(() => {
    alertMap.value.delete(id)
  }, 3000)
}
</script>

<template>
  <div class="alert-container">
    <v-alert
        class="v-alert"
        v-for="(alert, index) in Array.from(alertMap.values())"
        :key="index"
        :type="alert.type"
        border="start"
        closable
        close-label="Close Alert"
        :text="alert.message"
    >
    </v-alert>
  </div>
</template>

<style scoped>
.alert-container {
  position: fixed;
  top: 50px;
  left: 50%;
  transform: translate(-50%);
  z-index:100000000;
}

.v-alert {
  margin-top: 0.2rem !important;
}
</style>
