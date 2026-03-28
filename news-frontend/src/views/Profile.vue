<template>
  <div class="page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>个人中心</span>
          <el-button type="danger" plain size="small" @click="clearHistoryData">清空浏览历史</el-button>
        </div>
      </template>

      <el-tabs v-model="tab">
        <el-tab-pane label="我的收藏" name="favorites">
          <div class="card-grid">
            <NewsCardItem v-for="item in favorites" :key="item.id" :item="item" />
          </div>
          <el-empty v-if="!favorites.length" description="暂无收藏" />
        </el-tab-pane>

        <el-tab-pane label="浏览历史" name="history">
          <div class="card-grid">
            <NewsCardItem v-for="item in histories" :key="item.id" :item="item" />
          </div>
          <el-empty v-if="!histories.length" description="暂无浏览历史" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { clearMyHistory, getMyFavorites, getMyHistory } from '@/api/news'
import NewsCardItem from '@/components/NewsCardItem.vue'

const tab = ref('favorites')
const favorites = ref([])
const histories = ref([])

const load = async () => {
  const [favRes, hisRes] = await Promise.all([
    getMyFavorites({ current: 1, size: 30 }),
    getMyHistory({ current: 1, size: 30 })
  ])
  favorites.value = favRes.data.records || []
  histories.value = hisRes.data.records || []
}

const clearHistoryData = async () => {
  await ElMessageBox.confirm('确认清空浏览历史吗？', '提示', { type: 'warning' })
  await clearMyHistory()
  ElMessage.success('已清空')
  await load()
}

onMounted(load)
</script>
