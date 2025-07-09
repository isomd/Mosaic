import axios from 'axios'
import {useStatusStore} from "@/store/useStatusStore";
import {message} from "./message";
let statusStore
const request = axios.create(<any>{
    baseURL: '/mosaic',
    timeout: 60000,
})

request.interceptors.request.use(config => {
    statusStore = useStatusStore()
    // config.headers.token = ''
    return config
}, (error) => Promise.reject(error))

request.interceptors.response.use(response => {
    const res = response.data
    if(res.code !=200){
        message.error(res.message||'Error')
    }
    return res
}, (error) => {
    statusStore.setLoading(false)
    message.error(error)
    return Promise.reject(error)
})

export default request
