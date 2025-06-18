import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Pages from 'vite-plugin-pages'

import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import monacoEditorEsmPlugin from 'vite-plugin-monaco-editor-esm'

import { fileURLToPath, URL } from 'node:url'
// https://vite.dev/config/
export default defineConfig({
  plugins: [
    Pages({
      dirs: 'src/pages',
    }),
    AutoImport({
      imports: [
        'vue',
        'pinia',
        {
          'vue-router/auto': ['useRoute', 'useRouter'],
        },
      ],
      dts: 'src/auto-imports.d.ts',
      eslintrc: {
        enabled: true,
      },
      vueTemplate: true,
    }),
    Components({
      dirs: ['src/components'],
      extensions: ['vue'],
      dts: 'src/components.d.ts',
    }),
    vue(),
    monacoEditorEsmPlugin(<any>{
      languageWorkers: ['editorWorkerService'],
      globalAPI: true,
      customLanguages: [{
        label: 'java',
        entry: 'monaco-editor/esm/vs/basic-languages/java/java.contribution.js'
      }],
      disableBuiltinLanguages: true,
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
    extensions: [
      '.js',
      '.json',
      '.jsx',
      '.mjs',
      '.ts',
      '.tsx',
      '.vue',
    ],
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          monaco: ['monaco-editor']
        }
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      "/mosaic":{
        target: "http://localhost:9000/",
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, "/"),
      },
    },
  },
})
