import { ref } from 'vue'

export interface AlertInfo {
    id: string,
    type: string,
    message: string
}

export const newAlert = ref<AlertInfo>({
    id: 'alert' + 0,
    type: '',
    message: ''
})

export const alert = (type: string, message: string) => {
    newAlert.value.id = Math.random().toString()
    newAlert.value.type = type
    newAlert.value.message = message
}

export const message = {
    error: (message: string) => {
        alert('error', message)
    },
    success: (message: string) => {
        alert('success', message)
    },
    info: (message: string) => {
        alert('info', message)
    },
    warning: (message: string) => {
        alert('warning', message)
    }
}
