import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useStatusStore = defineStore('status', () => {
    const showSidebar = ref<boolean>(localStorage.getItem('showSidebar') == 'true')

    const setShowSidebar = (value:boolean) => {
        showSidebar.value = value
        localStorage.setItem('showSidebar', (value as string))
    }

    return { showSidebar, setShowSidebar }
})
