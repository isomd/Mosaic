<script lang="ts" setup>
import {defineEmits, defineProps} from 'vue'
import {useI18n} from 'vue-i18n'
const {t} = useI18n()
const props = defineProps({
  title: {
    default: 'Confirm?'
  }
})
const handleConfirm = () => {
  emit('confirm')
}
const handleCancel = () => {
  emit('update:modelValue', false)
}
const emit = defineEmits(['confirm','cancel'])
const dialog = ref(false)
</script>
<template>
  <v-dialog v-model="dialog" max-width="640">
    <v-card>
      <v-card-title>{{props.title}}</v-card-title>
      <v-card-actions>
        <v-btn @click="handleConfirm">{{$t('common.confirm')}}</v-btn>
        <v-btn @click="handleCancel">{{$t('common.cancel')}}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<style lang="scss">

</style>
