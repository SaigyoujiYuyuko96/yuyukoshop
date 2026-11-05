<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-title-group">
        <div class="title-decoration"></div>
        <div>
          <h2 class="page-title">帖子管理</h2>
          <span class="page-subtitle">POST MANAGEMENT</span>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <el-table :data="posts" v-loading="loading" class="cyber-table">
        <el-table-column prop="id" label="ID" width="80">
          <template #default="{ row }">
            <span class="cell-id">#{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题">
          <template #default="{ row }">
            <span class="cell-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120">
          <template #default="{ row }">
            <span class="cell-author">{{ row.author }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">
            <span class="cell-time">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <span :class="['neon-tag', row.status === 'APPROVED' ? 'neon-tag-approved' : 'neon-tag-pending']">
              {{ row.status === 'APPROVED' ? '已通过' : '待审核' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button v-if="row.status !== 'APPROVED'" size="small" class="cyber-btn-approve" @click="approvePost(row.id)">通过</el-button>
              <el-button size="small" class="cyber-btn-danger" @click="deletePost(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="fetchPosts" class="cyber-pagination" />
    </div>
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const posts = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

const fetchPosts = async () => {
  loading.value = true
  try { const res = await request.get('/admin/posts', { params: { page: page.value, size: size.value } }); posts.value = res.data?.records || []; total.value = res.data?.total || 0 } catch (e) { console.error(e) } finally { loading.value = false }
}

const approvePost = async (id) => {
  try { await request.put(`/admin/posts/${id}/approve`); ElMessage.success('审核通过'); fetchPosts() } catch (e) { console.error(e) }
}

const deletePost = async (id) => {
  try { await ElMessageBox.confirm('确认删除该帖子吗？', '提示', { type: 'warning' }); await request.delete(`/admin/posts/${id}`); ElMessage.success('删除成功'); fetchPosts() } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(fetchPosts)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic:wght@400;500;700;900&family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

.admin-page {
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  color: var(--text-primary, #E8E8E8);
}

/* ========== PAGE HEADER ========== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title-group {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-decoration {
  width: 4px;
  height: 36px;
  background: linear-gradient(180deg, var(--neon-purple, #B388FF), var(--neon-cyan, #00F5FF));
  border-radius: 2px;
  box-shadow: 0 0 12px rgba(179, 136, 255, 0.5);
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--neon-purple, #B388FF), var(--sakura, #FFB7C5));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 10px;
  letter-spacing: 3px;
  color: var(--text-secondary, #9CA3AF);
  font-weight: 700;
  opacity: 0.6;
}

/* ========== GLASS CARD ========== */
.glass-card {
  background: var(--glass-bg, rgba(255, 255, 255, 0.05));
  border: 1px solid var(--glass-border, rgba(255, 255, 255, 0.1));
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(10px);
}

/* ========== TABLE ========== */
.cyber-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.cyber-table :deep(.el-table__header-wrapper th) {
  background: rgba(179, 136, 255, 0.08) !important;
  color: var(--neon-cyan, #00F5FF) !important;
  border-bottom: 1px solid rgba(179, 136, 255, 0.2) !important;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
}

.cyber-table :deep(.el-table__body-wrapper td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
  background: transparent !important;
  color: var(--text-primary, #E8E8E8);
}

.cyber-table :deep(.el-table__body tr) {
  transition: all 0.3s;
}

.cyber-table :deep(.el-table__body tr:hover > td) {
  background: rgba(179, 136, 255, 0.08) !important;
}

.cyber-table :deep(.el-table__empty-block) {
  background: transparent !important;
}

.cyber-table :deep(.el-table__body-wrapper),
.cyber-table :deep(.el-table__header-wrapper) {
  background: transparent !important;
}

/* ========== CELL STYLES ========== */
.cell-id {
  color: var(--text-secondary, #9CA3AF);
  font-size: 12px;
  font-weight: 500;
}

.cell-title {
  font-weight: 500;
  color: var(--text-primary, #E8E8E8);
}

.cell-author {
  font-weight: 500;
  color: var(--neon-purple, #B388FF);
}

.cell-time {
  font-size: 12px;
  color: var(--text-secondary, #9CA3AF);
}

/* ========== NEON STATUS TAGS ========== */
.neon-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.neon-tag-approved {
  background: rgba(0, 255, 136, 0.1);
  border: 1px solid rgba(0, 255, 136, 0.4);
  color: #00ff88;
  text-shadow: 0 0 6px rgba(0, 255, 136, 0.3);
}

.neon-tag-pending {
  background: rgba(255, 215, 0, 0.12);
  border: 1px solid rgba(255, 215, 0, 0.4);
  color: var(--gold, #FFD700);
  text-shadow: 0 0 6px rgba(255, 215, 0, 0.3);
  animation: pending-pulse 2s ease-in-out infinite;
}

@keyframes pending-pulse {
  0%, 100% { box-shadow: 0 0 4px rgba(255, 215, 0, 0.1); }
  50% { box-shadow: 0 0 12px rgba(255, 215, 0, 0.25); }
}

/* ========== BUTTONS ========== */
.cyber-btn-approve {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.15), rgba(0, 245, 255, 0.1)) !important;
  border: 1px solid rgba(0, 255, 136, 0.4) !important;
  color: #00ff88 !important;
  border-radius: 6px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  transition: all 0.3s !important;
}

.cyber-btn-approve:hover {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.25), rgba(0, 245, 255, 0.15)) !important;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.3) !important;
}

.cyber-btn-danger {
  background: rgba(255, 107, 157, 0.1) !important;
  border: 1px solid rgba(255, 107, 157, 0.3) !important;
  color: var(--neon-pink, #FF6B9D) !important;
  border-radius: 6px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  transition: all 0.3s !important;
}

.cyber-btn-danger:hover {
  background: rgba(255, 107, 157, 0.2) !important;
  box-shadow: 0 0 12px rgba(255, 107, 157, 0.3) !important;
}

/* ========== ACTION BUTTONS ========== */
.action-buttons {
  display: flex;
  gap: 8px;
}

/* ========== PAGINATION ========== */
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.cyber-pagination :deep(.el-pagination__total) {
  color: var(--text-secondary, #9CA3AF);
}

.cyber-pagination :deep(.btn-prev),
.cyber-pagination :deep(.btn-next),
.cyber-pagination :deep(.el-pager li) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: var(--text-secondary, #9CA3AF) !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px !important;
  transition: all 0.3s;
}

.cyber-pagination :deep(.el-pager li:hover) {
  color: var(--neon-cyan, #00F5FF) !important;
  border-color: rgba(0, 245, 255, 0.3);
}

.cyber-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, rgba(179, 136, 255, 0.3), rgba(0, 245, 255, 0.2)) !important;
  color: var(--neon-cyan, #00F5FF) !important;
  border-color: rgba(179, 136, 255, 0.5);
  box-shadow: 0 0 10px rgba(179, 136, 255, 0.2);
}
</style>
