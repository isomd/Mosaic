import request from "@/utils/request";
import {type CreatePointForm} from "@/api/hotSwap/hotSwapType";

export function getClassStr(form:String){
    return request({
        url: '/hotSwap/getClassStr',
        method: 'get',
        params: {
            className: form
        }
    })
}

export function createPoint(form:CreatePointForm){
    return request({
        url: '/hotSwap/create/point',
        method: 'post',
        data:form
    })
}

