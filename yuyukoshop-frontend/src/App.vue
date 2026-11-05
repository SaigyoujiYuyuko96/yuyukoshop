<template>
  <router-view />
</template>

<style>
/* ============================================
   幽幽子Shop - 全局样式
   设计风格：日系赛博朋克动漫风
   ============================================ */

/* ---------- CSS 变量体系 ---------- */
:root {
  /* 主题色 */
  --sakura: #FFB7C5;
  --neon-purple: #B388FF;
  --deep-blue: #1A1A2E;
  --cyber-blue: #16213E;
  --neon-cyan: #00F5FF;
  --neon-pink: #FF6B9D;
  --gold: #FFD700;

  /* 文字色 */
  --text-primary: #E8E8E8;
  --text-secondary: #9CA3AF;

  /* 毛玻璃效果 */
  --glass-bg: rgba(255, 255, 255, 0.05);
  --glass-border: rgba(255, 255, 255, 0.1);
}

/* ---------- 全局重置 ---------- */
*,
*::before,
*::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* ---------- 全局过渡 ---------- */
* {
  transition: all 0.3s ease;
}

/* ---------- Body 基础 ---------- */
body {
  font-family: 'Noto Sans SC', 'Helvetica Neue', Helvetica, 'PingFang SC',
    'Microsoft YaHei', Arial, sans-serif;
  background-color: #0F0F1A;
  /* 微妙的径向渐变，营造深空氛围 */
  background-image: radial-gradient(ellipse at 20% 50%, rgba(179, 136, 255, 0.08) 0%, transparent 50%),
                    radial-gradient(ellipse at 80% 20%, rgba(0, 245, 255, 0.05) 0%, transparent 50%),
                    radial-gradient(ellipse at 50% 80%, rgba(255, 107, 157, 0.05) 0%, transparent 50%);
  background-attachment: fixed;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
  min-height: 100vh;
}

/* ---------- 标题字体 ---------- */
h1, h2, h3, h4, h5, h6 {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-weight: 700;
  line-height: 1.3;
}

/* ---------- 通用容器 ---------- */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ============================================
   自定义滚动条 - 霓虹紫渐变风格
   ============================================ */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(15, 15, 26, 0.8);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, var(--neon-purple), var(--neon-pink));
  border-radius: 4px;
  border: 1px solid rgba(179, 136, 255, 0.3);
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, var(--neon-pink), var(--neon-purple));
  box-shadow: 0 0 8px rgba(179, 136, 255, 0.5);
}

/* Firefox 滚动条 */
* {
  scrollbar-width: thin;
  scrollbar-color: var(--neon-purple) rgba(15, 15, 26, 0.8);
}

/* ============================================
   全局链接样式
   ============================================ */
a {
  text-decoration: none;
  color: var(--text-primary);
  position: relative;
}

a:hover {
  color: var(--neon-pink);
  text-shadow: 0 0 8px rgba(255, 107, 157, 0.6);
}

/* ============================================
   选中文字样式
   ============================================ */
::selection {
  background-color: rgba(179, 136, 255, 0.4);
  color: #fff;
}

::-moz-selection {
  background-color: rgba(179, 136, 255, 0.4);
  color: #fff;
}

/* ============================================
   关键帧动画
   ============================================ */

/* 悬浮动画 - 轻柔上下浮动 */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 霓虹发光脉冲 - 呼吸式光晕 */
@keyframes glow {
  0%, 100% {
    box-shadow: 0 0 5px rgba(179, 136, 255, 0.3),
                0 0 10px rgba(179, 136, 255, 0.2);
  }
  50% {
    box-shadow: 0 0 15px rgba(179, 136, 255, 0.6),
                0 0 30px rgba(179, 136, 255, 0.3),
                0 0 45px rgba(179, 136, 255, 0.1);
  }
}

/* 从下滑入 */
@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 淡入 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 微光扫过效果 - 从左到右的光泽 */
@keyframes shimmer {
  0% {
    background-position: -200% center;
  }
  100% {
    background-position: 200% center;
  }
}

/* ---------- 动画工具类 ---------- */
.animate-float {
  animation: float 3s ease-in-out infinite;
}

.animate-glow {
  animation: glow 2s ease-in-out infinite;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out forwards;
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out forwards;
}

.animate-shimmer {
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.08) 50%,
    transparent 100%
  );
  background-size: 200% 100%;
  animation: shimmer 2s linear infinite;
}

/* ============================================
   Element Plus 组件覆盖
   ============================================ */

/* ---------- 按钮：渐变背景 + hover 发光 ---------- */
.el-button {
  font-family: 'Noto Sans SC', sans-serif;
  font-weight: 500;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink));
  color: #fff;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.el-button:hover {
  box-shadow: 0 0 15px rgba(179, 136, 255, 0.5),
              0 0 30px rgba(255, 107, 157, 0.3);
  transform: translateY(-1px);
}

.el-button:active {
  transform: translateY(0);
}

/* 主要按钮增强 */
.el-button--primary {
  background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink)) !important;
  border: none !important;
}

.el-button--primary:hover {
  box-shadow: 0 0 20px rgba(179, 136, 255, 0.6),
              0 0 40px rgba(255, 107, 157, 0.3) !important;
}

/* 成功按钮 */
.el-button--success {
  background: linear-gradient(135deg, #00c9a7, #00f5d4) !important;
  border: none !important;
  color: #0F0F1A !important;
}

.el-button--success:hover {
  box-shadow: 0 0 20px rgba(0, 201, 167, 0.5),
              0 0 40px rgba(0, 245, 212, 0.3) !important;
}

/* 危险按钮 */
.el-button--danger {
  background: linear-gradient(135deg, #ff4757, #ff6b81) !important;
  border: none !important;
}

.el-button--danger:hover {
  box-shadow: 0 0 20px rgba(255, 71, 87, 0.5),
              0 0 40px rgba(255, 107, 129, 0.3) !important;
}

/* 警告按钮 */
.el-button--warning {
  background: linear-gradient(135deg, var(--gold), #ffaa00) !important;
  border: none !important;
  color: #0F0F1A !important;
}

.el-button--warning:hover {
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.5),
              0 0 40px rgba(255, 170, 0, 0.3) !important;
}

/* 朴素/文字按钮 */
.el-button--default {
  background: var(--glass-bg) !important;
  border: 1px solid var(--glass-border) !important;
  color: var(--text-primary) !important;
}

.el-button--default:hover {
  border-color: var(--neon-purple) !important;
  color: var(--neon-purple) !important;
  box-shadow: 0 0 10px rgba(179, 136, 255, 0.3);
}

/* ---------- 卡片：毛玻璃效果 ---------- */
.el-card {
  background: var(--glass-bg) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 16px !important;
  color: var(--text-primary);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.el-card:hover {
  border-color: rgba(179, 136, 255, 0.3) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3),
              0 0 15px rgba(179, 136, 255, 0.1);
}

.el-card__header {
  border-bottom: 1px solid var(--glass-border) !important;
  color: var(--text-primary);
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

.el-card__body {
  color: var(--text-primary);
}

/* ---------- 输入框：暗色背景 + 聚焦霓虹青边框 ---------- */
.el-input__wrapper {
  background: rgba(22, 33, 62, 0.8) !important;
  border: 1px solid var(--glass-border) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  color: var(--text-primary);
}

.el-input__wrapper:hover {
  border-color: rgba(0, 245, 255, 0.3) !important;
}

.el-input__wrapper.is-focus {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.2),
              0 0 20px rgba(0, 245, 255, 0.1) !important;
}

.el-input__inner {
  color: var(--text-primary) !important;
  font-family: 'Noto Sans SC', sans-serif;
}

.el-input__inner::placeholder {
  color: var(--text-secondary) !important;
}

/* 文本域 */
.el-textarea__inner {
  background: rgba(22, 33, 62, 0.8) !important;
  border: 1px solid var(--glass-border) !important;
  border-radius: 8px !important;
  color: var(--text-primary) !important;
  font-family: 'Noto Sans SC', sans-serif;
}

.el-textarea__inner:hover {
  border-color: rgba(0, 245, 255, 0.3) !important;
}

.el-textarea__inner:focus {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.2),
              0 0 20px rgba(0, 245, 255, 0.1) !important;
}

/* ---------- 标签：暗色背景 + 霓虹色文字 ---------- */
.el-tag {
  background: rgba(22, 33, 62, 0.8) !important;
  border: 1px solid var(--glass-border) !important;
  border-radius: 6px !important;
  color: var(--neon-cyan) !important;
  font-family: 'Noto Sans SC', sans-serif;
}

.el-tag--success {
  background: rgba(0, 201, 167, 0.15) !important;
  border-color: rgba(0, 201, 167, 0.3) !important;
  color: #00f5d4 !important;
}

.el-tag--warning {
  background: rgba(255, 215, 0, 0.15) !important;
  border-color: rgba(255, 215, 0, 0.3) !important;
  color: var(--gold) !important;
}

.el-tag--danger {
  background: rgba(255, 71, 87, 0.15) !important;
  border-color: rgba(255, 71, 87, 0.3) !important;
  color: #ff6b81 !important;
}

.el-tag--info {
  background: rgba(156, 163, 175, 0.15) !important;
  border-color: rgba(156, 163, 175, 0.3) !important;
  color: var(--text-secondary) !important;
}

/* ---------- 对话框 ---------- */
.el-dialog {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 48px rgba(0, 0, 0, 0.5),
              0 0 30px rgba(179, 136, 255, 0.1);
}

.el-dialog__header {
  border-bottom: 1px solid var(--glass-border);
}

.el-dialog__title {
  color: var(--text-primary) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

.el-dialog__body {
  color: var(--text-primary);
}

.el-dialog__headerbtn .el-dialog__close {
  color: var(--text-secondary) !important;
}

.el-dialog__headerbtn .el-dialog__close:hover {
  color: var(--neon-pink) !important;
}

/* ---------- 下拉菜单 ---------- */
.el-dropdown-menu {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 8px !important;
}

.el-dropdown-menu__item {
  color: var(--text-primary) !important;
}

.el-dropdown-menu__item:hover {
  background: rgba(179, 136, 255, 0.15) !important;
  color: var(--neon-purple) !important;
}

/* ---------- 表格 ---------- */
.el-table {
  background: transparent !important;
  color: var(--text-primary);
}

.el-table tr {
  background: transparent !important;
}

.el-table th.el-table__cell {
  background: rgba(22, 33, 62, 0.6) !important;
  color: var(--neon-cyan) !important;
  border-bottom: 1px solid var(--glass-border) !important;
}

.el-table td.el-table__cell {
  border-bottom: 1px solid var(--glass-border) !important;
}

.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
  background: rgba(179, 136, 255, 0.08) !important;
}

.el-table__empty-text {
  color: var(--text-secondary) !important;
}

/* ---------- 分页 ---------- */
.el-pagination {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--text-secondary);
  --el-pagination-button-bg-color: var(--glass-bg);
  --el-pagination-hover-color: var(--neon-purple);
}

.el-pagination .el-pager li {
  background: var(--glass-bg) !important;
  color: var(--text-secondary) !important;
  border-radius: 6px;
}

.el-pagination .el-pager li.is-active {
  background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink)) !important;
  color: #fff !important;
}

.el-pagination .el-pager li:hover {
  color: var(--neon-purple) !important;
}

/* ---------- 消息提示 ---------- */
.el-message {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 10px !important;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.4);
}

.el-message__content {
  color: var(--text-primary) !important;
}

/* ---------- 导航菜单 ---------- */
.el-menu {
  background: transparent !important;
  border: none !important;
}

.el-menu-item,
.el-sub-menu__title {
  color: var(--text-secondary) !important;
}

.el-menu-item:hover,
.el-menu-item.is-active {
  color: var(--neon-purple) !important;
  background: rgba(179, 136, 255, 0.1) !important;
}

/* ---------- 选择器 ---------- */
.el-select-dropdown {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 8px !important;
}

.el-select-dropdown__item {
  color: var(--text-primary) !important;
}

.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover {
  background: rgba(179, 136, 255, 0.15) !important;
}

.el-select-dropdown__item.selected {
  color: var(--neon-purple) !important;
  font-weight: 700;
}
</style>
