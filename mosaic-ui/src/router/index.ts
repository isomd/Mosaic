
import { createRouter, createWebHashHistory } from 'vue-router/auto'
import routes from '~pages'

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
