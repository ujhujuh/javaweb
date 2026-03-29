<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <section v-if="banner.length" class="hero-section fade-in">
      <el-carousel height="380px" indicator-position="outside" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in banner" :key="item.id">
          <RouterLink :to="`/detail/${item.id}`" class="hero-card" :style="heroStyle(item.coverImage)">
            <div class="hero-overlay">
              <div class="hero-content">
                <div class="hero-badge">精选推荐</div>
                <h2 class="hero-title">{{ item.title }}</h2>
                <p class="hero-summary">{{ item.summary }}</p>
                <div class="hero-meta">
                  <span class="meta-item">
                    <span class="meta-icon">👁️</span>
                    {{ item.viewCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">👍</span>
                    {{ item.likeCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </RouterLink>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- Featured Section -->
    <section class="content-section fade-in">
      <div class="section-header">
        <div class="section-title">
          <span class="title-icon">⭐</span>
          <h3>置顶精选</h3>
        </div>
        <RouterLink to="/list?sort=top" class="more-link">
          查看更多
          <span class="arrow">→</span>
        </RouterLink>
      </div>
      <div class="card-grid" v-if="featured.length">
        <NewsCardItem v-for="(item, index) in featured" :key="item.id" :item="item" :style="{ animationDelay: `${index * 0.1}s` }" />
      </div>
      <el-empty v-else description="暂无精选内容" />
    </section>

    <!-- Latest Section -->
    <section class="content-section fade-in">
      <div class="section-header">
        <div class="section-title">
          <span class="title-icon">🔥</span>
          <h3>最新资讯</h3>
        </div>
        <RouterLink to="/list?sort=latest" class="more-link">
          查看更多
          <span class="arrow">→</span>
        </RouterLink>
      </div>
      <div class="card-grid" v-if="latest.length">
        <NewsCardItem v-for="(item, index) in latest" :key="item.id" :item="item" :style="{ animationDelay: `${index * 0.1}s` }" />
      </div>
      <el-empty v-else description="暂无最新内容" />
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHomeData } from '@/api/news'
import NewsCardItem from '@/components/NewsCardItem.vue'
import { toFileUrl } from '@/utils/file-url'

const banner = ref([])
const featured = ref([])
const latest = ref([])

const heroStyle = (cover) => ({
  backgroundImage: `linear-gradient(135deg, rgba(13, 148, 136, 0.9) 0%, rgba(20, 184, 166, 0.75) 50%, rgba(168, 85, 247, 0.7) 100%), url(${toFileUrl(cover) || 'https://picsum.photos/seed/hero/1200/600'})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center'
})

onMounted(async () => {
  const res = await getHomeData()
  banner.value = res.data.banner || []
  featured.value = res.data.featured || []
  latest.value = res.data.latest || []
})
</script>

<style scoped>
.home-page {
  padding-top: 12px;
}

.hero-section {
  margin-bottom: 40px;
}

:deep(.el-carousel) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

:deep(.el-carousel__indicators--outside) {
  margin-top: 20px;
}

:deep(.el-carousel__indicator) {
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 4px;
  height: 6px;
  width: 24px;
  transition: all 0.3s ease;
}

:deep(.el-carousel__indicator.is-active) {
  background-color: var(--brand);
  width: 32px;
  height: 6px;
}

.hero-card {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
  transition: transform 0.5s ease;
}

.hero-card:hover {
  transform: scale(1.02);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.85) 0%, rgba(20, 184, 166, 0.7) 50%, rgba(168, 85, 247, 0.65) 100%);
  display: flex;
  align-items: center;
  padding: 56px;
}

.hero-content {
  max-width: 700px;
  color: white;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  margin: 0 0 16px;
  font-size: 36px;
  font-weight: 700;
  line-height: 1.3;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.hero-summary {
  margin: 0 0 24px;
  font-size: 16px;
  line-height: 1.8;
  opacity: 0.95;
  max-width: 80%;
}

.hero-meta {
  display: flex;
  gap: 24px;
  font-size: 14px;
  font-weight: 500;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.meta-icon {
  font-size: 16px;
}

.content-section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 0 4px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.section-title h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.more-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--brand);
  font-weight: 500;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  background: rgba(13, 148, 136, 0.05);
  border: 1px solid rgba(13, 148, 136, 0.2);
}

.more-link:hover {
  background: rgba(13, 148, 136, 0.12);
  transform: translateX(4px);
}

.arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.more-link:hover .arrow {
  transform: translateX(4px);
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

@media (max-width: 768px) {
  .hero-section {
    margin-bottom: 28px;
  }

  :deep(.el-carousel) {
    height: 300px !important;
  }

  .hero-overlay {
    padding: 28px;
  }

  .hero-content {
    max-width: 100%;
  }

  .hero-title {
    font-size: 24px;
  }

  .hero-summary {
    font-size: 14px;
    max-width: 100%;
  }

  .hero-meta {
    flex-direction: column;
    gap: 12px;
  }

  .section-title h3 {
    font-size: 20px;
  }

  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .content-section {
    margin-bottom: 36px;
  }
}
</style>
