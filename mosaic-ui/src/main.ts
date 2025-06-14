// src/main.ts
import { createApp } from 'vue'

import { registerPlugins } from '@/plugins'

import '@/styles/minecraft1.scss'
import '@/styles/main.scss'
import App from './App.vue'

const app = createApp(App)

registerPlugins(app)

app.mount('#app')
