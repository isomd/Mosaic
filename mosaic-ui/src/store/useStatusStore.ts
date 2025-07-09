import { defineStore } from 'pinia'
import { ref } from 'vue'
import {type World} from "@/api/world/worldType";
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
import {getClassStr} from "@/api/hotSwap/hotSwapApi";
import {traverseWorld} from "../api/world/worldApi";
import {useWorldStore} from "./data/useWorldStore";

export const useStatusStore = defineStore('status', () => {
    const slotStore = useSlotStore()
    const worldStore = useWorldStore()
    const cubeStore = useCubeStore()
    const showSidebar = ref<boolean>(localStorage.getItem('showSidebar') == 'true')
    const showWorldPanel = ref<boolean>(false)
    const currentWorld = ref<World>()
    const recentClassName = ref<string>(localStorage.getItem('recentClassName')||'')
    const classCode = ref<string>('')
    const loading = ref<boolean>(false)
    const getClassCode = (className:string)=>{
        return getClassStr(className).then((res:any)=>{
            setRecentClassName(className)
            classCode.value = res.message
        })
    }
    const setShowSidebar = (value:boolean) => {
        showSidebar.value = value
        localStorage.setItem('showSidebar', (value as string))
    }
    const setShowWorldPanel = (value:boolean)=>{
        showWorldPanel.value = value
    }
    const setCurrentWorld = async (value:World)=>{
        setShowWorldPanel(false)
        setLoading(true)
        try {
            await traverseWorld({uuid:value.id.uuid}).then((res:any)=>{
                if(res.code == 200){
                    currentWorld.value = value
                }
            })
            await worldStore.getWorlds()
            await slotStore.getSlots()
            await cubeStore.getCubes()
            await getClassCode(recentClassName.value)
        } catch (error) {
            console.error(error)
        } finally {
            setLoading(false)
        }

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
    return { showSidebar,setShowSidebar,showWorldPanel,setShowWorldPanel,setCurrentWorld,currentWorld,getRecentClassName,setRecentClassName,getLoading,setLoading,getClassCode,classCode }
})
