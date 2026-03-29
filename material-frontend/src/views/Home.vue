<template>
  <div class="home-page fade-in">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-icon">🌟</span>
          <span>精品资料</span>
        </div>
        <h1 class="hero-title">资料线上售卖平台</h1>
        <p class="hero-description">精品资料聚合，支持筛选、下单、支付和自动发货</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="go('/list')" class="hero-btn primary">
            <span class="btn-icon">🛒</span>
            逛商城
          </el-button>
          <el-button v-if="!userStore.isLogin()" size="large" @click="go('/login')" class="hero-btn">
            <span class="btn-icon">🔐</span>
            登录
          </el-button>
          <el-button v-else size="large" @click="go('/profile')" class="hero-btn">
            <span class="btn-icon">👤</span>
            个人中心
          </el-button>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="decoration-item item-1">📚</div>
        <div class="decoration-item item-2">📖</div>
        <div class="decoration-item item-3">🎓</div>
        <div class="decoration-item item-4">💼</div>
      </div>
    </div>

    <!-- Categories Section -->
    <div class="section-panel">
      <div class="section-header">
        <div class="section-title">
          <span class="title-icon">📂</span>
          <h3>分类导航</h3>
        </div>
        <el-button link type="primary" @click="loadData" class="refresh-btn">
          <span class="btn-icon">🔄</span>
          刷新
        </el-button>
      </div>
      <div class="categories-grid">
        <div
          v-for="c in categories"
          :key="c.id"
          class="category-card"
          @click="go('/list?categoryId=' + c.id)"
        >
          <span class="category-icon">📁</span>
          <span class="category-name">{{ c.name }}</span>
          <span class="category-count">{{ c.count }}</span>
        </div>
      </div>
    </div>

    <!-- Latest Materials Section -->
    <div class="section-panel">
      <div class="section-header">
        <div class="section-title">
          <span class="title-icon">🔥</span>
          <h3>最新上架</h3>
        </div>
        <RouterLink to="/list" class="more-link">
          查看全部
          <span class="arrow">→</span>
        </RouterLink>
      </div>
      <div class="goods-grid" v-if="latest.length">
        <div
          v-for="(item, index) in latest"
          :key="item.id"
          class="goods-card"
          @click="go('/detail/' + item.id)"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <img class="goods-image" :src="item.coverImage || defaultCover" alt="cover" />
          <div class="goods-body">
            <div class="goods-title">{{ item.title }}</div>
            <div class="goods-summary">{{ item.summary || '暂无简介' }}</div>
            <div class="goods-meta">
              <span class="price">¥{{ item.price }}</span>
              <span class="muted">销量 {{ item.salesCount }}</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无商品" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getMaterialList } from '@/api/material'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const categories = ref([])
const latest = ref([])
const defaultCover = 'https://picsum.photos/seed/material-cover/600/400'

const go = (path) => router.push(path)

const loadData = async () => {
  const [categoryRes, listRes] = await Promise.all([
    getCategories(),
    getMaterialList({ current: 1, size: 10, sort: 'latest' })
  ])
  categories.value = categoryRes.data || []
  latest.value = listRes.data?.records || []
}

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
  await loadData()
})
</script>

<style scoped>
.home-page {
  padding-top: 8px;
}

.hero-banner {
  border-radius: 24px;
  padding: 48px 40px;
  color: #fff;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 40%, #10b981 100%);
  box-shadow: 0 20px 50px rgba(37, 99, 235, 0.35);
  position: relative;
  overflow: hidden;
  margin-bottom: 24px;
}

.hero-banner::before {
  content: '';
  position: absolute;
  top: -30%;
  right: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 600px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.badge-icon {
  font-size: 18px;
}

.hero-title {
  margin: 0 0 16px;
  font-size: 40px;
  font-weight: 800;
  line-height: 1.2;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.6s ease-out 0.1s backwards;
}

.hero-description {
  margin: 0 0 28px;
  font-size: 18px;
  line-height: 1.8;
  opacity: 0.95;
  animation: slideUp 0.6s ease-out 0.2s backwards;
}

.hero-actions {
  display: flex;
  gap: 16px;
  animation: slideUp 0.6s ease-out 0.3s backwards;
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

.hero-btn {
  height: 48px;
  padding: 0 32px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  border: 2px solid rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: white;
  transition: all 0.3s ease;
}

.hero-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.hero-btn.primary {
  background: white;
  color: var(--brand);
  border-color: white;
}

.hero-btn.primary:hover {
  background: #f8fafc;
  box-shadow: 0 8px 24px rgba(255, 255, 255, 0.4);
}

.btn-icon {
  margin-right: 8px;
  font-size: 18px;
}

.hero-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  pointer-events: none;
}

.decoration-item {
  position: absolute;
  font-size: 48px;
  opacity: 0.3;
  animation: float 6s ease-in-out infinite;
}

.item-1 {
  top: 10%;
  right: 15%;
  animation-delay: 0s;
}

.item-2 {
  top: 30%;
  right: 8%;
  animation-delay: 1.5s;
}

.item-3 {
  bottom: 30%;
  right: 12%;
  animation-delay: 3s;
}

.item-4 {
  bottom: 10%;
  right: 20%;
  animation-delay: 4.5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(5deg);
  }
}

.section-panel {
  margin-bottom: 24px;
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

.refresh-btn {
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  transform: rotate(180deg);
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
  background: rgba(37, 99, 235, 0.05);
  border: 1px solid rgba(37, 99, 235, 0.2);
}

.more-link:hover {
  background: rgba(37, 99, 235, 0.12);
  transform: translateX(4px);
}

.arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.more-link:hover .arrow {
  transform: translateX(4px);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 14px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px 16px;
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--brand-gradient);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.2);
  border-color: rgba(37, 99, 235, 0.3);
}

.category-card:hover::before {
  opacity: 1;
}

.category-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.category-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.category-count {
  font-size: 12px;
  color: var(--muted);
  background: rgba(37, 99, 235, 0.1);
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
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

@media (max-width: 768px) {
  .hero-banner {
    padding: 32px 24px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-description {
    font-size: 15px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .hero-btn {
    width: 100%;
  }

  .hero-decoration {
    display: none;
  }

  .section-title h3 {
    font-size: 20px;
  }

  .categories-grid {
    grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
    gap: 10px;
  }

  .category-card {
    padding: 16px 12px;
  }

  .category-icon {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .home-page {
    padding-top: 4px;
  }

  .hero-banner {
    padding: 24px 20px;
    border-radius: 16px;
    margin-bottom: 20px;
  }

  .hero-badge {
    padding: 6px 14px;
    font-size: 12px;
    margin-bottom: 16px;
  }

  .hero-title {
    font-size: 24px;
    margin-bottom: 12px;
  }

  .hero-description {
    font-size: 14px;
    margin-bottom: 20px;
  }

  .hero-btn {
    height: 44px;
    font-size: 15px;
    padding: 0 24px;
  }

  .btn-icon {
    font-size: 16px;
  }

  .section-panel {
    margin-bottom: 20px;
  }

  .section-header {
    margin-bottom: 16px;
    padding: 0 2px;
  }

  .section-title h3 {
    font-size: 18px;
  }

  .title-icon {
    font-size: 24px;
  }

  .refresh-btn {
    font-size: 13px;
  }

  .more-link {
    padding: 6px 12px;
    font-size: 13px;
  }

  .categories-grid {
    grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
    gap: 8px;
  }

  .category-card {
    padding: 14px 10px;
    border-radius: 12px;
  }

  .category-icon {
    font-size: 20px;
  }

  .category-name {
    font-size: 13px;
  }

  .category-count {
    font-size: 11px;
    padding: 3px 8px;
  }
}
</style>
