import {defineStore} from 'pinia'
import {type Slot} from '@/api/slot/slotType'
import {getSlotList} from "@/api/slot/slotApi";
import {list2Map} from "@/utils/dataUtils";

export const useSlotStore = defineStore('slot', () => {
    let slotList = ref<Slot[]>([])
    const initFromLocalStorage = () => {
        const saved = localStorage.getItem('slotList')
        if (saved) {
            try {
                slotList.value = JSON.parse(saved) as Slot[]
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
            slotList.value = list
            localStorage.setItem('slotList',JSON.stringify(list))
            return list;
        } catch (error) {
            console.error( error);
            throw error;
        }
    }
    const getSlotById = (id:String)=>{
        return list2Map<Slot>(slotList.value, "slotId").get(id)
    }

    return { getSlots,getSlotById,slotList }
})
