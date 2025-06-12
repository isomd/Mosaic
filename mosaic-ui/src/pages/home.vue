<script lang="ts"setup>
import {useLocaleStore} from "@/store/useLocaleStore";
import router from "@/router";

const local = useLocaleStore()

const language = ref(false)
const change = () => {
  if(language.value == true) {
    local.setLocale("zh")
    router.go(0)
  } else {
    local.setLocale("en")
    router.go(0)
  }
}
onMounted(()=>{
  language.value = (local.currentLocale == 'zh')
})
</script>
<template>
  <div class="home">
    {{$t('welcome')}}
    <v-switch :label="local.currentLocale" @input="change" v-model="language"></v-switch>
  </div>
</template>

<style scoped>

</style>
