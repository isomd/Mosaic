import axios from 'axios'
import {useStatusStore} from "@/store/useStatusStore";
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
    return res
}, (error) => {
    statusStore.setLoading(false)
    return Promise.reject(error)
})

export default request
