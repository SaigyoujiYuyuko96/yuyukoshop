<template>
  <div class="cart-universe">
    <NavHeader />

    <!-- 背景装饰粒子 -->
    <div class="cyber-grid-bg"></div>
    <div class="floating-particles">
      <span v-for="i in 6" :key="i" class="particle" :class="'p-' + i"></span>
    </div>

    <div class="cart-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-decoration left-deco">
          <span class="deco-line"></span>
          <span class="deco-dot"></span>
        </div>
        <h2 class="page-title">
          <span class="title-icon">&#x26A1;</span>
          我的购物车
          <span class="title-sub">CART_TERMINAL</span>
        </h2>
        <div class="header-decoration right-deco">
          <span class="deco-dot"></span>
          <span class="deco-line"></span>
        </div>
      </div>

      <!-- 空购物车状态 -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-card">
          <div class="empty-icon-wrapper">
            <div class="empty-hexagon">
              <span class="empty-emoji">&#x1F6D2;</span>
            </div>
            <div class="empty-ring ring-1"></div>
            <div class="empty-ring ring-2"></div>
          </div>
          <p class="empty-text">购物车是空的</p>
          <p class="empty-sub-text">数据流中尚未检测到商品信号...</p>
          <button class="cyber-btn go-shop-btn" @click="$router.push('/products')">
            <span class="btn-text">前往选购</span>
            <span class="btn-glitch"></span>
          </button>
        </div>
      </div>

      <!-- 购物车列表 -->
      <div v-else class="cart-content">
        <!-- 全选控制栏 -->
        <div class="selection-bar glass-card">
          <label class="select-all-wrapper">
            <input type="checkbox" class="cyber-checkbox" :checked="isAllSelected" @change="toggleSelectAll" />
            <span class="checkbox-custom"></span>
            <span class="select-all-text">全选 / SELECT_ALL</span>
          </label>
          <span class="item-count-badge">
            <span class="badge-count">{{ cartItems.length }}</span>
            件商品在线
          </span>
        </div>

        <!-- 商品卡片列表 -->
        <TransitionGroup name="cart-item" tag="div" class="cart-items-list">
          <div
            v-for="row in cartItems"
            :key="row.id"
            class="cart-item-card glass-card"
            :class="{ selected: isSelected(row) }"
          >
            <!-- 选中指示条 -->
            <div class="select-indicator">
              <label class="item-checkbox-wrapper">
                <input
                  type="checkbox"
                  class="cyber-checkbox"
                  :checked="isSelected(row)"
                  @change="toggleItem(row)"
                />
                <span class="checkbox-custom"></span>
              </label>
            </div>

            <!-- 商品信息 -->
            <div class="item-image-wrapper">
              <el-image :src="row.productImage" fit="cover" class="item-image" />
              <div class="image-scanline"></div>
            </div>

            <div class="item-info">
              <h3 class="item-name">{{ row.productName }}</h3>
              <div class="item-meta">
                <span class="meta-tag">ID: {{ row.productId }}</span>
                <span class="meta-tag stock-tag" :class="{ 'low-stock': row.productStock <= 3 }">
                  库存: {{ row.productStock }}
                </span>
              </div>
            </div>

            <!-- 价格 -->
            <div class="item-price-section">
              <span class="price-label">单价</span>
              <span class="item-price">&yen;{{ row.productPrice }}</span>
            </div>

            <!-- 数量控制 -->
            <div class="item-quantity-section">
              <span class="price-label">数量</span>
              <div class="quantity-control">
                <button
                  class="qty-btn qty-minus"
                  :disabled="row.quantity <= 1"
                  @click="changeQty(row, -1)"
                >
                  <span class="qty-icon">&#x2212;</span>
                </button>
                <span class="qty-display">{{ row.quantity }}</span>
                <button
                  class="qty-btn qty-plus"
                  :disabled="row.quantity >= row.productStock"
                  @click="changeQty(row, 1)"
                >
                  <span class="qty-icon">&#x002B;</span>
                </button>
              </div>
            </div>

            <!-- 小计 -->
            <div class="item-subtotal-section">
              <span class="price-label">小计</span>
              <span class="item-subtotal">&yen;{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
            </div>

            <!-- 删除按钮 -->
            <button class="delete-btn" @click="removeItem(row.id)">
              <span class="delete-icon">&times;</span>
              <span class="delete-text">DEL</span>
            </button>

            <!-- 选中时的边框光效 -->
            <div v-if="isSelected(row)" class="selected-glow"></div>
          </div>
        </TransitionGroup>

        <!-- 底部结算栏 -->
        <div class="cart-footer glass-card">
          <div class="footer-info">
            <div class="selected-summary">
              <span class="summary-label">已选中</span>
              <span class="summary-value neon-text">{{ selectedItems.length }}</span>
              <span class="summary-label">件商品</span>
            </div>
            <div class="divider-v"></div>
            <div class="total-section">
              <span class="total-label">合计金额</span>
              <span class="total-price">
                <span class="currency">&yen;</span>
                <span class="amount">{{ totalPrice.toFixed(2) }}</span>
              </span>
            </div>
          </div>
          <button
            class="checkout-btn"
            :class="{ disabled: selectedItems.length === 0 }"
            :disabled="selectedItems.length === 0"
            @click="checkout"
          >
            <span class="checkout-text">立即结算</span>
            <span class="checkout-sub">CHECKOUT</span>
            <span class="checkout-arrow">&#x2192;</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const selectedItems = ref([])
const loading = ref(false)
const updating = ref(false)

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
})

const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length
})

const isSelected = (row) => {
  return selectedItems.value.some(item => item.id === row.id)
}

const toggleItem = (row) => {
  const index = selectedItems.value.findIndex(item => item.id === row.id)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(row)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedItems.value = []
  } else {
    selectedItems.value = [...cartItems.value]
  }
}

const changeQty = (row, delta) => {
  const newQty = row.quantity + delta
  if (newQty < 1 || newQty > row.productStock) return
  row.quantity = newQty
  updateQuantity(row)
}

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await request.get('/cart', { silent: true })
    cartItems.value = res.data || []
  } catch (e) {
    // 静默请求，错误由 request.js 统一处理
    cartItems.value = []
    console.error('获取购物车失败', e)
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 防抖处理
let updateTimer = null
const updateQuantity = async (row) => {
  if (updateTimer) clearTimeout(updateTimer)

  updateTimer = setTimeout(async () => {
    if (updating.value) return
    updating.value = true

    try {
      await request.put(`/cart/items/${row.id}`, { quantity: row.quantity })
      ElMessage.success('数量已更新')
    } catch (e) {
      ElMessage.error('更新数量失败')
      console.error(e)
      // 恢复原有数量
      fetchCart()
    } finally {
      updating.value = false
    }
  }, 500)
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该商品吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })

    await request.delete(`/cart/items/${id}`)
    ElMessage.success('已删除')
    fetchCart()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(e)
    }
  }
}

const checkout = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await ElMessageBox.confirm('确认提交订单吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })

    const productIds = selectedItems.value.map(item => item.productId)
    const res = await request.post('/orders', { productIds })
    ElMessage.success('订单创建成功')
    router.push(`/orders/${res.data}`)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('创建订单失败')
      console.error(e)
    }
  }
}

onMounted(fetchCart)
</script>

<style scoped>
/* ============================================
   幽幽子Shop - 购物车页 | 日系赛博朋克动漫风
   ============================================ */

/* --- 基础变量 & 全局 --- */
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

.cart-universe {
  min-height: 100vh;
  background: var(--deep-blue);
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  position: relative;
  overflow-x: hidden;
  padding-bottom: 60px;
}

/* --- 背景网格 & 粒子 --- */
.cyber-grid-bg {
  position: fixed;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 245, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 245, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  pointer-events: none;
  z-index: 0;
}

.floating-particles {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  animation: floatParticle 8s ease-in-out infinite;
}

.particle.p-1 { background: var(--neon-cyan); top: 15%; left: 10%; animation-delay: 0s; }
.particle.p-2 { background: var(--neon-pink); top: 30%; left: 80%; animation-delay: 1.5s; }
.particle.p-3 { background: var(--sakura); top: 60%; left: 20%; animation-delay: 3s; }
.particle.p-4 { background: var(--neon-purple); top: 75%; left: 70%; animation-delay: 4.5s; }
.particle.p-5 { background: var(--gold); top: 45%; left: 50%; animation-delay: 2s; }
.particle.p-6 { background: var(--neon-cyan); top: 85%; left: 35%; animation-delay: 5.5s; }

@keyframes floatParticle {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.4; }
  50% { transform: translateY(-30px) scale(1.5); opacity: 0.8; }
}

/* --- 容器 --- */
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  position: relative;
  z-index: 1;
}

/* --- 页面标题 --- */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
  padding-top: 20px;
}

.header-decoration {
  display: flex;
  align-items: center;
  gap: 6px;
}

.deco-line {
  display: block;
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan));
}

.right-deco .deco-line {
  background: linear-gradient(90deg, var(--neon-cyan), transparent);
}

.deco-dot {
  display: block;
  width: 8px;
  height: 8px;
  background: var(--neon-cyan);
  border-radius: 50%;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple), var(--sakura));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: none;
}

.title-icon {
  -webkit-text-fill-color: var(--gold);
  font-size: 24px;
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.title-sub {
  font-size: 11px;
  letter-spacing: 3px;
  -webkit-text-fill-color: var(--text-secondary);
  font-weight: 400;
  text-transform: uppercase;
}

/* --- 玻璃卡片通用 --- */
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  transition: all 0.3s ease;
}

/* --- 空购物车 --- */
.empty-cart {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.empty-card {
  text-align: center;
  padding: 60px 40px;
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease;
}

.empty-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.05), rgba(179, 136, 255, 0.05));
  pointer-events: none;
}

.empty-icon-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 30px;
}

.empty-hexagon {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, var(--cyber-blue), var(--deep-blue));
  border: 2px solid var(--neon-cyan);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.2);
}

.empty-emoji {
  font-size: 40px;
}

.empty-ring {
  position: absolute;
  border: 1px solid rgba(0, 245, 255, 0.2);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: ringPulse 3s ease-in-out infinite;
}

.ring-1 {
  width: 140px;
  height: 140px;
  animation-delay: 0s;
}

.ring-2 {
  width: 170px;
  height: 170px;
  animation-delay: 1s;
  border-color: rgba(179, 136, 255, 0.15);
}

@keyframes ringPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 1; }
}

.empty-text {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.empty-sub-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 30px;
  letter-spacing: 1px;
}

.go-shop-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 36px;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: var(--deep-blue);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.go-shop-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 245, 255, 0.3);
}

/* --- 选择栏 --- */
.selection-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  margin-bottom: 16px;
}

.select-all-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.select-all-text {
  font-size: 14px;
  color: var(--text-secondary);
  letter-spacing: 1px;
}

.item-count-badge {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.badge-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 8px;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: var(--deep-blue);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

/* --- 自定义复选框 --- */
.cyber-checkbox {
  display: none;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
  border: 2px solid var(--neon-cyan);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  flex-shrink: 0;
}

.checkbox-custom::after {
  content: '';
  width: 10px;
  height: 10px;
  background: var(--neon-cyan);
  border-radius: 3px;
  transform: scale(0);
  transition: transform 0.2s ease;
}

.cyber-checkbox:checked + .checkbox-custom {
  background: rgba(0, 245, 255, 0.15);
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.3);
}

.cyber-checkbox:checked + .checkbox-custom::after {
  transform: scale(1);
}

/* --- 购物车商品列表 --- */
.cart-items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.cart-item-card {
  display: grid;
  grid-template-columns: 50px 90px 1fr 120px 160px 120px 60px;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.cart-item-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.02), rgba(179, 136, 255, 0.02));
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.cart-item-card:hover {
  border-color: rgba(0, 245, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.cart-item-card:hover::before {
  opacity: 1;
}

.cart-item-card.selected {
  border-color: rgba(0, 245, 255, 0.4);
  background: rgba(0, 245, 255, 0.03);
}

.selected-glow {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, var(--neon-cyan), var(--neon-purple));
  box-shadow: 0 0 15px var(--neon-cyan);
}

/* --- 选中指示 --- */
.select-indicator {
  display: flex;
  align-items: center;
}

.item-checkbox-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
}

/* --- 商品图片 --- */
.item-image-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--glass-border);
  flex-shrink: 0;
}

.item-image {
  width: 100%;
  height: 100%;
  border-radius: 12px;
}

.image-scanline {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 3px,
    rgba(0, 245, 255, 0.03) 3px,
    rgba(0, 245, 255, 0.03) 4px
  );
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cart-item-card:hover .image-scanline {
  opacity: 1;
}

/* --- 商品信息 --- */
.item-info {
  min-width: 0;
}

.item-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.meta-tag {
  font-size: 11px;
  color: var(--text-secondary);
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  letter-spacing: 0.5px;
}

.stock-tag.low-stock {
  color: var(--neon-pink);
  background: rgba(255, 107, 157, 0.1);
  border: 1px solid rgba(255, 107, 157, 0.2);
}

/* --- 价格区域 --- */
.item-price-section,
.item-quantity-section,
.item-subtotal-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.price-label {
  font-size: 11px;
  color: var(--text-secondary);
  letter-spacing: 1px;
  text-transform: uppercase;
}

.item-price {
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 500;
}

/* --- 数量控制 --- */
.quantity-control {
  display: flex;
  align-items: center;
  gap: 0;
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
}

.qty-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--neon-cyan);
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.qty-btn:hover:not(:disabled) {
  background: rgba(0, 245, 255, 0.1);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.2);
}

.qty-btn:disabled {
  color: var(--text-secondary);
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-icon {
  font-size: 16px;
  line-height: 1;
}

.qty-display {
  width: 44px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  border-left: 1px solid var(--glass-border);
  border-right: 1px solid var(--glass-border);
}

/* --- 小计 --- */
.item-subtotal {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--neon-pink), var(--sakura));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* --- 删除按钮 --- */
.delete-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  background: transparent;
  border: 1px solid rgba(255, 107, 157, 0.2);
  border-radius: 10px;
  width: 48px;
  height: 48px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--neon-pink);
}

.delete-btn:hover {
  background: rgba(255, 107, 157, 0.1);
  border-color: var(--neon-pink);
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.2);
  transform: scale(1.05);
}

.delete-icon {
  font-size: 20px;
  line-height: 1;
}

.delete-text {
  font-size: 9px;
  letter-spacing: 1px;
  font-weight: 600;
}

/* --- 底部结算栏 --- */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
  margin-top: 8px;
  position: sticky;
  bottom: 20px;
  z-index: 10;
  border: 1px solid rgba(0, 245, 255, 0.15);
  box-shadow: 0 -4px 30px rgba(0, 0, 0, 0.3);
}

.footer-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.selected-summary {
  display: flex;
  align-items: center;
  gap: 6px;
}

.summary-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.summary-value {
  font-size: 22px;
  font-weight: 700;
}

.neon-text {
  color: var(--neon-cyan);
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
}

.divider-v {
  width: 1px;
  height: 36px;
  background: linear-gradient(180deg, transparent, var(--glass-border), transparent);
}

.total-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.total-price {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 18px;
  font-weight: 600;
  color: var(--gold);
}

.amount {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--gold), #FFA500);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: none;
}

/* --- 结算按钮 --- */
.checkout-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: var(--deep-blue);
  border: none;
  border-radius: 14px;
  font-size: 18px;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.checkout-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.checkout-btn:hover:not(:disabled)::before {
  transform: translateX(100%);
}

.checkout-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow:
    0 8px 30px rgba(0, 245, 255, 0.4),
    0 0 60px rgba(179, 136, 255, 0.2);
}

.checkout-btn:active:not(:disabled) {
  transform: translateY(-1px);
}

.checkout-btn.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  filter: grayscale(0.5);
}

.checkout-text {
  position: relative;
  z-index: 1;
}

.checkout-sub {
  font-size: 10px;
  letter-spacing: 2px;
  opacity: 0.7;
  position: relative;
  z-index: 1;
}

.checkout-arrow {
  font-size: 20px;
  transition: transform 0.3s ease;
  position: relative;
  z-index: 1;
}

.checkout-btn:hover:not(:disabled) .checkout-arrow {
  transform: translateX(4px);
}

/* --- 动画 --- */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.cart-item-enter-active {
  animation: fadeInUp 0.4s ease;
}

.cart-item-leave-active {
  animation: fadeInUp 0.3s ease reverse;
}

.cart-item-move {
  transition: transform 0.4s ease;
}

/* --- 响应式设计 --- */
@media (max-width: 1024px) {
  .cart-item-card {
    grid-template-columns: 40px 70px 1fr auto auto auto;
    gap: 12px;
    padding: 16px;
  }

  .item-price-section {
    display: none;
  }
}

@media (max-width: 768px) {
  .cart-container {
    padding: 20px 12px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 22px;
  }

  .title-sub {
    display: none;
  }

  .deco-line {
    width: 40px;
  }

  .cart-item-card {
    grid-template-columns: 36px 64px 1fr;
    grid-template-rows: auto auto;
    gap: 10px;
    padding: 16px;
    border-radius: 14px;
  }

  .select-indicator {
    grid-row: 1 / 3;
  }

  .item-image-wrapper {
    grid-row: 1 / 3;
    width: 64px;
    height: 64px;
  }

  .item-info {
    grid-column: 3;
  }

  .item-price-section {
    display: none;
  }

  .item-quantity-section {
    grid-column: 2 / 4;
    flex-direction: row;
    gap: 8px;
    justify-content: flex-start;
  }

  .item-subtotal-section {
    grid-column: 2 / 4;
    flex-direction: row;
    gap: 8px;
    justify-content: flex-start;
  }

  .delete-btn {
    grid-column: 3;
    grid-row: 2;
    width: 36px;
    height: 36px;
    justify-self: end;
  }

  .delete-text {
    display: none;
  }

  .cart-footer {
    flex-direction: column;
    gap: 16px;
    padding: 16px 20px;
    bottom: 10px;
  }

  .footer-info {
    flex-direction: column;
    gap: 12px;
  }

  .divider-v {
    display: none;
  }

  .checkout-btn {
    width: 100%;
    justify-content: center;
    padding: 14px 24px;
  }

  .empty-card {
    padding: 40px 24px;
  }

  .empty-hexagon {
    width: 80px;
    height: 80px;
  }

  .empty-emoji {
    font-size: 32px;
  }

  .empty-ring.ring-1 {
    width: 110px;
    height: 110px;
  }

  .empty-ring.ring-2 {
    width: 140px;
    height: 140px;
  }

  .selection-bar {
    padding: 12px 16px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 18px;
    gap: 6px;
  }

  .title-icon {
    font-size: 18px;
  }

  .amount {
    font-size: 26px;
  }

  .item-name {
    font-size: 13px;
  }

  .item-subtotal {
    font-size: 16px;
  }

  .meta-tag {
    font-size: 10px;
  }
}
</style>
