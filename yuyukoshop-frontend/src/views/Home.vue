<template>
  <div class="home">
    <NavHeader />

    <!-- Hero 轮播区域 -->
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-grid"></div>
        <div class="hero-particles"></div>
      </div>
      
      <el-carousel height="600px" class="hero-carousel" v-loading="bannerLoading">
        <el-carousel-item v-for="(banner, index) in banners" :key="banner.id">
          <div class="hero-slide" :style="getSlideStyle(banner, index)">
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <div class="slide-tag">
                <span class="tag-line"></span>
                <span class="tag-text">FEATURED</span>
              </div>
              <h1 class="slide-title">{{ banner.title }}</h1>
              <p class="slide-subtitle">{{ banner.subtitle }}</p>
              <button class="cyber-btn cyber-btn-primary" @click="goToLink(banner.linkUrl)">
                <span>{{ getButtonText(banner.linkUrl) }}</span>
                <el-icon><ArrowRight /></el-icon>
              </button>
            </div>
            <div class="slide-decoration">
              <div class="deco-circle"></div>
              <div class="deco-line"></div>
            </div>
          </div>
        </el-carousel-item>

        <!-- 默认轮播 -->
        <el-carousel-item v-if="banners.length === 0 && !bannerLoading">
          <div class="hero-slide hero-default">
            <div class="slide-content">
              <div class="hero-logo-wrap">
                <img src="/logo.jpg" alt="幽幽子Shop" class="hero-logo" />
                <div class="hero-logo-glow"></div>
              </div>
              <h1 class="slide-title">幽幽子<span class="title-accent">Shop</span></h1>
              <p class="slide-subtitle">你的专属动漫周边商城 / Your Anime Merchandise Paradise</p>
              <div class="hero-tags">
                <span class="hero-tag">手办</span>
                <span class="hero-tag">周边</span>
                <span class="hero-tag">同人</span>
                <span class="hero-tag">限定</span>
              </div>
              <button class="cyber-btn cyber-btn-primary" @click="$router.push('/products')">
                <span>立即选购</span>
                <el-icon><ArrowRight /></el-icon>
              </button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 滚动提示 -->
      <div class="scroll-hint">
        <span class="scroll-text">SCROLL</span>
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- 热门分类 -->
    <section class="section section-categories">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-title-wrap">
            <span class="section-number">01</span>
            <h2 class="section-title">热门分类</h2>
          </div>
          <span class="section-subtitle">CATEGORIES</span>
        </div>

        <div class="category-grid">
          <div
            class="category-card"
            v-for="(cat, index) in categories"
            :key="cat.id"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="goToCategory(cat.id)"
          >
            <div class="category-bg"></div>
            <div class="category-icon" :style="{ background: getCategoryGradient(index) }">
              <el-icon :size="28"><Goods /></el-icon>
            </div>
            <span class="category-name">{{ cat.name }}</span>
            <span class="category-count">{{ cat.productCount || 0 }} 件商品</span>
            <div class="category-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <el-empty v-if="categories.length === 0 && !loading" description="暂无分类" />
      </div>
    </section>

    <!-- 热门推荐 -->
    <section class="section section-products">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-title-wrap">
            <span class="section-number">02</span>
            <h2 class="section-title">热门推荐</h2>
          </div>
          <el-button text class="view-all-btn" @click="$router.push('/products')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="product-grid">
          <div
            class="product-card"
            v-for="(product, index) in hotProducts"
            :key="product.id"
            :style="{ animationDelay: `${index * 0.08}s` }"
            @click="goToDetail(product.id)"
          >
            <div class="product-image-wrap">
              <img
                :src="product.thumbnail || 'https://via.placeholder.com/300x300?text=No+Image'"
                class="product-image"
                alt=""
              />
              <div class="product-overlay">
                <button class="quick-view-btn">
                  <el-icon><View /></el-icon>
                  <span>查看详情</span>
                </button>
              </div>
              <span class="product-badge hot">HOT</span>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.name }}</h4>
              <p class="product-desc">{{ product.description?.substring(0, 50) || '精选动漫周边' }}</p>
              <div class="product-footer">
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ product.price }}</span>
                </div>
                <span class="product-sales">已售 {{ product.sales || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="hotProducts.length === 0 && !loading" description="暂无商品" />
      </div>
    </section>

    <!-- 社区精选 -->
    <section class="section section-posts">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-title-wrap">
            <span class="section-number">03</span>
            <h2 class="section-title">社区精选</h2>
          </div>
          <el-button text class="view-all-btn" @click="$router.push('/community')">
            进入社区 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="post-grid">
          <div
            class="post-card"
            v-for="(post, index) in hotPosts"
            :key="post.id"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="goToPost(post.id)"
          >
            <div class="post-header">
              <img :src="post.authorAvatar || defaultAvatar" class="post-avatar" alt="" />
              <div class="post-author-info">
                <span class="post-author">{{ post.author || '匿名用户' }}</span>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>
            <h4 class="post-title">{{ post.title }}</h4>
            <p class="post-content">{{ post.content?.substring(0, 100) || '' }}...</p>
            <div class="post-footer">
              <div class="post-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ post.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><ChatLineRound /></el-icon>
                  {{ post.commentCount || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ post.likeCount || 0 }}
                </span>
              </div>
              <div class="post-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="hotPosts.length === 0 && !loading" description="暂无帖子" />
      </div>
    </section>

    <!-- 底部装饰 -->
    <footer class="home-footer">
      <div class="footer-line"></div>
      <div class="footer-content">
        <span class="footer-text">© 2024 幽幽子Shop - 动漫周边商城</span>
        <span class="footer-divider">|</span>
        <span class="footer-text">Made with 💜 for Anime Lovers</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import ShopLogo from '@/components/ShopLogo.vue'
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const categories = ref([])
const hotProducts = ref([])
const hotPosts = ref([])
const banners = ref([])
const loading = ref(false)
const bannerLoading = ref(false)
const defaultAvatar = 'https://via.placeholder.com/40?text=User'

onMounted(async () => {
  loading.value = true
  bannerLoading.value = true
  try {
    const [catRes, prodRes, postRes, bannerRes] = await Promise.allSettled([
      request.get('/categories'),
      request.get('/products/hot'),
      request.get('/forums/posts/hot'),
      request.get('/banners/active')
    ])

    if (catRes.status === 'fulfilled') categories.value = catRes.value.data || []
    if (prodRes.status === 'fulfilled') hotProducts.value = prodRes.value.data || []
    if (postRes.status === 'fulfilled') hotPosts.value = postRes.value.data || []
    if (bannerRes.status === 'fulfilled') banners.value = bannerRes.value.data || []
  } catch (e) {
    ElMessage.error('加载数据失败')
    console.error(e)
  } finally {
    loading.value = false
    bannerLoading.value = false
  }
})

const getSlideStyle = (banner, index) => {
  const gradients = [
    'linear-gradient(135deg, rgba(179, 136, 255, 0.3) 0%, rgba(10, 10, 15, 0.9) 100%)',
    'linear-gradient(135deg, rgba(0, 245, 255, 0.3) 0%, rgba(10, 10, 15, 0.9) 100%)',
    'linear-gradient(135deg, rgba(255, 107, 157, 0.3) 0%, rgba(10, 10, 15, 0.9) 100%)'
  ]
  return {
    background: `${gradients[index % 3]}, url(${banner.imageUrl}) center/cover`
  }
}

const getCategoryGradient = (index) => {
  const gradients = [
    'linear-gradient(135deg, #B388FF, #FF6B9D)',
    'linear-gradient(135deg, #00F5FF, #00D4FF)',
    'linear-gradient(135deg, #FF6B9D, #FFD700)',
    'linear-gradient(135deg, #00FF88, #00F5FF)',
    'linear-gradient(135deg, #FFD700, #FF8C00)'
  ]
  return gradients[index % gradients.length]
}

const goToCategory = (id) => router.push(`/products?categoryId=${id}`)
const goToDetail = (id) => router.push(`/products/${id}`)
const goToPost = (id) => router.push(`/community/posts/${id}`)
const goToLink = (url) => url ? router.push(url) : router.push('/products')

const getButtonText = (url) => {
  if (!url) return '立即查看'
  if (url.includes('products')) return '立即选购'
  if (url.includes('community')) return '加入社区'
  return '立即查看'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 86400000) return '今天'
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}
</script>

<style scoped>
/* ========== 首页主体 ========== */
.home {
  min-height: 100vh;
  background: var(--bg-primary);
  overflow-x: hidden;
}

/* ========== Hero 区域 ========== */
.hero-section {
  position: relative;
  height: 600px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(179, 136, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(179, 136, 255, 0.02) 1px, transparent 1px);
  background-size: 60px 60px;
}

.hero-particles {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 50%, rgba(179, 136, 255, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 70% 30%, rgba(0, 245, 255, 0.08) 0%, transparent 40%);
}

.hero-carousel {
  height: 100%;
}

.hero-slide {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.hero-default {
  background: linear-gradient(135deg, #1a1a2e 0%, #0a0a0f 100%);
}

.slide-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 0%, rgba(10, 10, 15, 0.5) 100%);
}

.slide-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 0 24px;
}

.slide-tag {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.tag-line {
  width: 40px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan));
}

.tag-text {
  font-family: 'Orbitron', sans-serif;
  font-size: 12px;
  letter-spacing: 4px;
  color: var(--neon-cyan);
}

.hero-logo-wrap {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  animation: float 3s ease-in-out infinite;
}

.hero-logo {
  width: 120px;
  height: 120px;
  border-radius: 24px;
  object-fit: cover;
  position: relative;
  z-index: 2;
  filter: drop-shadow(0 0 20px rgba(179, 136, 255, 0.5));
}

.hero-logo-glow {
  position: absolute;
  inset: -10px;
  border-radius: 34px;
  background: linear-gradient(135deg, #B388FF, #00F5FF, #FF6B9D);
  opacity: 0.4;
  filter: blur(20px);
  z-index: 1;
}

.title-accent {
  background: linear-gradient(135deg, #B388FF, #00F5FF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-tags {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 32px;
}

.hero-tag {
  padding: 6px 20px;
  font-size: 13px;
  color: #9CA3AF;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.hero-tag:hover {
  color: #B388FF;
  border-color: #B388FF;
  box-shadow: 0 0 12px rgba(179, 136, 255, 0.3);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.slide-title {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 56px;
  font-weight: 900;
  color: #fff;
  margin-bottom: 16px;
  text-shadow: 0 0 40px rgba(179, 136, 255, 0.5);
  letter-spacing: 4px;
}

.slide-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 40px;
  letter-spacing: 2px;
}

.slide-decoration {
  position: absolute;
  right: 10%;
  top: 50%;
  transform: translateY(-50%);
}

.deco-circle {
  width: 200px;
  height: 200px;
  border: 1px solid rgba(179, 136, 255, 0.2);
  border-radius: 50%;
  animation: pulse-glow 4s ease-in-out infinite;
}

.deco-line {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 300px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-purple), transparent);
  transform: translate(-50%, -50%) rotate(45deg);
}

/* 滚动提示 */
.scroll-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  z-index: 10;
}

.scroll-text {
  font-family: 'Orbitron', sans-serif;
  font-size: 10px;
  letter-spacing: 3px;
  color: var(--text-muted);
}

.scroll-line {
  width: 1px;
  height: 40px;
  background: linear-gradient(180deg, var(--neon-purple), transparent);
  animation: slide-down 1.5s ease-in-out infinite;
}

@keyframes slide-down {
  0% { transform: scaleY(0); transform-origin: top; }
  50% { transform: scaleY(1); transform-origin: top; }
  51% { transform: scaleY(1); transform-origin: bottom; }
  100% { transform: scaleY(0); transform-origin: bottom; }
}

/* ========== 通用区域样式 ========== */
.section {
  padding: 80px 0;
  position: relative;
}

.section-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
}

.section-title-wrap {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.section-number {
  font-family: 'Orbitron', sans-serif;
  font-size: 48px;
  font-weight: 900;
  color: rgba(179, 136, 255, 0.15);
  line-height: 1;
}

.section-title {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
}

.section-subtitle {
  font-family: 'Orbitron', sans-serif;
  font-size: 12px;
  letter-spacing: 4px;
  color: var(--text-muted);
}

.view-all-btn {
  color: var(--neon-cyan);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-all-btn:hover {
  color: var(--neon-purple);
}

/* ========== 分类卡片 ========== */
.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.category-card {
  position: relative;
  padding: 32px 24px;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s ease;
  animation: slide-up 0.6s ease forwards;
  opacity: 0;
}

.category-card:hover {
  border-color: var(--neon-purple);
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), var(--shadow-glow-purple);
}

.category-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 0%, rgba(179, 136, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.category-card:hover .category-bg {
  opacity: 1;
}

.category-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  transition: transform 0.4s ease;
}

.category-card:hover .category-icon {
  transform: scale(1.1) rotate(-5deg);
}

.category-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
}

.category-count {
  font-size: 12px;
  color: var(--text-muted);
}

.category-arrow {
  position: absolute;
  right: 20px;
  bottom: 20px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

.category-card:hover .category-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--neon-cyan);
}

/* ========== 商品卡片 ========== */
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.product-card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  animation: slide-up 0.6s ease forwards;
  opacity: 0;
}

.product-card:hover {
  border-color: var(--neon-purple);
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), var(--shadow-glow-purple);
}

.product-image-wrap {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.product-card:hover .product-image {
  transform: scale(1.1);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(10, 10, 15, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.quick-view-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--gradient-primary);
  border: none;
  border-radius: 24px;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transform: translateY(20px);
  transition: transform 0.3s ease;
}

.product-card:hover .quick-view-btn {
  transform: translateY(0);
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  font-family: 'Orbitron', sans-serif;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1px;
  border-radius: 4px;
}

.product-badge.hot {
  background: var(--gradient-primary);
  color: #fff;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 16px;
  line-height: 1.5;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 14px;
  color: var(--neon-pink);
  margin-right: 2px;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--neon-pink);
}

.product-sales {
  font-size: 12px;
  color: var(--text-muted);
}

/* ========== 帖子卡片 ========== */
.post-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.post-card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.4s ease;
  animation: slide-up 0.6s ease forwards;
  opacity: 0;
}

.post-card:hover {
  border-color: var(--neon-purple);
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.post-author-info {
  display: flex;
  flex-direction: column;
}

.post-author {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.post-time {
  font-size: 12px;
  color: var(--text-muted);
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 12px;
  line-height: 1.4;
}

.post-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 20px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--glass-border);
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-muted);
}

.post-arrow {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  transition: all 0.3s ease;
}

.post-card:hover .post-arrow {
  background: var(--gradient-primary);
  color: #fff;
}

/* ========== 底部 ========== */
.home-footer {
  padding: 40px 24px;
  text-align: center;
}

.footer-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--glass-border), transparent);
  margin-bottom: 24px;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.footer-text {
  font-size: 13px;
  color: var(--text-muted);
}

.footer-divider {
  color: var(--glass-border);
}

/* ========== 动画 ========== */
@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

/* ========== 响应式 ========== */
@media (max-width: 1200px) {
  .category-grid, .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 992px) {
  .category-grid, .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .post-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-section {
    height: 500px;
  }
  
  .slide-title {
    font-size: 36px;
  }
  
  .category-grid, .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .section {
    padding: 48px 0;
  }
  
  .section-title {
    font-size: 24px;
  }
}
</style>
