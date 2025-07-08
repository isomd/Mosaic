<script lang="ts" setup>
import {defineProps, defineEmits} from "vue";
import {useI18n} from 'vue-i18n'
import {createOrSetup} from "@/api/slot/slotApi";
import {useSlotStore} from "@/store/data/useSlotStore";

const {t} = useI18n()
const emit = defineEmits(['update:modelValue'])
const slotStore = useSlotStore()
const props = defineProps({
  modelValue: Boolean,
  slot: {
    default: null
  }
})
const dialog = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const handleSubmit = (createSlotForm) => {
  createOrSetup(createSlotForm).then((res: any) => {
    if (res.code == 200) {
      dialog.value = false
      slotStore.getSlots()

    } else {
      //
    }
  })
}

</script>
<template>
  <v-dialog v-model="dialog" max-width="640">
    <v-card>
      <v-toolbar color="primary" dark>
        <v-toolbar-title>{{ $t('slot.add') }}</v-toolbar-title>
      </v-toolbar>
      <CreateSlotFormComponent :slot="slot" @submitValue="handleSubmit">
        <template v-slot:actions>
          <v-btn type="submit">{{ $t('slot.save') }}</v-btn>
        </template>
      </CreateSlotFormComponent>
    </v-card>
  </v-dialog>
</template>
<style lang="scss" scoped>

</style>
