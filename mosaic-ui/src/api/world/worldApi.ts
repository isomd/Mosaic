import request from "@/utils/request";
import {CreateWorldForm} from "@/api/world/worldType";
export function createWorld (form:CreateWorldForm) {
    return request({
        url:'/',
        params: form,
        method: 'get'
    })
}
