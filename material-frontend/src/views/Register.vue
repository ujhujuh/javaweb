<template>
  <div class="page-wrap" style="max-width: 520px;">
    <div class="panel">
      <h3 style="margin-top: 0;">注册</h3>
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="form.confirmPassword" type="password" show-password /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交注册</el-button>
          <el-button @click="go('/login')">去登录</el-button>
        </el-form-item>
      </el-form>
    </div>
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

const go = (path) => router.push(path)

const submit = async () => {
  if (!form.username || !form.nickname || !form.email || !form.password || !form.confirmPassword) {
    ElMessage.warning('请完整填写注册信息')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入密码不一致')
    return
  }
  await register(form)
  ElMessage.success('注册成功，请登录')
  router.push('/login')
}
</script>
