import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
    const currentTheme = ref(localStorage.getItem('currentTheme')||'bright')
    const theme = ref({
        'bright': {
            borderColor: '#D1D9E0',
            fontColor: '#FFFFFF',
        },
        'dark': {
            borderColor: '#D1D9E0',
            fontColor: '#FFFFFF',
        }
    })
    const getTheme = () => {
        return theme.value[currentTheme.value]
    }
    return { currentTheme,getTheme }
})
