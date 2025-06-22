import request from "@/utils/request";
import {type CreateSlotForm} from "@/api/slot/slotType";

export function createOrSetup(form:CreateSlotForm){
    return request({
        url: '/slot/createOrSetup',
        method: 'post',
        data: form
    })
}

export function getSlotList(){
    return request({
        url: '/slot/list',
        method: 'get'
    })
}

export function deleteSlot(form:String){
    return request({
        url: '/slot/delete',
        method: 'get',
        params: {
            slotId: form
        }
    })
}
export function unSetup(form:String){
    return request({
        url: '/slot/unSetup',
        method: 'get',
        params: {
            slotId: form
        }
    })
}
