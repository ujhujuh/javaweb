<template>
  <div class="list-page fade-in">
    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-card">
        <div class="filter-header">
          <span class="filter-icon">🔍</span>
          <span class="filter-title">筛选资讯</span>
        </div>
        <div class="filter-content">
          <div class="filter-item">
            <label class="filter-label">关键词</label>
            <el-input
              v-model="query.keyword"
              placeholder="搜索标题/摘要/标签"
              clearable
              class="filter-input"
              @keyup.enter="load(1)"
            >
              <template #prefix>
                <span>🔎</span>
              </template>
            </el-input>
          </div>
          <div class="filter-item">
            <label class="filter-label">分类</label>
            <el-select v-model="query.categoryId" placeholder="选择分类" clearable class="filter-select">
              <el-option
                v-for="item in categories"
                :key="item.id"
                :label="`${item.name} (${item.count})`"
                :value="item.id"
              />
            </el-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">排序</label>
            <el-select v-model="query.sort" class="filter-select">
              <el-option label="🆕 最新" value="latest" />
              <el-option label="🔥 最热" value="hot" />
              <el-option label="⭐ 置顶" value="top" />
            </el-select>
          </div>
          <el-button type="primary" @click="load(1)" class="search-btn">
            <span class="btn-icon">🔍</span>
            搜索
          </el-button>
        </div>
      </div>
    </div>

    <!-- Results -->
    <div class="results-section">
      <div class="results-header">
        <div class="results-info">
          <span class="results-count">共找到 <strong>{{ total }}</strong> 条资讯</span>
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

      <div class="card-grid" v-if="list.length">
        <NewsCardItem
          v-for="(item, index) in list"
          :key="item.id"
          :item="item"
          :style="{ animationDelay: `${index * 0.08}s` }"
        />
      </div>

      <el-empty v-else description="暂无数据，试试其他关键词吧" class="empty-state">
        <el-button class="clear-btn" @click="clearAll">
          <span class="btn-icon">🔄</span>
          清除筛选条件
        </el-button>
      </el-empty>
    </div>

    <!-- Pagination -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="query.current"
        :page-size="query.size"
        @current-change="load"
        :page-count="Math.ceil(total / query.size)"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getNewsList } from '@/api/news'
import NewsCardItem from '@/components/NewsCardItem.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const list = ref([])
const total = ref(0)

const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  sort: 'latest'
})

const hasActiveFilters = computed(() => {
  return query.keyword || query.categoryId
})

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : ''
}

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const syncRoute = () => {
  query.keyword = route.query.keyword || ''
  query.sort = route.query.sort || 'latest'
  query.categoryId = route.query.categoryId ? Number(route.query.categoryId) : undefined
  query.current = route.query.current ? Number(route.query.current) : 1
}

const pushRoute = () => {
  router.replace({
    path: '/list',
    query: {
      keyword: query.keyword || undefined,
      sort: query.sort || undefined,
      categoryId: query.categoryId || undefined,
      current: query.current
    }
  })
}

const load = async (current = query.current) => {
  query.current = current
  pushRoute()
  const res = await getNewsList({ ...query })
  list.value = res.data.records || []
  total.value = res.data.total || 0
}

const clearKeyword = () => {
  query.keyword = ''
  load(1)
}

const clearCategory = () => {
  query.categoryId = undefined
  load(1)
}

const clearAll = () => {
  query.keyword = ''
  query.categoryId = undefined
  query.sort = 'latest'
  load(1)
}

watch(() => route.fullPath, syncRoute)

onMounted(async () => {
  syncRoute()
  await loadCategories()
  await load(query.current)
})
</script>

<style scoped>
.list-page {
  padding-top: 12px;
}

.filter-bar {
  margin-bottom: 32px;
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
  padding: 20px 28px;
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.08) 0%, rgba(20, 184, 166, 0.05) 100%);
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
  gap: 20px;
  padding: 24px 28px;
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
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
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
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
}

.search-btn {
  height: 40px;
  padding: 0 28px;
  border-radius: 12px;
  font-weight: 600;
  background: var(--brand-gradient);
  border: none;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  box-shadow: 0 6px 16px rgba(13, 148, 136, 0.4);
  transform: translateY(-2px);
}

.btn-icon {
  margin-right: 6px;
}

.results-section {
  margin-bottom: 40px;
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
  background: rgba(13, 148, 136, 0.1);
  border: 1px solid rgba(13, 148, 136, 0.3);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: var(--brand);
  transition: all 0.2s ease;
}

.filter-tag:hover {
  background: rgba(13, 148, 136, 0.15);
  border-color: rgba(13, 148, 136, 0.4);
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
  background: rgba(13, 148, 136, 0.08);
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.card-grid > :deep(*) {
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

.empty-state {
  padding: 60px 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: var(--brand-gradient);
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
  border: none;
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid rgba(13, 148, 136, 0.1);
  background: rgba(255, 255, 255, 0.6);
}

:deep(.el-pagination.is-background .el-pager li:hover:not(.is-disabled):not(.is-active)) {
  color: var(--brand);
  background: rgba(13, 148, 136, 0.1);
  border-color: rgba(13, 148, 136, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(13, 148, 136, 0.15);
}

:deep(.el-pagination.is-background button) {
  border-radius: 8px;
  border: 1px solid rgba(13, 148, 136, 0.1);
  background: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

:deep(.el-pagination.is-background button:hover:not(:disabled)) {
  color: var(--brand);
  background: rgba(13, 148, 136, 0.1);
  border-color: rgba(13, 148, 136, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(13, 148, 136, 0.15);
}

@media (max-width: 768px) {
  .filter-bar {
    margin-bottom: 24px;
  }

  .filter-content {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-item {
    min-width: 100%;
  }

  .search-btn {
    width: 100%;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .results-section {
    margin-bottom: 32px;
  }
}

.clear-btn {
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 600;
  font-size: 14px;
  border: 1px solid rgba(99, 102, 241, 0.3);
  background: rgba(99, 102, 241, 0.05);
  color: #6366f1;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.1);
}

.clear-btn:hover {
  background: rgba(99, 102, 241, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.btn-icon {
  margin-right: 6px;
  font-size: 16px;
}
</style>
