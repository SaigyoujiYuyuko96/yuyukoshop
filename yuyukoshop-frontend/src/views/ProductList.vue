<template>
  <div>
    <NavHeader />
    <div class="container product-list-page">
      <el-row :gutter="20">
        <!-- 侧边栏筛选 -->
        <el-col :span="5">
          <el-card class="filter-card">
            <h3>商品分类</h3>
            <el-menu :default-active="activeCategory" @select="handleCategorySelect">
              <el-menu-item index="0">全部商品</el-menu-item>
              <el-menu-item v-for="cat in categories" :key="cat.id" :index="String(cat.id)">
                {{ cat.name }}
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>

        <!-- 商品列表 -->
        <el-col :span="19">
          <div class="search-bar">
            <el-input v-model="searchKeyword" placeholder="搜索商品" clearable @keyup.enter="handleSearch">
              <template #append>
                <el-button :icon="Search" @click="handleSearch" />
              </template>
            </el-input>
            <el-select v-model="sortField" placeholder="综合排序" @change="handleSort">
              <el-option label="综合排序" value="" />
              <el-option label="价格从低到高" value="price" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="销量优先" value="sales" />
            </el-select>
          </div>

          <el-empty v-if="products.length === 0" description="暂无商品" />
          <el-row :gutter="20" v-else>
            <el-col :span="6" v-for="product in products" :key="product.id">
              <el-card :body-style="{ padding: '0px' }" class="product-card" shadow="hover" @click="goToDetail(product.id)">
                <img :src="product.thumbnail || 'https://via.placeholder.com/300x200?text=No+Image'" class="product-image" />
                <div class="product-info">
                  <h4>{{ product.name }}</h4>
                  <p class="desc">{{ product.description }}</p>
                  <div class="product-footer">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="stock">库存 {{ product.stock }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            @change="handlePageChange"
            class="pagination"
          />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import NavHeader from '@/components/NavHeader.vue'
import request from '@/api/request'
import { Search } from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const products = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const searchKeyword = ref('')
const sortField = ref('')
const activeCategory = ref('0')
const loading = ref(false)

const fetchCategories = async () => {
  try {
    const res = await request.get('/categories', { silent: true })
    categories.value = res.data || []
  } catch (e) { 
    console.error('获取分类失败', e)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value || undefined,
      categoryId: activeCategory.value !== '0' ? activeCategory.value : undefined,
      sortField: sortField.value === 'price_desc' ? 'price' : sortField.value || undefined,
      sortOrder: sortField.value === 'price_desc' ? 'desc' : sortField.value ? 'asc' : undefined
    }
    const res = await request.get('/products', { params })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { 
    ElMessage.error('获取商品列表失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchProducts()
}

const handleCategorySelect = (index) => {
  activeCategory.value = index
  page.value = 1
  fetchProducts()
}

const handleSort = () => {
  page.value = 1
  fetchProducts()
}

const handlePageChange = () => {
  fetchProducts()
}

const goToDetail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(() => {
  fetchCategories()
  if (route.query.categoryId) {
    activeCategory.value = String(route.query.categoryId)
  }
  fetchProducts()
})

watch(() => route.query.categoryId, (val) => {
  if (val) {
    activeCategory.value = String(val)
    page.value = 1
    fetchProducts()
  }
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.filter-card h3 {
  margin-bottom: 15px;
  color: #333;
}
.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}
.search-bar .el-input {
  flex: 1;
}
.product-card {
  cursor: pointer;
  margin-bottom: 20px;
}
.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.product-info {
  padding: 14px;
}
.product-info h4 {
  margin-bottom: 8px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.desc {
  color: #999;
  font-size: 13px;
  margin-bottom: 10px;
  height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.stock {
  color: #999;
  font-size: 12px;
}
.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
