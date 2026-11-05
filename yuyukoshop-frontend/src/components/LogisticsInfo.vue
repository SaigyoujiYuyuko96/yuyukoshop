<template>
  <div class="logistics-info">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>物流信息</span>
          <el-button 
            type="primary" 
            link 
            :icon="RefreshRight" 
            @click="refreshLogistics"
            :loading="loading"
          >
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 物流基本信息 -->
      <div v-if="logistics" class="logistics-header">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="快递公司">
            {{ logistics.companyName || logistics.companyCode }}
          </el-descriptions-item>
          <el-descriptions-item label="快递单号">
            {{ logistics.trackingNo }}
            <el-button 
              type="primary" 
              link 
              size="small"
              @click="copyTrackingNo"
            >
              复制
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="物流状态">
            <el-tag :type="getStateType(logistics.state)">
              {{ logistics.stateName }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <!-- 物流轨迹 -->
      <div v-if="logistics && logistics.traces && logistics.traces.length > 0" class="logistics-timeline">
        <el-divider content-position="left">物流轨迹</el-divider>
        <el-timeline>
          <el-timeline-item
            v-for="(trace, index) in logistics.traces"
            :key="index"
            :timestamp="trace.time"
            :type="index === 0 ? 'primary' : 'info'"
            :hollow="index !== 0"
            placement="top"
          >
            <div class="trace-content">
              {{ trace.context }}
            </div>
            <div class="trace-time" v-if="trace.ftime">
              {{ trace.ftime }}
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <!-- 无物流信息 -->
      <el-empty 
        v-if="!loading && (!logistics || !logistics.traces || logistics.traces.length === 0)" 
        description="暂无物流信息"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'
import request from '@/api/request'

const props = defineProps({
  orderId: {
    type: [Number, String],
    required: true
  }
})

const loading = ref(false)
const logistics = ref(null)

// 监听订单ID变化
watch(() => props.orderId, () => {
  fetchLogistics()
}, { immediate: true })

onMounted(() => {
  fetchLogistics()
})

const fetchLogistics = async () => {
  loading.value = true
  try {
    const res = await request.get(`/logistics/order/${props.orderId}`)
    if (res.code === 200) {
      logistics.value = res.data
    } else {
      ElMessage.warning(res.message || '获取物流信息失败')
    }
  } catch (e) {
    console.error('获取物流信息失败', e)
    ElMessage.error('获取物流信息失败')
  } finally {
    loading.value = false
  }
}

const refreshLogistics = () => {
  fetchLogistics()
}

const copyTrackingNo = () => {
  if (logistics.value && logistics.value.trackingNo) {
    navigator.clipboard.writeText(logistics.value.trackingNo)
    ElMessage.success('已复制到剪贴板')
  }
}

const getStateType = (state) => {
  const stateMap = {
    '0': 'primary',  // 在途
    '1': 'warning',  // 揽收
    '3': 'success',  // 签收
    '5': 'primary',  // 派件
  }
  return stateMap[state] || 'info'
}
</script>

<style scoped>
.logistics-info {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logistics-header {
  margin-bottom: 20px;
}

.logistics-timeline {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.trace-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.trace-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #666;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
}
</style>
