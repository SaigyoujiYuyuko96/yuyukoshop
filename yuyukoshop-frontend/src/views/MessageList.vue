<template>
  <div class="message-page">
    <NavHeader />
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">私信</h2>
        <span class="unread-badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
      </div>

      <div class="conversation-list" v-loading="loading">
        <div
          class="conversation-item"
          v-for="conv in conversations"
          :key="conv.targetId"
          @click="goToChat(conv.targetId)"
        >
          <div class="avatar-wrap">
            <img
              :src="conv.targetAvatar || defaultAvatar"
              class="avatar"
              alt=""
            />
            <span class="system-badge" v-if="conv.type === 1">系统</span>
          </div>
          <div class="conv-info">
            <div class="conv-header">
              <span class="conv-name">{{ conv.targetName }}</span>
              <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
            </div>
            <div class="conv-content">
              <span class="last-message">{{ conv.lastMessage }}</span>
              <span class="unread-dot" v-if="conv.unreadCount > 0">{{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}</span>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && conversations.length === 0" description="暂无私信" />
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const conversations = ref([])
const unreadCount = ref(0)
const loading = ref(false)
const defaultAvatar = 'https://via.placeholder.com/50?text=User'

onMounted(async () => {
  await fetchConversations()
  await fetchUnreadCount()
})

const fetchConversations = async () => {
  loading.value = true
  try {
    const res = await request.get('/messages/conversations')
    conversations.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await request.get('/messages/unread-count')
    unreadCount.value = res.data || 0
  } catch (e) {
    console.error(e)
  }
}

const goToChat = (targetId) => {
  router.push(`/messages/chat/${targetId}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  return date.toLocaleDateString()
}
</script>

<style scoped>
.message-page {
  min-height: 100vh;
  background: var(--bg-primary, #0a0a0f);
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  color: #fff;
  font-weight: 600;
  margin: 0;
}

.unread-badge {
  background: var(--neon-pink, #FF6B9D);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

.conversation-list {
  background: var(--glass-bg, rgba(255,255,255,0.05));
  border: 1px solid var(--glass-border, rgba(255,255,255,0.1));
  border-radius: 16px;
  overflow: hidden;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid var(--glass-border, rgba(255,255,255,0.05));
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: rgba(255,255,255,0.05);
}

.avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.system-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  background: var(--neon-purple, #B388FF);
  color: #fff;
  font-size: 10px;
  padding: 1px 5px;
  border-radius: 6px;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.conv-name {
  font-size: 15px;
  color: #fff;
  font-weight: 500;
}

.conv-time {
  font-size: 12px;
  color: var(--text-secondary, #9CA3AF);
}

.conv-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  font-size: 13px;
  color: var(--text-secondary, #9CA3AF);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 8px;
}

.unread-dot {
  background: var(--neon-pink, #FF6B9D);
  color: #fff;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  font-weight: 600;
  flex-shrink: 0;
}
</style>
