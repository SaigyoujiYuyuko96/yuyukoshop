<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-title-group">
        <div class="title-decoration"></div>
        <div>
          <h2 class="page-title">订单管理</h2>
          <span class="page-subtitle">ORDER MANAGEMENT</span>
        </div>
      </div>
    </div>

    <div class="glass-card filter-card">
      <el-form :inline="true" class="cyber-filter-form">
        <el-form-item label="订单状态">
          <el-select v-model="filterStatus" placeholder="全部" clearable @change="fetchOrders" class="cyber-select">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="-1" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="filterOrderNo" placeholder="请输入订单号" clearable @keyup.enter="fetchOrders" class="cyber-input" />
        </el-form-item>
        <el-form-item>
          <el-button class="cyber-btn-primary" @click="fetchOrders">
            <span class="btn-icon-search">&#x1F50D;</span>
            <span>搜索</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="glass-card">
      <el-table :data="orders" v-loading="loading" class="cyber-table">
        <el-table-column prop="id" label="ID" width="80">
          <template #default="{ row }">
            <span class="cell-id">#{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span class="cell-order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="100">
          <template #default="{ row }">
            <span class="cell-user">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="110">
          <template #default="{ row }">
            <span class="cell-price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <span :class="['neon-tag', `neon-tag-${statusClass(row.status)}`]">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="trackingNo" label="物流单号" width="150">
          <template #default="{ row }">
            <span v-if="row.trackingNo" class="cell-tracking">{{ row.trackingNo }}</span>
            <span v-else class="cell-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170">
          <template #default="{ row }">
            <span class="cell-time">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button v-if="row.status === 1" size="small" class="cyber-btn-ship" @click="showShipDialog(row)">发货</el-button>
              <el-button size="small" class="cyber-btn-edit" @click="viewDetail(row.id)">详情</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @change="fetchOrders" class="cyber-pagination" />
    </div>

    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px" class="cyber-dialog">
      <el-form :model="shipForm" label-width="100px" class="cyber-form">
        <el-form-item label="订单号">
          <el-input :value="currentOrder?.orderNo" disabled class="cyber-input" />
        </el-form-item>
        <el-form-item label="收货人">
          <el-input :value="currentOrder?.receiverName" disabled class="cyber-input" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input :value="currentOrder?.shippingAddress" disabled type="textarea" :rows="2" class="cyber-input" />
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" class="cyber-input" />
        </el-form-item>
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.companyCode" placeholder="可选，系统将自动识别" class="cyber-select" style="width:100%">
            <el-option label="自动识别" value="" />
            <el-option label="顺丰速运" value="shunfeng" />
            <el-option label="圆通速递" value="yuantong" />
            <el-option label="申通快递" value="shentong" />
            <el-option label="中通快递" value="zhongtong" />
            <el-option label="韵达快递" value="yunda" />
            <el-option label="京东物流" value="jd" />
            <el-option label="EMS" value="ems" />
            <el-option label="极兔速递" value="jinguangsudu" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cyber-btn-cancel" @click="shipDialogVisible = false">取消</el-button>
          <el-button class="cyber-btn-primary" @click="confirmShip" :loading="shipLoading">确认发货</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const filterStatus = ref(null)
const filterOrderNo = ref('')
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const currentOrder = ref(null)
const shipForm = ref({ trackingNo: '', companyCode: '' })

const statusMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '退款中', 5: '已退款', '-1': '已取消' }
const statusText = (status) => statusMap[status] || '未知'
const statusType = (status) => { const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'success', 4: 'warning', 5: 'info', '-1': 'danger' }; return map[status] || 'info' }
const statusClass = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'done', 4: 'warning', 5: 'info', '-1': 'danger' }
  return map[status] || 'info'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filterStatus.value !== null) params.status = filterStatus.value
    if (filterOrderNo.value) params.orderNo = filterOrderNo.value
    const res = await request.get('/admin/orders', { params })
    orders.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e); ElMessage.error('获取订单列表失败') } finally { loading.value = false }
}

const showShipDialog = (order) => { currentOrder.value = order; shipForm.value = { trackingNo: '', companyCode: '' }; shipDialogVisible.value = true }

const confirmShip = async () => {
  if (!shipForm.value.trackingNo) { ElMessage.warning('请输入物流单号'); return }
  shipLoading.value = true
  try { await request.put(`/admin/orders/${currentOrder.value.id}/ship`, { trackingNo: shipForm.value.trackingNo, companyCode: shipForm.value.companyCode }); ElMessage.success('发货成功'); shipDialogVisible.value = false; fetchOrders() } catch (e) { console.error(e); ElMessage.error('发货失败') } finally { shipLoading.value = false }
}

const viewDetail = (id) => { router.push(`/orders/${id}`) }
onMounted(fetchOrders)
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
  padding: 20px;
  backdrop-filter: blur(10px);
}

.filter-card {
  margin-bottom: 20px;
}

/* ========== FILTER FORM ========== */
.cyber-filter-form :deep(.el-form-item__label) {
  color: var(--text-secondary, #9CA3AF) !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif;
  font-weight: 500;
}

.cyber-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  color: var(--text-primary, #E8E8E8) !important;
  transition: all 0.3s;
}

.cyber-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(179, 136, 255, 0.3) !important;
}

.cyber-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--neon-purple, #B388FF) !important;
  box-shadow: 0 0 12px rgba(179, 136, 255, 0.15) !important;
}

.cyber-input :deep(.el-input__inner) {
  color: var(--text-primary, #E8E8E8) !important;
}

.cyber-input :deep(.el-input__inner::placeholder) {
  color: rgba(156, 163, 175, 0.5) !important;
}

.cyber-select :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  color: var(--text-primary, #E8E8E8) !important;
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

.btn-icon-search {
  margin-right: 4px;
  font-size: 13px;
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

.cyber-btn-ship {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.15), rgba(0, 245, 255, 0.1)) !important;
  border: 1px solid rgba(0, 255, 136, 0.4) !important;
  color: #00ff88 !important;
  border-radius: 6px !important;
  font-family: 'Zen Maru Gothic', 'Noto Sans SC', sans-serif !important;
  transition: all 0.3s !important;
}

.cyber-btn-ship:hover {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.25), rgba(0, 245, 255, 0.15)) !important;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.3) !important;
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

.cell-order-no {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: var(--neon-cyan, #00F5FF);
  letter-spacing: 0.5px;
}

.cell-user {
  font-weight: 500;
  color: var(--text-primary, #E8E8E8);
}

.cell-price {
  color: var(--neon-pink, #FF6B9D);
  font-weight: 700;
  font-size: 14px;
  text-shadow: 0 0 8px rgba(255, 107, 157, 0.3);
}

.cell-tracking {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: var(--text-primary, #E8E8E8);
}

.cell-muted {
  color: var(--text-secondary, #9CA3AF);
  opacity: 0.4;
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

.neon-tag-warning {
  background: rgba(255, 215, 0, 0.12);
  border: 1px solid rgba(255, 215, 0, 0.4);
  color: var(--gold, #FFD700);
  text-shadow: 0 0 6px rgba(255, 215, 0, 0.3);
}

.neon-tag-primary {
  background: rgba(179, 136, 255, 0.12);
  border: 1px solid rgba(179, 136, 255, 0.4);
  color: var(--neon-purple, #B388FF);
  text-shadow: 0 0 6px rgba(179, 136, 255, 0.3);
}

.neon-tag-success {
  background: rgba(0, 255, 136, 0.1);
  border: 1px solid rgba(0, 255, 136, 0.4);
  color: #00ff88;
  text-shadow: 0 0 6px rgba(0, 255, 136, 0.3);
}

.neon-tag-done {
  background: rgba(0, 245, 255, 0.1);
  border: 1px solid rgba(0, 245, 255, 0.4);
  color: var(--neon-cyan, #00F5FF);
  text-shadow: 0 0 6px rgba(0, 245, 255, 0.3);
}

.neon-tag-info {
  background: rgba(156, 163, 175, 0.1);
  border: 1px solid rgba(156, 163, 175, 0.3);
  color: var(--text-secondary, #9CA3AF);
}

.neon-tag-danger {
  background: rgba(255, 107, 157, 0.12);
  border: 1px solid rgba(255, 107, 157, 0.4);
  color: var(--neon-pink, #FF6B9D);
  text-shadow: 0 0 6px rgba(255, 107, 157, 0.3);
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
</style>
