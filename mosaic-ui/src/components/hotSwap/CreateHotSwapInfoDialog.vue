<script lang="ts" setup>
import {defineEmits, defineProps, nextTick} from "vue";
import {type Slot} from '@/api/slot/slotType'
import {createPoint} from "@/api/hotSwap/hotSwapApi";
import {type CreatePointForm} from "@/api/hotSwap/hotSwapType";
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
import {useStatusStore} from "@/store/useStatusStore";
import type {ExtensionPackage, ExtensionPoint} from "@/api/plugin/pluginType";
const statusStore = useStatusStore()

const cubeStore = useCubeStore()
const slotStore = useSlotStore()
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


const handelSave = () => {
  dialog.value = false
  statusStore.setLoading(true)
  createPoint(createPointForm.value).then((res: any) => {
    if (res.code == 200) {
      emit('updateCode', res.data.code)
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
const createSlot = ref<boolean>(false)
const handleSlotChange = (newSlot) => {
  if(newSlot.slotId === 'yqdsmyszmdxz'){
    createSlot.value = true
    createPointForm.value = {
      slotId: '',
      cubeId: "",
      exPackageId: "",
      exPointId: "",
      resName: "",
      className: '',
      targetLine: 0,
      changeType: 'INSERT_AFTER',
      args: []
    }
    return
  }
  createSlot.value = false
  createPointForm.value = {
    ...createPointForm.value,
    ...newSlot
  }
}

const combobox = ref()
const createSlotRef = ref()
const handleSubmit = (form) => {
  createPointForm.value = {
    ...createPointForm.value,
    ...form
  }
  handelSave()
}
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
  let exPackages: ExtensionPackage[] = cubeStore.getCubeById(form.value.cubeId).extensionPackages as ExtensionPackage[]
  form.value.exPackageId = exPackages[0]?.id
  return exPackages
})
const extensionPoints = computed(() => {
  let form = createPointForm
  if (!form.value.exPackageId || form.value.exPackageId == '') {
    form.value.exPointId = ''
    return [] as ExtensionPoint[]
  }
  let exPoints: ExtensionPoint[] = extensionPackages.value.find((exPackage) => exPackage.id == form.value.exPackageId)
      .extensionPoints
  form.value.exPointId = exPoints[0]?.id
  return exPoints
})
const closeChip = (index) =>{
  createPointForm.value.args.splice(index,1)
}
</script>
<template>
  <v-dialog class="hotSwap" v-model="dialog" max-width="480">
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
            <v-select :items="slotList"
                      variant="outlined"
                      :model-value="createPointForm.slotId"
                      @update:modelValue="handleSlotChange">
              <template v-slot:item="{props:itemProps,item,index}">
                <v-list-item v-bind="itemProps" :title="''" :key="item.id" v-if="item.raw.slotId === 'yqdsmyszmdxz'">
                  <template #title>{{ $t('hotSwap.createSlot') }}</template>
                </v-list-item>
                <v-list-item v-bind="itemProps" :title="''" :key="index" v-else>
                  <template #title>{{ cubeStore.getExPointBySlotId(item.raw.slotId)?.extensionName||item.raw.slotId }}</template>
                  <template #subtitle>{{ cubeStore.getExPointBySlotId(item.raw.slotId)?.description||$t('hotSwap.emptySlot') }}</template>
                </v-list-item>
              </template>
            </v-select>
          </v-list-item-subtitle>
<!--          <v-list-item v-if="createPointForm.slotId">-->
<!--            <v-list-item-title>-->
<!--              {{ $t('hotSwap.plugin') }}:-->
<!--            </v-list-item-title>-->
<!--            <v-list-item-subtitle>-->
<!--              {{ createPointForm.cubeId }}-->
<!--            </v-list-item-subtitle>-->
<!--            <v-list-item-title>-->
<!--              {{ $t('hotSwap.exPackage') }}:-->
<!--            </v-list-item-title>-->
<!--            <v-list-item-subtitle>-->
<!--              {{ createPointForm.exPackageId }}-->
<!--            </v-list-item-subtitle>-->
<!--            <v-list-item-title>-->
<!--              {{ $t('hotSwap.exPoint') }}:-->
<!--            </v-list-item-title>-->
<!--            <v-list-item-subtitle>-->
<!--              {{ createPointForm.exPointId }}-->
<!--            </v-list-item-subtitle>-->
<!--            <v-list-item-title>-->
<!--              {{ $t('hotSwap.parameterTypes') }}:-->
<!--            </v-list-item-title>-->
<!--            <v-list-item-action>-->
<!--              <v-chip-group>-->
<!--                <v-chip v-for="type in cubeStore.getExPointBySlotId(createPointForm.slotId)?.parameterTypes"><span-->
<!--                    style="color:#CC7731">{{ type }}</span></v-chip>-->
<!--              </v-chip-group>-->
<!--            </v-list-item-action>-->
<!--          </v-list-item>-->
        </v-list-item>
        <CreateSlotFormComponent @submitValue="handleSubmit" ref="createSlotRef" :key="createPointForm" :slot="createPointForm" :create="true"></CreateSlotFormComponent>
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
            >
              <template v-slot:append>
                {{`${createPointForm.args.length}/${extensionPoints.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.parameterTypes.length||0}`}}
              </template>
              <template v-slot:item="{ props:itemProps, item }">
                <v-list-item v-bind="itemProps" class="selection">
                </v-list-item>
              </template>
              <template v-slot:chip="{ props:chipProps, item,index }">
                <v-chip @click:close="closeChip(index)">
                  <strong>{{ item.raw }}</strong>&nbsp;&nbsp;
                  <span style="color: #CC7731">({{
                      extensionPoints.filter(exPoint=>exPoint.id === createPointForm.exPointId)[0]?.parameterTypes[index]
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
        <v-btn @click="createSlotRef.handleSubmit()">
          保存
        </v-btn>
      </v-card-actions>
    </v-card>

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
