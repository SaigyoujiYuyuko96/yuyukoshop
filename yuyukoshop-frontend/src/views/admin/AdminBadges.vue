<template>
  <div>
    <div class="page-header">
      <h2>勋章管理</h2>
      <el-button type="primary" @click="openAdd">新增勋章</el-button>
    </div>
    <el-table :data="badges" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图标" width="70">
        <template #default="{ row }">
          <span class="badge-icon">{{ row.icon || row.name?.charAt(0) || '🏅' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" width="120" />
      <el-table-column prop="description" label="描述" min-width="160" />
      <el-table-column prop="type" label="类型" width="110">
        <template #default="{ row }">
          <el-tag :type="row.type === 'ACHIEVEMENT' ? 'success' : 'info'" size="small">
            {{ row.type === 'ACHIEVEMENT' ? '成就勋章' : '系统勋章' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="conditionType" label="条件类型" width="120" />
      <el-table-column prop="conditionValue" label="条件值" width="80" />
      <el-table-column prop="sortOrder" label="排序" width="60" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteBadge(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑勋章' : '新增勋章'" width="500px">
      <el-form :model="dialog" label-width="100px">
        <el-form-item label="勋章名称"><el-input v-model="dialog.name" /></el-form-item>
        <el-form-item label="图标表情"><el-input v-model="dialog.icon" placeholder="如 🏅" maxlength="4" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="dialog.description" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="dialog.type">
            <el-radio value="SYSTEM">系统勋章</el-radio>
            <el-radio value="ACHIEVEMENT">成就勋章</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="条件类型">
          <el-select v-model="dialog.conditionType" placeholder="选择条件" style="width:100%">
            <el-option label="注册" value="REGISTER" />
            <el-option label="首次下单" value="FIRST_ORDER" />
            <el-option label="订单数量" value="ORDER_COUNT" />
            <el-option label="发帖数量" value="POST_COUNT" />
            <el-option label="粉丝数量" value="FOLLOWER_COUNT" />
            <el-option label="收藏数量" value="FAVORITE_COUNT" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件值"><el-input-number v-model="dialog.conditionValue" :min="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="dialog.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dialog.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveBadge" :loading="dialog.loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const badges = ref([])
const loading = ref(false)

const dialog = reactive({
  visible: false, isEdit: false, loading: false,
  id: null, name: '', icon: '', description: '', type: 'SYSTEM',
  conditionType: '', conditionValue: 0, sortOrder: 0, status: 1
})

const fetchBadges = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/badges')
    badges.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const resetDialog = () => Object.assign(dialog, {
  id: null, name: '', icon: '', description: '', type: 'SYSTEM',
  conditionType: '', conditionValue: 0, sortOrder: 0, status: 1
})

const openAdd = () => { resetDialog(); dialog.visible = true; dialog.isEdit = false }
const openEdit = (row) => {
  resetDialog()
  Object.assign(dialog, row)
  dialog.visible = true; dialog.isEdit = true
}

const saveBadge = async () => {
  if (!dialog.name) { ElMessage.warning('请输入名称'); return }
  dialog.loading = true
  try {
    const data = {
      name: dialog.name, icon: dialog.icon, description: dialog.description,
      type: dialog.type, conditionType: dialog.conditionType || null,
      conditionValue: dialog.conditionValue, sortOrder: dialog.sortOrder, status: dialog.status
    }
    if (dialog.isEdit) {
      await request.put(`/admin/badges/${dialog.id}`, data)
    } else {
      await request.post('/admin/badges', data)
    }
    ElMessage.success(dialog.isEdit ? '更新成功' : '创建成功')
    dialog.visible = false
    fetchBadges()
  } catch (e) { console.error(e) } finally { dialog.loading = false }
}

const deleteBadge = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除勋章"${row.name}"吗？`, '提示', { type: 'warning' })
    await request.delete(`/admin/badges/${row.id}`)
    ElMessage.success('删除成功')
    fetchBadges()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(fetchBadges)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px }
.badge-icon { font-size: 28px }
</style>
