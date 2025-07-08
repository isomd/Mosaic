<script lang="ts" setup>
import {type CreateSlotForm, type Slot} from "@/api/slot/slotType";
import {type Cube, type ExtensionPackage, type ExtensionPoint} from "@/api/plugin/pluginType";
import {useCubeStore} from "@/store/data/useCubeStore";
import {createOrSetup} from "@/api/slot/slotApi";
import {useI18n} from 'vue-i18n'
import {defineEmits, defineProps,defineExpose} from "vue";
const cubeStore = useCubeStore()
import router from "@/router";
const emit = defineEmits(['submitValue'])

const props = defineProps({
  slot: {
    default: null
  },
  readOnly: {
    default: false
  }
})
let cubeList = computed(()=>{
  return cubeStore.cubeList
})
onMounted(() => {
  if (props.slot) {
    createSlotForm.value = props.slot
    createSlotForm.value.setupFlag = true
    isSetup.value = true
  }
})
const isSetup = ref(false)
const {t} = useI18n()
let createSlotForm = ref<CreateSlotForm>({
  slotId: '',
  setupFlag: true,
  cubeId: '',
  exPackageId: '',
  exPointId: '',
  resName: '',
})
const rules = [
  value => {
    if (value) return true
    return t('slot.rule.empty')
  },
]
const setupRules = [
  value => {
    if (!createSlotForm.value.setupFlag) return true
    if (value) return true
    return t('slot.rule.empty')
  },
]
const extensionPackages = computed(() => {
  if (!createSlotForm.value.cubeId || createSlotForm.value.cubeId == '') {
    createSlotForm.value.exPackageId = ''
    return [] as ExtensionPackage[]
  }
  let exPackages: ExtensionPackage[] = cubeStore.getCubeById(createSlotForm.value.cubeId).extensionPackages as ExtensionPackage[]
  createSlotForm.value.exPackageId = exPackages[0]?.id
  return exPackages
})
const extensionPoints = computed(() => {
  if (!createSlotForm.value.exPackageId || createSlotForm.value.exPackageId == '') {
    createSlotForm.value.exPointId = ''
    return [] as ExtensionPoint[]
  }
  let exPoints: ExtensionPoint[] = extensionPackages.value.find((exPackage) => exPackage.id == createSlotForm.value.exPackageId)
      .extensionPoints
  createSlotForm.value.exPointId = exPoints[0]?.id
  return exPoints
})
const form = ref(null)
const handleSubmit = async () => {
  const {valid} = await form.value.validate();
  if (!valid) return
  emit('submitValue',createSlotForm.value)
}
defineExpose({handleSubmit})
</script>
<template>
  <v-form ref="form" @submit.prevent="handleSubmit">
    <v-card-text>
      <v-label>
        {{ $t('slot.slotId') }}
      </v-label>
      <v-text-field
          v-model="createSlotForm.slotId"
          outlined
          dense
          class="mb-4"
          variant="outlined"
          :rules="rules"
          :disabled="isSetup"
      ></v-text-field>
      <v-label>
        {{ $t('slot.resName') }}
      </v-label>
      <v-text-field
          :disabled="readOnly"
          v-model="createSlotForm.resName"
          outlined
          dense
          class="mb-4"
          variant="outlined"
      ></v-text-field>
      <v-label v-if="!isSetup">
        {{ $t('slot.create') }}
      </v-label>
      <v-switch v-if="!isSetup"
          color="primary"
          inset
          v-model="createSlotForm.setupFlag"
          hide-details
          :false-value="true"
          :true-value="false"
          :disabled="isSetup"
      ></v-switch>
      <v-divider></v-divider>
      <br>
      <v-label>
        {{ $t('slot.cube') }}
      </v-label>
      <v-select :disabled="!createSlotForm.setupFlag || readOnly" :rules="setupRules" :items="cubeList" item-title="name"
                variant="outlined" item-value="id" v-model="createSlotForm.cubeId">
        <template v-slot:item="{ props: itemProps, item }">
          <v-list-item v-bind="itemProps" :subtitle="item.raw.description">

          </v-list-item>
        </template>
      </v-select>
      <v-label>
        {{ $t('slot.extensionPackage') }}
      </v-label>
      <v-select :disabled="!createSlotForm.setupFlag||createSlotForm.cubeId===''||readOnly" :rules="setupRules"
                :items="extensionPackages" item-title="name" variant="outlined" item-value="id"
                v-model="createSlotForm.exPackageId">
        <template v-slot:item="{ props: itemProps, item }">
          <v-list-item v-bind="itemProps" :subtitle="item.raw.description">

          </v-list-item>
        </template>
      </v-select>
      <v-label>
        {{ $t('slot.extensionPoint') }}
      </v-label>
      <v-select :disabled="!createSlotForm.setupFlag||createSlotForm.exPackageId===''||readOnly" :rules="setupRules"
                :items="extensionPoints" item-title="extensionName" variant="outlined" item-value="id"
                v-model="createSlotForm.exPointId">
        <template v-slot:item="{ props: itemProps, item }">
          <v-list-item v-bind="itemProps">
            <v-list-item-subtitle class="d-flex align-center">
              <v-chip x-small label class="mr-2">{{ item.raw.methodName }}()</v-chip>
              <v-chip x-small label color="indigo" text-color="white">
                {{ item.raw.returnType }}
              </v-chip>
            </v-list-item-subtitle>
          </v-list-item>
        </template>
      </v-select>

    </v-card-text>
    <v-card-actions>
      <slot name="actions"></slot>
    </v-card-actions>
  </v-form>
</template>

<style scoped lang="scss">
.v-label{
  opacity: 1;
}
</style>
