<template>
  <div class="image-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :data="{ type: uploadType }"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :accept="acceptTypes"
      :drag="drag"
      :disabled="disabled"
      class="uploader"
    >
      <div v-if="!modelValue" class="upload-placeholder">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <div class="upload-text">{{ placeholder }}</div>
      </div>
      <div v-else class="upload-preview">
        <img :src="getImageUrl(modelValue)" class="preview-image" />
        <div class="preview-mask">
          <el-icon class="preview-action" @click.stop="handleRemove"><Delete /></el-icon>
        </div>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  uploadType: { type: String, default: 'product' },
  maxSize: { type: Number, default: 10 },
  drag: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '点击上传图片' }
})

const emit = defineEmits(['update:modelValue'])

const uploadUrl = computed(() => '/api/upload/image')

const headers = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: 'Bearer ' + token } : {}
})

const acceptTypes = '.jpg,.jpeg,.png,.gif,.webp'

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

const beforeUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isValidType) {
    ElMessage.error('仅支持 JPG/PNG/GIF/WEBP 格式')
    return false
  }
  const isLtSize = file.size / 1024 / 1024 < props.maxSize
  if (!isLtSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

const handleSuccess = (response) => {
  if (response.code === 200) {
    emit('update:modelValue', response.data.url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleError = () => {
  ElMessage.error('上传失败，请重试')
}

const handleRemove = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.image-upload {
  display: inline-block;
}

.uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.uploader :deep(.el-upload-dragger) {
  border: none;
  border-radius: 6px;
}

.upload-placeholder {
  width: 148px;
  height: 148px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
}

.upload-preview {
  width: 148px;
  height: 148px;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-preview:hover .preview-mask {
  opacity: 1;
}

.preview-action {
  font-size: 24px;
  color: #fff;
  cursor: pointer;
}
</style>
