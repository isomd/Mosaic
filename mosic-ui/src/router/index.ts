
import { createRouter, createWebHistory } from 'vue-router/auto'
import routes from '~pages'

const router = createRouter({
  history: createWebHistory(),
  routes,
})
export default router
