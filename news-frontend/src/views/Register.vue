<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2>注册账号</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">注册</el-button>
      </el-form>
      <div class="links">
        <RouterLink to="/login">已有账号？去登录</RouterLink>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const submit = async () => {
  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  await register({
    username: form.username,
    nickname: form.nickname,
    email: form.email,
    phone: form.phone,
    password: form.password
  })
  ElMessage.success('注册成功，请登录')
  router.push('/login')
}
</script>

<style scoped>
.auth-wrap {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
}

.auth-card {
  width: 460px;
}

.links {
  margin-top: 12px;
  text-align: right;
}
</style>
