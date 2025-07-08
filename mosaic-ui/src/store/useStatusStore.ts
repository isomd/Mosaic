import { defineStore } from 'pinia'
import { ref } from 'vue'
import {type World} from "@/api/world/worldType";
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
export const useStatusStore = defineStore('status', () => {
    const slotStore = useSlotStore()
    const cubeStore = useCubeStore()
    const showSidebar = ref<boolean>(localStorage.getItem('showSidebar') == 'true')
    const showWorldPanel = ref<boolean>(false)
    const currentWorld = ref<World>()
    const recentClassName = ref<string>(localStorage.getItem('recentClassName')||'')
    const loading = ref<boolean>(false)
    const setShowSidebar = (value:boolean) => {
        showSidebar.value = value
        localStorage.setItem('showSidebar', (value as string))
    }
    const setShowWorldPanel = (value:boolean)=>{
        showWorldPanel.value = value
    }
    const setCurrentWorld = (value:World)=>{
        setShowWorldPanel(false)
        setLoading(true)
        currentWorld.value = value
        slotStore.getSlots().then(()=>{
            cubeStore.getCubes().then(()=>{
                setLoading(false)
            })
        })

    }
    const getRecentClassName = () => {
        return recentClassName.value
    }
    const setRecentClassName = (className:string)=>{
        localStorage.setItem('recentClassName',className)
        recentClassName.value = className
    }
    const getLoading = ()=>{
        return loading.value
    }
    const setLoading = (val:boolean)=>{
        loading.value = val
    }
    return { showSidebar,setShowSidebar,showWorldPanel,setShowWorldPanel,setCurrentWorld,currentWorld,getRecentClassName,setRecentClassName,getLoading,setLoading }
})
