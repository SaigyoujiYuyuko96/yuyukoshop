<template>
  <div class="follow-page">
    <NavHeader />
    <div class="cyber-container">
      <div class="follow-layout">
        <aside class="sidebar-glass">
          <div class="avatar-section">
            <div class="avatar-ring">
              <el-avatar :size="80" :src="userInfo.avatar" class="cyber-avatar">
                <el-icon :size="36"><User /></el-icon>
              </el-avatar>
            </div>
            <h3 class="cyber-username">{{ userInfo.username }}</h3>
            <div class="follow-stats">
              <div class="stat-item" @click="activeTab = 'following'">
                <div class="stat-num">{{ followCount.following || 0 }}</div>
                <div class="stat-label">关注</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item" @click="activeTab = 'followers'">
                <div class="stat-num">{{ followCount.followers || 0 }}</div>
                <div class="stat-label">粉丝</div>
              </div>
            </div>
          </div>
          <nav class="cyber-nav">
            <div class="nav-item" :class="{ active: activeTab === 'following' }" @click="activeTab = 'following'">
              <el-icon><Connection /></el-icon>
              <span>我的关注</span>
            </div>
            <div class="nav-item" :class="{ active: activeTab === 'followers' }" @click="activeTab = 'followers'">
              <el-icon><UserFilled /></el-icon>
              <span>我的粉丝</span>
            </div>
          </nav>
        </aside>
        <main class="content-area">
          <h2 class="content-title">{{ activeTab === 'following' ? '我的关注' : '我的粉丝' }}</h2>
          <div v-if="activeTab === 'following'" class="user-list">
            <div v-if="followingList.length === 0" class="empty-state">
              <p>还没有关注任何人</p>
            </div>
            <div v-else class="user-grid">
              <div v-for="user in followingList" :key="user.id" class="user-card">
                <el-avatar :size="60" :src="user.avatar || defaultAvatar"><el-icon><User /></el-icon></el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ user.nickname || user.username }}</div>
                  <div class="user-username">@{{ user.username }}</div>
                </div>
                <div class="user-actions">
                  <button class="btn-msg" @click="goChat(user.id)">私信</button>
                  <button class="btn-unfollow" @click="unfollow(user.id)">取消关注</button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="user-list">
            <div v-if="followerList.length === 0" class="empty-state">
              <p>还没有粉丝</p>
            </div>
            <div v-else class="user-grid">
              <div v-for="user in followerList" :key="user.id" class="user-card">
                <el-avatar :size="60" :src="user.avatar || defaultAvatar"><el-icon><User /></el-icon></el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ user.nickname || user.username }}</div>
                  <div class="user-username">@{{ user.username }}</div>
                </div>
                <div class="user-actions">
                  <button class="btn-msg" @click="goChat(user.id)">私信</button>
                  <button class="btn-follow" @click="follow(user.id)">关注</button>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>
<script setup>
import NavHeader from '@/components/NavHeader.vue'
import UserPopover from '@/components/UserPopover.vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = 'https://via.placeholder.com/50?text=User'
const activeTab = ref('following')
const userInfo = ref(userStore.userInfo)
const followCount = ref({})
const followingList = ref([])
const followerList = ref([])
const fetchFollowCount = async () => {
  try { const res = await request.get('/users/follow/count'); followCount.value = res.data || {} } catch (e) {}
}
const fetchFollowing = async () => {
  try { const res = await request.get('/users/following'); followingList.value = res.data || [] } catch (e) {}
}
const fetchFollowers = async () => {
  try { const res = await request.get('/users/followers'); followerList.value = res.data || [] } catch (e) {}
}
const unfollow = async (userId) => {
  try { await request.delete(`/users/follow/${userId}`); ElMessage.success('取消关注成功'); fetchFollowing(); fetchFollowCount() } catch (e) {}
}
const follow = async (userId) => {
  try { await request.post(`/users/follow/${userId}`); ElMessage.success('关注成功'); fetchFollowers(); fetchFollowCount() } catch (e) {}
}
const goChat = (userId) => {
  router.push(`/messages/chat/${userId}`)
}
onMounted(() => { fetchFollowCount(); fetchFollowing(); fetchFollowers() })
</script>
<style scoped>
.follow-page { min-height: 100vh; background: #1A1A2E; color: #E8E8E8; }
.cyber-container { max-width: 1280px; margin: 0 auto; padding: 30px 24px; }
.follow-layout { display: grid; grid-template-columns: 280px 1fr; gap: 24px; }
.sidebar-glass { background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 24px; }
.avatar-section { text-align: center; padding-bottom: 20px; margin-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,0.1); }
.avatar-ring { display: inline-block; margin-bottom: 12px; }
.cyber-avatar { border: 2px solid #B388FF; }
.cyber-username { font-size: 18px; margin: 0 0 16px; }
.follow-stats { display: flex; justify-content: center; align-items: center; gap: 20px; }
.stat-item { cursor: pointer; text-align: center; }
.stat-num { font-size: 20px; font-weight: 700; }
.stat-label { font-size: 12px; color: #9CA3AF; }
.stat-divider { width: 1px; height: 30px; background: rgba(255,255,255,0.2); }
.cyber-nav { display: flex; flex-direction: column; gap: 8px; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 10px; cursor: pointer; transition: all 0.3s; }
.nav-item:hover, .nav-item.active { background: rgba(179,136,255,0.15); color: #00F5FF; }
.content-area { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; padding: 24px; }
.content-title { font-size: 20px; margin: 0 0 20px; padding-bottom: 16px; border-bottom: 1px solid rgba(255,255,255,0.1); }
.empty-state { text-align: center; padding: 60px 20px; color: #9CA3AF; }
.user-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; }
.user-card { display: flex; align-items: center; gap: 16px; padding: 16px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 12px; }
.user-info { flex: 1; }
.user-name { font-size: 16px; font-weight: 600; }
.user-username { font-size: 13px; color: #9CA3AF; }
.btn-unfollow { padding: 8px 16px; background: transparent; border: 1px solid rgba(255,255,255,0.2); color: #9CA3AF; border-radius: 8px; cursor: pointer; }
.btn-unfollow:hover { border-color: #FF6B9D; color: #FF6B9D; }
.user-actions { display: flex; flex-direction: column; gap: 6px; flex-shrink: 0; }
.btn-msg { padding: 6px 14px; background: rgba(0, 245, 255, 0.1); border: 1px solid rgba(0, 245, 255, 0.3); color: #00F5FF; border-radius: 8px; cursor: pointer; font-size: 12px; transition: all 0.3s; }
.btn-msg:hover { background: rgba(0, 245, 255, 0.2); box-shadow: 0 0 10px rgba(0, 245, 255, 0.2); }
.btn-follow { padding: 6px 14px; background: rgba(179, 136, 255, 0.1); border: 1px solid rgba(179, 136, 255, 0.3); color: #B388FF; border-radius: 8px; cursor: pointer; font-size: 12px; transition: all 0.3s; }
.btn-follow:hover { background: rgba(179, 136, 255, 0.2); box-shadow: 0 0 10px rgba(179, 136, 255, 0.2); }
@media (max-width: 900px) { .follow-layout { grid-template-columns: 1fr; } }
</style>

