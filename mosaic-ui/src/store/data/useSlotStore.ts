import { defineStore } from 'pinia'
import {type Slot} from '@/api/slot/slotType'
import {getSlotList} from "@/api/slot/slotApi";
import {list2Map} from "@/utils/dataUtils";

export const useSlotStore = defineStore('slot', () => {
    let slotMap = ref<Map<String,Slot>>(new Map())
    const initFromLocalStorage = () => {
        const saved = localStorage.getItem('slotList')
        if (saved) {
            try {
                const list = JSON.parse(saved) as Slot[]
                slotMap.value = list2Map<Slot>(list, "slotId")
            } catch (e) {
                console.error(e)
            }
        }
    }
    initFromLocalStorage()
    const getSlots = async () => {
        try {
            const res = await getSlotList();
            const list = res.data.slotList
            slotMap.value = list2Map<Slot>(list,"slotId")
            localStorage.setItem('slotList',JSON.stringify(list))
            return list;
        } catch (error) {
            console.error( error);
            throw error;
        }
    }
    const getSlotById = (id:String)=>{
        return slotMap.value.get(id)
    }

    return { getSlots,getSlotById }
})
