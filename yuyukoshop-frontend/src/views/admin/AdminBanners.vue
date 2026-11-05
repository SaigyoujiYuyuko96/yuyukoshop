<template>
  <div class="banners-page">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <el-button type="primary" @click="showAddDialog">+ 新增轮播图</el-button>
    </div>

    <el-table :data="banners" v-loading="loading" stripe class="banners-table">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <img :src="row.imageUrl" class="banner-thumb" alt="" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="subtitle" label="副标题" />
      <el-table-column prop="linkUrl" label="链接" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => toggleStatus(row.id, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑轮播图' : '新增轮播图'"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接，如 /products" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const banners = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentId = ref(null)

const form = ref({
  title: '',
  subtitle: '',
  imageUrl: '',
  linkUrl: '',
  sortOrder: 0,
  status: 1
})

onMounted(() => {
  fetchBanners()
})

const fetchBanners = async () => {
  loading.value = true
  try {
    const res = await request.get('/banners')
    banners.value = res.data?.records || []
  } catch (e) {
    console.error(e)
    ElMessage.error('获取轮播图失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  currentId.value = null
  form.value = {
    title: '',
    subtitle: '',
    imageUrl: '',
    linkUrl: '',
    sortOrder: 0,
    status: 1
  }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.title || !form.value.imageUrl) {
    ElMessage.warning('请填写标题和图片URL')
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/banners/${currentId.value}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/banners', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchBanners()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' })
    await request.delete(`/banners/${id}`)
    ElMessage.success('删除成功')
    fetchBanners()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const toggleStatus = async (id, status) => {
  try {
    await request.put(`/banners/${id}/status`, { status })
    ElMessage.success('状态更新成功')
  } catch (e) {
    ElMessage.error('状态更新失败')
    fetchBanners()
  }
}
</script>

<style scoped>
.banners-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  color: #E8E8E8;
  margin: 0;
  font-weight: 600;
}

.banners-table :deep(.el-table__header-wrapper th) {
  background: rgba(255,255,255,0.05) !important;
  color: #9CA3AF !important;
  border: none !important;
}

.banners-table :deep(.el-table__body-wrapper td) {
  background: transparent !important;
  color: #E8E8E8 !important;
  border-bottom: 1px solid rgba(255,255,255,0.05) !important;
}

.banner-thumb {
  width: 80px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
