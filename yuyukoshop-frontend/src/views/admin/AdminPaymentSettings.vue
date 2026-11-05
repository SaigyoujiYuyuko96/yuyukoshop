<template>
  <div>
    <h2>收款码设置</h2>
    <el-card style="margin-top:20px; max-width:500px">
      <p style="color:#666; margin-bottom:15px">上传你的支付宝/微信收款二维码，用户下单后将展示此码进行扫码支付。</p>
      <div class="qr-box">
        <el-image :src="previewKey" fit="contain" style="width:240px;height:240px">
          <template #error><el-empty description="未设置" /></template>
        </el-image>
      </div>
      <div style="margin-top:20px; text-align:center">
        <input ref="f" type="file" accept="image/*" style="display:none" @change="upload" />
        <el-button type="primary" @click="$refs.f.click()" :loading="loading">上传收款码</el-button>
        <p style="color:#999;font-size:12px;margin-top:8px">JPG/PNG，建议正方形</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const previewKey = ref('/uploads/images/payment_qr.png?t=' + Date.now())

const upload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  loading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    await request.post('/admin/payment-qr', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    ElMessage.success('收款码已更新')
    previewKey.value = '/uploads/images/payment_qr.png?t=' + Date.now()
  } catch (e) { ElMessage.error('上传失败'); console.error(e) } finally { loading.value = false }
}
</script>

<style scoped>
.qr-box { width: 250px; height: 250px; border: 2px dashed #ddd; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 20px auto }
</style>
