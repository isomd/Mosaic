<script lang="ts" setup>
import {type CreateSlotForm, type Slot} from "@/api/slot/slotType";
import {defineProps, defineEmits} from "vue";
import {useCubeStore} from "@/store/data/useCubeStore";
import {type Cube, type ExtensionPackage, type ExtensionPoint} from "@/api/plugin/pluginType";
import {createOrSetup} from "@/api/slot/slotApi";
import {useI18n} from 'vue-i18n'
import router from "@/router";

const {t} = useI18n()
const emit = defineEmits(['input'])
const cubeStore = useCubeStore()
const props = defineProps({
  value: Boolean,
  slot: {
    default: null
  }
})
const form = ref(null)
let cubeList = reactive<Cube[]>([])
let createSlotForm = reactive<CreateSlotForm>({
  slotId: '',
  setupFlag: false,
  cubeId: '',
  exPackageId: '',
  exPointId: '',
  resName: '',
})
const dialog = computed({
  get() {
    return props.value
  },
  set(val) {
    emit('input', val)
  }
})
const isSetup = ref(false)
onMounted(() => {
  if (props.slot) {
    createSlotForm = props.slot
    createSlotForm.setupFlag = true
    isSetup.value = true
  }
  getCubeListFunc()
})
const getCubeListFunc = () => {
  cubeStore.getCubes().then((res: Cube[]) => {
    cubeList = res
  })
}
const extensionPackages = computed(() => {
  if (!createSlotForm.cubeId || createSlotForm.cubeId == '') return [] as ExtensionPackage[]
  let exPackages: ExtensionPackage[] = cubeStore.getCubeById(createSlotForm.cubeId).extensionPackages as ExtensionPackage[]
  createSlotForm.exPackageId = exPackages[0].id
  return exPackages
})
const extensionPoints = computed(() => {
  if (!createSlotForm.exPackageId || createSlotForm.exPackageId == '') return [] as ExtensionPoint[]
  let exPoints: ExtensionPoint[] = extensionPackages.value.find(exPackage => exPackage.id == createSlotForm.exPackageId)
      .extensionPoints
  createSlotForm.exPointId = exPoints[0].id
  return exPoints
})
const rules = [
  value => {
    if (value) return true
    return t('slot.rule.empty')
  },
]
const setupRules = [
  value => {
    if (!createSlotForm.setupFlag) return true
    if (value) return true
    return t('slot.rule.empty')
  },
]
const handleSubmit = async () => {
  const {valid} = await form.value.validate();
  if (!valid) return
  createOrSetup(createSlotForm).then((res: any) => {
    if (res.code == 200) {
      //TODO: message
      router.go(0)
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
              v-model="createSlotForm.resName"
              outlined
              dense
              class="mb-4"
              variant="outlined"
          ></v-text-field>
          <v-label>
            {{ $t('slot.create') }}
          </v-label>
          <v-switch
              color="primary"
              inset
              v-model="createSlotForm.setupFlag"
              hide-details
              :false-value="true"
              :true-value="false"
              :disabled="isSetup"
          ></v-switch>
          <br>
          <v-divider></v-divider>
          <br>
          <v-label>
            {{ $t('slot.cube') }}
          </v-label>
          <v-select :disabled="!createSlotForm.setupFlag" :rules="setupRules" :items="cubeList" item-title="name"
                    variant="outlined" item-value="id" v-model="createSlotForm.cubeId">
            <template v-slot:item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" :subtitle="item.raw.description">

              </v-list-item>
            </template>
          </v-select>
          <v-label>
            {{ $t('slot.extensionPackage') }}
          </v-label>
          <v-select :disabled="!createSlotForm.setupFlag||createSlotForm.cubeId===''" :rules="setupRules"
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
          <v-select :disabled="!createSlotForm.setupFlag||createSlotForm.exPackageId===''" :rules="setupRules"
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
          <v-btn type="submit">{{ $t('slot.save') }}</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>
<style lang="scss" scoped>

</style>
