import { defineStore } from 'pinia'
import { ref } from 'vue'
import {type World} from "@/api/world/worldType";

export const useStatusStore = defineStore('status', () => {
    const showSidebar = ref<boolean>(localStorage.getItem('showSidebar') == 'true')
    const showWorldPanel = ref<boolean>(false)
    const currentWorld = ref<World>()
    const setShowSidebar = (value:boolean) => {
        showSidebar.value = value
        localStorage.setItem('showSidebar', (value as string))
    }
    const setShowWorldPanel = (value:boolean)=>{
        showWorldPanel.value = value
    }
    const setCurrentWorld = (value:World)=>{
        currentWorld.value = value
    }
    return { showSidebar,setShowSidebar,showWorldPanel,setShowWorldPanel,setCurrentWorld,currentWorld }
})
