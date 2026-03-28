<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2>登录资讯站</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">登录</el-button>
      </el-form>
      <div class="links">
        <RouterLink to="/register">没有账号？去注册</RouterLink>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: ''
})

const submit = async () => {
  await userStore.loginAction(form)
  router.push('/')
}
</script>

<style scoped>
.auth-wrap {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-card {
  width: 420px;
}

.links {
  margin-top: 12px;
  text-align: right;
}
</style>
