<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-title-group">
        <div class="title-decoration"></div>
        <div>
          <h2 class="page-title">商品管理</h2>
          <span class="page-subtitle">PRODUCT MANAGEMENT</span>
        </div>
      </div>
      <el-button class="cyber-btn-primary" @click="showDialog = true; resetForm()">
        <span class="btn-plus">+</span>
        <span>新增商品</span>
      </el-button>
    </div>

    <div class="glass-card">
      <el-table :data="products" v-loading="loading" class="cyber-table">
        <el-table-column prop="id" label="ID" width="80">
          <template #default="{ row }">
            <span class="cell-id">#{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <div class="product-image-wrapper">
              <el-image :src="getImageUrl(row.thumbnail)" fit="cover" class="product-image" :preview-src-list="[getImageUrl(row.thumbnail)]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称">
          <template #default="{ row }">
            <span class="cell-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <span class="cell-tag">{{ row.categoryName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="110">
          <template #default="{ row }">
            <span class="cell-price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <span :class="['cell-stock', { 'low-stock': row.stock <= 5 }]">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100">
          <template #default="{ row }">
            <span class="cell-sales">{{ row.sales }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" class="cyber-btn-edit" @click="editProduct(row)">编辑</el-button>
              <el-button size="small" class="cyber-btn-danger" @click="deleteProduct(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="fetchProducts" class="cyber-pagination" />
    </div>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑商品' : '新增商品'" width="650px" class="cyber-dialog" @close="resetForm">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules" class="cyber-form">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" class="cyber-input" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%" class="cyber-select">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="商品图片">
          <ImageUpload v-model="form.thumbnail" upload-type="product" placeholder="上传商品图片" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入商品描述" class="cyber-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cyber-btn-cancel" @click="showDialog = false">取消</el-button>
          <el-button class="cyber-btn-primary" @click="saveProduct" :loading="saving">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/api/request'
import ImageUpload from '@/components/ImageUpload.vue'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const products = ref([])
const categories = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const defaultForm = { id: null, name: '', categoryId: null, price: 0, stock: 0, thumbnail: '', description: '' }
const form = reactive({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/60x60?text=No+Image'
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/products', { params: { page: page.value, size: size.value } })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const fetchCategories = async () => {
  try { const res = await request.get('/categories'); categories.value = res.data || [] } catch (e) { console.error(e) }
}

const resetForm = () => { Object.assign(form, defaultForm); isEdit.value = false }
const editProduct = (row) => { isEdit.value = true; Object.assign(form, row); showDialog.value = true }

const saveProduct = async () => {
  if (formRef.value) { await formRef.value.validate() }
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/admin/products/${form.id}`, form) } else { await request.post('/admin/products', form) }
    ElMessage.success('保存成功'); showDialog.value = false; fetchProducts()
  } catch (e) { if (e !== false) console.error(e) } finally { saving.value = false }
}

const deleteProduct = async (id) => {
  try { await ElMessageBox.confirm('确认删除该商品吗？', '提示', { type: 'warning' }); await request.delete(`/admin/products/${id}`); ElMessage.success('删除成功'); fetchProducts() } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => { fetchProducts(); fetchCategories() })
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

/* ========== BUTTONS ========== */
.cyber-btn-primary {
  background: linear-gradient(135deg, rgba(179, 136, 255, 0.3), rgba(0, 245, 255, 0.2)) !important;
  border: 1px solid rgba(179, 136, 255, 0.5) !important;
  color: var(--neon-cyan, #00F5FF) !important;
  border-radius: 8px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  font-weight: 500;
  transition: all 0.3s !important;
  box-shadow: 0 0 10px rgba(179, 136, 255, 0.15);
}

.cyber-btn-primary:hover {
  background: linear-gradient(135deg, rgba(179, 136, 255, 0.5), rgba(0, 245, 255, 0.3)) !important;
  box-shadow: 0 0 20px rgba(179, 136, 255, 0.3), 0 0 40px rgba(0, 245, 255, 0.1) !important;
  transform: translateY(-1px);
}

.btn-plus {
  font-size: 16px;
  margin-right: 4px;
  font-weight: 700;
}

.cyber-btn-edit {
  background: rgba(0, 245, 255, 0.1) !important;
  border: 1px solid rgba(0, 245, 255, 0.3) !important;
  color: var(--neon-cyan, #00F5FF) !important;
  border-radius: 6px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  transition: all 0.3s !important;
}

.cyber-btn-edit:hover {
  background: rgba(0, 245, 255, 0.2) !important;
  box-shadow: 0 0 12px rgba(0, 245, 255, 0.3) !important;
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

.cyber-btn-cancel {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: var(--text-secondary, #9CA3AF) !important;
  border-radius: 8px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  transition: all 0.3s !important;
}

.cyber-btn-cancel:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: var(--text-primary, #E8E8E8) !important;
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

.cyber-table :deep(.el-table__body-wrapper) {
  background: transparent !important;
}

.cyber-table :deep(.el-table__header-wrapper) {
  background: transparent !important;
}

/* ========== CELL STYLES ========== */
.cell-id {
  color: var(--text-secondary, #9CA3AF);
  font-size: 12px;
  font-weight: 500;
}

.cell-name {
  font-weight: 500;
  color: var(--text-primary, #E8E8E8);
}

.cell-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  background: rgba(179, 136, 255, 0.15);
  border: 1px solid rgba(179, 136, 255, 0.3);
  color: var(--neon-purple, #B388FF);
}

.cell-price {
  color: var(--neon-pink, #FF6B9D);
  font-weight: 700;
  font-size: 14px;
  text-shadow: 0 0 8px rgba(255, 107, 157, 0.3);
}

.cell-stock {
  color: var(--neon-cyan, #00F5FF);
  font-weight: 500;
}

.cell-stock.low-stock {
  color: var(--neon-pink, #FF6B9D);
  text-shadow: 0 0 8px rgba(255, 107, 157, 0.3);
}

.cell-sales {
  color: var(--gold, #FFD700);
  font-weight: 500;
}

/* ========== PRODUCT IMAGE ========== */
.product-image-wrapper {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(179, 136, 255, 0.2);
  box-shadow: 0 0 10px rgba(179, 136, 255, 0.1);
}

.product-image {
  width: 60px;
  height: 60px;
  display: block;
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

/* ========== DIALOG ========== */
.cyber-dialog :deep(.el-dialog) {
  background: linear-gradient(180deg, rgba(22, 33, 62, 0.98), rgba(26, 26, 46, 0.98)) !important;
  border: 1px solid rgba(179, 136, 255, 0.3) !important;
  border-radius: 16px !important;
  box-shadow: 0 0 40px rgba(179, 136, 255, 0.15), 0 20px 60px rgba(0, 0, 0, 0.5) !important;
  backdrop-filter: blur(20px);
}

.cyber-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(179, 136, 255, 0.2);
  padding: 20px 24px;
}

.cyber-dialog :deep(.el-dialog__title) {
  color: var(--neon-cyan, #00F5FF) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-weight: 700;
  font-size: 16px;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
}

.cyber-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: var(--text-secondary, #9CA3AF);
}

.cyber-dialog :deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: var(--neon-pink, #FF6B9D);
}

.cyber-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.cyber-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid rgba(179, 136, 255, 0.15);
  padding: 16px 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* ========== FORM ========== */
.cyber-form :deep(.el-form-item__label) {
  color: var(--text-secondary, #9CA3AF) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-weight: 500;
}

.cyber-form :deep(.el-input__wrapper),
.cyber-form :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  color: var(--text-primary, #E8E8E8) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  transition: all 0.3s;
}

.cyber-form :deep(.el-input__wrapper:hover),
.cyber-form :deep(.el-textarea__inner:hover) {
  border-color: rgba(179, 136, 255, 0.3) !important;
}

.cyber-form :deep(.el-input__wrapper.is-focus),
.cyber-form :deep(.el-textarea__inner:focus) {
  border-color: var(--neon-purple, #B388FF) !important;
  box-shadow: 0 0 12px rgba(179, 136, 255, 0.15) !important;
}

.cyber-form :deep(.el-input__inner) {
  color: var(--text-primary, #E8E8E8) !important;
}

.cyber-form :deep(.el-input__inner::placeholder),
.cyber-form :deep(.el-textarea__inner::placeholder) {
  color: rgba(156, 163, 175, 0.5) !important;
}

.cyber-form :deep(.el-select .el-select__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.cyber-form :deep(.el-input-number__decrease),
.cyber-form :deep(.el-input-number__increase) {
  background: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: var(--text-secondary, #9CA3AF) !important;
}
</style>
