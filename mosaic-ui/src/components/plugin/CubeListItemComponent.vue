<script lang="ts" setup>
import {defineProps} from "vue";
import {type Cube} from "../../api/plugin/pluginType";

const props = defineProps({
  cube: {
    default: {
      cubeName:'',
      description:'',
      version:'',
      author:'',
      cubeId:''
    } as Cube
  }
})
onMounted(()=>{
  console.log(props.cube)
})
</script>
<template>
  <v-card width="100%"
          class=" jar-card">
    <template v-slot:title>
      <span class="cubeName">
        {{props.cube.cubeName}}
      </span>
      <span class="version">
        {{props.cube.version}}
      </span>
    </template>
    <template v-slot:text>
      {{props.cube.description}}
    </template>
    <v-card-actions>
      <v-btn color="black" :text="$t('plugins.details')"></v-btn>
      <v-btn color="black" :text="$t('plugins.remove')"></v-btn>
      <v-spacer></v-spacer>
      <span>
        <p class="id">
        {{`ID: ${props.cube.cubeId}`}}
      </p>
      <p class="id">
        {{`${$t('plugins.author')}: ${props.cube.author}`}}
      </p>
      </span>
    </v-card-actions>
  </v-card>
</template>
<style scoped lang="scss">
.cubeName{
  font-size: 0.8rem;
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
