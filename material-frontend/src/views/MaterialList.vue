<template>
  <div class="list-page fade-in">
    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-card">
        <div class="filter-header">
          <span class="filter-icon">🔍</span>
          <span class="filter-title">筛选资料</span>
        </div>
        <div class="filter-content">
          <div class="filter-item">
            <label class="filter-label">关键词</label>
            <el-input
              v-model="query.keyword"
              placeholder="搜索标题/简介/标签"
              clearable
              class="filter-input"
              @keyup.enter="search"
            >
              <template #prefix>
                <span>🔎</span>
              </template>
            </el-input>
          </div>
          <div class="filter-item">
            <label class="filter-label">分类</label>
            <el-select v-model="query.categoryId" placeholder="选择分类" clearable class="filter-select" @change="search">
              <el-option
                v-for="c in categories"
                :key="c.id"
                :label="c.name"
                :value="c.id"
              />
            </el-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">排序</label>
            <el-select v-model="query.sort" placeholder="排序方式" class="filter-select" @change="search">
              <el-option label="🆕 最新上架" value="latest" />
              <el-option label="🔥 销量优先" value="sales" />
              <el-option label="💰 价格从低到高" value="priceAsc" />
              <el-option label="💎 价格从高到低" value="priceDesc" />
            </el-select>
          </div>
          <el-button type="primary" @click="search" class="search-btn">
            <span class="btn-icon">🔍</span>
            查询
          </el-button>
        </div>
      </div>
    </div>

    <!-- Results -->
    <div class="results-section">
      <div class="results-header">
        <div class="results-info">
          <span class="results-count">共找到 <strong>{{ total }}</strong> 份资料</span>
        </div>
        <div class="active-filters" v-if="hasActiveFilters">
          <span class="filter-tag" v-if="query.keyword">
            关键词: {{ query.keyword }}
            <span class="tag-close" @click="clearKeyword">×</span>
          </span>
          <span class="filter-tag" v-if="query.categoryId">
            分类: {{ getCategoryName(query.categoryId) }}
            <span class="tag-close" @click="clearCategory">×</span>
          </span>
          <span class="clear-all" @click="clearAll">清除全部</span>
        </div>
      </div>

      <div class="goods-grid" v-if="records.length">
        <div
          v-for="(item, index) in records"
          :key="item.id"
          class="goods-card"
          @click="go('/detail/' + item.id)"
          :style="{ animationDelay: `${index * 0.08}s` }"
        >
          <img class="goods-image" :src="item.coverImage || defaultCover" alt="cover" />
          <div class="goods-body">
            <div class="goods-title">{{ item.title }}</div>
            <div class="goods-summary">{{ item.summary || '暂无简介' }}</div>
            <div class="goods-meta">
              <span class="price">¥{{ item.price }}</span>
              <span class="muted">销量 {{ item.salesCount }}</span>
            </div>
            <div class="goods-footer">
              <span class="muted">{{ item.category }} · {{ item.publishTime }}</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无资料，试试其他筛选条件吧" class="empty-state">
        <el-button type="primary" @click="clearAll">清除筛选条件</el-button>
      </el-empty>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > 0">
        <div class="pagination-info">
          <span class="muted">第 {{ query.current }} / {{ Math.ceil(total / query.size) }} 页</span>
        </div>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :current-page="query.current"
          :page-size="query.size"
          @current-change="onPageChange"
          :page-count="Math.ceil(total / query.size)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getMaterialList } from '@/api/material'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const categories = ref([])
const records = ref([])
const total = ref(0)
const defaultCover = 'https://picsum.photos/seed/material-cover/600/400'
const query = ref({ current: 1, size: 10, keyword: '', categoryId: undefined, sort: 'latest' })

const hasActiveFilters = computed(() => {
  return query.value.keyword || query.value.categoryId
})

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : ''
}

const go = (path) => router.push(path)

const fetchList = async () => {
  const res = await getMaterialList(query.value)
  records.value = res.data.records || []
  total.value = res.data.total || 0
}

const search = async () => {
  query.value.current = 1
  await fetchList()
}

const clearKeyword = () => {
  query.value.keyword = ''
  search()
}

const clearCategory = () => {
  query.value.categoryId = undefined
  search()
}

const clearAll = () => {
  query.value.keyword = ''
  query.value.categoryId = undefined
  query.value.sort = 'latest'
  search()
}

const onPageChange = async (page) => {
  query.value.current = page
  await fetchList()
}

onMounted(async () => {
  const cid = route.query.categoryId
  if (cid) query.value.categoryId = Number(cid)
  const categoryRes = await getCategories()
  categories.value = categoryRes.data || []
  await fetchList()
})
</script>

<style scoped>
.list-page {
  padding-top: 8px;
}

.filter-bar {
  margin-bottom: 24px;
}

.filter-card {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  box-shadow: var(--card-shadow);
  overflow: hidden;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 24px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08) 0%, rgba(59, 130, 246, 0.05) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.filter-icon {
  font-size: 24px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.filter-title {
  font-size: 18px;
  font-weight: 700;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.filter-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 20px 24px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}

.filter-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  padding-left: 4px;
}

.filter-input {
  flex: 1;
}

.filter-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.filter-input :deep(.el-input__wrapper:hover),
.filter-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.filter-select {
  flex: 1;
}

.filter-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.filter-select :deep(.el-select__wrapper:hover),
.filter-select :deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.search-btn {
  height: 40px;
  padding: 0 28px;
  border-radius: 12px;
  font-weight: 600;
  background: var(--brand-gradient);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
  transform: translateY(-2px);
}

.btn-icon {
  margin-right: 6px;
}

.results-section {
  margin-bottom: 32px;
}

.results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.results-info {
  padding: 0 4px;
}

.results-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.results-count strong {
  color: var(--brand);
  font-weight: 700;
  font-size: 16px;
}

.active-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.3);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: var(--brand);
  transition: all 0.2s ease;
}

.filter-tag:hover {
  background: rgba(37, 99, 235, 0.15);
  border-color: rgba(37, 99, 235, 0.4);
}

.tag-close {
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  transition: transform 0.2s ease;
}

.tag-close:hover {
  transform: scale(1.2);
}

.clear-all {
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.clear-all:hover {
  color: var(--brand);
  background: rgba(37, 99, 235, 0.08);
}

.goods-grid > :deep(*) {
  animation: fadeInUp 0.5s ease-out backwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.goods-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(226, 232, 240, 0.6);
}

.empty-state {
  padding: 60px 20px;
}

.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
  margin-top: 24px;
}

.pagination-info {
  font-size: 14px;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: var(--brand-gradient);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-pagination.is-background .el-pager li:hover) {
  color: var(--brand);
}

@media (max-width: 768px) {
  .list-page {
    padding-top: 4px;
  }

  .filter-bar {
    margin-bottom: 20px;
  }

  .filter-header {
    padding: 14px 16px;
  }

  .filter-icon {
    font-size: 20px;
  }

  .filter-title {
    font-size: 16px;
  }

  .filter-content {
    flex-direction: column;
    align-items: stretch;
    padding: 16px;
  }

  .filter-item {
    min-width: 100%;
  }

  .filter-label {
    font-size: 12px;
  }

  .search-btn {
    width: 100%;
    height: 44px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 16px;
  }

  .results-count {
    font-size: 13px;
  }

  .active-filters {
    width: 100%;
  }

  .filter-tag {
    padding: 5px 10px;
    font-size: 12px;
  }

  .clear-all {
    padding: 5px 10px;
    font-size: 12px;
  }

  .pagination-wrapper {
    flex-direction: column;
    gap: 16px;
    padding: 16px 0;
  }

  .pagination-info {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .filter-header {
    padding: 12px 14px;
  }

  .filter-content {
    padding: 14px;
    gap: 12px;
  }

  .filter-item {
    gap: 6px;
  }

  .filter-label {
    font-size: 11px;
  }

  .search-btn {
    height: 40px;
    font-size: 14px;
  }

  .btn-icon {
    font-size: 14px;
    margin-right: 4px;
  }

  .results-info {
    padding: 0 2px;
  }

  .results-count {
    font-size: 12px;
  }

  .results-count strong {
    font-size: 14px;
  }

  .filter-tag {
    padding: 4px 8px;
    font-size: 11px;
  }

  .tag-close {
    font-size: 14px;
  }

  .clear-all {
    padding: 4px 8px;
    font-size: 11px;
  }

  .empty-state {
    padding: 40px 16px;
  }

  .pagination-wrapper {
    padding: 12px 0;
    margin-top: 20px;
  }

  .pagination-info {
    font-size: 12px;
  }
}
</style>
