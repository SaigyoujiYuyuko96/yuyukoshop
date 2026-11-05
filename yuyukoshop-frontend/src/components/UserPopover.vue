<template>
  <el-popover
    placement="bottom"
    :width="180"
    trigger="click"
    :show-arrow="false"
    popper-class="user-popover"
    @show="onShow"
  >
    <template #reference>
      <div class="avatar-trigger" :style="{ width: size + 'px', height: size + 'px' }">
        <slot></slot>
      </div>
    </template>

    <div class="popover-menu">
      <div class="pop-user-info">
        <span class="pop-username">{{ userName }}</span>
      </div>
      <div class="pop-divider"></div>

      <div class="pop-item" @click="handleFollow" v-if="userId && userId !== currentUserId">
        <el-icon v-if="following"><Select /></el-icon>
        <el-icon v-else><Plus /></el-icon>
        <span>{{ following ? '已关注' : '关注' }}</span>
      </div>

      <div class="pop-item" @click="handleMessage" v-if="userId && userId !== currentUserId">
        <el-icon><ChatDotRound /></el-icon>
        <span>私信</span>
      </div>

      <div class="pop-item disabled" v-if="userId === currentUserId">
        <el-icon><User /></el-icon>
        <span>我自己</span>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { Plus, Select, ChatDotRound, User } from '@element-plus/icons-vue'

const props = defineProps({
  userId: { type: [Number, null], default: null },
  userName: { type: String, default: '' },
  size: { type: Number, default: 44 }
})

const router = useRouter()
const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)
const following = ref(false)

const onShow = async () => {
  if (!props.userId || props.userId === currentUserId.value) return
  try {
    const res = await request.get(`/users/is-following/${props.userId}`)
    following.value = res.data || false
  } catch (e) {
    following.value = false
  }
}

const handleFollow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (following.value) {
      await request.delete(`/users/follow/${props.userId}`)
      following.value = false
      ElMessage.success('已取消关注')
    } else {
      await request.post(`/users/follow/${props.userId}`)
      following.value = true
      ElMessage.success('关注成功')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const handleMessage = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  router.push('/messages/chat')
}
</script>

<style scoped>
.avatar-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: transform 0.2s ease;
}

.avatar-trigger:hover {
  transform: scale(1.08);
}
</style>

<style>
.user-popover {
  padding: 0 !important;
  background: rgba(26, 26, 46, 0.95) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  border-radius: 12px !important;
  backdrop-filter: blur(20px) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4), 0 0 20px rgba(179, 136, 255, 0.1) !important;
}

.user-popover .pop-user-info {
  padding: 12px 14px 8px;
}

.user-popover .pop-username {
  font-size: 14px;
  font-weight: 600;
  color: #E8E8E8;
}

.user-popover .pop-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 0 8px;
}

.user-popover .pop-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  cursor: pointer;
  color: #E8E8E8;
  font-size: 13px;
  transition: all 0.2s ease;
  border-radius: 0;
}

.user-popover .pop-item:hover {
  background: rgba(179, 136, 255, 0.15);
  color: #00F5FF;
}

.user-popover .pop-item.disabled {
  color: #6B7280;
  cursor: default;
}

.user-popover .pop-item.disabled:hover {
  background: transparent;
  color: #6B7280;
}

.user-popover .pop-item .el-icon {
  font-size: 16px;
}
</style>
