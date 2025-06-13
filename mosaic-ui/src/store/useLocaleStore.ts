import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLocaleStore = defineStore('locale', () => {
    const currentLocale = ref(localStorage.getItem('locale'))

    const setLocale = (locale) => {
        currentLocale.value = locale
        localStorage.setItem('locale', locale)
    }

    return { currentLocale, setLocale }
})
