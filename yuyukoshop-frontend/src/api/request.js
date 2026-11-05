import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

let loadingInstance = null
let requestCount = 0

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 显示全局加载状态
const showLoading = () => {
  if (requestCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
  requestCount++
}

// 隐藏全局加载状态
const hideLoading = () => {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    loadingInstance?.close()
  }
}

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 对于非静默请求显示加载状态
    if (!config.silent) {
      showLoading()
    }
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    hideLoading()
    const res = response.data
    if (res.code !== 200) {
      // 只在非静默请求时显示错误
      if (!response.config.silent) {
        ElMessage.error(res.msg || '请求失败')
      }
      if (res.code === 401 || res.code === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('role')
        // 只在非登录页面跳转
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/login'
        }
      }
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  (error) => {
    hideLoading()
    // 检查是否是静默请求
    if (error.config?.silent) {
      return Promise.reject(error)
    }
    // 根据状态码显示不同的错误消息
    const status = error.response?.status
    let message = error.response?.data?.msg || '网络错误，请稍后重试'
    if (status === 401) {
      message = '请先登录'
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
    } else if (status === 403) {
      message = '没有权限访问'
    } else if (status === 404) {
      message = '请求的资源不存在'
    } else if (status >= 500) {
      message = '服务器错误，请稍后重试'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
