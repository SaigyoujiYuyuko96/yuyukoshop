<template>
  <div class="cyber-post-detail">
    <NavHeader />
    <div class="container detail-page" v-loading="loading">
      <!-- 返回导航 -->
      <div class="back-nav">
        <button class="back-btn" @click="$router.push('/community')">
          <span class="back-arrow">&#8592;</span>
          <span>返回社区</span>
          <span class="back-sub">BACK TO COMMUNITY</span>
        </button>
      </div>

      <!-- 帖子内容 -->
      <div v-if="post" class="glass-card post-content-card">
        <div class="card-glow-top"></div>
        <div class="card-inner">
          <!-- 标签 -->
          <div class="post-tag">POST DETAIL</div>

          <!-- 标题 -->
          <h1 class="post-title">{{ post.title }}</h1>

          <!-- 作者信息 -->
          <div class="author-info">
            <UserPopover :userId="post.userId" :userName="post.username" :size="44">
              <el-avatar :size="44" :src="post.userAvatar || defaultAvatar" class="author-avatar-img">
                <el-icon :size="20"><User /></el-icon>
              </el-avatar>
            </UserPopover>
            <div class="author-meta">
              <div class="author-name">{{ post.username }}</div>
              <div class="post-time">{{ post.createTime }}</div>
            </div>
            <div class="author-decoration"></div>
          </div>

          <!-- 分割线 -->
          <div class="divider">
            <span class="divider-dot"></span>
            <span class="divider-line"></span>
            <span class="divider-dot"></span>
          </div>

          <!-- 正文 -->
          <div class="content-text">{{ post.content }}</div>
        </div>
      </div>

      <!-- 评论区 -->
      <div v-if="post" class="glass-card comments-section">
        <div class="card-inner">
          <div class="section-header">
            <div class="section-decoration"></div>
            <h3 class="section-title">
              <span class="section-icon">&#9830;</span>
              评论
              <span class="comment-count">({{ comments.length }})</span>
            </h3>
          </div>

          <!-- 评论输入 -->
          <div v-if="userStore.isLoggedIn" class="comment-form glass-card-inner">
            <textarea
              v-model="newComment"
              rows="3"
              placeholder="发表你的评论..."
              class="comment-input"
            ></textarea>
            <div class="form-actions">
              <span class="char-hint">{{ newComment.length }} chars</span>
              <button class="neon-btn submit-btn" @click="submitComment">
                <span class="btn-text">发表评论</span>
              </button>
            </div>
          </div>

          <div v-else class="login-tip glass-card-inner">
            <span class="tip-icon">&#9888;</span>
            <span>请</span>
            <a class="login-link" @click="$router.push('/login')">登录</a>
            <span>后发表评论</span>
          </div>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div
              v-for="(comment, index) in comments"
              :key="comment.id"
              class="comment-item glass-card-inner"
              :style="{ animationDelay: `${index * 0.06}s` }"
            >
              <div class="comment-header">
                <UserPopover :userId="comment.userId" :userName="comment.username" :size="32" v-if="comment.userId">
                <el-avatar :size="32" :src="comment.userAvatar || defaultAvatar" class="comment-avatar-img">
                  <el-icon :size="14"><User /></el-icon>
                </el-avatar>
              </UserPopover>
              <el-avatar v-else :size="32" class="comment-avatar-img">
                <el-icon :size="14"><User /></el-icon>
              </el-avatar>
                <div class="comment-meta">
                  <span class="comment-author">{{ comment.username }}</span>
                  <span class="comment-time">{{ comment.createTime }}</span>
                </div>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>

          <!-- 空评论 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <span class="empty-icon">&#9734;</span>
            <p>暂无评论，来说点什么吧</p>
          </div>
        </div>
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
import UserPopover from '@/components/UserPopover.vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const post = ref(null)
const comments = ref([])
const newComment = ref('')
const loading = ref(false)
const defaultAvatar = 'https://via.placeholder.com/50?text=User'

const fetchPost = async () => {
  loading.value = true
  try {
    const res = await request.get(`/forums/posts/${route.params.id}`)
    post.value = res.data
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const fetchComments = async () => {
  try {
    const res = await request.get(`/forums/posts/${route.params.id}/comments`)
    comments.value = res.data || []
  } catch (e) { console.error(e) }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    await request.post(`/forums/posts/${route.params.id}/comments`, { content: newComment.value })
    ElMessage.success('评论成功')
    newComment.value = ''
    fetchComments()
  } catch (e) { console.error(e) }
}

onMounted(() => {
  fetchPost()
  fetchComments()
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

/* ========== 全局容器 ========== */
.cyber-post-detail {
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

.detail-page {
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
  background: rgba(179, 136, 255, 0.1);
  top: -150px;
  left: -100px;
  animation: floatGlow 8s ease-in-out infinite;
}

.bg-glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(255, 107, 157, 0.08);
  bottom: -100px;
  right: -100px;
  animation: floatGlow 10s ease-in-out infinite reverse;
}

@keyframes floatGlow {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -20px); }
}

/* ========== 返回导航 ========== */
.back-nav {
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  color: var(--text-secondary);
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.back-btn:hover {
  border-color: rgba(0, 245, 255, 0.3);
  color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.1);
}

.back-arrow {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.back-btn:hover .back-arrow {
  transform: translateX(-4px);
}

.back-sub {
  font-size: 10px;
  letter-spacing: 2px;
  opacity: 0.5;
  margin-left: 4px;
}

/* ========== 玻璃卡片通用 ========== */
.glass-card {
  position: relative;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  overflow: hidden;
  margin-bottom: 24px;
  animation: slideUp 0.5s ease both;
}

.card-glow-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple), var(--sakura));
}

.card-inner {
  padding: 32px;
  position: relative;
  z-index: 1;
}

.glass-card-inner {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.glass-card-inner:hover {
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
}

/* ========== 帖子内容 ========== */
.post-tag {
  display: inline-block;
  font-size: 10px;
  letter-spacing: 3px;
  color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.08);
  border: 1px solid rgba(0, 245, 255, 0.2);
  padding: 4px 12px;
  border-radius: 4px;
  margin-bottom: 16px;
  font-weight: 600;
}

.post-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 24px 0;
  line-height: 1.4;
  text-shadow: 0 0 30px rgba(0, 245, 255, 0.1);
}

/* ========== 作者信息 ========== */
.author-info {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
  position: relative;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(179, 136, 255, 0.3), rgba(255, 107, 157, 0.3));
  border: 1px solid rgba(179, 136, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: var(--neon-purple);
  flex-shrink: 0;
}

.author-meta {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 15px;
  margin-bottom: 2px;
}

.post-time {
  color: var(--text-secondary);
  font-size: 12px;
}

.author-decoration {
  position: absolute;
  bottom: -12px;
  left: 0;
  right: 0;
}

/* ========== 分割线 ========== */
.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.divider-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--neon-purple);
  box-shadow: 0 0 6px rgba(179, 136, 255, 0.5);
  flex-shrink: 0;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, var(--glass-border), transparent);
}

/* ========== 正文 ========== */
.content-text {
  line-height: 2;
  color: var(--text-primary);
  white-space: pre-wrap;
  font-size: 15px;
  opacity: 0.9;
}

/* ========== 评论区 ========== */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-decoration {
  width: 3px;
  height: 24px;
  background: linear-gradient(180deg, var(--sakura), var(--neon-purple));
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(255, 183, 197, 0.4);
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  color: var(--sakura);
  font-size: 14px;
}

.comment-count {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 400;
}

/* ========== 评论表单 ========== */
.comment-form {
  margin-bottom: 24px;
  padding: 20px;
}

.comment-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  padding: 14px 16px;
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  line-height: 1.7;
  resize: vertical;
  outline: none;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.comment-input::placeholder {
  color: var(--text-secondary);
  opacity: 0.5;
}

.comment-input:focus {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.1), inset 0 0 20px rgba(0, 245, 255, 0.03);
  background: rgba(0, 245, 255, 0.02);
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.char-hint {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.5;
  letter-spacing: 1px;
}

.submit-btn {
  padding: 10px 24px;
  border: 1px solid var(--neon-cyan);
  background: rgba(0, 245, 255, 0.1);
  color: var(--neon-cyan);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.submit-btn:hover {
  background: rgba(0, 245, 255, 0.2);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2);
  transform: translateY(-1px);
}

/* ========== 登录提示 ========== */
.login-tip {
  margin-bottom: 24px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.tip-icon {
  color: var(--gold);
}

.login-link {
  color: var(--neon-cyan);
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 600;
}

.login-link:hover {
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
}

/* ========== 评论列表 ========== */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  animation: slideUp 0.4s ease both;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.2), rgba(179, 136, 255, 0.2));
  border: 1px solid rgba(0, 245, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: var(--neon-cyan);
  flex-shrink: 0;
}

.comment-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.comment-author {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
}

.comment-time {
  color: var(--text-secondary);
  font-size: 11px;
  opacity: 0.7;
}

.comment-content {
  color: var(--text-primary);
  line-height: 1.7;
  padding-left: 42px;
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

/* ========== 空评论 ========== */
.empty-comments {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-secondary);
}

.empty-comments .empty-icon {
  font-size: 32px;
  color: var(--neon-purple);
  text-shadow: 0 0 15px rgba(179, 136, 255, 0.4);
  display: block;
  margin-bottom: 12px;
}

.empty-comments p {
  margin: 0;
  font-size: 14px;
}

/* ========== 动画 ========== */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .card-inner {
    padding: 20px;
  }

  .post-title {
    font-size: 22px;
  }

  .back-sub {
    display: none;
  }

  .comment-content {
    padding-left: 0;
    padding-top: 8px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 12px;
  }

  .card-inner {
    padding: 16px;
  }

  .post-title {
    font-size: 20px;
  }

  .form-actions {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }

  .submit-btn {
    text-align: center;
  }
}
</style>

