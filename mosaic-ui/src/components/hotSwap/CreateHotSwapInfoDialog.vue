<script lang="ts" setup>
import {defineEmits, defineProps, nextTick} from "vue";
import {type Slot} from '@/api/slot/slotType'
import {createPoint} from "@/api/hotSwap/hotSwapApi";
import {type CreatePointForm} from "@/api/hotSwap/hotSwapType";
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
import {useStatusStore} from "@/store/useStatusStore";
import type {ExtensionPackage, ExtensionPoint} from "@/api/plugin/pluginType";
import {useI18n} from "vue-i18n";
const statusStore = useStatusStore()

const cubeStore = useCubeStore()
const slotStore = useSlotStore()
const {t} = useI18n()

const emit = defineEmits(['update:modelValue', 'updateCode'])
const props = defineProps({
  value: Boolean,
  targetLine: Number,
  className: String
})
const slotList = computed(()=>{
  return slotStore.slotList
})
const dialog = computed({
  get() {
    return props.value
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const changeTypeSelection = ref([
    "INSERT_AFTER",
    "REPLACE_ASSIGN_RIGHT"
])
const createPointForm = ref<CreatePointForm>({
  slotId: '',
  cubeId: "",
  exPackageId: "",
  exPointId: "",
  resName: "",
  className: '',
  targetLine: 0,
  changeType: 'INSERT_AFTER',
  args: []
})
const comboboxInput = ref<String>('')
const argsSelection = computed(() => {
  if (comboboxInput.value == '') return []
  let args = []
  if (isJavaIdentifierValid(comboboxInput.value)) {
    args.push(comboboxInput.value)
  }
  args.push(`\"${comboboxInput.value}\"`)
  return args
})
onMounted(() => {
  createPointForm.value.targetLine = props.targetLine
  createPointForm.value.className = props.className
})


const handleSave = () => {
  dialog.value = false
  statusStore.setLoading(true)
  createPoint(createPointForm.value).then((res: any) => {
    if (res.code == 200) {
      emit('updateCode', res.data.code)
      slotStore.getSlots()
      statusStore.setLoading(false)
    } else {
      statusStore.setLoading(false)
    }
  })
}

const isJavaIdentifierValid = (name: String) => {
  // 1. 检查空值
  if (!name || name.length === 0) return false;

  // 2. 首字符规则检查：字母、$ 或 _
  const firstChar = name.charAt(0);
  if (!(/[a-zA-Z]/.test(firstChar) && firstChar !== '$' && firstChar !== '_')) {
    return false;
  }

  // 3. 后续字符规则检查：字母、数字、$ 或 _
  for (let i = 1; i < name.length; i++) {
    const char = name.charAt(i);
    if (!(/[a-zA-Z0-9]/.test(char) && char !== '$' && char !== '_')) {
      return false;
    }
  }

  // 4. 关键字禁止检查
  const javaKeywords = new Set([
    'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch', 'char', 'class', 'const',
    'continue', 'default', 'do', 'double', 'else', 'enum', 'extends', 'final', 'finally', 'float',
    'for', 'goto', 'if', 'implements', 'import', 'instanceof', 'int', 'interface', 'long', 'native',
    'new', 'package', 'private', 'protected', 'public', 'return', 'short', 'static', 'strictfp', 'super',
    'switch', 'synchronized', 'this', 'throw', 'throws', 'transient', 'try', 'void', 'volatile', 'while'
  ]);

  return !javaKeywords.has(name);
}
const handleSlotChange = (value) => {
  if (typeof value === 'string') {
    createPointForm.value.slotId = value
  } else if(typeof value === 'object'){
    createPointForm.value.slotId = value.slotId
  }
}
const handleSubmit = async () => {
  const {valid} = await form.value.validate();
  if (!valid) return
  handleSave()
}
const combobox = ref()
const form = ref()
const update = (val) =>{
  if(val.length > (extensionPoints.value.filter(exPoint=>exPoint.id === createPointForm.value.exPointId)[0]?.parameterTypes.length||0)){
    return
  }
  createPointForm.value.args = val
}
const extensionPackages = computed(() => {
  let form = createPointForm
  if (!form.value.cubeId || form.value.cubeId == '') {
    form.value.exPackageId = ''
    return [] as ExtensionPackage[]
  }
  let exPackages: ExtensionPackage[] = cubeStore.getCubeById(form.value.cubeId)?.extensionPackages as ExtensionPackage[]
  return exPackages
})
const extensionPoints = computed(() => {
  let form = createPointForm
  if (!form.value.exPackageId || form.value.exPackageId == '') {
    form.value.exPointId = ''
    return [] as ExtensionPoint[]
  }
  let exPoints: ExtensionPoint[] = extensionPackages.value.find((exPackage) => exPackage.id == form.value.exPackageId)?.extensionPoints
  return exPoints
})
const closeChip = (index) =>{
  createPointForm.value.args.splice(index,1)
}
let cubeList = computed(()=>{
  return cubeStore.cubeList
})
const updateCube = (val)=>{
  createPointForm.value.cubeId = val.id
  updateExPackage(val.extensionPackages[0])
}
const updateExPackage = (val)=>{
  createPointForm.value.exPackageId = val?.id
  updateExPoint(val?.extensionPoints[0])
}
const updateExPoint = (val)=>{
  createPointForm.value.exPointId = val?.id
  createPointForm.value.resName = "default"
}
const rules = [
  value => {
    if (value) return true
    return t('slot.rule.empty')
  },
]
</script>
<template>
  <v-dialog class="hotSwap" v-model="dialog" max-width="480">
    <v-form @submit.prevent="handleSubmit" ref="form">
    <v-card style="overflow: hidden">

      <v-list>
        <v-list-item>
          <v-list-item-title>
            {{ $t('hotSwap.lineNumber') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            {{ props.targetLine }}
          </v-list-item-subtitle>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>
            {{ $t('hotSwap.slot') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-combobox :items="slotList.filter((slot)=>{return !slot.cubeId})"
                        :rules="rules"
                        variant="outlined"
                        v-model="createPointForm.slotId"
                        item-value="slotId"
                        item-title="slotId"
                        aria-autocomplete="none"
                        @update:modelValue="handleSlotChange"
            >
            </v-combobox>
          </v-list-item-subtitle>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>
            {{ $t('slot.cube') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-select :items="cubeList" item-title="name" :rules="rules"
                      variant="outlined" item-value="id" v-model="createPointForm.cubeId">
              <template v-slot:item="{ props: itemProps, item }">
                <v-list-item v-bind="itemProps" :subtitle="item.raw.description" @click="updateCube(item.raw)">

                </v-list-item>
              </template>
            </v-select>
          </v-list-item-subtitle>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>
            {{ $t('slot.extensionPackage') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-select
                :items="extensionPackages" item-title="name" variant="outlined" item-value="id" :rules="rules"
                v-model="createPointForm.exPackageId">
              <template v-slot:item="{ props: itemProps, item }">
                <v-list-item v-bind="itemProps" :subtitle="item.raw.description" @click="updateExPackage(item.raw)">

                </v-list-item>
              </template>
            </v-select>
          </v-list-item-subtitle>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>
            {{ $t('slot.extensionPoint') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-select
                :items="extensionPoints" item-title="extensionName" variant="outlined" item-value="id" :rules="rules"
                v-model="createPointForm.exPointId">
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
          </v-list-item-subtitle>
        </v-list-item>
        <v-list-item>
          <v-list-item-title>
            {{ $t('slot.resName') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-select  :items="extensionPoints?.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.pointResult?.pointItems" :rules="rules"
                       variant="outlined" v-model="createPointForm.resName" item-title="itemName">
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
          </v-list-item-subtitle>
        </v-list-item>
        <!--        <CreateSlotFormComponent @submitValue="handleSubmit" ref="createSlotRef" :key="createPointForm.slotId" :slot="createPointForm" :create="true"></CreateSlotFormComponent>-->
        <v-list-item>
          <v-list-item-title>
            {{ $t('hotSwap.args') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            <v-combobox
                :model-value="createPointForm.args"
                :items="argsSelection"
                variant="outlined"
                chips
                multiple
                closable-chips
                :value-comparator="()=>false"
                v-model:search="comboboxInput"
                aria-autocomplete="none"
                ref="combobox"
                @click:clear.stop
                @update:modelValue="update"
                :rules="[()=>createPointForm.args.length>=(extensionPoints?.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.parameterTypes.length||0)]"
            >
              <template v-slot:append>
                {{`${createPointForm.args.length}/${extensionPoints?.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.parameterTypes.length||0}`}}
              </template>
              <template v-slot:item="{ props:itemProps, item }">
                <v-list-item v-bind="itemProps" class="selection">
                </v-list-item>
              </template>
              <template v-slot:chip="{ props:chipProps, item,index }">
                <v-chip @click:close="closeChip(index)">
                  <strong>{{ item.raw }}</strong>&nbsp;&nbsp;
                  <span style="color: #CC7731">({{
                      extensionPoints?.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.parameterTypes[index]
                    }})</span>
                </v-chip>
              </template>
            </v-combobox>
          </v-list-item-subtitle>
        </v-list-item>
        <!--        <v-list-item>-->
        <!--          <v-list-item-title>-->
        <!--            {{ $t('hotSwap.resName') }}-->
        <!--          </v-list-item-title>-->
        <!--          <v-list-item-subtitle>-->

        <!--            <v-select :items="cubeStore.getExPointBySlotId(createPointForm.slotId)?.pointResult?.pointItems"-->
        <!--                      variant="outlined" v-model="createPointForm.resName" item-title="itemName">-->
        <!--              <template v-slot:item="{props:itemProps,item,index}">-->
        <!--                <v-list-item v-bind="itemProps"-->
        <!--                             :value="item.raw">-->
        <!--                  <template #title><span>{{ item.raw.itemName }}  </span>-->
        <!--                    <v-chip>{{ item.raw.itemClass }}</v-chip>-->
        <!--                  </template>-->
        <!--                  <template #subtitle>{{ item.raw.description }}</template>-->
        <!--                </v-list-item>-->
        <!--              </template>-->
        <!--            </v-select>-->
        <!--          </v-list-item-subtitle>-->
        <!--        </v-list-item>-->
        <v-list-item>
          <v-list-item-title>
            {{ $t('hotSwap.changeType') }}
          </v-list-item-title>
          <v-list-item-subtitle>

            <v-select :items="changeTypeSelection"
                      variant="outlined" v-model="createPointForm.changeType" :item-title="$t(`hotSwap.changeTypes.${createPointForm.changeType}`)">
              <template v-slot:item="{props:itemProps,item,index}">
                <v-list-item v-bind="itemProps"
                             :value="item.raw">
                  <template #title>
                    <span>{{ item.raw }}</span>
                  </template>
                </v-list-item>
              </template>
            </v-select>
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>
      <v-card-actions>
        <v-btn @click="emit('update:modelValue', false)">
          关闭
        </v-btn>
        <v-btn type="submit">
          保存
        </v-btn>
      </v-card-actions>

    </v-card>
    </v-form>
  </v-dialog>
</template>
<style lang="scss" scoped>
.v-list {
  background-color: transparent;
}

.selection {
  &:hover {
    background-color: rgba(0, 0, 0, 0.1);
  }
}

.v-list-item-title {
  margin-top: 12px;
}

.v-list-item-subtitle {
  margin-top: 6px;
}

</style>
