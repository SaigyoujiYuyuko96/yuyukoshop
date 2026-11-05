<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <div class="admin-logo">
        <span class="logo-icon">&#x2740;</span>
        <span class="logo-text">幽幽子Shop</span>
        <span class="logo-sub">ADMIN</span>
      </div>
      <div class="sidebar-divider"></div>
      <el-menu :default-active="$route.path" router class="cyber-menu">
        <el-menu-item index="/admin/products" class="cyber-menu-item">
          <el-icon><Goods /></el-icon>
          <span class="menu-label">商品管理</span>
          <span class="menu-en">PRODUCTS</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders" class="cyber-menu-item">
          <el-icon><List /></el-icon>
          <span class="menu-label">订单管理</span>
          <span class="menu-en">ORDERS</span>
        </el-menu-item>
        <el-menu-item index="/admin/users" class="cyber-menu-item">
          <el-icon><User /></el-icon>
          <span class="menu-label">用户管理</span>
          <span class="menu-en">USERS</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners" class="cyber-menu-item">
          <el-icon><Picture /></el-icon>
          <span class="menu-label">轮播图管理</span>
          <span class="menu-en">BANNERS</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts" class="cyber-menu-item">
          <el-icon><Document /></el-icon>
          <span class="menu-label">帖子管理</span>
          <span class="menu-en">POSTS</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="cyber-line"></div>
        <span class="footer-text">SYSTEM v2.0</span>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="admin-header">
        <div class="header-left">
          <div class="header-decoration"></div>
          <span class="header-title">管理控制台</span>
        </div>
        <div class="header-right">
          <div class="status-indicator">
            <span class="status-dot"></span>
            <span class="status-text">ONLINE</span>
          </div>
          <div class="avatar-wrapper">
            <el-avatar :size="36" class="neon-avatar">{{ userStore.userInfo.username?.charAt(0) }}</el-avatar>
            <span class="username">{{ userStore.userInfo.username }}</span>
          </div>
          <el-button class="cyber-btn-logout" size="small" @click="logout">
            <span class="btn-icon">&#x2716;</span>
            <span>退出</span>
          </el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic:wght@400;500;700;900&family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

:root {
  --sakura: #FFB7C5;
  --neon-purple: #B388FF;
  --deep-blue: #1A1A2E;
  --cyber-blue: #16213E;
  --neon-cyan: #00F5FF;
  --neon-pink: #FF6B9D;
  --gold: #FFD700;
  --text-primary: #E8E8E8;
  --text-secondary: #9CA3AF;
  --glass-bg: rgba(255, 255, 255, 0.05);
  --glass-border: rgba(255, 255, 255, 0.1);
}

.admin-layout {
  min-height: 100vh;
  background: var(--deep-blue);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

/* ========== SIDEBAR ========== */
.admin-sidebar {
  background: linear-gradient(180deg, rgba(22, 33, 62, 0.95) 0%, rgba(26, 26, 46, 0.98) 100%);
  backdrop-filter: blur(20px);
  border-right: 1px solid var(--glass-border);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.admin-sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 2px,
      rgba(179, 136, 255, 0.03) 2px,
      rgba(179, 136, 255, 0.03) 4px
    );
  pointer-events: none;
  z-index: 0;
}

.admin-sidebar > * {
  position: relative;
  z-index: 1;
}

.admin-logo {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 16px;
  border-bottom: 1px solid var(--glass-border);
}

.logo-icon {
  font-size: 24px;
  color: var(--sakura);
  text-shadow: 0 0 10px rgba(255, 183, 197, 0.6);
  animation: pulse-glow 3s ease-in-out infinite;
}

@keyframes pulse-glow {
  0%, 100% { text-shadow: 0 0 10px rgba(255, 183, 197, 0.4); }
  50% { text-shadow: 0 0 20px rgba(255, 183, 197, 0.8), 0 0 40px rgba(255, 183, 197, 0.3); }
}

.logo-text {
  font-size: 18px;
  font-weight: 900;
  background: linear-gradient(135deg, var(--neon-purple), var(--sakura), var(--neon-cyan));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
}

.logo-sub {
  font-size: 9px;
  font-weight: 700;
  color: var(--neon-cyan);
  letter-spacing: 3px;
  opacity: 0.7;
  align-self: flex-end;
  margin-bottom: 8px;
}

.sidebar-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-purple), transparent);
  margin: 0 16px;
  opacity: 0.5;
}

/* ========== MENU ========== */
.cyber-menu {
  border-right: none !important;
  background: transparent !important;
  padding: 8px 0;
  flex: 1;
}

.cyber-menu :deep(.el-menu-item) {
  color: var(--text-secondary) !important;
  height: 52px;
  line-height: 52px;
  margin: 4px 10px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: transparent !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.cyber-menu :deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--neon-purple);
  border-radius: 0 3px 3px 0;
  opacity: 0;
  transition: opacity 0.3s;
}

.cyber-menu :deep(.el-menu-item:hover) {
  background: var(--glass-bg) !important;
  color: var(--text-primary) !important;
  border-color: var(--glass-border);
}

.cyber-menu :deep(.el-menu-item:hover::before) {
  opacity: 1;
}

.cyber-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(179, 136, 255, 0.15), rgba(0, 245, 255, 0.05)) !important;
  color: var(--neon-cyan) !important;
  border-color: rgba(179, 136, 255, 0.3);
  box-shadow: 0 0 15px rgba(179, 136, 255, 0.1), inset 0 0 15px rgba(179, 136, 255, 0.05);
}

.cyber-menu :deep(.el-menu-item.is-active::before) {
  opacity: 1;
  background: linear-gradient(180deg, var(--neon-purple), var(--neon-cyan));
  box-shadow: 0 0 10px var(--neon-purple);
}

.cyber-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 10px;
  transition: all 0.3s;
}

.cyber-menu :deep(.el-menu-item:hover .el-icon),
.cyber-menu :deep(.el-menu-item.is-active .el-icon) {
  filter: drop-shadow(0 0 6px var(--neon-cyan));
}

.menu-label {
  font-size: 14px;
  font-weight: 500;
}

.menu-en {
  font-size: 8px;
  letter-spacing: 2px;
  color: var(--text-secondary);
  opacity: 0.5;
  margin-left: auto;
  font-weight: 700;
}

/* ========== SIDEBAR FOOTER ========== */
.sidebar-footer {
  padding: 12px 16px;
  text-align: center;
}

.cyber-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-purple), var(--neon-cyan), transparent);
  margin-bottom: 8px;
  opacity: 0.4;
}

.footer-text {
  font-size: 9px;
  letter-spacing: 3px;
  color: var(--text-secondary);
  opacity: 0.4;
}

/* ========== HEADER ========== */
.admin-header {
  background: rgba(26, 26, 46, 0.8) !important;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--glass-border);
  box-shadow: 0 1px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  position: relative;
}

.admin-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-purple), var(--neon-cyan), var(--sakura), transparent);
  opacity: 0.6;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-decoration {
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, var(--neon-purple), var(--neon-cyan));
  border-radius: 2px;
  box-shadow: 0 0 10px rgba(179, 136, 255, 0.5);
}

.header-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #00ff88;
  box-shadow: 0 0 8px rgba(0, 255, 136, 0.6);
  animation: blink 2s ease-in-out infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.status-text {
  font-size: 10px;
  letter-spacing: 2px;
  color: #00ff88;
  font-weight: 700;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.neon-avatar {
  border: 2px solid var(--neon-purple) !important;
  box-shadow: 0 0 12px rgba(179, 136, 255, 0.4), inset 0 0 8px rgba(179, 136, 255, 0.1);
  background: linear-gradient(135deg, rgba(179, 136, 255, 0.3), rgba(0, 245, 255, 0.2)) !important;
  color: var(--neon-cyan) !important;
  font-weight: 700;
  font-size: 14px;
}

.username {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

/* ========== LOGOUT BUTTON ========== */
.cyber-btn-logout {
  background: rgba(255, 107, 157, 0.1) !important;
  border: 1px solid rgba(255, 107, 157, 0.3) !important;
  color: var(--neon-pink) !important;
  border-radius: 6px !important;
  transition: all 0.3s !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
}

.cyber-btn-logout:hover {
  background: rgba(255, 107, 157, 0.2) !important;
  border-color: var(--neon-pink) !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3) !important;
}

.btn-icon {
  margin-right: 4px;
  font-size: 11px;
}

/* ========== MAIN CONTENT ========== */
.admin-main {
  background: var(--deep-blue);
  min-height: calc(100vh - 60px);
  padding: 24px;
  position: relative;
}

.admin-main::before {
  content: '';
  position: fixed;
  top: 60px;
  right: 0;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(179, 136, 255, 0.05) 0%, transparent 70%);
  pointer-events: none;
  z-index: 0;
}

.main-container {
  position: relative;
}
</style>


