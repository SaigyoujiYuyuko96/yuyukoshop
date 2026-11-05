<template>
  <div class="cyber-login-page">
    <!-- Animated background layer -->
    <div class="bg-grid"></div>
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    <div class="bg-orb orb-3"></div>
    <div class="bg-orb orb-4"></div>
    <div class="scanline"></div>

    <!-- Back to home -->
    <div class="back-home">
      <el-button
        type="primary"
        :icon="HomeFilled"
        circle
        @click="$router.push('/')"
        class="cyber-back-btn"
      />
      <span class="back-text">返回主页</span>
    </div>

    <!-- Glass morphism register card -->
    <div class="cyber-login-box">
      <div class="card-glow"></div>
      <div class="login-header">
        <div class="icon-wrapper">
          <el-icon :size="52" class="shop-icon"><Shop /></el-icon>
        </div>
        <h2 class="cyber-title">幽幽子Shop</h2>
        <p class="subtitle">注册账号，开启精彩之旅</p>
        <div class="title-decoration">
          <span class="deco-line"></span>
          <span class="deco-diamond">&#9670;</span>
          <span class="deco-line"></span>
        </div>
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-position="top"
        class="cyber-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            class="cyber-input"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
            class="cyber-input"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            size="large"
            class="cyber-input"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            size="large"
            class="cyber-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width:100%"
            :loading="loading"
            @click="handleRegister"
            class="cyber-submit-btn"
          >
            注 册
          </el-button>
        </el-form-item>
        <div class="register-link">
          已有账号？
          <el-link type="primary" @click="$router.push('/login')" class="cyber-link">
            去登录
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Shop } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    await request.post('/auth/register', {
      username: form.username,
      email: form.email,
      password: form.password
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    if (e.message) {
      ElMessage.error(e.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
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

/* ===== Page Container ===== */
.cyber-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--deep-blue);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

/* ===== Circuit Grid Background ===== */
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 245, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 245, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  z-index: 0;
}

.bg-grid::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(179, 136, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(179, 136, 255, 0.02) 1px, transparent 1px);
  background-size: 20px 20px;
}

/* ===== Animated Gradient Orbs ===== */
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  z-index: 0;
  animation: orbFloat 12s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--neon-pink), transparent 70%);
  top: -10%;
  right: -10%;
  animation-duration: 14s;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, var(--neon-purple), transparent 70%);
  bottom: -10%;
  left: -10%;
  animation-duration: 16s;
  animation-delay: -4s;
}

.orb-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, var(--sakura), transparent 70%);
  top: 40%;
  left: 20%;
  opacity: 0.2;
  animation-duration: 18s;
  animation-delay: -8s;
}

.orb-4 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, var(--neon-cyan), transparent 70%);
  top: 10%;
  left: 50%;
  opacity: 0.15;
  animation-duration: 20s;
  animation-delay: -2s;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.1); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(40px, 30px) scale(1.05); }
}

/* ===== Scanline Effect ===== */
.scanline {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 245, 255, 0.008) 2px,
    rgba(0, 245, 255, 0.008) 4px
  );
  pointer-events: none;
  z-index: 1;
}

/* ===== Back Home Button ===== */
.back-home {
  position: absolute;
  top: 24px;
  left: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 10;
}

.cyber-back-btn {
  background: var(--glass-bg) !important;
  border: 1px solid var(--glass-border) !important;
  color: var(--neon-cyan) !important;
  backdrop-filter: blur(10px);
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.15);
  transition: all 0.3s ease;
}

.cyber-back-btn:hover {
  background: rgba(0, 245, 255, 0.1) !important;
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3), inset 0 0 12px rgba(0, 245, 255, 0.1);
  transform: scale(1.05);
}

.back-text {
  color: var(--neon-cyan);
  font-size: 14px;
  text-shadow: 0 0 8px rgba(0, 245, 255, 0.4);
  letter-spacing: 1px;
}

/* ===== Glass Morphism Register Card ===== */
.cyber-login-box {
  position: relative;
  z-index: 5;
  width: 100%;
  max-width: 460px;
  background: var(--glass-bg);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  padding: 44px 40px 40px;
  animation: cardFloat 6s ease-in-out infinite;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.card-glow {
  position: absolute;
  inset: -2px;
  border-radius: 21px;
  background: linear-gradient(
    135deg,
    rgba(255, 107, 157, 0.3),
    rgba(0, 245, 255, 0.15),
    rgba(179, 136, 255, 0.2),
    rgba(255, 107, 157, 0.1)
  );
  z-index: -1;
  animation: borderGlow 4s ease-in-out infinite alternate;
}

@keyframes cardFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes borderGlow {
  0% {
    opacity: 0.5;
    filter: blur(8px);
  }
  100% {
    opacity: 1;
    filter: blur(12px);
  }
}

/* ===== Header ===== */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(179, 136, 255, 0.08);
  border: 1px solid rgba(179, 136, 255, 0.2);
  margin-bottom: 16px;
  animation: iconPulse 3s ease-in-out infinite;
}

.shop-icon {
  color: var(--neon-purple) !important;
  filter: drop-shadow(0 0 12px rgba(179, 136, 255, 0.6));
  animation: iconGlow 2s ease-in-out infinite alternate;
}

@keyframes iconPulse {
  0%, 100% { box-shadow: 0 0 20px rgba(179, 136, 255, 0.2); }
  50% { box-shadow: 0 0 40px rgba(179, 136, 255, 0.4); }
}

@keyframes iconGlow {
  0% { filter: drop-shadow(0 0 8px rgba(179, 136, 255, 0.4)); }
  100% { filter: drop-shadow(0 0 20px rgba(179, 136, 255, 0.8)); }
}

.cyber-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--sakura), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
  letter-spacing: 1px;
}

.title-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}

.deco-line {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-pink), transparent);
}

.deco-diamond {
  color: var(--neon-pink);
  font-size: 10px;
  text-shadow: 0 0 6px rgba(255, 107, 157, 0.6);
  animation: diamondSpin 4s linear infinite;
}

@keyframes diamondSpin {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(90deg); }
  50% { transform: rotate(180deg); }
  75% { transform: rotate(270deg); }
}

/* ===== Form Styles ===== */
.cyber-form :deep(.el-form-item__label) {
  color: var(--text-primary) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  letter-spacing: 0.5px;
  padding-bottom: 6px;
}

.cyber-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.04) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  color: var(--text-primary) !important;
  transition: all 0.3s ease;
  padding: 4px 16px !important;
}

.cyber-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 245, 255, 0.2) !important;
  background: rgba(255, 255, 255, 0.06) !important;
}

.cyber-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 16px rgba(0, 245, 255, 0.2), inset 0 0 8px rgba(0, 245, 255, 0.05) !important;
  background: rgba(0, 245, 255, 0.04) !important;
}

.cyber-input :deep(.el-input__inner) {
  color: var(--text-primary) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

.cyber-input :deep(.el-input__inner::placeholder) {
  color: var(--text-secondary) !important;
}

.cyber-input :deep(.el-input__prefix .el-icon) {
  color: var(--text-secondary) !important;
  transition: color 0.3s ease;
}

.cyber-input :deep(.el-input__wrapper.is-focus .el-input__prefix .el-icon) {
  color: var(--neon-cyan) !important;
}

.cyber-input :deep(.el-input__suffix .el-icon) {
  color: var(--text-secondary) !important;
}

/* ===== Submit Button ===== */
.cyber-submit-btn {
  background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink)) !important;
  border: none !important;
  border-radius: 12px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 4px !important;
  color: #fff !important;
  height: 48px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 20px rgba(179, 136, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.cyber-submit-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 30%, rgba(255, 255, 255, 0.15) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.cyber-submit-btn:hover::before {
  transform: translateX(100%);
}

.cyber-submit-btn:hover {
  box-shadow: 0 6px 30px rgba(179, 136, 255, 0.5), 0 0 40px rgba(255, 107, 157, 0.2) !important;
  transform: translateY(-2px);
}

.cyber-submit-btn:active {
  transform: translateY(0);
}

/* ===== Register Link ===== */
.register-link {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 8px;
}

.cyber-link {
  color: var(--neon-cyan) !important;
  font-weight: 500;
  text-shadow: 0 0 8px rgba(0, 245, 255, 0.3);
  transition: all 0.3s ease;
}

.cyber-link:hover {
  text-shadow: 0 0 16px rgba(0, 245, 255, 0.6);
}

/* ===== Responsive ===== */
@media (max-width: 480px) {
  .cyber-login-box {
    padding: 32px 20px 28px;
    border-radius: 16px;
    max-width: 100%;
  }

  .cyber-title {
    font-size: 22px;
  }

  .icon-wrapper {
    width: 64px;
    height: 64px;
  }

  .shop-icon {
    font-size: 40px !important;
  }

  .back-home {
    top: 16px;
    left: 16px;
  }

  .orb-1, .orb-2 {
    width: 250px;
    height: 250px;
  }

  .orb-3, .orb-4 {
    width: 150px;
    height: 150px;
  }
}
</style>
