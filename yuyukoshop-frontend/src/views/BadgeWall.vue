<template>
  <div class="badge-wall-page">
    <NavHeader />
    <div class="cyber-container">
      <div class="page-header">
        <h1 class="page-title">我的勋章墙</h1>
        <p class="page-desc">收集勋章，展示你的成就</p>
      </div>

      <!-- 已获得勋章 -->
      <div class="section" v-if="earnedBadges.length > 0">
        <h2 class="section-title">
          <span class="title-icon">&#x2728;</span>
          已获得 ({{ earnedBadges.length }})
        </h2>
        <div class="badge-grid">
          <div v-for="badge in earnedBadges" :key="badge.id" class="badge-card earned">
            <div class="badge-icon-wrap earned-glow">
              <div class="badge-icon">{{ getBadgeEmoji(badge) }}</div>
            </div>
            <div class="badge-name">{{ badge.name }}</div>
            <div class="badge-desc">{{ badge.description }}</div>
            <div class="badge-time">{{ formatDate(badge.earnedTime) }}</div>
          </div>
        </div>
      </div>

      <!-- 未获得勋章 -->
      <div class="section" v-if="lockedBadges.length > 0">
        <h2 class="section-title">
          <span class="title-icon">&#x1F512;</span>
          未解锁 ({{ lockedBadges.length }})
        </h2>
        <div class="badge-grid">
          <div v-for="badge in lockedBadges" :key="badge.id" class="badge-card locked">
            <div class="badge-icon-wrap locked-glow">
              <div class="badge-icon locked-icon">{{ getBadgeEmoji(badge) }}</div>
              <div class="lock-overlay">&#x1F512;</div>
            </div>
            <div class="badge-name">{{ badge.name }}</div>
            <div class="badge-desc">{{ badge.description }}</div>
            <div class="badge-condition">解锁条件：{{ getConditionText(badge) }}</div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="allBadges.length === 0" class="empty-state">
        <div class="empty-icon">&#x1F3C6;</div>
        <p>暂无勋章数据</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, computed, onMounted } from 'vue'

const allBadges = ref([])

const earnedBadges = computed(() => allBadges.value.filter(b => b.earned))
const lockedBadges = computed(() => allBadges.value.filter(b => !b.earned))

const fetchBadges = async () => {
  try {
    const res = await request.get('/users/badges/wall')
    allBadges.value = res.data || []
  } catch (e) { console.error(e) }
}

const getBadgeEmoji = (badge) => {
  const emojis = {
    '新人入坑': '&#x1F3AF;',
    '首次下单': '&#x1F6D2;',
    '购物达人': '&#x1F4B0;',
    '购物狂人': '&#x1F451;',
    '社区新人': '&#x270D;',
    '论坛达人': '&#x1F4DD;',
    '人气之星': '&#x2B50;',
    '社交蝴蝶': '&#x1F98B;',
    '收藏家': '&#x1F4E6;'
  }
  return emojis[badge.name] || '&#x1F3C6;'
}

const getConditionText = (badge) => {
  const conditions = {
    'REGISTER': '完成注册',
    'FIRST_ORDER': '完成首次下单',
    'ORDER_COUNT': `累计完成${badge.conditionValue}笔订单`,
    'POST_COUNT': `累计发布${badge.conditionValue}篇帖子`,
    'FOLLOWER_COUNT': `粉丝数达到${badge.conditionValue}人`,
    'FAVORITE_COUNT': `收藏商品达到${badge.conditionValue}件`
  }
  return conditions[badge.conditionType] || '达成特定条件'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

onMounted(() => { fetchBadges() })
</script>

<style scoped>
.badge-wall-page {
  min-height: 100vh;
  background: #1A1A2E;
  color: #E8E8E8;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

.cyber-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, #FFD700, #FF6B9D, #B388FF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 8px;
}

.page-desc {
  color: #9CA3AF;
  font-size: 14px;
  margin: 0;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #E8E8E8;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 20px;
}

.badge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.badge-card {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 16px;
  padding: 24px 16px;
  text-align: center;
  transition: all 0.3s;
}

.badge-card.earned {
  border-color: rgba(255,215,0,0.3);
  background: linear-gradient(135deg, rgba(255,215,0,0.08), rgba(255,107,157,0.05));
}

.badge-card.earned:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(255,215,0,0.15);
}

.badge-card.locked {
  opacity: 0.5;
}

.badge-icon-wrap {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.earned-glow {
  background: linear-gradient(135deg, rgba(255,215,0,0.2), rgba(255,107,157,0.15));
  box-shadow: 0 0 20px rgba(255,215,0,0.2);
}

.locked-glow {
  background: rgba(255,255,255,0.05);
}

.badge-icon {
  font-size: 36px;
}

.locked-icon {
  filter: grayscale(1);
}

.lock-overlay {
  position: absolute;
  bottom: -2px;
  right: -2px;
  font-size: 16px;
  background: rgba(26,26,46,0.8);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-name {
  font-size: 16px;
  font-weight: 700;
  color: #E8E8E8;
  margin-bottom: 6px;
}

.badge-desc {
  font-size: 12px;
  color: #9CA3AF;
  margin-bottom: 8px;
  line-height: 1.5;
}

.badge-time {
  font-size: 11px;
  color: #FFD700;
}

.badge-condition {
  font-size: 11px;
  color: #9CA3AF;
  font-style: italic;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #9CA3AF;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .badge-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
  .badge-card { padding: 16px 12px; }
  .badge-icon-wrap { width: 60px; height: 60px; }
  .badge-icon { font-size: 28px; }
}
</style>
