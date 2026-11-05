import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  { path: '/products', name: 'ProductList', component: () => import('@/views/ProductList.vue') },
  { path: '/products/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue') },
  { path: '/cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { requireAuth: true } },
  { path: '/orders', name: 'OrderList', component: () => import('@/views/OrderList.vue'), meta: { requireAuth: true } },
  { path: '/orders/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue'), meta: { requireAuth: true } },
  { path: '/orders/:id/pay', name: 'Pay', component: () => import('@/views/Pay.vue'), meta: { requireAuth: true } },
  { path: '/community', name: 'PostList', component: () => import('@/views/PostList.vue') },
  { path: '/community/posts/:id', name: 'PostDetail', component: () => import('@/views/PostDetail.vue') },
  { path: '/community/new-post', name: 'NewPost', component: () => import('@/views/NewPost.vue'), meta: { requireAuth: true } },
  { path: '/profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { requireAuth: true } },
  { path: '/follows', name: 'FollowList', component: () => import('@/views/FollowList.vue'), meta: { requireAuth: true } },
  { path: '/badges', name: 'BadgeWall', component: () => import('@/views/BadgeWall.vue'), meta: { requireAuth: true } },
  { path: '/messages', name: 'MessageList', component: () => import('@/views/MessageList.vue'), meta: { requireAuth: true } },
  { path: '/messages/chat/:targetId', name: 'Chat', component: () => import('@/views/Chat.vue'), meta: { requireAuth: true } },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/AdminDashboard.vue') },
      { path: 'products', name: 'AdminProducts', component: () => import('@/views/admin/AdminProducts.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/AdminOrders.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/AdminUsers.vue') },
      { path: 'banners', name: 'AdminBanners', component: () => import('@/views/admin/AdminBanners.vue') },
      { path: 'posts', name: 'AdminPosts', component: () => import('@/views/admin/AdminPosts.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requireAuth && !userStore.isLoggedIn) { next('/login'); return }
  if (to.meta.requireAdmin && !userStore.isAdmin) { next('/'); return }
  next()
})

export default router


