<script lang="ts" setup>
import {uploadPluginJar,getCubeList} from "@/api/plugin/pluginApi";
import {type Cube} from "@/api/plugin/pluginType";
const uploadRef = ref()
const upload = (item) => {
  uploadPluginJar(item.target.files[0]).then((res:any)=>{
    if(res.code == 200) {

    } else {
      //
    }
  })
}

const cubeList = ref<Cube[]>([])
const getCubeListFunction = () => {
  // getCubeList().then((res:any)=>{
  //   if(res.code == 200) {
  //     // cubeList.value = res.data
  //
  //   } else {
  //     //
  //   }
  // })
  new Promise((resolve => {
    setTimeout(()=>{
      resolve({
        code: 200, // 添加 code 字段
        data: [    // 将数组包装在 data 字段中
          {
            cubeName: 'TestCube',
            description: '[vite] hot updated: /src/components/plugin/CubeListItemComponent.vue?vue&type=style&index=0&scoped=bb932527&lang.scss\n',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '21331232'
          },
          {
            cubeName: 'TestCube',
            description: '[vite] hot updated: /src/components/plugin/CubeListItemComponent.vue\n',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '31331232'
          },
          {
            cubeName: 'TestCube',
            description: 'Proxy(Object) {cubeName: \'TestCube\', description: \'[vite] hot updated: /src/components/plugin/CubeLis…vue&type=style&index=0&scoped=bb932527&lang.scss\\n\', version: \'1.20.1\', author: \'dhx\', cubeId: \'21331232\'}',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '21331232'
          },
          {
            cubeName: 'TestCube',
            description: 'a test cube',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '21331232'
          },
          {
            cubeName: 'TestCube',
            description: 'a test cube',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '21331232'
          },
          {
            cubeName: 'TestCube',
            description: 'a test cube',
            version: '1.20.1',
            author: 'dhx',
            cubeId: '21331232'
          }
        ]
      });
    },300)
  })).then((res:any)=>{
    if(res.code == 200) {
      cubeList.value = res.data
    }
  })
}
onMounted(()=>{
  getCubeListFunction()
})
</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.plugins')}}</h1>
  </div>
  <div class="operation">
    <v-btn
        color="primary" small @click="uploadRef.click()">
      {{$t("plugins.upload")}}
      <template v-slot:prepend>
        <v-icon>mdi-upload</v-icon>
      </template>
    </v-btn>
  </div>
  <ul class="cube-list">
    <li class="cube-list-item" v-for="cube in cubeList" :key="cube.cubeId">
      <CubeListItemComponent :cube="cube"></CubeListItemComponent>
    </li>
  </ul>
  <input type="file" accept=".jar" style="visibility: hidden" ref="uploadRef" @input="upload">

</template>
<style scoped lang="scss">
.cube-list {
  width: 100%;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 24px;
  row-gap: 24px;
  .cube-list-item{
    width: 30%;
    cursor: pointer;
  }
}
.upload-btn{
  position: relative;
  width: 90%;
  height: 48px;
  left: 50%;
  transform: translate(-50%);
}
.operation{
  width: 100%;
  display: flex;
  justify-content: end;
  padding: 16px 48px;
}
</style>
