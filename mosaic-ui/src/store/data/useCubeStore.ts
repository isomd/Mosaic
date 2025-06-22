import { defineStore } from 'pinia'
import {type Cube,type ExtensionPackage,type ExtensionPoint} from "@/api/plugin/pluginType";
import {getCubeList} from "@/api/plugin/pluginApi";

export const useCubeStore = defineStore('cube', () => {
    let cubeMap = reactive<Map<String,Cube>>(new Map())

    const getCubes = async () => {
        try {
            const res = await getCubeList();
            const newMap = new Map<string, Cube>();
            for (const cube of res.data.cubeList) {
                newMap.set(cube.id, cube);
            }
            cubeMap = newMap
            return Array.from(newMap.values());
        } catch (error) {
            console.error( error);
            throw error;
        }
    }
    const getCubeById = (id:String)=>{
        return cubeMap.get(id)
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
    return { getCubes,getCubeById,getExPoint }
})
