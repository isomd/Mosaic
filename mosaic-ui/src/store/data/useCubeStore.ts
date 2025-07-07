import { defineStore } from 'pinia'
import {type Cube,type ExtensionPackage,type ExtensionPoint} from "@/api/plugin/pluginType";
import {getCubeList} from "@/api/plugin/pluginApi";
import {useSlotStore} from "./useSlotStore";
import {list2Map} from "@/utils/dataUtils";

export const useCubeStore = defineStore('cube', () => {
    const cubeList = ref<Cube[]>([])
    const slotStore = useSlotStore()
    const initFromLocalStorage = () => {
        const saved = localStorage.getItem('cubeList')
        if (saved) {
            try {
                cubeList.value = JSON.parse(saved) as Cube[]
            } catch (e) {
                console.error(e)
            }
        }
    }
    initFromLocalStorage()
    const getCubes = async () => {
        try {
            const res = await getCubeList();
            const list = res.data.cubeList
            cubeList.value = list
            localStorage.setItem('cubeList',JSON.stringify(list))
            return list;
        } catch (error) {
            console.error( error);
            throw error;
        }
    }
    const getCubeById = (id:String)=>{
        return list2Map<Cube>(cubeList.value,"id").get(id)
    }
    const getExPointBySlotId = (slotId:String)=>{
        let slot = slotStore.getSlotById(slotId)
        if(!slot) return null
        return getExPoint(slot.cubeId,slot.exPackageId,slot.exPointId)
    }
    const getExPoint = (cubeId:String,exPackageId:String,exPointId:String) =>{
        if(!cubeId||cubeId == '')return null
        let cube:Cube = getCubeById(cubeId)
        if(!cube)return null
        let exPackage:ExtensionPackage = cube.extensionPackages.find(exPackage=>exPackage.id == exPackageId)
        if(!exPackage)return null
        let exPoint:ExtensionPoint = exPackage.extensionPoints.find(exPoint=>exPoint.id==exPointId)
        return exPoint
    }
    return { getCubes,getExPointBySlotId,getCubeById,getExPoint,cubeList }
})
