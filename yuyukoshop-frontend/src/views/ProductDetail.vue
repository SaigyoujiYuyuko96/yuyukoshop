<template>
  <div class="cyber-detail-wrapper">
    <NavHeader />
    <div class="cyber-detail-container" v-loading="loading" element-loading-background="rgba(26,26,46,0.8)">
      <!-- Glitch scan line overlay -->
      <div class="scanline-overlay"></div>

      <el-row :gutter="40" v-if="product" class="product-main-row">
        <!-- Product Image Section -->
        <el-col :xs="24" :sm="24" :md="10" :lg="10">
          <div class="image-showcase">
            <div class="image-frame">
              <div class="neon-corner neon-corner--tl"></div>
              <div class="neon-corner neon-corner--tr"></div>
              <div class="neon-corner neon-corner--bl"></div>
              <div class="neon-corner neon-corner--br"></div>
              <div class="glow-ring"></div>
              <el-image
                :src="product.thumbnail || 'https://via.placeholder.com/400x400?text=No+Image'"
                fit="cover"
                class="product-image"
              />
              <div class="image-overlay">
                <span class="cyber-tag">PRODUCT</span>
              </div>
            </div>
          </div>
        </el-col>

        <!-- Product Info Section -->
        <el-col :xs="24" :sm="24" :md="14" :lg="14">
          <div class="info-panel glass-card">
            <!-- Category badge -->
            <div class="category-badge">
              <span class="badge-dot"></span>
              {{ product.categoryName || '未分类' }}
            </div>

            <h1 class="product-name">
              <span class="name-deco">//</span>
              {{ product.name }}
            </h1>

            <p class="product-desc">{{ product.description }}</p>

            <!-- Price display -->
            <div class="price-block">
              <div class="price-label">PRICE</div>
              <div class="price-value">
                <span class="currency">¥</span>{{ product.price }}
              </div>
              <div class="price-glow"></div>
            </div>

            <!-- Meta info badges -->
            <div class="meta-badges">
              <div class="meta-badge">
                <span class="meta-icon">📦</span>
                <span class="meta-label">库存</span>
                <span class="meta-value">{{ product.stock }}</span>
              </div>
              <div class="meta-badge">
                <span class="meta-icon">🔥</span>
                <span class="meta-label">销量</span>
                <span class="meta-value">{{ product.sales }}</span>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="action-bar">
              <div class="qty-selector glass-card-inner">
                <button class="qty-btn" @click="quantity > 1 && quantity--" :disabled="quantity <= 1">−</button>
                <span class="qty-value">{{ quantity }}</span>
                <button class="qty-btn" @click="quantity < product.stock && quantity++" :disabled="quantity >= product.stock">+</button>
              </div>
              <button class="cyber-btn cyber-btn--cart" @click="addToCart">
                <span class="btn-icon">🛒</span>
                <span class="btn-text">加入购物车</span>
                <span class="btn-glitch"></span>
              </button>
              <button class="cyber-btn cyber-btn--fav" :class="{ 'is-favorited': isFavorited }" @click="toggleFavorite">
                <span class="btn-icon">{{ isFavorited ? '💖' : '🤍' }}</span>
                <span class="btn-text">{{ isFavorited ? '已收藏' : '收藏' }}</span>
                <span class="btn-glitch"></span>
              </button>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Reviews Section -->
      <div class="reviews-section" v-if="product">
        <div class="section-header">
          <div class="section-line"></div>
          <h3 class="section-title">
            <span class="title-icon">💬</span>
            商品评价
            <span class="title-count" v-if="reviews.length">({{ reviews.length }})</span>
          </h3>
          <div class="section-line"></div>
        </div>

        <el-empty v-if="reviews.length === 0" description="暂无评价" class="empty-state">
          <template #description>
            <span class="empty-text">暂无评价 — 成为第一个评价者吧</span>
          </template>
        </el-empty>

        <div v-else class="review-list">
          <div v-for="(review, index) in reviews" :key="review.id" class="review-card glass-card" :style="{ animationDelay: `${index * 0.08}s` }">
            <div class="review-header">
              <div class="reviewer-avatar">
                <UserPopover :userId="review.userId" :userName="review.username" :size="36" v-if="review.userId">
                  <el-avatar :size="36" :src="review.userAvatar || defaultAvatar" class="avatar-cyber">
                    <el-icon :size="16"><User /></el-icon>
                  </el-avatar>
                </UserPopover>
                <el-avatar v-else :size="36" class="avatar-cyber">
                  <el-icon :size="16"><User /></el-icon>
                </el-avatar>
                <div class="avatar-ring"></div>
              </div>
              <div class="reviewer-info">
                <span class="reviewer-name">{{ review.username }}</span>
                <el-rate v-model="review.rating" disabled class="cyber-rate" />
              </div>
              <span class="review-time">{{ review.createTime }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import UserPopover from '@/components/UserPopover.vue'
import request from '@/api/request'
import { ShoppingCart, Star, User } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const reviews = ref([])
const quantity = ref(1)
const loading = ref(false)
const isFavorited = ref(false)
const defaultAvatar = 'https://via.placeholder.com/50?text=User'

const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await request.get(`/products/${route.params.id}`)
    product.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
  try {
    const res = await request.get(`/reviews/product/${route.params.id}`)
    reviews.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const addToCart = async () => {
  try {
    await request.post('/cart/items', {
      productId: product.value.id,
      quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch (e) {
    console.error(e)
  }
}

const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await request.delete(`/favorites/${product.value.id}`)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await request.post(`/favorites/${product.value.id}`)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchProduct()
  fetchReviews()
})
</script>

<style scoped>
/* ============================================
   幽幽子Shop — 日系赛博朋克动漫风 ProductDetail
   ============================================ */

@import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic:wght@400;500;700;900&display=swap');

/* ---------- Animations ---------- */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes neonPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

@keyframes glowRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes scanline {
  0% { top: -10%; }
  100% { top: 110%; }
}

@keyframes borderGlow {
  0%, 100% { border-color: var(--neon-cyan); box-shadow: 0 0 8px var(--neon-cyan); }
  33% { border-color: var(--neon-purple); box-shadow: 0 0 8px var(--neon-purple); }
  66% { border-color: var(--neon-pink); box-shadow: 0 0 8px var(--neon-pink); }
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

/* ---------- Wrapper ---------- */
.cyber-detail-wrapper {
  min-height: 100vh;
  background: var(--deep-blue, #1A1A2E);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  color: var(--text-primary, #E8E8E8);
  animation: fadeIn 0.5s ease-out;
}

.cyber-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px 60px;
  position: relative;
  overflow: hidden;
}

/* ---------- Scanline Overlay ---------- */
.scanline-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 245, 255, 0.015) 2px,
    rgba(0, 245, 255, 0.015) 4px
  );
}

.scanline-overlay::after {
  content: '';
  position: absolute;
  left: 0;
  width: 100%;
  height: 60px;
  background: linear-gradient(
    180deg,
    transparent,
    rgba(0, 245, 255, 0.06),
    transparent
  );
  animation: scanline 4s linear infinite;
}

/* ---------- Glass Card ---------- */
.glass-card {
  background: var(--glass-bg, rgba(255, 255, 255, 0.05));
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--glass-border, rgba(255, 255, 255, 0.1));
  border-radius: 16px;
  animation: slideUp 0.6s ease-out both;
}

.glass-card-inner {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
}

/* ---------- Product Main Row ---------- */
.product-main-row {
  animation: slideUp 0.6s ease-out both;
}

/* ---------- Image Showcase ---------- */
.image-showcase {
  animation: fadeIn 0.8s ease-out both;
}

.image-frame {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  padding: 4px;
  background: linear-gradient(135deg, var(--neon-cyan, #00F5FF), var(--neon-purple, #B388FF), var(--neon-pink, #FF6B9D));
  background-size: 300% 300%;
  animation: borderGlow 4s ease-in-out infinite, shimmer 3s linear infinite;
}

.product-image {
  width: 100%;
  border-radius: 12px;
  display: block;
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0.1) 0%,
    transparent 30%,
    transparent 70%,
    rgba(26, 26, 46, 0.6) 100%
  );
  pointer-events: none;
  border-radius: 12px;
}

.cyber-tag {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  color: var(--neon-cyan, #00F5FF);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 3px;
  padding: 4px 12px;
  border: 1px solid rgba(0, 245, 255, 0.3);
  border-radius: 4px;
}

/* Neon corners */
.neon-corner {
  position: absolute;
  width: 20px;
  height: 20px;
  z-index: 2;
}

.neon-corner--tl {
  top: -2px; left: -2px;
  border-top: 2px solid var(--neon-cyan, #00F5FF);
  border-left: 2px solid var(--neon-cyan, #00F5FF);
  border-radius: 4px 0 0 0;
  box-shadow: -2px -2px 8px var(--neon-cyan, #00F5FF);
}

.neon-corner--tr {
  top: -2px; right: -2px;
  border-top: 2px solid var(--neon-purple, #B388FF);
  border-right: 2px solid var(--neon-purple, #B388FF);
  border-radius: 0 4px 0 0;
  box-shadow: 2px -2px 8px var(--neon-purple, #B388FF);
}

.neon-corner--bl {
  bottom: -2px; left: -2px;
  border-bottom: 2px solid var(--neon-pink, #FF6B9D);
  border-left: 2px solid var(--neon-pink, #FF6B9D);
  border-radius: 0 0 0 4px;
  box-shadow: -2px 2px 8px var(--neon-pink, #FF6B9D);
}

.neon-corner--br {
  bottom: -2px; right: -2px;
  border-bottom: 2px solid var(--sakura, #FFB7C5);
  border-right: 2px solid var(--sakura, #FFB7C5);
  border-radius: 0 0 4px 0;
  box-shadow: 2px 2px 8px var(--sakura, #FFB7C5);
}

.glow-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 20px;
  background: conic-gradient(
    from 0deg,
    transparent,
    var(--neon-cyan, #00F5FF),
    transparent,
    var(--neon-purple, #B388FF),
    transparent,
    var(--neon-pink, #FF6B9D),
    transparent
  );
  opacity: 0.3;
  filter: blur(12px);
  animation: glowRotate 6s linear infinite;
  z-index: -1;
}

/* ---------- Info Panel ---------- */
.info-panel {
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.info-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(
    90deg,
    var(--neon-cyan, #00F5FF),
    var(--neon-purple, #B388FF),
    var(--neon-pink, #FF6B9D),
    var(--sakura, #FFB7C5)
  );
}

/* Category badge */
.category-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(179, 136, 255, 0.12);
  border: 1px solid rgba(179, 136, 255, 0.3);
  color: var(--neon-purple, #B388FF);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 2px;
  text-transform: uppercase;
  padding: 6px 16px;
  border-radius: 20px;
  margin-bottom: 20px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--neon-purple, #B388FF);
  animation: neonPulse 1.5s ease-in-out infinite;
  box-shadow: 0 0 6px var(--neon-purple, #B388FF);
}

/* Product name */
.product-name {
  font-size: 28px;
  font-weight: 900;
  color: var(--text-primary, #E8E8E8);
  margin-bottom: 16px;
  line-height: 1.3;
  letter-spacing: 1px;
}

.name-deco {
  color: var(--neon-cyan, #00F5FF);
  margin-right: 8px;
  font-weight: 400;
  opacity: 0.6;
}

/* Description */
.product-desc {
  color: var(--text-secondary, #9CA3AF);
  line-height: 1.8;
  margin-bottom: 24px;
  font-size: 14px;
}

/* Price block */
.price-block {
  position: relative;
  margin-bottom: 28px;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.08), rgba(179, 136, 255, 0.08));
  border: 1px solid rgba(255, 107, 157, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.price-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 4px;
  color: var(--neon-pink, #FF6B9D);
  margin-bottom: 4px;
  text-transform: uppercase;
}

.price-value {
  font-size: 36px;
  font-weight: 900;
  background: linear-gradient(135deg, var(--neon-pink, #FF6B9D), var(--sakura, #FFB7C5), var(--neon-purple, #B388FF));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.currency {
  font-size: 22px;
}

.price-glow {
  position: absolute;
  bottom: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(255, 107, 157, 0.15), transparent 70%);
  border-radius: 50%;
}

/* Meta badges */
.meta-badges {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.meta-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: var(--glass-bg, rgba(255, 255, 255, 0.05));
  border: 1px solid var(--glass-border, rgba(255, 255, 255, 0.1));
  border-radius: 10px;
  transition: all 0.3s ease;
}

.meta-badge:hover {
  border-color: var(--neon-cyan, #00F5FF);
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.15);
  transform: translateY(-2px);
}

.meta-icon {
  font-size: 16px;
}

.meta-label {
  font-size: 12px;
  color: var(--text-secondary, #9CA3AF);
  letter-spacing: 1px;
}

.meta-value {
  font-size: 16px;
  font-weight: 700;
  color: var(--neon-cyan, #00F5FF);
}

/* ---------- Action Bar ---------- */
.action-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

/* Quantity selector */
.qty-selector {
  display: flex;
  align-items: center;
  gap: 0;
  overflow: hidden;
}

.qty-btn {
  width: 40px;
  height: 40px;
  background: transparent;
  border: none;
  color: var(--neon-cyan, #00F5FF);
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-btn:hover:not(:disabled) {
  background: rgba(0, 245, 255, 0.1);
  text-shadow: 0 0 8px var(--neon-cyan, #00F5FF);
}

.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-value {
  width: 48px;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary, #E8E8E8);
  border-left: 1px solid var(--glass-border, rgba(255, 255, 255, 0.1));
  border-right: 1px solid var(--glass-border, rgba(255, 255, 255, 0.1));
}

/* Cyber buttons */
.cyber-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cyber-btn .btn-glitch {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  transition: left 0.5s ease;
}

.cyber-btn:hover .btn-glitch {
  left: 100%;
}

.cyber-btn--cart {
  background: linear-gradient(135deg, var(--neon-cyan, #00F5FF), var(--neon-purple, #B388FF));
  color: var(--deep-blue, #1A1A2E);
  box-shadow: 0 4px 20px rgba(0, 245, 255, 0.25);
}

.cyber-btn--cart:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 30px rgba(0, 245, 255, 0.4);
}

.cyber-btn--fav {
  background: transparent;
  border: 1px solid var(--glass-border, rgba(255, 255, 255, 0.15));
  color: var(--text-primary, #E8E8E8);
}

.cyber-btn--fav:hover {
  border-color: var(--neon-pink, #FF6B9D);
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.2);
  transform: translateY(-2px);
}

.cyber-btn--fav.is-favorited {
  border-color: var(--neon-pink, #FF6B9D);
  background: rgba(255, 107, 157, 0.1);
  color: var(--neon-pink, #FF6B9D);
  box-shadow: 0 0 16px rgba(255, 107, 157, 0.15);
}

.btn-icon {
  font-size: 16px;
}

/* ---------- Reviews Section ---------- */
.reviews-section {
  margin-top: 50px;
  animation: slideUp 0.8s ease-out both;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    var(--glass-border, rgba(255, 255, 255, 0.1)),
    transparent
  );
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary, #E8E8E8);
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 20px;
}

.title-count {
  font-size: 13px;
  color: var(--text-secondary, #9CA3AF);
  font-weight: 400;
}

/* Empty state */
.empty-state {
  padding: 40px 0;
}

.empty-text {
  color: var(--text-secondary, #9CA3AF);
  font-size: 14px;
  letter-spacing: 1px;
}

/* Review list */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  padding: 24px;
  animation: slideUp 0.5s ease-out both;
  transition: all 0.3s ease;
}

.review-card:hover {
  border-color: rgba(0, 245, 255, 0.2);
  box-shadow: 0 4px 24px rgba(0, 245, 255, 0.06);
  transform: translateY(-2px);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.reviewer-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-cyber {
  background: linear-gradient(135deg, var(--neon-purple, #B388FF), var(--neon-cyan, #00F5FF)) !important;
  color: var(--deep-blue, #1A1A2E) !important;
  font-weight: 700 !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
}

.avatar-ring {
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  border: 1px solid rgba(0, 245, 255, 0.3);
  animation: neonPulse 2s ease-in-out infinite;
}

.reviewer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary, #E8E8E8);
}

.cyber-rate {
  transform: scale(0.8);
  transform-origin: left center;
}

.cyber-rate :deep(.el-rate__icon) {
  color: var(--gold, #FFD700) !important;
  filter: drop-shadow(0 0 4px rgba(255, 215, 0, 0.4));
}

.review-time {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary, #9CA3AF);
  letter-spacing: 0.5px;
}

.review-content {
  color: var(--text-secondary, #9CA3AF);
  line-height: 1.8;
  font-size: 14px;
  padding-left: 48px;
}

/* ---------- Responsive ---------- */
@media (max-width: 768px) {
  .cyber-detail-container {
    padding: 16px 12px 40px;
  }

  .product-main-row {
    flex-direction: column;
  }

  .image-frame {
    max-width: 360px;
    margin: 0 auto;
  }

  .info-panel {
    padding: 24px 18px;
    margin-top: 24px;
  }

  .product-name {
    font-size: 22px;
  }

  .price-value {
    font-size: 28px;
  }

  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .qty-selector {
    justify-content: center;
  }

  .cyber-btn {
    justify-content: center;
    padding: 14px 20px;
  }

  .meta-badges {
    flex-direction: column;
  }

  .review-content {
    padding-left: 0;
    margin-top: 8px;
  }

  .review-time {
    margin-left: 0;
    width: 100%;
    margin-top: 4px;
  }

  .review-header {
    flex-wrap: wrap;
  }

  .section-header {
    gap: 10px;
  }

  .section-title {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .product-name {
    font-size: 20px;
  }

  .price-value {
    font-size: 24px;
  }

  .info-panel {
    padding: 18px 14px;
  }

  .review-card {
    padding: 16px;
  }
}
</style>

