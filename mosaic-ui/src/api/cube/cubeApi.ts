import request from "@/utils/request";
import {GetCubeListForm} from "@/api/cube/cubeType";

export function getCubeList(form:GetCubeListForm){
    return request({
        url: '/cube/getCubeList',
        params: form, //requestParams
        // data: form, // requestBody
        method: 'get'
    })
}
