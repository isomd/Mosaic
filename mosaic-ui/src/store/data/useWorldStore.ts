import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWorldStore = defineStore('world', () => {
    const worldList = ref(JSON.parse(localStorage.getItem("worldList")||'[]'))
    return {worldList}
})
