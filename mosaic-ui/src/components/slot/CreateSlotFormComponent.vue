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
  if (props.slot&&props.slot.slotId) {
    createSlotForm.value = JSON.parse(JSON.stringify(props.slot))
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
  resName: 'default',
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
  // if(createSlotForm.value.exPackageId==='')createSlotForm.value.exPackageId = exPackages[0]?.id
  return exPackages
})
const extensionPoints = computed(() => {
  if (!createSlotForm.value.exPackageId || createSlotForm.value.exPackageId == '') {
    createSlotForm.value.exPointId = ''
    return [] as ExtensionPoint[]
  }
  let exPoints: ExtensionPoint[] = extensionPackages.value.find((exPackage) => exPackage.id == createSlotForm.value.exPackageId)?.extensionPoints
  // if(createSlotForm.value.exPointId==='')createSlotForm.value.exPointId = exPoints[0]?.id

  return exPoints
})
const form = ref(null)
const handleSubmit = async () => {
  const {valid} = await form.value.validate();
  if (!valid) return
  emit('submitValue',createSlotForm.value)
}

const updateCube = (val)=>{
  createSlotForm.value.cubeId = val.id
  updateExPackage(val.extensionPackages[0])
}
const updateExPackage = (val)=>{
  createSlotForm.value.exPackageId = val?.id
  updateExPoint(val?.extensionPoints[0])
}
const updateExPoint = (val)=>{
  createSlotForm.value.exPointId = val?.id
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
          <v-list-item v-bind="itemProps" :subtitle="item.raw.description" @click="updateCube(item.raw)">

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
          <v-list-item v-bind="itemProps" :subtitle="item.raw.description" @click="updateExPackage(item.raw)">

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
      <v-label>
        {{ $t('slot.resName') }}
      </v-label>
      <v-select :disabled="readOnly" :items="extensionPoints?.filter(exPoint=>exPoint.id === createSlotForm.exPointId)[0]?.pointResult?.pointItems"
                variant="outlined" v-model="createSlotForm.resName" item-title="itemName">
        <template v-slot:item="{props:itemProps,item,index}">
          <v-list-item v-bind="itemProps"
                       :value="item.raw">
            <template #title><span>{{ item.raw.itemName }}  </span>
              <v-chip>{{ item.raw.itemClass }}</v-chip>
            </template>
            <template #subtitle>{{ item.raw.description }}</template>
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
