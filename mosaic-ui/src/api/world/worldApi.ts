import request from "@/utils/request";
import type {CreateWorldForm,TraverseWorldForm} from "@/api/world/worldType";
export function createWorld (form:CreateWorldForm) {
    return request({
        url:'/world/create',
        data: form,
        method: 'post'
    })
}
export function getWorldList () {
    return request({
        url:'/world/getAll',
        method: 'get'
    })
}
export function traverseWorld(form:TraverseWorldForm){
    return request({
        url:'/world/traverse',
        params: form,
        method: 'get'
    })
}
export function removeWorld(form:TraverseWorldForm){
    return request({
        url:'/world/remove',
        params: form,
        method: 'get'
    })
}
export function getCurrentWorld(){
    return request({
        url:'/world/getNowWorld',
        method: 'get'
    })
}
