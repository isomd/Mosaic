import { defineStore } from 'pinia'
import {type Cube} from "@/api/plugin/pluginType";
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
    return { getCubes,getCubeById }
})
