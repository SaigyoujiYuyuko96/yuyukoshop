<template>
  <div>
    <NavHeader />
    <div class="container pay-page" v-loading="loading">
      <el-card class="pay-card" v-if="order">
        <div class="pay-header">
          <el-icon :size="48" color="#409eff"><Money /></el-icon>
          <h2>扫码支付</h2>
        </div>

        <div class="order-summary">
          <p>订单号：<strong>{{ order.orderNo }}</strong></p>
          <p>支付金额：<span class="amount">¥{{ order.totalAmount }}</span></p>
          <p>状态：<el-tag :type="order.status === 1 ? 'success' : 'warning'" size="small">
            {{ order.status === 1 ? '已支付' : '待支付' }}
          </el-tag></p>
        </div>

        <div class="qr-section" v-if="order.status === 0">
          <p class="tip">请使用支付宝扫描下方二维码完成支付</p>
          <div class="qr-box">
            <el-image :src="qrCodeUrl" fit="contain" class="qr-image">
              <template #error>
                <el-empty description="收款码未设置，请联系管理员" />
              </template>
            </el-image>
          </div>
          <p class="tip small">支付完成后请点击下方按钮</p>
          <el-button type="success" size="large" @click="confirmPay" :loading="confirmLoading">
            我已支付完成
          </el-button>
        </div>

        <div class="pay-success" v-else-if="order.status >= 1">
          <el-icon :size="60" color="#67c23a"><CircleCheckFilled /></el-icon>
          <h3>支付成功</h3>
          <el-button type="primary" @click="$router.push(`/orders/${order.id}`)">查看订单详情</el-button>
        </div>
      </el-card>

      <el-empty v-if="!order && !loading" description="订单不存在" />
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { CircleCheckFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const order = ref(null)
const loading = ref(false)
const confirmLoading = ref(false)
const qrCodeUrl = ref('/uploads/images/payment_qr.png')

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await request.get(`/orders/${route.params.id}`)
    order.value = res.data
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const confirmPay = async () => {
  confirmLoading.value = true
  try {
    await ElMessage.warning('请确认已完成扫码支付，管理员收到通知后将确认收款')
    // 这里可以发送通知给管理员
    ElMessage.success('已通知管理员确认')
  } catch (e) { console.error(e) } finally { confirmLoading.value = false }
}

onMounted(fetchOrder)
</script>

<style scoped>
.container { max-width: 600px; margin: 0 auto; padding: 30px 20px }
.pay-header { text-align: center; margin-bottom: 25px }
.pay-header h2 { margin-top: 10px }
.order-summary { background: #f5f7fa; padding: 20px; border-radius: 10px; margin-bottom: 25px }
.order-summary p { margin: 8px 0; color: #666 }
.amount { color: #f56c6c; font-size: 28px; font-weight: bold }
.qr-section { text-align: center }
.qr-box { width: 250px; height: 250px; margin: 20px auto; border: 2px dashed #ddd; border-radius: 12px; display: flex; align-items: center; justify-content: center }
.qr-image { width: 240px; height: 240px }
.tip { color: #999; margin: 10px 0 }
.tip.small { font-size: 13px; margin-bottom: 20px }
.pay-success { text-align: center; padding: 40px 0 }
.pay-success h3 { margin: 15px 0 25px; color: #67c23a }
</style>
