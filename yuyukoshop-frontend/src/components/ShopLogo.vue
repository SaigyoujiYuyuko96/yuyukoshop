<template>
  <div class="logo-container" @click="$router.push('/')">
    <div class="logo-icon-wrap">
      <img src="/logo.jpg" alt="幽幽子Shop" class="logo-img" />
      <div class="logo-glow"></div>
      <div class="logo-ring"></div>
    </div>
    <div class="logo-text-wrap">
      <span class="logo-text">幽幽子<span class="logo-accent">Shop</span></span>
      <span class="logo-sub">YUYUKO</span>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
  transition: transform 0.3s ease;
}

.logo-container:hover {
  transform: scale(1.03);
}

.logo-container:hover .logo-glow {
  opacity: 0.6;
}

.logo-container:hover .logo-ring {
  transform: rotate(360deg);
  opacity: 0.8;
}

.logo-icon-wrap {
  position: relative;
  width: 42px;
  height: 42px;
  flex-shrink: 0;
}

.logo-img {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  object-fit: cover;
  position: relative;
  z-index: 2;
}

.logo-glow {
  position: absolute;
  inset: -4px;
  border-radius: 16px;
  background: linear-gradient(135deg, #B388FF, #00F5FF, #FF6B9D);
  opacity: 0;
  filter: blur(8px);
  transition: opacity 0.4s ease;
  z-index: 1;
}

.logo-ring {
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  border: 1.5px solid transparent;
  background: linear-gradient(135deg, #B388FF, #00F5FF, #FF6B9D, #B388FF) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s ease, transform 0.6s ease;
  z-index: 3;
}

.logo-text-wrap {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.logo-text {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: #F0F0F5;
  letter-spacing: 1px;
}

.logo-accent {
  background: linear-gradient(135deg, #B388FF, #00F5FF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-sub {
  font-family: 'Orbitron', monospace;
  font-size: 9px;
  font-weight: 500;
  letter-spacing: 3px;
  color: #6B7280;
  margin-top: 1px;
}

/* 响应式 */
@media (max-width: 768px) {
  .logo-text-wrap {
    display: none;
  }
  
  .logo-icon-wrap {
    width: 36px;
    height: 36px;
  }
  
  .logo-img {
    width: 36px;
    height: 36px;
    border-radius: 10px;
  }
}
</style>
