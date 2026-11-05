<template>
  <div>
    <h2>评论管理</h2>
    <el-table :data="comments" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="内容" min-width="300" />
      <el-table-column prop="postId" label="帖子ID" width="100" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '显示' : '屏蔽' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="danger" @click="toggleStatus(row)">屏蔽</el-button>
          <el-button v-if="row.status === 0" size="small" type="success" @click="toggleStatus(row)">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="fetchComments" class="pagination" />
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comments', { params: { page: page.value, size: size.value } })
    comments.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const toggleStatus = async (row) => {
  try {
    const action = row.status === 1 ? '屏蔽' : '恢复'
    await ElMessageBox.confirm(`确认${action}该评论吗？`, '提示', { type: 'warning' })
    await request.put(`/admin/comments/${row.id}/status`, { status: row.status === 1 ? 0 : 1 })
    ElMessage.success(`${action}成功`)
    fetchComments()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(fetchComments)
</script>

<style scoped>
.pagination { margin-top: 20px; justify-content: flex-end }
</style>
