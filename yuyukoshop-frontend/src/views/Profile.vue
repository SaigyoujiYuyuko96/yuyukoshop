<template>
  <div class="cyber-profile-page">
    <NavHeader />
    <div class="cyber-container">
      <!-- 背景装饰粒子 -->
      <div class="bg-particles">
        <span v-for="n in 6" :key="n" class="particle" :class="'particle-' + n"></span>
      </div>

      <div class="profile-layout">
        <!-- 左侧边栏 -->
        <aside class="sidebar-glass">
          <!-- 用户头像卡片 -->
          <div class="avatar-section">
            <div class="avatar-ring">
              <div class="avatar-glow"></div>
              <el-avatar :size="100" :src="userInfo.avatar" class="cyber-avatar">
                <el-icon :size="48"><User /></el-icon>
              </el-avatar>
            </div>
            <h3 class="cyber-username">{{ userInfo.username }}</h3>
            <p class="cyber-email">{{ userInfo.email }}</p>
            <div class="status-line">
              <span class="status-dot"></span>
              <span class="status-text">ONLINE</span>
            </div>
          </div>

          <!-- 导航菜单 -->
          <nav class="cyber-nav">
            <div
              v-for="item in menuItems"
              :key="item.key"
              class="nav-item"
              :class="{ active: activeMenu === item.key }"
              @click="handleMenuSelect(item.key)"
            >
              <div class="nav-icon-wrap">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <span class="nav-label">{{ item.label }}</span>
              <div class="nav-active-bar"></div>
            </div>
          </nav>

          <!-- 底部装饰 -->
          <div class="sidebar-deco">
            <div class="deco-line"></div>
            <span class="deco-text">YUYUKO SHOP v2.0</span>
            <div class="deco-line"></div>
          </div>
        </aside>

        <!-- 右侧内容区 -->
        <main class="content-area">
          <!-- 内容标题栏 -->
          <div class="content-header">
            <div class="header-left">
              <div class="header-icon-box">
                <el-icon :size="20"><component :is="currentMenuIcon" /></el-icon>
              </div>
              <h2 class="content-title">{{ menuTitle }}</h2>
            </div>
            <div class="header-right">
              <span class="header-tag">PROFILE</span>
            </div>
          </div>

          <!-- 个人信息 -->
          <div v-if="activeMenu === 'info'" class="glass-card fade-in">
            <div class="card-section-title">
              <span class="section-dot"></span>
              基本信息
            </div>
            <el-form :model="form" label-width="100px" class="cyber-form">
              <div class="form-grid">
                <div class="form-item-full">
                  <label class="cyber-label">头像</label>
                  <div class="avatar-upload-area">
                    <ImageUpload v-model="form.avatar" upload-type="avatar" placeholder="上传头像" />
                  </div>
                </div>
                <div class="form-item">
                  <label class="cyber-label">用户名</label>
                  <el-input v-model="form.username" class="cyber-input" placeholder="输入用户名" />
                </div>
                <div class="form-item">
                  <label class="cyber-label">邮箱</label>
                  <el-input v-model="form.email" class="cyber-input" placeholder="输入邮箱" />
                </div>
                <div class="form-item">
                  <label class="cyber-label">手机号</label>
                  <el-input v-model="form.phone" class="cyber-input" placeholder="输入手机号" />
                </div>
              </div>
              <div class="form-actions">
                <button class="cyber-btn cyber-btn-primary" @click="updateProfile">
                  <span class="btn-glow"></span>
                  <el-icon><Check /></el-icon>
                  保存修改
                </button>
              </div>
            </el-form>
          </div>

          <!-- 收货地址 -->
          <div v-else-if="activeMenu === 'addresses'" class="glass-card fade-in">
            <div class="card-section-title">
              <span class="section-dot"></span>
              地址管理
            </div>
            <div class="address-header">
              <button class="cyber-btn cyber-btn-accent" @click="showAddressDialog = true">
                <el-icon><Plus /></el-icon>
                新增地址
              </button>
            </div>
            <div class="address-list" v-if="addresses.length > 0">
              <div class="address-card" v-for="addr in addresses" :key="addr.id">
                <div class="address-card-header">
                  <span class="address-name">{{ addr.receiverName }}</span>
                  <span class="address-phone">{{ addr.phone }}</span>
                </div>
                <div class="address-detail">{{ addr.detailAddress }}</div>
                <div class="address-actions">
                  <button class="cyber-btn cyber-btn-danger-sm" @click="deleteAddress(addr.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <el-icon :size="48" class="empty-icon"><Location /></el-icon>
              <p>暂无收货地址</p>
            </div>
          </div>

          <!-- 我的收藏 -->
          <div v-else-if="activeMenu === 'favorites'" class="glass-card fade-in">
            <div class="card-section-title">
              <span class="section-dot"></span>
              我的收藏
            </div>
            <div v-if="favorites.length === 0" class="empty-state">
              <el-icon :size="48" class="empty-icon"><Star /></el-icon>
              <p>暂无收藏商品</p>
            </div>
            <div v-else class="favorites-grid">
              <div
                class="favorite-card"
                v-for="item in favorites"
                :key="item.id"
                @click="$router.push(`/products/${item.productId}`)"
              >
                <div class="favorite-img-wrap">
                  <img :src="item.productImage" class="favorite-image" />
                  <div class="favorite-overlay">
                    <el-icon :size="24"><View /></el-icon>
                  </div>
                </div>
                <div class="favorite-info">
                  <h4 class="favorite-name">{{ item.productName }}</h4>
                  <p class="favorite-price">¥{{ item.productPrice }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的帖子 -->
          <div v-else-if="activeMenu === 'posts'" class="glass-card fade-in">
            <div class="card-section-title">
              <span class="section-dot"></span>
              我的帖子
            </div>
            <div v-if="myPosts.length === 0" class="empty-state">
              <el-icon :size="48" class="empty-icon"><Document /></el-icon>
              <p>暂无帖子</p>
            </div>
            <div v-else class="posts-list">
              <div class="post-card" v-for="post in myPosts" :key="post.id">
                <div class="post-card-content">
                  <div class="post-indicator"></div>
                  <div class="post-card-body">
                    <div class="post-title">{{ post.title }}</div>
                    <div class="post-meta">
                      <span class="post-time">
                        <el-icon><Clock /></el-icon>
                        {{ post.createTime }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="post-card-actions">
                  <button class="cyber-btn cyber-btn-danger-sm" @click="deletePost(post.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的订单 -->
          <div v-else-if="activeMenu === 'orders'" class="glass-card fade-in">
            <div class="card-section-title">
              <span class="section-dot"></span>
              订单中心
            </div>
            <div class="orders-redirect">
              <div class="orders-icon-wrap">
                <el-icon :size="64"><List /></el-icon>
              </div>
              <p class="orders-desc">查看和管理你的所有订单</p>
              <button class="cyber-btn cyber-btn-primary" @click="$router.push('/orders')">
                <span class="btn-glow"></span>
                <el-icon><Right /></el-icon>
                查看全部订单
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 新增地址弹窗 -->
    <el-dialog
      v-model="showAddressDialog"
      title="新增收货地址"
      width="500px"
      class="cyber-dialog"
      :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <div class="dialog-header-bar"></div>
          <span class="dialog-title">新增收货地址</span>
        </div>
      </template>
      <el-form :model="addressForm" label-width="100px" class="cyber-form">
        <div class="form-item">
          <label class="cyber-label">收货人</label>
          <el-input v-model="addressForm.receiverName" class="cyber-input" placeholder="输入收货人姓名" />
        </div>
        <div class="form-item">
          <label class="cyber-label">电话</label>
          <el-input v-model="addressForm.phone" class="cyber-input" placeholder="输入联系电话" />
        </div>
        <div class="form-item">
          <label class="cyber-label">详细地址</label>
          <el-input v-model="addressForm.detailAddress" class="cyber-input" type="textarea" placeholder="输入详细地址" :rows="3" />
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="cyber-btn cyber-btn-ghost" @click="showAddressDialog = false">取消</button>
          <button class="cyber-btn cyber-btn-primary" @click="addAddress">
            <span class="btn-glow"></span>
            确定添加
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const activeMenu = ref('info')
const userInfo = computed(() => userStore.userInfo)

const menuTitles = {
  info: '个人信息',
  orders: '我的订单',
  addresses: '收货地址',
  favorites: '我的收藏',
  posts: '我的帖子'
}
const menuTitle = computed(() => menuTitles[activeMenu.value])

const form = reactive({
  username: '',
  email: '',
  phone: ''
})

const addresses = ref([])
const favorites = ref([])
const myPosts = ref([])
const showAddressDialog = ref(false)
const addressForm = reactive({
  receiverName: '',
  phone: '',
  detailAddress: ''
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'addresses') fetchAddresses()
  if (index === 'favorites') fetchFavorites()
  if (index === 'posts') fetchMyPosts()
}

const fetchUserInfo = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    const res = await request.get('/auth/me', { silent: true })
    userStore.setUserInfo(res.data)
    Object.assign(form, res.data)
  } catch (e) {
    console.error(e)
    // Token 无效，跳转登录
    userStore.logout()
    router.push('/login')
  }
}

const updateProfile = async () => {
  try {
    await request.put('/users/profile', form)
    ElMessage.success('更新成功')
    fetchUserInfo()
  } catch (e) { console.error(e) }
}

const fetchAddresses = async () => {
  try {
    const res = await request.get('/users/addresses')
    addresses.value = res.data || []
  } catch (e) { console.error(e) }
}

const addAddress = async () => {
  try {
    await request.post('/users/addresses', addressForm)
    ElMessage.success('添加成功')
    showAddressDialog.value = false
    Object.assign(addressForm, { receiverName: '', phone: '', detailAddress: '' })
    fetchAddresses()
  } catch (e) { console.error(e) }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该地址吗？', '提示', { type: 'warning' })
    await request.delete(`/users/addresses/${id}`)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const fetchFavorites = async () => {
  try {
    const res = await request.get('/favorites')
    favorites.value = res.data || []
  } catch (e) { console.error(e) }
}

const fetchMyPosts = async () => {
  try {
    const res = await request.get('/forums/posts/my')
    myPosts.value = res.data || []
  } catch (e) { console.error(e) }
}

const deletePost = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该帖子吗？', '提示', { type: 'warning' })
    await request.delete(`/forums/posts/${id}`)
    ElMessage.success('删除成功')
    fetchMyPosts()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
/* ========== CSS 变量 ========== */
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

/* ========== 全局页面 ========== */
.cyber-profile-page {
  min-height: 100vh;
  background: var(--deep-blue);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  color: var(--text-primary);
  position: relative;
  overflow-x: hidden;
}

.cyber-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 30px 24px;
  position: relative;
  z-index: 1;
}

/* ========== 背景粒子 ========== */
.bg-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  animation: floatParticle 20s infinite ease-in-out;
}

.particle-1 {
  width: 300px; height: 300px;
  background: radial-gradient(circle, var(--neon-purple), transparent 70%);
  top: 10%; left: -5%;
  animation-delay: 0s;
}

.particle-2 {
  width: 250px; height: 250px;
  background: radial-gradient(circle, var(--neon-cyan), transparent 70%);
  top: 60%; right: -5%;
  animation-delay: -5s;
}

.particle-3 {
  width: 200px; height: 200px;
  background: radial-gradient(circle, var(--neon-pink), transparent 70%);
  bottom: 10%; left: 30%;
  animation-delay: -10s;
}

.particle-4 {
  width: 180px; height: 180px;
  background: radial-gradient(circle, var(--sakura), transparent 70%);
  top: 30%; right: 20%;
  animation-delay: -3s;
}

.particle-5 {
  width: 150px; height: 150px;
  background: radial-gradient(circle, var(--gold), transparent 70%);
  top: 5%; right: 40%;
  animation-delay: -7s;
  opacity: 0.08;
}

.particle-6 {
  width: 220px; height: 220px;
  background: radial-gradient(circle, var(--neon-cyan), transparent 70%);
  bottom: 20%; right: 5%;
  animation-delay: -12s;
  opacity: 0.1;
}

@keyframes floatParticle {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.1); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, 35px) scale(1.05); }
}

/* ========== 布局 ========== */
.profile-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: start;
}

@media (max-width: 900px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}

/* ========== 侧边栏 ========== */
.sidebar-glass {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 28px 16px;
  position: sticky;
  top: 90px;
  animation: slideInLeft 0.6s ease-out;
}

@keyframes slideInLeft {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}

/* ========== 头像区域 ========== */
.avatar-section {
  text-align: center;
  padding-bottom: 24px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--glass-border);
}

.avatar-ring {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.avatar-ring::before {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: conic-gradient(
    var(--neon-cyan),
    var(--neon-purple),
    var(--neon-pink),
    var(--sakura),
    var(--neon-cyan)
  );
  animation: rotateBorder 4s linear infinite;
  z-index: 0;
}

.avatar-ring::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 50%;
  background: var(--deep-blue);
  z-index: 1;
}

.avatar-glow {
  position: absolute;
  inset: -20px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(179, 136, 255, 0.3), transparent 70%);
  z-index: -1;
  animation: pulseGlow 3s ease-in-out infinite;
}

@keyframes rotateBorder {
  to { transform: rotate(360deg); }
}

@keyframes pulseGlow {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

.cyber-avatar {
  position: relative;
  z-index: 2;
  border: 3px solid var(--deep-blue);
  background: var(--cyber-blue);
}

.cyber-username {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple), var(--sakura));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 6px;
  letter-spacing: 1px;
}

.cyber-email {
  color: var(--text-secondary);
  font-size: 13px;
  margin: 0 0 12px;
  word-break: break-all;
}

.status-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  background: rgba(0, 245, 255, 0.08);
  border: 1px solid rgba(0, 245, 255, 0.2);
  border-radius: 20px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--neon-cyan);
  box-shadow: 0 0 8px var(--neon-cyan);
  animation: blink 2s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.status-text {
  font-size: 11px;
  color: var(--neon-cyan);
  letter-spacing: 2px;
  font-weight: 600;
}

/* ========== 导航菜单 ========== */
.cyber-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid transparent;
}

.nav-item:hover {
  background: rgba(179, 136, 255, 0.08);
  border-color: rgba(179, 136, 255, 0.15);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.1), rgba(179, 136, 255, 0.1));
  border-color: rgba(0, 245, 255, 0.3);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.08);
}

.nav-item.active .nav-icon-wrap {
  color: var(--neon-cyan);
  text-shadow: 0 0 12px var(--neon-cyan);
}

.nav-item.active .nav-label {
  color: var(--neon-cyan);
}

.nav-item.active .nav-active-bar {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: var(--neon-cyan);
  border-radius: 0 3px 3px 0;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.nav-icon-wrap {
  font-size: 18px;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.nav-label {
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  font-weight: 500;
}

/* ========== 侧边栏装饰 ========== */
.sidebar-deco {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid var(--glass-border);
}

.deco-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--glass-border), transparent);
}

.deco-text {
  font-size: 10px;
  color: var(--text-secondary);
  opacity: 0.4;
  letter-spacing: 1px;
  white-space: nowrap;
}

/* ========== 内容区域 ========== */
.content-area {
  min-height: 500px;
  animation: fadeInUp 0.6s ease-out 0.15s both;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 16px 24px;
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon-box {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.15), rgba(179, 136, 255, 0.15));
  border: 1px solid rgba(0, 245, 255, 0.3);
  border-radius: 10px;
  color: var(--neon-cyan);
}

.content-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(90deg, var(--text-primary), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-tag {
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--neon-cyan);
  padding: 4px 12px;
  border: 1px solid rgba(0, 245, 255, 0.25);
  border-radius: 6px;
  background: rgba(0, 245, 255, 0.05);
}

/* ========== 玻璃卡片 ========== */
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 20px;
  transition: border-color 0.3s ease;
}

.glass-card:hover {
  border-color: rgba(179, 136, 255, 0.2);
}

.card-section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--glass-border);
}

.section-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  background: var(--neon-cyan);
  box-shadow: 0 0 10px var(--neon-cyan);
  transform: rotate(45deg);
}

/* ========== 表单 ========== */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-item-full {
  grid-column: 1 / -1;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cyber-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
  letter-spacing: 0.5px;
}

.cyber-input :deep(.el-input__wrapper),
.cyber-input :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.04) !important;
  border: 1px solid var(--glass-border) !important;
  border-radius: 10px !important;
  color: var(--text-primary) !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
}

.cyber-input :deep(.el-input__wrapper:hover),
.cyber-input :deep(.el-textarea__inner:hover) {
  border-color: rgba(179, 136, 255, 0.3) !important;
}

.cyber-input :deep(.el-input__wrapper.is-focus),
.cyber-input :deep(.el-textarea__inner:focus) {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 16px rgba(0, 245, 255, 0.12) !important;
}

.cyber-input :deep(.el-input__inner),
.cyber-input :deep(.el-textarea__inner) {
  color: var(--text-primary) !important;
}

.cyber-input :deep(.el-input__inner::placeholder),
.cyber-input :deep(.el-textarea__inner::placeholder) {
  color: rgba(156, 163, 175, 0.5) !important;
}

.form-actions {
  margin-top: 28px;
  display: flex;
  justify-content: flex-end;
}

/* ========== 按钮 ========== */
.cyber-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.cyber-btn-primary {
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: #fff;
  box-shadow: 0 4px 20px rgba(0, 245, 255, 0.25);
}

.cyber-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 30px rgba(0, 245, 255, 0.4);
}

.cyber-btn-primary:active {
  transform: translateY(0);
}

.cyber-btn-primary .btn-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 30%, rgba(255, 255, 255, 0.15) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.cyber-btn-primary:hover .btn-glow {
  transform: translateX(100%);
}

.cyber-btn-accent {
  background: linear-gradient(135deg, var(--neon-pink), var(--sakura));
  color: #fff;
  box-shadow: 0 4px 20px rgba(255, 107, 157, 0.25);
}

.cyber-btn-accent:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 30px rgba(255, 107, 157, 0.4);
}

.cyber-btn-ghost {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--glass-border);
}

.cyber-btn-ghost:hover {
  border-color: rgba(179, 136, 255, 0.3);
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.04);
}

.cyber-btn-danger-sm {
  padding: 6px 14px;
  font-size: 12px;
  background: rgba(255, 107, 157, 0.1);
  color: var(--neon-pink);
  border: 1px solid rgba(255, 107, 157, 0.2);
  border-radius: 8px;
}

.cyber-btn-danger-sm:hover {
  background: rgba(255, 107, 157, 0.2);
  border-color: rgba(255, 107, 157, 0.4);
  box-shadow: 0 0 16px rgba(255, 107, 157, 0.15);
}

/* ========== 地址卡片 ========== */
.address-header {
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 18px 20px;
  transition: all 0.3s ease;
}

.address-card:hover {
  border-color: rgba(179, 136, 255, 0.25);
  background: rgba(255, 255, 255, 0.05);
}

.address-card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.address-name {
  font-weight: 600;
  font-size: 15px;
  color: var(--text-primary);
}

.address-phone {
  font-size: 13px;
  color: var(--neon-cyan);
  font-family: monospace;
}

.address-detail {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
}

/* ========== 收藏网格 ========== */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

.favorite-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
}

.favorite-card:hover {
  transform: translateY(-4px);
  border-color: rgba(179, 136, 255, 0.3);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3), 0 0 20px rgba(179, 136, 255, 0.1);
}

.favorite-img-wrap {
  position: relative;
  overflow: hidden;
}

.favorite-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.favorite-card:hover .favorite-image {
  transform: scale(1.08);
}

.favorite-overlay {
  position: absolute;
  inset: 0;
  background: rgba(26, 26, 46, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: var(--neon-cyan);
}

.favorite-card:hover .favorite-overlay {
  opacity: 1;
}

.favorite-info {
  padding: 14px 16px;
}

.favorite-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.favorite-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--neon-pink);
  margin: 0;
  text-shadow: 0 0 12px rgba(255, 107, 157, 0.3);
}

/* ========== 帖子列表 ========== */
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s ease;
}

.post-card:hover {
  border-color: rgba(179, 136, 255, 0.25);
  background: rgba(255, 255, 255, 0.05);
}

.post-card-content {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.post-indicator {
  width: 4px;
  height: 36px;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--neon-cyan), var(--neon-purple));
  flex-shrink: 0;
}

.post-card-body {
  flex: 1;
  min-width: 0;
}

.post-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.post-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

.post-card-actions {
  flex-shrink: 0;
  margin-left: 16px;
}

/* ========== 订单重定向 ========== */
.orders-redirect {
  text-align: center;
  padding: 40px 20px;
}

.orders-icon-wrap {
  color: var(--neon-purple);
  margin-bottom: 16px;
  animation: floatIcon 3s ease-in-out infinite;
}

@keyframes floatIcon {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.orders-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 24px;
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  color: var(--text-secondary);
  opacity: 0.3;
  margin-bottom: 16px;
}

.empty-state p {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

/* ========== 弹窗样式 ========== */
:deep(.cyber-dialog) {
  border-radius: 16px !important;
  overflow: hidden;
}

:deep(.cyber-dialog .el-dialog) {
  background: var(--cyber-blue) !important;
  border: 1px solid var(--glass-border) !important;
  border-radius: 16px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5), 0 0 40px rgba(179, 136, 255, 0.1) !important;
}

:deep(.cyber-dialog .el-dialog__header) {
  padding: 0 !important;
  margin: 0 !important;
}

:deep(.cyber-dialog .el-dialog__body) {
  padding: 24px 28px !important;
  color: var(--text-primary) !important;
}

:deep(.cyber-dialog .el-dialog__footer) {
  padding: 16px 28px 24px !important;
  border-top: 1px solid var(--glass-border);
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 28px;
  border-bottom: 1px solid var(--glass-border);
}

.dialog-header-bar {
  width: 4px;
  height: 20px;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--neon-cyan), var(--neon-purple));
}

.dialog-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* ========== 动画 ========== */
.fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .cyber-container {
    padding: 16px 12px;
  }

  .sidebar-glass {
    position: static;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }

  .post-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .post-card-actions {
    margin-left: 0;
  }

  .content-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}

@media (max-width: 600px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }

  .glass-card {
    padding: 18px 14px;
  }

  .cyber-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}
</style>
