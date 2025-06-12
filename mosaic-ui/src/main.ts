// src/main.ts
import { createApp } from 'vue'
import { createI18n } from 'vue-i18n'
import { createPinia } from 'pinia'

import { registerPlugins } from '@/plugins'

import App from './App.vue'

const app = createApp(App)

registerPlugins(app)

app.mount('#app')
