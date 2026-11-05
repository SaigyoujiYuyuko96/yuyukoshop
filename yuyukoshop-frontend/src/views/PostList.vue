<template>
  <div class="cyber-post-list">
    <NavHeader />
    <div class="container post-page">
      <!-- 页面标题区 -->
      <div class="post-header">
        <div class="header-left">
          <div class="header-decoration"></div>
          <h2 class="page-title">
            <span class="title-icon">&#9830;</span>
            社区交流
            <span class="title-sub">COMMUNITY</span>
          </h2>
        </div>
        <button class="neon-btn create-btn" @click="$router.push('/community/new-post')">
          <span class="btn-glow"></span>
          <span class="btn-text">+ 发布帖子</span>
        </button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-wrapper">
        <div class="search-bar">
          <span class="search-icon">&#9906;</span>
          <input
            v-model="searchKeyword"
            placeholder="搜索帖子..."
            clearable
            @keyup.enter="fetchPosts"
            class="search-input"
          />
          <button class="search-btn" @click="fetchPosts">
            <span>SEARCH</span>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="posts.length === 0" class="empty-state">
        <div class="empty-icon">&#9734;</div>
        <p>暂无帖子</p>
        <span class="empty-sub">NO POSTS FOUND</span>
      </div>

      <!-- 帖子列表 -->
      <div v-else class="posts-grid">
        <div
          v-for="(post, index) in posts"
          :key="post.id"
          class="glass-card post-card"
          :style="{ animationDelay: `${index * 0.08}s` }"
          @click="goToDetail(post.id)"
        >
          <div class="card-glow"></div>
          <div class="card-content">
            <div class="post-index">#{{ String(index + 1).padStart(2, '0') }}</div>
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-summary">{{ post.content?.substring(0, 100) }}...</p>
            <div class="post-meta">
              <span class="meta-item">
                <span class="meta-icon">&#9679;</span>
                {{ post.author }}
              </span>
              <span class="meta-item">
                <span class="meta-icon">&#9201;</span>
                {{ post.createTime }}
              </span>
              <span class="meta-item">
                <span class="meta-icon">&#9830;</span>
                {{ post.commentCount || 0 }}
              </span>
            </div>
          </div>
          <div class="card-border"></div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="posts.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @change="fetchPosts"
          class="pagination"
          background
        />
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-grid"></div>
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { Search } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const posts = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const fetchPosts = async () => {
  try {
    const params = { page: page.value, size: size.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res = await request.get('/forums/posts', { params })
    posts.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

const goToDetail = (id) => {
  router.push(`/community/posts/${id}`)
}

onMounted(fetchPosts)
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

/* ========== 全局容器 ========== */
.cyber-post-list {
  min-height: 100vh;
  background: var(--deep-blue);
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  position: relative;
  overflow-x: hidden;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 2;
}

.post-page {
  margin-top: 20px;
}

/* ========== 背景装饰 ========== */
.bg-grid {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(0, 245, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 245, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  pointer-events: none;
  z-index: 0;
}

.bg-glow {
  position: fixed;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  z-index: 0;
}

.bg-glow-1 {
  width: 500px;
  height: 500px;
  background: rgba(179, 136, 255, 0.12);
  top: -100px;
  right: -100px;
  animation: floatGlow 8s ease-in-out infinite;
}

.bg-glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(255, 107, 157, 0.08);
  bottom: -50px;
  left: -100px;
  animation: floatGlow 10s ease-in-out infinite reverse;
}

@keyframes floatGlow {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -20px); }
}

/* ========== 页面头部 ========== */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--glass-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-decoration {
  width: 4px;
  height: 40px;
  background: linear-gradient(180deg, var(--neon-cyan), var(--neon-purple));
  border-radius: 2px;
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.4);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  color: var(--sakura);
  font-size: 20px;
  text-shadow: 0 0 10px rgba(255, 183, 197, 0.6);
}

.title-sub {
  font-size: 11px;
  color: var(--text-secondary);
  letter-spacing: 4px;
  font-weight: 400;
  margin-left: 8px;
}

/* ========== 霓虹按钮 ========== */
.neon-btn {
  position: relative;
  padding: 12px 28px;
  border: 1px solid var(--neon-cyan);
  background: rgba(0, 245, 255, 0.08);
  color: var(--neon-cyan);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.neon-btn:hover {
  background: rgba(0, 245, 255, 0.15);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3), inset 0 0 20px rgba(0, 245, 255, 0.1);
  transform: translateY(-1px);
}

.neon-btn:active {
  transform: translateY(0);
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.neon-btn:hover .btn-glow {
  left: 100%;
}

/* ========== 搜索栏 ========== */
.search-wrapper {
  margin-bottom: 32px;
}

.search-bar {
  display: flex;
  align-items: center;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 4px;
  backdrop-filter: blur(20px);
  transition: all 0.3s ease;
}

.search-bar:focus-within {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.15), inset 0 0 20px rgba(0, 245, 255, 0.05);
}

.search-icon {
  padding: 0 16px;
  color: var(--text-secondary);
  font-size: 16px;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 15px;
  padding: 12px 0;
}

.search-input::placeholder {
  color: var(--text-secondary);
  opacity: 0.6;
}

.search-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.15), rgba(179, 136, 255, 0.15));
  border: 1px solid rgba(0, 245, 255, 0.3);
  color: var(--neon-cyan);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.25), rgba(179, 136, 255, 0.25));
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.2);
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  animation: fadeIn 0.5s ease;
}

.empty-icon {
  font-size: 48px;
  color: var(--neon-purple);
  text-shadow: 0 0 20px rgba(179, 136, 255, 0.5);
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.empty-sub {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.5;
  letter-spacing: 3px;
}

/* ========== 帖子卡片 ========== */
.posts-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.glass-card {
  position: relative;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: slideUp 0.5s ease both;
}

.glass-card:hover {
  border-color: rgba(0, 245, 255, 0.3);
  transform: translateY(-4px);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 0 20px rgba(0, 245, 255, 0.1);
}

.glass-card:hover .card-glow {
  opacity: 1;
}

.glass-card:hover .card-border {
  background-position: 0 0;
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple), var(--sakura));
  opacity: 0;
  transition: opacity 0.4s ease;
}

.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.2), transparent, rgba(179, 136, 255, 0.2));
  background-size: 200% 200%;
  background-position: 100% 100%;
  transition: background-position 0.6s ease;
  pointer-events: none;
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 24px;
}

.post-index {
  font-size: 11px;
  color: var(--neon-cyan);
  letter-spacing: 2px;
  font-weight: 600;
  margin-bottom: 8px;
  opacity: 0.7;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  transition: color 0.3s ease;
  line-height: 1.4;
}

.glass-card:hover .post-title {
  color: var(--neon-cyan);
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
}

.post-summary {
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 16px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: var(--text-secondary);
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
}

.glass-card:hover .meta-item {
  color: var(--neon-purple);
}

.meta-icon {
  font-size: 8px;
  color: var(--sakura);
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
}

.pagination :deep(.el-pagination) {
  --el-pagination-bg-color: var(--glass-bg);
  --el-pagination-text-color: var(--text-secondary);
  --el-pagination-button-bg-color: rgba(255, 255, 255, 0.05);
  --el-pagination-hover-color: var(--neon-cyan);
}

.pagination :deep(.el-pager li) {
  background: var(--glass-bg) !important;
  color: var(--text-secondary) !important;
  border: 1px solid var(--glass-border);
  border-radius: 8px !important;
  margin: 0 2px;
  backdrop-filter: blur(10px);
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.2), rgba(179, 136, 255, 0.2)) !important;
  color: var(--neon-cyan) !important;
  border-color: rgba(0, 245, 255, 0.4) !important;
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.2);
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  background: var(--glass-bg) !important;
  color: var(--text-secondary) !important;
  border: 1px solid var(--glass-border);
  border-radius: 8px !important;
}

.pagination :deep(.el-pagination__total) {
  color: var(--text-secondary) !important;
}

/* ========== 动画 ========== */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .post-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .page-title {
    font-size: 22px;
  }

  .title-sub {
    display: none;
  }

  .post-meta {
    flex-wrap: wrap;
    gap: 12px;
  }

  .search-bar {
    flex-wrap: wrap;
  }

  .search-btn {
    width: 100%;
    text-align: center;
  }

  .pagination-wrapper {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 12px;
  }

  .card-content {
    padding: 16px;
  }

  .post-title {
    font-size: 16px;
  }
}
</style>
