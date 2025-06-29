import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {World} from "@/api/world/worldType";
import {getWorldList} from "@/api/world/worldApi";

export const useWorldStore = defineStore('world', () => {
    const worldList = ref<World[]>([])
    const getWorlds = async ()=>{
        try {
            worldList.value = (await getWorldList()).data
            return worldList.value
        } catch (error) {
            console.error( error);
            throw error;
        }
    }
    const clear = () => {
        worldList.value = []
    }
    return {worldList,getWorlds,clear}
})
