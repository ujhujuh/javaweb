<template>
  <RouterLink :to="`/detail/${item.id}`" class="news-card">
    <img class="cover" :src="coverUrl" :alt="item.title" />
    <div class="content">
      <h3 class="title">{{ item.title }}</h3>
      <p class="desc">{{ item.summary || '暂无摘要' }}</p>
      <div class="meta">
        <span>{{ item.category || '未分类' }}</span>
        <span style="margin-left: 8px">阅读 {{ item.viewCount || 0 }}</span>
      </div>
      <div v-if="item.locked" class="lock">登录后查看完整详情</div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue'
import { toFileUrl } from '@/utils/file-url'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const fallback = 'https://picsum.photos/seed/news-fallback/600/300'
const coverUrl = computed(() => toFileUrl(props.item.coverImage) || fallback)
</script>

<style scoped>
.title {
  margin: 0 0 8px;
  font-size: 16px;
  line-height: 1.4;
}

.desc {
  margin: 0 0 10px;
  color: var(--muted);
  line-height: 1.5;
  min-height: 44px;
}

.lock {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: var(--brand-dark);
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.1) 0%, rgba(20, 184, 166, 0.08) 100%);
  border: 1px solid rgba(13, 148, 136, 0.2);
  border-radius: 8px;
  padding: 6px 10px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.lock:hover {
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.15) 0%, rgba(20, 184, 166, 0.12) 100%);
  border-color: rgba(13, 148, 136, 0.3);
}
</style>
