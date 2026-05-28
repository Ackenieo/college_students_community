import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/feed'
  },
  {
    path: '/feed',
    name: 'Feed',
    component: () => import('@/pages/feed/feed.vue')
  },
  {
    path: '/detail/:id',
    name: 'Detail',
    component: () => import('@/pages/detail/detail.vue')
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/pages/publish/publish.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('@/pages/messages/messages.vue')
  },
  {
    path: '/auth',
    name: 'Auth',
    component: () => import('@/pages/auth/auth.vue')
  },
  {
    path: '/shopping',
    name: 'Shopping',
    component: () => import('@/pages/shopping/shopping.vue')
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})
