<template>
  <div>
    <NavHeader />
    <div class="container chat-page">
      <el-row :gutter="0" class="chat-container">
        <el-col :span="6" class="conversation-list">
          <el-card class="conv-card">
            <h3>私信</h3>
            <div v-for="conv in conversations" :key="conv.targetId"
                 :class="['conv-item', { active: activeUserId === conv.targetId }]"
                 @click="openChat(conv)">
              <el-avatar :size="40" :src="conv.targetAvatar">{{ conv.targetName?.charAt(0) }}</el-avatar>
              <div class="conv-info">
                <div class="conv-name">
                  {{ conv.targetName }}
                  <el-badge v-if="conv.unreadCount" :value="conv.unreadCount" class="unread" />
                </div>
                <div class="conv-last">{{ conv.lastMessage }}</div>
              </div>
            </div>
            <el-empty v-if="!conversations.length" description="暂无会话" />
          </el-card>
        </el-col>
        <el-col :span="18" class="chat-area" v-if="activeUserId">
          <el-card class="chat-card">
            <div class="chat-header">
              <span>{{ activeUserName }}</span>
            </div>
            <div class="chat-messages" ref="msgContainer">
              <div v-for="msg in messages" :key="msg.id" :class="['msg-item', msg.isSelf ? 'msg-self' : 'msg-other']">
                <el-avatar :size="32" :src="msg.isSelf ? myAvatar : activeUserAvatar">
                  {{ msg.isSelf ? (myName?.charAt(0)) : (activeUserName?.charAt(0)) }}
                </el-avatar>
                <div class="msg-bubble">{{ msg.content }}</div>
              </div>
              <el-empty v-if="!messages.length" description="暂无消息，发送第一条吧" />
            </div>
            <div class="chat-input">
              <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="sendMessage" />
              <el-button type="primary" :icon="Promotion" @click="sendMessage" :disabled="!inputText.trim()">发送</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="18" v-else>
          <el-empty description="选择一位用户开始聊天" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()

const conversations = ref([])
const messages = ref([])
const activeUserId = ref(null)
const activeUserName = ref('')
const activeUserAvatar = ref('')
const inputText = ref('')
const msgContainer = ref(null)

const myAvatar = ref('')
const myName = ref('')

let pollTimer = null

const fetchConversations = async () => {
  try {
    const res = await request.get('/messages/conversations')
    conversations.value = res.data || []
  } catch (e) {}
}

const openChat = async (conv) => {
  activeUserId.value = conv.targetId
  activeUserName.value = conv.targetName
  activeUserAvatar.value = conv.targetAvatar
  await fetchMessages()
}

const fetchMessages = async () => {
  if (!activeUserId.value) return
  try {
    const res = await request.get(`/messages/chat/${activeUserId.value}`)
    messages.value = res.data || []
    await nextTick()
    if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  } catch (e) {}
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return
  try {
    await request.post('/messages/send', { receiverId: activeUserId.value, content: inputText.value })
    inputText.value = ''
    await fetchMessages()
    await fetchConversations()
  } catch (e) { ElMessage.error('发送失败') }
}

onMounted(async () => {
  myAvatar.value = userStore.userInfo.avatar || ''
  myName.value = userStore.userInfo.nickname || userStore.userInfo.username || ''
  await fetchConversations()
  const targetId = route.query.userId
  if (targetId) {
    activeUserId.value = Number(targetId)
    try {
      const uRes = await request.get(`/users/${targetId}`, { silent: true })
      activeUserName.value = uRes.data.nickname || uRes.data.username
      activeUserAvatar.value = uRes.data.avatar
    } catch (e) {}
    await fetchMessages()
  }
  pollTimer = setInterval(fetchConversations, 10000)
})
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px }
.chat-container { height: calc(100vh - 110px); background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.08) }
.conversation-list { border-right: 1px solid #eee; height: 100% }
.conv-card { height: 100%; border: none; overflow-y: auto }
.conv-card h3 { margin-bottom: 15px }
.conv-item { display: flex; align-items: center; gap: 12px; padding: 12px; cursor: pointer; border-radius: 8px; margin-bottom: 4px }
.conv-item:hover, .conv-item.active { background: #f0f2ff }
.conv-info { flex: 1; min-width: 0 }
.conv-name { font-weight: 500; display: flex; align-items: center; gap: 8px }
.conv-last { color: #999; font-size: 12px; margin-top: 3px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap }
.chat-area { height: 100% }
.chat-card { height: 100%; border: none; display: flex; flex-direction: column }
.chat-header { padding: 12px 20px; font-weight: 600; border-bottom: 1px solid #eee }
.chat-messages { flex: 1; overflow-y: auto; padding: 15px 20px }
.msg-item { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 16px }
.msg-self { flex-direction: row-reverse }
.msg-bubble { max-width: 60%; padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5 }
.msg-other .msg-bubble { background: #f0f2f5 }
.msg-self .msg-bubble { background: #409eff; color: #fff }
.chat-input { display: flex; gap: 10px; padding: 15px 20px; border-top: 1px solid #eee }
.chat-input .el-input { flex: 1 }
</style>
