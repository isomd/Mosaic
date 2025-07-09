import { defineStore } from 'pinia'
import { ref } from 'vue'
import {useI18n} from 'vue-i18n'
export const useLocaleStore = defineStore('locale', () => {
    const i18n  = useI18n()

    const currentLocale = ref(localStorage.getItem('locale'))

    const setLocale = (locale) => {
        currentLocale.value = locale
        i18n.locale.value = locale
        localStorage.setItem('locale', locale)
    }

    return { currentLocale, setLocale }
})
