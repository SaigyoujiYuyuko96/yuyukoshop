<template>
  <div class="order-list-page">
    <NavHeader />
    <div class="cyber-container">
      <!-- 页面标题区域 -->
      <div class="page-header">
        <div class="header-decoration">
          <span class="deco-line"></span>
          <span class="deco-diamond"></span>
          <span class="deco-line"></span>
        </div>
        <h2 class="page-title">
          <span class="title-icon">&#9830;</span>
          我的订单
          <span class="title-icon">&#9830;</span>
        </h2>
        <p class="page-subtitle">MY ORDERS</p>
      </div>

      <!-- 空状态 -->
      <div v-if="orders.length === 0" class="empty-state">
        <div class="empty-icon">
          <div class="empty-circle"></div>
          <span class="empty-text-icon">?</span>
        </div>
        <p class="empty-desc">暂无订单数据</p>
        <p class="empty-sub">EMPTY ORDER LIST</p>
        <button class="cyber-btn neon-pink" @click="$router.push('/products')">
          <span class="btn-glow"></span>
          去购物
        </button>
      </div>

      <!-- 订单列表 -->
      <div v-else class="order-list">
        <div
          v-for="(order, index) in orders"
          :key="order.id"
          class="glass-card order-card"
          :style="{ animationDelay: `${index * 0.08}s` }"
        >
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-no">
              <span class="label-tag">ORDER NO.</span>
              <span class="order-no-value">{{ order.orderNo }}</span>
            </div>
            <span :class="['neon-badge', `badge-${order.status}`]">
              <span class="badge-dot"></span>
              {{ order.statusText }}
            </span>
          </div>

          <!-- 订单商品 -->
          <div class="order-items">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="order-item"
            >
              <div class="item-image-wrapper">
                <el-image
                  :src="item.productThumbnail || 'https://via.placeholder.com/80x80?text=No+Image'"
                  fit="cover"
                  class="item-image"
                />
                <div class="image-scanline"></div>
              </div>
              <div class="item-info">
                <h4 class="item-name">{{ item.productName }}</h4>
                <div class="item-meta">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-qty">x {{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-time">
              <span class="time-icon">&#9201;</span>
              {{ order.createTime }}
            </div>
            <div class="order-actions">
              <span class="total-price">
                <span class="total-label">合计</span>
                <span class="total-amount">¥{{ order.totalAmount }}</span>
              </span>
              <button
                v-if="order.status === 0"
                class="cyber-btn neon-cyan sm"
                @click="goToPay(order.id)"
              >
                <span class="btn-glow"></span>
                去支付
              </button>
              <button
                v-if="order.status === 0"
                class="cyber-btn outline sm"
                @click="cancelOrder(order.id)"
              >
                取消订单
              </button>
              <button
                v-if="order.status === 2"
                class="cyber-btn neon-green sm"
                @click="confirmReceive(order.id)"
              >
                <span class="btn-glow"></span>
                确认收货
              </button>
              <button
                class="cyber-btn ghost sm"
                @click="$router.push(`/orders/${order.id}`)"
              >
                查看详情
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            layout="total, prev, pager, next"
            @change="fetchOrders"
            class="cyber-pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const fetchOrders = async () => {
  try {
    const res = await request.get('/orders', { params: { page: page.value, size: size.value } })
    orders.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
    ElMessage.error('获取订单列表失败')
  }
}

const goToPay = (id) => {
  router.push(`/orders/${id}/pay`)
}

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确认取消该订单吗？', '提示', { type: 'warning' })
    await request.put(`/orders/${id}/cancel`)
    ElMessage.success('取消订单成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const confirmReceive = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', { type: 'warning' })
    await request.put(`/orders/${id}/receive`)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
/* ===== CSS 变量 ===== */
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

/* ===== 全局页面 ===== */
.order-list-page {
  min-height: 100vh;
  background: var(--deep-blue);
  background-image:
    radial-gradient(ellipse at 20% 50%, rgba(179, 136, 255, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(0, 245, 255, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 80%, rgba(255, 107, 157, 0.05) 0%, transparent 50%);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  color: var(--text-primary);
  padding-bottom: 60px;
}

/* ===== 容器 ===== */
.cyber-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* ===== 页面标题 ===== */
.page-header {
  text-align: center;
  margin-bottom: 40px;
  animation: fadeSlideDown 0.6s ease-out;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.deco-line {
  display: inline-block;
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), transparent);
}

.deco-diamond {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: var(--neon-cyan);
  transform: rotate(45deg);
  box-shadow: 0 0 8px var(--neon-cyan);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple), var(--sakura));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.title-icon {
  font-size: 14px;
  -webkit-text-fill-color: var(--neon-pink);
  margin: 0 6px;
}

.page-subtitle {
  font-size: 11px;
  letter-spacing: 8px;
  color: var(--text-secondary);
  margin-top: 6px;
  text-transform: uppercase;
}

/* ===== 玻璃卡片 ===== */
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.glass-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.3), rgba(179, 136, 255, 0.3), transparent);
}

.glass-card:hover {
  border-color: rgba(0, 245, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 20px rgba(0, 245, 255, 0.05);
  transform: translateY(-2px);
}

/* ===== 订单卡片 ===== */
.order-card {
  margin-bottom: 20px;
  animation: fadeSlideUp 0.5s ease-out both;
}

/* ===== 订单头部 ===== */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 16px;
}

.order-no {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label-tag {
  font-size: 10px;
  letter-spacing: 2px;
  color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.08);
  padding: 3px 10px;
  border-radius: 4px;
  border: 1px solid rgba(0, 245, 255, 0.15);
  font-weight: 600;
}

.order-no-value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* ===== 霓虹状态徽章 ===== */
.neon-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  position: relative;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  animation: pulse-dot 2s infinite;
}

.badge-0 {
  background: rgba(255, 215, 0, 0.1);
  color: var(--gold);
  border: 1px solid rgba(255, 215, 0, 0.25);
}
.badge-0 .badge-dot { background: var(--gold); box-shadow: 0 0 6px var(--gold); }

.badge-1 {
  background: rgba(0, 245, 255, 0.1);
  color: var(--neon-cyan);
  border: 1px solid rgba(0, 245, 255, 0.25);
}
.badge-1 .badge-dot { background: var(--neon-cyan); box-shadow: 0 0 6px var(--neon-cyan); }

.badge-2 {
  background: rgba(0, 255, 136, 0.1);
  color: #00FF88;
  border: 1px solid rgba(0, 255, 136, 0.25);
}
.badge-2 .badge-dot { background: #00FF88; box-shadow: 0 0 6px #00FF88; }

.badge-3 {
  background: rgba(179, 136, 255, 0.1);
  color: var(--neon-purple);
  border: 1px solid rgba(179, 136, 255, 0.25);
}
.badge-3 .badge-dot { background: var(--neon-purple); box-shadow: 0 0 6px var(--neon-purple); }

.badge--1 {
  background: rgba(255, 107, 157, 0.1);
  color: var(--neon-pink);
  border: 1px solid rgba(255, 107, 157, 0.25);
}
.badge--1 .badge-dot { background: var(--neon-pink); box-shadow: 0 0 6px var(--neon-pink); }

/* ===== 订单商品 ===== */
.order-items {
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
  transition: all 0.2s ease;
  border-radius: 8px;
}

.order-item:hover {
  background: rgba(255, 255, 255, 0.02);
  padding-left: 8px;
}

.item-image-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid var(--glass-border);
}

.item-image {
  width: 80px;
  height: 80px;
  display: block;
}

.image-scanline {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 245, 255, 0.03) 2px,
    rgba(0, 245, 255, 0.03) 4px
  );
  pointer-events: none;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-price {
  font-size: 14px;
  color: var(--sakura);
  font-weight: 600;
}

.item-qty {
  font-size: 13px;
  color: var(--text-secondary);
}

/* ===== 订单底部 ===== */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  flex-wrap: wrap;
  gap: 12px;
}

.order-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.time-icon {
  font-size: 14px;
  color: var(--neon-purple);
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.total-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-right: 6px;
}

.total-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.total-amount {
  font-size: 20px;
  font-weight: 700;
  color: var(--neon-pink);
  text-shadow: 0 0 12px rgba(255, 107, 157, 0.4);
}

/* ===== 赛博按钮 ===== */
.cyber-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.cyber-btn.sm {
  padding: 6px 16px;
  font-size: 12px;
  border-radius: 6px;
}

.cyber-btn.neon-cyan {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.15), rgba(0, 245, 255, 0.05));
  color: var(--neon-cyan);
  border: 1px solid rgba(0, 245, 255, 0.35);
}
.cyber-btn.neon-cyan:hover {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.25), rgba(0, 245, 255, 0.1));
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2), inset 0 0 20px rgba(0, 245, 255, 0.05);
  transform: translateY(-1px);
}

.cyber-btn.neon-pink {
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.2), rgba(255, 107, 157, 0.08));
  color: var(--neon-pink);
  border: 1px solid rgba(255, 107, 157, 0.4);
  padding: 12px 32px;
  font-size: 15px;
}
.cyber-btn.neon-pink:hover {
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.3), rgba(255, 107, 157, 0.15));
  box-shadow: 0 0 24px rgba(255, 107, 157, 0.25), inset 0 0 24px rgba(255, 107, 157, 0.05);
  transform: translateY(-2px);
}

.cyber-btn.neon-green {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.15), rgba(0, 255, 136, 0.05));
  color: #00FF88;
  border: 1px solid rgba(0, 255, 136, 0.35);
}
.cyber-btn.neon-green:hover {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.25), rgba(0, 255, 136, 0.1));
  box-shadow: 0 0 20px rgba(0, 255, 136, 0.2), inset 0 0 20px rgba(0, 255, 136, 0.05);
  transform: translateY(-1px);
}

.cyber-btn.outline {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid rgba(255, 255, 255, 0.12);
}
.cyber-btn.outline:hover {
  color: var(--neon-pink);
  border-color: rgba(255, 107, 157, 0.3);
  background: rgba(255, 107, 157, 0.05);
}

.cyber-btn.ghost {
  background: transparent;
  color: var(--neon-purple);
  border: 1px solid rgba(179, 136, 255, 0.2);
}
.cyber-btn.ghost:hover {
  border-color: rgba(179, 136, 255, 0.4);
  background: rgba(179, 136, 255, 0.08);
  box-shadow: 0 0 16px rgba(179, 136, 255, 0.1);
}

.btn-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
  pointer-events: none;
}

.cyber-btn:active .btn-glow {
  width: 200px;
  height: 200px;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  animation: fadeSlideUp 0.6s ease-out;
}

.empty-icon {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 24px;
}

.empty-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px dashed rgba(179, 136, 255, 0.3);
  animation: rotate-slow 20s linear infinite;
}

.empty-text-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 36px;
  color: var(--neon-purple);
  text-shadow: 0 0 20px rgba(179, 136, 255, 0.4);
}

.empty-desc {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.empty-sub {
  font-size: 11px;
  letter-spacing: 4px;
  color: var(--text-secondary);
  margin-bottom: 30px;
}

/* ===== 分页 ===== */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.cyber-pagination :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--text-secondary);
  --el-pagination-button-bg-color: var(--glass-bg);
  --el-pagination-button-color: var(--text-primary);
  --el-pagination-hover-color: var(--neon-cyan);
}

.cyber-pagination :deep(.el-pager li) {
  background: var(--glass-bg) !important;
  color: var(--text-secondary) !important;
  border: 1px solid var(--glass-border);
  border-radius: 6px;
  margin: 0 2px;
  backdrop-filter: blur(10px);
}

.cyber-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.15), rgba(179, 136, 255, 0.15)) !important;
  color: var(--neon-cyan) !important;
  border-color: rgba(0, 245, 255, 0.3);
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.15);
}

.cyber-pagination :deep(.btn-prev),
.cyber-pagination :deep(.btn-next) {
  background: var(--glass-bg) !important;
  color: var(--text-secondary) !important;
  border: 1px solid var(--glass-border);
  border-radius: 6px;
  backdrop-filter: blur(10px);
}

.cyber-pagination :deep(.btn-prev:hover),
.cyber-pagination :deep(.btn-next:hover) {
  color: var(--neon-cyan) !important;
  border-color: rgba(0, 245, 255, 0.2);
}

.cyber-pagination :deep(.el-pagination__total) {
  color: var(--text-secondary) !important;
}

/* ===== 动画 ===== */
@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeSlideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

@keyframes rotate-slow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .cyber-container {
    padding: 20px 12px;
  }

  .page-title {
    font-size: 22px;
    letter-spacing: 2px;
  }

  .order-card {
    padding: 16px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .order-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .total-amount {
    font-size: 18px;
  }

  .order-no {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .item-image-wrapper,
  .item-image {
    width: 64px;
    height: 64px;
  }

  .item-name {
    font-size: 13px;
  }

  .cyber-btn.sm {
    padding: 5px 12px;
    font-size: 11px;
  }
}
</style>
