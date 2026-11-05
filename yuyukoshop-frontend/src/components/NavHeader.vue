<template>
  <header class="cyber-nav">
    <!-- 背景装饰 -->
    <div class="nav-bg">
      <div class="nav-grid"></div>
      <div class="nav-glow"></div>
    </div>

    <div class="nav-container">
      <!-- Logo -->
      <ShopLogo />

      <!-- 导航链接 -->
      <nav class="nav-links">
        <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
          <span class="link-text">首页</span>
          <span class="link-en">HOME</span>
        </router-link>
        <router-link to="/products" class="nav-link" :class="{ active: $route.path.startsWith('/products') }">
          <span class="link-text">商城</span>
          <span class="link-en">SHOP</span>
        </router-link>
        <router-link to="/community" class="nav-link" :class="{ active: $route.path.startsWith('/community') }">
          <span class="link-text">社区</span>
          <span class="link-en">COMMUNITY</span>
        </router-link>
      </nav>

      <!-- 搜索框 -->
      <div class="nav-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品..."
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <!-- 用户区域 -->
      <div class="nav-user">
        <template v-if="userStore.isLoggedIn">
          <!-- 购物车 -->
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="nav-badge">
            <el-button text class="icon-btn" @click="$router.push('/cart')">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>

          <!-- 消息 -->
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="nav-badge">
            <el-button text class="icon-btn" @click="$router.push('/messages')">
              <el-icon :size="20"><ChatDotRound /></el-icon>
            </el-button>
          </el-badge>

          <!-- 用户下拉 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar-wrap">
              <img :src="userStore.userInfo?.avatar || defaultAvatar" class="user-avatar" alt="" />
              <span class="avatar-ring"></span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="cyber-dropdown">
                <div class="dropdown-header">
                  <span class="dropdown-username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                </div>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><List /></el-icon>
                  <span>我的订单</span>
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>
                  <span>我的收藏</span>
                </el-dropdown-item>
                <el-dropdown-item command="follows">
                  <el-icon><Connection /></el-icon>
                  <span>关注/粉丝</span>
                </el-dropdown-item>
                <el-dropdown-item command="badges">
                  <el-icon><Medal /></el-icon>
                  <span>勋章墙</span>
                </el-dropdown-item>
                <el-dropdown-item command="messages">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>私信</span>
                </el-dropdown-item>
                <el-dropdown-item command="cart">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>购物车</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button text class="login-btn" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" class="register-btn" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>

    <!-- 底部装饰线 -->
    <div class="nav-line"></div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ShopLogo from './ShopLogo.vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const cartCount = ref(0)
const unreadCount = ref(0)
const defaultAvatar = 'https://via.placeholder.com/40?text=User'

onMounted(async () => {
  if (userStore.isLoggedIn) {
    await Promise.all([fetchCartCount(), fetchUnreadCount()])
  }
})

const fetchCartCount = async () => {
  try {
    const res = await request.get('/cart/count')
    cartCount.value = res.data || 0
  } catch (e) {
    console.error(e)
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await request.get('/messages/unread-count')
    unreadCount.value = res.data || 0
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/products?keyword=${encodeURIComponent(searchKeyword.value.trim())}`)
  }
}

const handleCommand = (command) => {
  const routeMap = {
    profile: '/profile',
    orders: '/orders',
    favorites: '/favorites',
    follows: '/follows',
    badges: '/badges',
    messages: '/messages',
    cart: '/cart'
  }

  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (routeMap[command]) {
    router.push(routeMap[command])
  }
}
</script>

<style scoped>
/* ========== 导航栏主体 ========== */
.cyber-nav {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 70px;
  background: rgba(10, 10, 15, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* ========== 背景装饰 ========== */
.nav-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.nav-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(179, 136, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(179, 136, 255, 0.03) 1px, transparent 1px);
  background-size: 30px 30px;
}

.nav-glow {
  position: absolute;
  top: -50%;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 100%;
  background: radial-gradient(ellipse, rgba(179, 136, 255, 0.1) 0%, transparent 70%);
}

/* ========== 容器 ========== */
.nav-container {
  position: relative;
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 32px;
}

/* ========== Logo ========== */
.nav-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.nav-logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 28px;
  animation: float 3s ease-in-out infinite;
}

.logo-text {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #B388FF 0%, #FF6B9D 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
}

.logo-dot {
  color: #00F5FF;
  font-size: 24px;
  font-weight: 900;
  animation: pulse-glow 2s ease-in-out infinite;
}

/* ========== 导航链接 ========== */
.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.05);
}

.nav-link.active {
  background: rgba(179, 136, 255, 0.1);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: linear-gradient(90deg, #B388FF, #FF6B9D);
  border-radius: 1px;
}

.link-text {
  font-size: 15px;
  font-weight: 500;
  color: #F0F0F5;
  letter-spacing: 1px;
}

.link-en {
  font-family: 'Orbitron', sans-serif;
  font-size: 9px;
  color: #6B7280;
  letter-spacing: 2px;
  margin-top: 2px;
}

.nav-link.active .link-text {
  color: #B388FF;
}

.nav-link.active .link-en {
  color: #9CA3AF;
}

/* ========== 搜索框 ========== */
.nav-search {
  flex: 1;
  max-width: 400px;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  box-shadow: none;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(179, 136, 255, 0.3);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #B388FF;
  box-shadow: 0 0 0 3px rgba(179, 136, 255, 0.1);
}

.search-input :deep(.el-input__inner) {
  color: #F0F0F5;
  font-size: 14px;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: #6B7280;
}

.search-icon {
  color: #6B7280;
}

/* ========== 用户区域 ========== */
.nav-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #FF6B9D, #B388FF);
  border: none;
}

.icon-btn {
  color: #9CA3AF;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.icon-btn:hover {
  color: #B388FF;
  background: rgba(179, 136, 255, 0.1);
}

/* ========== 用户头像 ========== */
.user-avatar-wrap {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.avatar-ring {
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #B388FF, #FF6B9D) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.user-avatar-wrap:hover .avatar-ring {
  opacity: 1;
}

.user-avatar-wrap:hover .user-avatar {
  border-color: rgba(179, 136, 255, 0.3);
}

/* ========== 登录/注册按钮 ========== */
.login-btn {
  color: #9CA3AF;
  font-size: 14px;
  padding: 8px 16px;
}

.login-btn:hover {
  color: #B388FF;
}

.register-btn {
  background: linear-gradient(135deg, #B388FF, #FF6B9D);
  border: none;
  border-radius: 20px;
  font-size: 14px;
  padding: 10px 24px;
  letter-spacing: 1px;
}

.register-btn:hover {
  box-shadow: 0 0 20px rgba(179, 136, 255, 0.4);
  transform: translateY(-1px);
}

/* ========== 下拉菜单 ========== */
.cyber-dropdown {
  background: rgba(18, 18, 26, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 8px;
  min-width: 180px;
}

.dropdown-header {
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  margin-bottom: 8px;
}

.dropdown-username {
  font-size: 14px;
  font-weight: 600;
  color: #F0F0F5;
}

.cyber-dropdown :deep(.el-dropdown-menu__item) {
  color: #9CA3AF;
  padding: 10px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.cyber-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(179, 136, 255, 0.1);
  color: #B388FF;
}

.cyber-dropdown :deep(.el-dropdown-menu__item.is-divided) {
  margin-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 16px;
}

/* ========== 底部装饰线 ========== */
.nav-line {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(179, 136, 255, 0.3), rgba(0, 245, 255, 0.3), rgba(255, 107, 157, 0.3), transparent);
}

/* ========== 动画 ========== */
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 1; text-shadow: 0 0 10px #00F5FF; }
  50% { opacity: 0.7; text-shadow: 0 0 20px #00F5FF, 0 0 30px #00F5FF; }
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .nav-links {
    display: none;
  }

  .nav-search {
    max-width: 300px;
  }
}

@media (max-width: 768px) {
  .nav-container {
    gap: 16px;
    padding: 0 16px;
  }

  .logo-text {
    display: none;
  }

  .nav-search {
    flex: 1;
    max-width: none;
  }
}
</style>
