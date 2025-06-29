<script lang="ts" setup>
import {defineProps,defineOptions} from "vue";
defineOptions({ name: 'ConfigFormComponent' })

const props = defineProps({
  configNode: {
    default: null,
    required: true
  },
  modelValue:{

  }
})
const val = ref({})


onMounted(()=>{
  val.value = props.configNode
})

</script>
<template>
  <template v-if="props.configNode" >
    <v-list-item v-for="(value,key) in props.configNode">

      <template v-if="value && typeof value==='object'">
        <v-label>{{key}}</v-label>
        <v-list>
          <ConfigFormComponent :configNode="value" v-model="val[key]"></ConfigFormComponent>
        </v-list>
      </template>

      <ConfigInputComponent v-else :label="key" :type="typeof value" v-model="val[key]"></ConfigInputComponent>
    </v-list-item>
  </template>

</template>
<style lang="scss" scoped>
.v-list{
  background-color: transparent;
}
.v-list-item{
  border-left: #8D8881 1px solid;
}
</style>
