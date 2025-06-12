// src/main.ts
import { createApp } from 'vue'
import App from './App.vue'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'

// 自定义Minecraft样式
import './styles/minecraft1.scss'

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
        sets: { mdi },
    },
    theme: {
        defaultTheme: 'minecraftTheme',
        themes: {
            minecraftTheme: {
                dark: false,
                colors: {
                    primary: '#8B4513',      // 橡木棕
                    secondary: '#228B22',    // 森林绿
                    accent: '#FFD700',       // 金锭色
                    error: '#DC143C',        // 红石红
                    info: '#4682B4',         // 青金石蓝
                    success: '#32CD32',      // 翠绿色
                    warning: '#FF8C00',      // 岩浆橙
                    surface: '#F5DEB3',      // 沙石色
                    background: '#87CEEB',   // 天空蓝
                    'on-primary': '#FFFFFF',
                    'on-secondary': '#FFFFFF',
                    'on-surface': '#2F2F2F',
                    'on-background': '#2F2F2F',
                },
            },
        },
    },
})

createApp(App).use(vuetify).mount('#app')