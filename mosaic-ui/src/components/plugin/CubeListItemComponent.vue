<script lang="ts" setup>
import {defineProps} from "vue";
import {type Cube} from "../../api/plugin/pluginType";
const dialog = ref(false)
const props = defineProps({
  cube: {
    default: {

    } as Cube
  }
})
onMounted(()=>{

})
</script>
<template>
  <v-card width="100%"
          class=" jar-card">
    <template v-slot:title>
      <span class="cubeName">
        {{props.cube.name}}
      </span>
      <span class="version">
        {{props.cube.version}}
      </span>
    </template>
    <template v-slot:text>
      {{props.cube.description}}
    </template>
    <v-card-actions>
      <v-btn color="black" :text="$t('plugins.details')" @click="dialog = true"></v-btn>
      <v-btn color="black" :text="$t('plugins.remove')"></v-btn>
      <v-spacer></v-spacer>
      <span>
        <p class="id">
        {{`ID: ${props.cube.id}`}}
      </p>
      <p class="id">
        {{`${$t('plugins.author')}: ${props.cube.author}`}}
      </p>
      </span>
    </v-card-actions>
  </v-card>
  <CubeDetailsComponent v-model="dialog" :pluginData="props.cube"></CubeDetailsComponent>
</template>
<style scoped lang="scss">
.cubeName{
  font-size: 1rem;
  text-align: left;
}
.version{
  margin-left: 8px;
  font-size: 0.5rem;
}
.id{
  font-size: 0.6rem;
}
</style>
