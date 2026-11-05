<template>
  <div>
    <NavHeader />
    <div class="container profile-page" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div class="user-header">
              <el-avatar :size="80" :src="user.avatar">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
              <h3>{{ user.nickname || user.username }}</h3>
              <p class="username">@{{ user.username }}</p>
            </div>
            <div class="follow-stats" v-if="followCount">
              <span @click="activeTab='following'">关注 {{ followCount.following || 0 }}</span>
              <span @click="activeTab='followers'">粉丝 {{ followCount.followers || 0 }}</span>
            </div>
            <div class="actions" v-if="!isSelf">
              <el-button :type="isFollowing ? 'default' : 'primary'" @click="toggleFollow" :loading="followLoading">
                {{ isFollowing ? '已关注' : '关注' }}
              </el-button>
              <el-button @click="goToChat">发私信</el-button>
            </div>
          </el-card>

          <el-card style="margin-top:15px" v-if="badges.length">
            <h4>勋章墙</h4>
            <div class="badge-list">
              <el-tooltip v-for="b in badges" :key="b.id" :content="b.name + ': ' + (b.description||'')">
                <span class="badge-item" :class="{ earned: b.earned }">
                  {{ b.icon || b.name?.charAt(0) }}
                </span>
              </el-tooltip>
            </div>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="TA的帖子" name="posts">
                <el-empty v-if="posts.length===0" description="暂无帖子" />
                <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover"
                         @click="$router.push(`/community/posts/${post.id}`)">
                  <div class="post-title">{{ post.title }}</div>
                  <div class="post-meta">
                    <span>{{ post.createTime }}</span>
                    <span>浏览 {{ post.viewCount }} · 赞 {{ post.likeCount }} · 评论 {{ post.commentCount }}</span>
                  </div>
                </el-card>
              </el-tab-pane>
              <el-tab-pane label="关注列表" name="following" v-if="!isSelf || activeTab==='following'">
                <div v-for="u in followingList" :key="u.id" class="follow-user" @click="$router.push(`/users/${u.id}`)">
                  <el-avatar :size="36" :src="u.avatar">{{ u.username?.charAt(0) }}</el-avatar>
                  <span>{{ u.nickname || u.username }}</span>
                </div>
                <el-empty v-if="!followingList.length" description="暂无关注" />
              </el-tab-pane>
              <el-tab-pane label="粉丝列表" name="followers" v-if="!isSelf || activeTab==='followers'">
                <div v-for="u in followerList" :key="u.id" class="follow-user" @click="$router.push(`/users/${u.id}`)">
                  <el-avatar :size="36" :src="u.avatar">{{ u.username?.charAt(0) }}</el-avatar>
                  <span>{{ u.nickname || u.username }}</span>
                </div>
                <el-empty v-if="!followerList.length" description="暂无粉丝" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const user = ref({})
const posts = ref([])
const badges = ref([])
const followCount = ref(null)
const isFollowing = ref(false)
const followLoading = ref(false)
const loading = ref(false)
const activeTab = ref('posts')
const followingList = ref([])
const followerList = ref([])

const isSelf = () => userStore.userInfo?.id === user.value.id

watch(activeTab, (tab) => {
  if (tab === 'following') fetchFollowing()
  if (tab === 'followers') fetchFollowers()
})

onMounted(async () => {
  const userId = route.params.id
  loading.value = true
  try {
    const [uRes, pRes, bRes, cRes, fRes] = await Promise.allSettled([
      request.get(`/users/${userId}`),
      request.get(`/forums/posts/user/${userId}`),
      request.get(`/users/badges/user/${userId}`),
      request.get(`/users/follow/count`),
      userStore.isLoggedIn && userId != userStore.userInfo.id ? request.get(`/users/is-following/${userId}`) : Promise.resolve(null)
    ])
    if (uRes.status === 'fulfilled') user.value = uRes.value.data || {}
    if (pRes.status === 'fulfilled') posts.value = pRes.value.data || []
    if (bRes.status === 'fulfilled') badges.value = bRes.value.data || []
    if (cRes.status === 'fulfilled') followCount.value = cRes.value.data
    if (fRes.status === 'fulfilled' && fRes.value) isFollowing.value = fRes.value.data
  } catch (e) { console.error(e) } finally { loading.value = false }
})

const toggleFollow = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return }
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await request.delete(`/users/follow/${user.value.id}`)
      isFollowing.value = false
      if (followCount.value) followCount.value.followers--
    } else {
      await request.post(`/users/follow/${user.value.id}`)
      isFollowing.value = true
      if (followCount.value) followCount.value.followers++
    }
  } catch (e) { console.error(e) } finally { followLoading.value = false }
}

const goToChat = () => router.push(`/messages?userId=${user.value.id}`)

const fetchFollowing = async () => {
  try { const res = await request.get(`/users/following`); followingList.value = res.data || [] } catch (e) {}
}
const fetchFollowers = async () => {
  try { const res = await request.get(`/users/followers`); followerList.value = res.data || [] } catch (e) {}
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px }
.profile-page { margin-top: 20px }
.user-header { text-align: center; padding: 20px 0 }
.user-header h3 { margin: 10px 0 5px }
.username { color: #999; font-size: 13px }
.follow-stats { display: flex; justify-content: center; gap: 30px; padding: 15px 0; border-top: 1px solid #eee; margin-top: 10px }
.follow-stats span { cursor: pointer; color: #409eff; font-weight: 500 }
.actions { text-align: center; padding-top: 10px }
.badge-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px }
.badge-item { width: 36px; height: 36px; border-radius: 50%; background: #eee; display: flex; align-items: center; justify-content: center; font-size: 16px; opacity: 0.4 }
.badge-item.earned { background: #ffd700; opacity: 1 }
.post-card { margin-bottom: 12px; cursor: pointer }
.post-title { font-weight: bold; margin-bottom: 8px }
.post-meta { display: flex; justify-content: space-between; color: #999; font-size: 13px }
.follow-user { display: flex; align-items: center; gap: 12px; padding: 10px; cursor: pointer; border-radius: 8px }
.follow-user:hover { background: #f5f7fa }
</style>
