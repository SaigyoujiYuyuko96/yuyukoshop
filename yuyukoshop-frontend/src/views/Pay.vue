<template>
  <div class="pay-page">
    <NavHeader />
    <div class="cyber-bg">
      <div class="grid-lines"></div>
      <div class="floating-particles">
        <span v-for="i in 6" :key="i" class="particle" :class="'p-' + i"></span>
      </div>
    </div>
    <div class="container">
      <div class="page-title-area">
        <h2 class="page-title">
          <span class="title-deco">&lt;/&gt;</span>
          订单支付
          <span class="title-sub">PAYMENT GATEWAY</span>
        </h2>
        <div class="title-line"></div>
      </div>

      <div class="pay-card" v-loading="loading" element-loading-background="rgba(26,26,46,0.8)">
        <!-- Order Info Section -->
        <div v-if="order" class="order-section">
          <div class="section-label">
            <span class="label-icon">&#9670;</span>
            ORDER INFO // 订单信息
          </div>
          <div class="info-grid">
            <div class="info-card">
              <div class="info-label">订单编号</div>
              <div class="info-value mono">{{ order.orderNo }}</div>
            </div>
            <div class="info-card amount-card">
              <div class="info-label">订单金额</div>
              <div class="info-value amount">¥{{ order.totalAmount }}</div>
              <div class="amount-glow"></div>
            </div>
            <div class="info-card">
              <div class="info-label">收货人</div>
              <div class="info-value">{{ order.receiverName }} {{ order.receiverPhone }}</div>
            </div>
            <div class="info-card full-width">
              <div class="info-label">收货地址</div>
              <div class="info-value">{{ order.shippingAddress }}</div>
            </div>
          </div>
        </div>

        <div class="divider-line">
          <span class="divider-dot"></span>
          <span class="divider-dot"></span>
          <span class="divider-dot"></span>
        </div>

        <!-- Payment Method Section -->
        <div class="pay-methods">
          <div class="section-label">
            <span class="label-icon">&#9670;</span>
            SELECT METHOD // 选择支付方式
          </div>
          <div class="method-list">
            <div
              class="method-item"
              :class="{ active: selectedMethod === 'alipay' }"
              @click="selectedMethod = 'alipay'"
            >
              <div class="method-indicator"></div>
              <div class="method-icon alipay-icon">
                <el-icon><Wallet /></el-icon>
              </div>
              <div class="method-info">
                <span class="method-name">支付宝</span>
                <span class="method-desc">Alipay - 推荐使用</span>
              </div>
              <div class="method-check">
                <span class="check-circle"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="pay-actions">
          <button
            class="cyber-btn pay-btn"
            :class="{ loading: payLoading }"
            :disabled="payLoading"
            @click="handlePay"
          >
            <span class="btn-shimmer"></span>
            <span class="btn-content">
              <span v-if="payLoading" class="btn-loading-spinner"></span>
              <span v-else>确认支付 ¥{{ order?.totalAmount }}</span>
            </span>
          </button>
          <button class="cyber-btn cancel-btn" @click="$router.push('/orders')">
            <span class="btn-content">取消支付</span>
          </button>
        </div>
      </div>

      <div class="footer-deco">
        <span>YUYUKO SHOP</span>
        <span class="footer-dots">:::</span>
        <span>SECURE PAYMENT</span>
      </div>
    </div>
    <div v-html="alipayForm" style="display: none;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id
const order = ref(null)
const loading = ref(false)
const payLoading = ref(false)
const selectedMethod = ref('alipay')
const alipayForm = ref('')

onMounted(async () => { await fetchOrderDetail() })

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/orders/${orderId}`)
    order.value = res.data
    if (order.value.status !== 0) {
      ElMessage.warning('该订单已支付或已取消')
      router.push('/orders')
    }
  } catch (e) {
    ElMessage.error('获取订单信息失败')
    console.error(e)
  } finally { loading.value = false }
}

const handlePay = async () => { if (selectedMethod.value === 'alipay') { await payWithAlipay() } }

const payWithAlipay = async () => {
  payLoading.value = true
  try {
    const res = await request.post(`/payment/alipay/${orderId}`)
    if (res.code === 200) {
      alipayForm.value = res.data
      ElMessageBox.confirm('即将跳转到支付宝支付页面，请在新页面完成支付', '确认支付', { confirmButtonText: '去支付', cancelButtonText: '取消', type: 'info' }).then(() => {
        setTimeout(() => { const form = document.querySelector('form[name=punchout_form]'); if (form) { form.submit() } }, 100)
        startPollingOrderStatus()
      })
    } else { ElMessage.error(res.message || '创建支付失败') }
  } catch (e) { ElMessage.error('支付请求失败'); console.error(e) } finally { payLoading.value = false }
}

const startPollingOrderStatus = () => {
  let count = 0
  const maxCount = 60
  const timer = setInterval(async () => {
    count++
    try {
      const res = await request.get(`/orders/${orderId}`)
      if (res.data.status === 1) { clearInterval(timer); ElMessage.success('支付成功！'); router.push(`/orders/${orderId}`) }
    } catch (e) { console.error('查询订单状态失败', e) }
    if (count >= maxCount) { clearInterval(timer); ElMessage.info('支付结果查询超时，请稍后到订单页面查看') }
  }, 5000)
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
  --glass-bg: rgba(255,255,255,0.05);
  --glass-border: rgba(255,255,255,0.1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.pay-page {
  min-height: 100vh;
  background: var(--deep-blue);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  color: var(--text-primary);
  position: relative;
  overflow-x: hidden;
}

/* === Cyberpunk Background === */
.cyber-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0,245,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,245,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: gridScroll 20s linear infinite;
}

@keyframes gridScroll {
  0% { transform: translateY(0); }
  100% { transform: translateY(60px); }
}

.floating-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  animation: floatUp linear infinite;
  opacity: 0;
}

.p-1 { left: 10%; background: var(--neon-cyan); animation-duration: 8s; animation-delay: 0s; }
.p-2 { left: 25%; background: var(--neon-pink); animation-duration: 12s; animation-delay: 2s; }
.p-3 { left: 45%; background: var(--sakura); animation-duration: 10s; animation-delay: 4s; }
.p-4 { left: 65%; background: var(--neon-purple); animation-duration: 9s; animation-delay: 1s; }
.p-5 { left: 80%; background: var(--neon-cyan); animation-duration: 11s; animation-delay: 3s; }
.p-6 { left: 92%; background: var(--gold); animation-duration: 7s; animation-delay: 5s; }

@keyframes floatUp {
  0% { transform: translateY(100vh) scale(0); opacity: 0; }
  10% { opacity: 0.8; }
  90% { opacity: 0.8; }
  100% { transform: translateY(-10vh) scale(1); opacity: 0; }
}

/* === Container === */
.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 40px 20px 60px;
  position: relative;
  z-index: 1;
}

/* === Page Title === */
.page-title-area {
  text-align: center;
  margin-bottom: 36px;
}

.page-title {
  font-size: 28px;
  font-weight: 900;
  letter-spacing: 4px;
  color: var(--text-primary);
  position: relative;
  display: inline-block;
}

.title-deco {
  color: var(--neon-cyan);
  font-size: 16px;
  margin-right: 10px;
  font-weight: 400;
  text-shadow: 0 0 10px var(--neon-cyan);
}

.title-sub {
  display: block;
  font-size: 11px;
  font-weight: 400;
  color: var(--text-secondary);
  letter-spacing: 6px;
  margin-top: 6px;
  font-family: 'Noto Sans SC', sans-serif;
}

.title-line {
  width: 200px;
  height: 2px;
  margin: 16px auto 0;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-pink), transparent);
  border-radius: 2px;
}

/* === Pay Card (Glass Morphism) === */
.pay-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 36px;
  position: relative;
  overflow: hidden;
  box-shadow:
    0 0 30px rgba(0,245,255,0.05),
    0 0 60px rgba(179,136,255,0.03),
    inset 0 1px 0 rgba(255,255,255,0.08);
}

.pay-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple), var(--neon-pink), var(--sakura));
  border-radius: 16px 16px 0 0;
}

.pay-card::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 20%, rgba(0,245,255,0.03) 0%, transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(179,136,255,0.03) 0%, transparent 50%);
  pointer-events: none;
}

/* === Section Label === */
.section-label {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 3px;
  color: var(--neon-cyan);
  text-transform: uppercase;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  text-shadow: 0 0 8px rgba(0,245,255,0.3);
}

.label-icon {
  color: var(--neon-pink);
  font-size: 10px;
  text-shadow: 0 0 6px var(--neon-pink);
}

/* === Order Info Grid === */
.order-section {
  position: relative;
  z-index: 1;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px;
  padding: 20px;
  position: relative;
  transition: all 0.3s ease;
}

.info-card:hover {
  border-color: rgba(0,245,255,0.2);
  background: rgba(0,245,255,0.03);
  box-shadow: 0 0 20px rgba(0,245,255,0.05);
}

.info-card.full-width {
  grid-column: 1 / -1;
}

.info-card.amount-card {
  border-color: rgba(255,107,157,0.2);
  background: rgba(255,107,157,0.05);
  overflow: hidden;
}

.amount-glow {
  position: absolute;
  bottom: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(255,107,157,0.15), transparent 70%);
  pointer-events: none;
}

.info-label {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-secondary);
  letter-spacing: 2px;
  text-transform: uppercase;
  margin-bottom: 10px;
}

.info-value {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  word-break: break-all;
}

.info-value.mono {
  font-family: 'Courier New', monospace;
  color: var(--neon-cyan);
  font-size: 14px;
  letter-spacing: 1px;
}

.info-value.amount {
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, var(--neon-pink), var(--sakura), var(--gold));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(255,107,157,0.4));
  line-height: 1.2;
}

/* === Divider === */
.divider-line {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 32px 0;
  position: relative;
  z-index: 1;
}

.divider-line::before,
.divider-line::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--glass-border), transparent);
}

.divider-dot {
  width: 4px;
  height: 4px;
  background: var(--neon-purple);
  border-radius: 50%;
  box-shadow: 0 0 6px var(--neon-purple);
}

/* === Payment Methods === */
.pay-methods {
  position: relative;
  z-index: 1;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.method-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(0,245,255,0.05), rgba(179,136,255,0.05));
  opacity: 0;
  transition: opacity 0.4s ease;
}

.method-item:hover {
  border-color: rgba(0,245,255,0.15);
  transform: translateY(-1px);
}

.method-item:hover::before {
  opacity: 1;
}

.method-item.active {
  border-color: var(--neon-cyan);
  box-shadow:
    0 0 20px rgba(0,245,255,0.1),
    0 0 40px rgba(0,245,255,0.05),
    inset 0 0 20px rgba(0,245,255,0.03);
}

.method-item.active::before {
  opacity: 1;
}

.method-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--neon-cyan);
  border-radius: 0 3px 3px 0;
  opacity: 0;
  transition: opacity 0.3s ease;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.method-item.active .method-indicator {
  opacity: 1;
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 22px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}

.alipay-icon {
  background: linear-gradient(135deg, #1677FF, #4096FF);
  color: white;
  box-shadow: 0 4px 15px rgba(22,119,255,0.3);
}

.method-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.method-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  letter-spacing: 0.5px;
}

.method-check {
  position: relative;
  z-index: 1;
  margin-left: 12px;
}

.check-circle {
  display: block;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.15);
  transition: all 0.3s ease;
  position: relative;
}

.method-item.active .check-circle {
  border-color: var(--neon-cyan);
  background: var(--neon-cyan);
  box-shadow: 0 0 12px rgba(0,245,255,0.4);
}

.method-item.active .check-circle::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotate(45deg);
  width: 6px;
  height: 10px;
  border: solid var(--deep-blue);
  border-width: 0 2px 2px 0;
}

/* === Pay Actions === */
.pay-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid rgba(255,255,255,0.06);
  position: relative;
  z-index: 1;
}

.cyber-btn {
  position: relative;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-weight: 700;
  letter-spacing: 1px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.pay-btn {
  padding: 16px 56px;
  font-size: 16px;
  color: white;
  background: linear-gradient(135deg, var(--neon-pink), var(--neon-purple), var(--neon-cyan));
  background-size: 200% 200%;
  animation: gradientShift 4s ease infinite;
  box-shadow:
    0 4px 20px rgba(255,107,157,0.3),
    0 0 40px rgba(179,136,255,0.1);
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow:
    0 6px 30px rgba(255,107,157,0.4),
    0 0 60px rgba(179,136,255,0.15);
}

.pay-btn:active {
  transform: translateY(0);
}

.pay-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-shimmer {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255,255,255,0.2),
    transparent
  );
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.btn-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-loading-spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.cancel-btn {
  padding: 16px 40px;
  font-size: 15px;
  color: var(--text-secondary);
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.1);
}

.cancel-btn:hover {
  color: var(--text-primary);
  border-color: rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.08);
}

/* === Footer Decoration === */
.footer-deco {
  text-align: center;
  margin-top: 40px;
  font-size: 11px;
  letter-spacing: 4px;
  color: var(--text-secondary);
  opacity: 0.4;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.footer-dots {
  color: var(--neon-purple);
  letter-spacing: 2px;
}

/* === Responsive === */
@media (max-width: 640px) {
  .container {
    padding: 24px 16px 40px;
  }

  .pay-card {
    padding: 24px 18px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-value.amount {
    font-size: 26px;
  }

  .page-title {
    font-size: 22px;
  }

  .pay-actions {
    flex-direction: column;
    gap: 12px;
  }

  .pay-btn, .cancel-btn {
    width: 100%;
    padding: 14px;
  }
}

/* === Element Plus Loading Override === */
:deep(.el-loading-mask) {
  background-color: rgba(26,26,46,0.85) !important;
  border-radius: 16px;
}

:deep(.el-loading-spinner .circular) {
  width: 42px;
  height: 42px;
}

:deep(.el-loading-spinner .path) {
  stroke: var(--neon-cyan);
  filter: drop-shadow(0 0 6px rgba(0,245,255,0.5));
}
</style>
