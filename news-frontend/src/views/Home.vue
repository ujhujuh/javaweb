<template>
  <div class="page">
    <el-carousel height="300px" indicator-position="outside" v-if="banner.length">
      <el-carousel-item v-for="item in banner" :key="item.id">
        <RouterLink :to="`/detail/${item.id}`" class="hero" :style="heroStyle(item.coverImage)">
          <div class="overlay">
            <h2>{{ item.title }}</h2>
            <p>{{ item.summary }}</p>
          </div>
        </RouterLink>
      </el-carousel-item>
    </el-carousel>

    <section class="block">
      <div class="head">
        <h3>置顶精选</h3>
        <RouterLink to="/list?sort=top">查看更多</RouterLink>
      </div>
      <div class="card-grid">
        <NewsCardItem v-for="item in featured" :key="item.id" :item="item" />
      </div>
    </section>

    <section class="block">
      <div class="head">
        <h3>最新资讯</h3>
        <RouterLink to="/list?sort=latest">查看更多</RouterLink>
      </div>
      <div class="card-grid">
        <NewsCardItem v-for="item in latest" :key="item.id" :item="item" />
      </div>
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
  backgroundImage: `linear-gradient(120deg, rgba(15,118,110,.82), rgba(20,184,166,.62)), url(${toFileUrl(cover) || 'https://picsum.photos/seed/hero/1200/600'})`
})

onMounted(async () => {
  const res = await getHomeData()
  banner.value = res.data.banner || []
  featured.value = res.data.featured || []
  latest.value = res.data.latest || []
})
</script>

<style scoped>
.hero {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  background-size: cover;
  background-position: center;
}

.overlay {
  color: #fff;
  padding: 28px;
}

.overlay h2 {
  margin: 0 0 12px;
  font-size: 28px;
}

.overlay p {
  margin: 0;
  max-width: 70%;
}

.block {
  margin-top: 26px;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.head h3 {
  margin: 0;
}
</style>
