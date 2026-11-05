<template>
  <div class="chat-page">
    <NavHeader />
    <div class="chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <el-button text @click="$router.back()" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <div class="target-info">
          <img :src="targetInfo.avatar || defaultAvatar" class="target-avatar" alt="" />
          <span class="target-name">{{ targetInfo.name }}</span>
        </div>
        <div class="placeholder"></div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div
          class="message-item"
          v-for="msg in messages"
          :key="msg.id"
          :class="{ 'is-self': msg.isSelf }"
        >
          <img
            :src="msg.isSelf ? userInfo.avatar : msg.senderAvatar || defaultAvatar"
            class="msg-avatar"
            alt=""
          />
          <div class="msg-content">
            <div class="msg-bubble">{{ msg.content }}</div>
            <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
          </div>
        </div>

        <el-empty v-if="!loading && messages.length === 0" description="暂无消息" />
      </div>

      <!-- 输入框 -->
      <div class="input-area" v-if="targetId != 0">
        <el-input
          v-model="inputText"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
          class="msg-input"
        />
        <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
      </div>
      <div class="system-tip" v-else>
        <el-icon><InfoFilled /></el-icon>
        <span>系统通知消息，无法回复</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const targetId = computed(() => Number(route.params.targetId))

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const sending = ref(false)
const messageListRef = ref(null)
const targetInfo = ref({ name: '', avatar: '' })
const defaultAvatar = 'https://via.placeholder.com/50?text=User'

const userInfo = computed(() => userStore.userInfo || {})

onMounted(async () => {
  await fetchChatHistory()
  loadTargetInfo()
})

const fetchChatHistory = async () => {
  loading.value = true
  try {
    const res = await request.get(`/messages/chat/${targetId.value}`)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadTargetInfo = () => {
  if (targetId.value === 0) {
    targetInfo.value = { name: '系统通知', avatar: '' }
  } else {
    // 从会话列表或消息中获取
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      targetInfo.value = {
        name: lastMsg.senderName,
        avatar: lastMsg.senderAvatar
      }
    }
  }
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return

  sending.value = true
  try {
    const res = await request.post('/messages/send', {
      receiverId: targetId.value,
      content: inputText.value.trim()
    })
    messages.value.push(res.data)
    inputText.value = ''
    await nextTick()
    scrollToBottom()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '发送失败')
  } finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: var(--bg-primary, #0a0a0f);
}

.chat-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--glass-bg, rgba(255,255,255,0.05));
  border-bottom: 1px solid var(--glass-border, rgba(255,255,255,0.1));
}

.back-btn {
  color: #fff;
}

.target-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.target-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.target-name {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.placeholder {
  width: 40px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-item.is-self {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.msg-content {
  max-width: 60%;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 16px;
  background: var(--glass-bg, rgba(255,255,255,0.1));
  color: #fff;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.is-self .msg-bubble {
  background: linear-gradient(135deg, var(--neon-purple, #B388FF), var(--neon-pink, #FF6B9D));
}

.msg-time {
  font-size: 11px;
  color: var(--text-secondary, #9CA3AF);
  margin-top: 4px;
  text-align: right;
}

.is-self .msg-time {
  text-align: left;
}

.input-area {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--glass-bg, rgba(255,255,255,0.05));
  border-top: 1px solid var(--glass-border, rgba(255,255,255,0.1));
}

.msg-input {
  flex: 1;
}

.msg-input :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  box-shadow: none;
}

.msg-input :deep(.el-input__inner) {
  color: #fff;
}

.system-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: var(--text-secondary, #9CA3AF);
  font-size: 13px;
  background: var(--glass-bg, rgba(255,255,255,0.05));
  border-top: 1px solid var(--glass-border, rgba(255,255,255,0.1));
}
</style>
