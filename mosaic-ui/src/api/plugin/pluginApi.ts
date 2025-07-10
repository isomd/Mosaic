import request from "@/utils/request";
import {type GetCubeListForm} from "./pluginType";

export function getCubeList(){
    return request({
        url: '/cube/list',
        method: 'get'
    })
}
export function uploadPluginJar(file:File){
    const formData = new FormData();
    formData.append('files', file);
    return request({
        method:'post',
        url:'/jar/batch-upload',
        data:formData,
        timeout: 100000
    })
}
export function getCubeConfiguration(form:string){
    return request({
        url: `/cube/${form}/getConfiguration`,
        method: 'get',
    })
}
export function updateCubeConfiguration(form){
    return request({
        url: '/cube/updateConfiguration',
        method: 'post',
        data:form
    })
}
