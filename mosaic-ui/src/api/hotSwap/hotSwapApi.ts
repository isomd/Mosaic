import request from "@/utils/request";
import type{ CreatePointForm,GetHotSwapPointsForm} from "@/api/hotSwap/hotSwapType";
import type{RollBackHotSwapPointForm} from "@/api/hotSwap/hotSwapType";

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

export function getHotSwapPoints(form:GetHotSwapPointsForm){
    return request({
        url: '/hotSwap/getHotSwapPoints',
        method: 'get',
        params: form
    })
}
export function rollBackHotSwapPoint(form:RollBackHotSwapPointForm){
    return request({
        url: '/hotSwap/rollBackHotSwapPoint',
        method: 'post',
        params: form
    })
}
