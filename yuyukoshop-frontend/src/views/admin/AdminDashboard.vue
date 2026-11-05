<template>
  <div class="dashboard-page">
    <h2 class="page-title">数据概览</h2>
    <div class="stats-grid">
      <div class="stat-card stat-users">
        <div class="stat-icon"><el-icon><User /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card stat-products">
        <div class="stat-icon"><el-icon><Goods /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
          <div class="stat-label">上架商品</div>
        </div>
      </div>
      <div class="stat-card stat-orders">
        <div class="stat-icon"><el-icon><List /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card stat-sales">
        <div class="stat-icon"><el-icon><Money /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatPrice(stats.totalSales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
    </div>
    <div class="today-stats">
      <div class="today-card">
        <div class="today-label">今日订单</div>
        <div class="today-value">{{ stats.todayOrders || 0 }}</div>
      </div>
      <div class="today-card">
        <div class="today-label">今日销售额</div>
        <div class="today-value">¥{{ formatPrice(stats.todaySales) }}</div>
      </div>
      <div class="today-card warning">
        <div class="today-label">待处理订单</div>
        <div class="today-value">{{ stats.pendingOrders || 0 }}</div>
      </div>
      <div class="today-card danger">
        <div class="today-label">待处理退款</div>
        <div class="today-value">{{ stats.pendingRefunds || 0 }}</div>
      </div>
    </div>
    <div class="recent-section">
      <h3 class="section-title">最近订单</h3>
      <el-table :data="stats.recentOrders || []" class="orders-table" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" />
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
const stats = ref({})
const fetchDashboard = async () => {
  try {
    const res = await request.get('/admin/dashboard')
    stats.value = res.data || {}
  } catch (e) { console.error(e); ElMessage.error('获取数据失败') }
}
const formatPrice = (price) => { if (!price) return '0.00'; return parseFloat(price).toFixed(2) }
const getStatusType = (status) => ({ '-1': 'info', '0': 'warning', '1': 'primary', '2': 'success', '3': 'success', '4': 'danger', '5': 'info' })[status] || 'info'
const getStatusText = (status) => ({ '-1': '已取消', '0': '待支付', '1': '已支付', '2': '已发货', '3': '已完成', '4': '退款中', '5': '已退款' })[status] || '未知'
onMounted(() => { fetchDashboard() })
</script>
<style scoped>
.dashboard-page { padding: 0; }
.page-title { font-size: 20px; color: #E8E8E8; margin: 0 0 24px; font-weight: 600; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.stat-card { background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 16px; transition: all 0.3s; }
.stat-card:hover { border-color: rgba(179,136,255,0.3); box-shadow: 0 0 20px rgba(179,136,255,0.1); }
.stat-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.stat-users .stat-icon { background: rgba(0,245,255,0.15); color: #00F5FF; }
.stat-products .stat-icon { background: rgba(179,136,255,0.15); color: #B388FF; }
.stat-orders .stat-icon { background: rgba(255,107,157,0.15); color: #FF6B9D; }
.stat-sales .stat-icon { background: rgba(255,215,0,0.15); color: #FFD700; }
.stat-info { flex: 1; }
.stat-value { font-size: 28px; font-weight: 700; color: #E8E8E8; line-height: 1.2; }
.stat-label { font-size: 13px; color: #9CA3AF; margin-top: 4px; }
.today-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.today-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); border-radius: 10px; padding: 16px; text-align: center; }
.today-card.warning { border-color: rgba(255,183,0,0.3); background: rgba(255,183,0,0.05); }
.today-card.danger { border-color: rgba(255,107,157,0.3); background: rgba(255,107,157,0.05); }
.today-label { font-size: 12px; color: #9CA3AF; margin-bottom: 8px; }
.today-value { font-size: 24px; font-weight: 700; color: #E8E8E8; }
.today-card.warning .today-value { color: #FFB700; }
.today-card.danger .today-value { color: #FF6B9D; }
.recent-section { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); border-radius: 12px; padding: 20px; }
.section-title { font-size: 16px; color: #E8E8E8; margin: 0 0 16px; font-weight: 600; }
.orders-table :deep(.el-table__header-wrapper th) { background: rgba(255,255,255,0.05) !important; color: #9CA3AF !important; border: none !important; }
.orders-table :deep(.el-table__body-wrapper td) { background: transparent !important; color: #E8E8E8 !important; border-bottom: 1px solid rgba(255,255,255,0.05) !important; }
@media (max-width: 1200px) { .stats-grid, .today-stats { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .stats-grid, .today-stats { grid-template-columns: 1fr; } }
</style>
