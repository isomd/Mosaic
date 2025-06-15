import request from "@/utils/request";
import {type GetCubeListForm} from "./pluginType";

export function getCubeList(form:GetCubeListForm){
    return request({
        url: '/plugin/getPluginList',
        params: form, //requestParams
        // data: form, // requestBody
        method: 'get'
    })
}
export function uploadPluginJar(file:File){
    const formData = new FormData();
    formData.append('file', file);
    return request({
        method:'post',
        url:'/jar/uploadJar',
        data:formData,
        timeout: 100000
    })
}
