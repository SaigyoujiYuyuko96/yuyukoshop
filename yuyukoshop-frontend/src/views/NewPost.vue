<template>
  <div class="cyber-new-post">
    <NavHeader />
    <div class="container new-post-page">
      <!-- 返回导航 -->
      <div class="back-nav">
        <button class="back-btn" @click="$router.push('/community')">
          <span class="back-arrow">&#8592;</span>
          <span>返回社区</span>
          <span class="back-sub">BACK TO COMMUNITY</span>
        </button>
      </div>

      <!-- 表单卡片 -->
      <div class="glass-card form-card">
        <div class="card-glow-top"></div>
        <div class="card-inner">
          <!-- 标题区 -->
          <div class="form-header">
            <div class="header-decoration"></div>
            <div>
              <h2 class="form-title">
                <span class="title-icon">&#9830;</span>
                发布新帖子
              </h2>
              <span class="form-sub">CREATE NEW POST</span>
            </div>
          </div>

          <!-- 表单 -->
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="cyber-form">
            <!-- 标题输入 -->
            <div class="form-group">
              <label class="form-label">
                <span class="label-dot"></span>
                标题
                <span class="label-required">*</span>
              </label>
              <div class="input-wrapper">
                <input
                  v-model="form.title"
                  placeholder="请输入帖子标题..."
                  class="cyber-input title-input"
                />
                <div class="input-glow"></div>
              </div>
            </div>

            <!-- 内容输入 -->
            <div class="form-group">
              <label class="form-label">
                <span class="label-dot"></span>
                内容
                <span class="label-required">*</span>
              </label>
              <div class="editor-wrapper">
                <div class="editor-toolbar">
                  <span class="toolbar-hint">EDITOR</span>
                  <span class="char-count">{{ form.content.length }} chars</span>
                </div>
                <textarea
                  v-model="form.content"
                  rows="12"
                  placeholder="在这里写下你的想法..."
                  class="cyber-textarea content-editor"
                ></textarea>
                <div class="editor-footer">
                  <div class="editor-line"></div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <button class="neon-btn cancel-btn" @click="$router.push('/community')">
                <span class="btn-text">取消</span>
              </button>
              <button class="neon-btn primary-btn" :disabled="loading" @click="submitPost">
                <span class="btn-spinner" v-if="loading"></span>
                <span class="btn-text">{{ loading ? '发布中...' : '发布帖子' }}</span>
                <span class="btn-glow"></span>
              </button>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 提示信息 -->
      <div class="tips-card glass-card">
        <div class="card-inner tips-inner">
          <div class="tip-item">
            <span class="tip-icon">&#9670;</span>
            <span>请遵守社区规范，友善交流</span>
          </div>
          <div class="tip-item">
            <span class="tip-icon">&#9670;</span>
            <span>优质内容将获得更多曝光</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-grid"></div>
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>
    <div class="bg-glow bg-glow-3"></div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const submitPost = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/forums/posts', form)
    ElMessage.success('发布成功')
    router.push('/community')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
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
.cyber-new-post {
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

.new-post-page {
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
  background: rgba(0, 245, 255, 0.08);
  top: -150px;
  right: -100px;
  animation: floatGlow 8s ease-in-out infinite;
}

.bg-glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(179, 136, 255, 0.1);
  bottom: -100px;
  left: -100px;
  animation: floatGlow 10s ease-in-out infinite reverse;
}

.bg-glow-3 {
  width: 300px;
  height: 300px;
  background: rgba(255, 183, 197, 0.06);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: floatGlow 12s ease-in-out infinite;
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

/* ========== 玻璃卡片 ========== */
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

/* ========== 表单头部 ========== */
.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--glass-border);
}

.header-decoration {
  width: 4px;
  height: 48px;
  background: linear-gradient(180deg, var(--neon-cyan), var(--sakura));
  border-radius: 2px;
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.4);
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  color: var(--sakura);
  font-size: 16px;
  text-shadow: 0 0 10px rgba(255, 183, 197, 0.6);
}

.form-sub {
  font-size: 11px;
  color: var(--text-secondary);
  letter-spacing: 3px;
  opacity: 0.6;
}

/* ========== 表单样式 ========== */
.cyber-form {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.label-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--neon-purple);
  box-shadow: 0 0 8px rgba(179, 136, 255, 0.5);
}

.label-required {
  color: var(--neon-pink);
  font-size: 12px;
}

/* ========== 输入框 ========== */
.input-wrapper {
  position: relative;
}

.cyber-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  padding: 14px 18px;
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 16px;
  outline: none;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.cyber-input::placeholder {
  color: var(--text-secondary);
  opacity: 0.4;
}

.cyber-input:focus {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.1), inset 0 0 20px rgba(0, 245, 255, 0.03);
  background: rgba(0, 245, 255, 0.02);
}

.input-glow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple));
  transition: width 0.4s ease;
  border-radius: 1px;
}

.cyber-input:focus ~ .input-glow {
  width: 100%;
}

/* ========== 编辑器区域 ========== */
.editor-wrapper {
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.02);
}

.editor-wrapper:focus-within {
  border-color: rgba(0, 245, 255, 0.3);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.08), inset 0 0 30px rgba(0, 245, 255, 0.02);
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid var(--glass-border);
}

.toolbar-hint {
  font-size: 10px;
  letter-spacing: 3px;
  color: var(--neon-purple);
  font-weight: 600;
  opacity: 0.7;
}

.char-count {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.5;
  letter-spacing: 1px;
}

.cyber-textarea {
  width: 100%;
  background: transparent;
  border: none;
  padding: 18px 16px;
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 15px;
  line-height: 1.8;
  resize: vertical;
  outline: none;
  box-sizing: border-box;
}

.cyber-textarea::placeholder {
  color: var(--text-secondary);
  opacity: 0.35;
}

.editor-footer {
  padding: 0 16px 10px;
}

.editor-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--glass-border), transparent);
}

/* ========== 操作按钮 ========== */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 8px;
}

.neon-btn {
  position: relative;
  padding: 12px 32px;
  border: 1px solid var(--glass-border);
  background: var(--glass-bg);
  color: var(--text-secondary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  letter-spacing: 1px;
  border-radius: 10px;
}

.neon-btn:hover {
  border-color: rgba(255, 255, 255, 0.2);
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.08);
}

.cancel-btn:hover {
  border-color: rgba(255, 107, 157, 0.3);
  color: var(--neon-pink);
}

.primary-btn {
  border-color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.1);
  color: var(--neon-cyan);
  min-width: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.primary-btn:hover:not(:disabled) {
  background: rgba(0, 245, 255, 0.2);
  box-shadow: 0 0 25px rgba(0, 245, 255, 0.25);
  transform: translateY(-1px);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.15), transparent);
  transition: left 0.5s ease;
}

.primary-btn:hover:not(:disabled) .btn-glow {
  left: 100%;
}

.btn-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(0, 245, 255, 0.3);
  border-top-color: var(--neon-cyan);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ========== 提示卡片 ========== */
.tips-card {
  animation: slideUp 0.5s ease 0.2s both;
}

.tips-inner {
  padding: 20px 24px;
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}

.tip-icon {
  color: var(--gold);
  font-size: 10px;
  opacity: 0.7;
}

/* ========== 隐藏 Element Plus 原始样式覆盖 ========== */
.cyber-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.cyber-form :deep(.el-form-item__label) {
  display: none;
}

.cyber-form :deep(.el-form-item__content) {
  display: block;
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

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .card-inner {
    padding: 20px;
  }

  .form-title {
    font-size: 20px;
  }

  .form-sub {
    display: none;
  }

  .back-sub {
    display: none;
  }

  .form-actions {
    flex-direction: column;
  }

  .neon-btn {
    width: 100%;
    text-align: center;
    justify-content: center;
  }

  .tips-inner {
    flex-direction: column;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 12px;
  }

  .card-inner {
    padding: 16px;
  }

  .form-title {
    font-size: 18px;
  }

  .header-decoration {
    height: 36px;
  }
}
</style>
