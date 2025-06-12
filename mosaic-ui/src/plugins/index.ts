import vuetify from './vuetify'
import router from '../router'
import pinia from "./pinia";
import i18n from './i18n'
// Types
import type { App } from 'vue'

export function registerPlugins (app: App) {
    app
        .use(vuetify)
        .use(i18n)
        .use(router)
        .use(pinia)
}
