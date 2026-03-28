<template>
  <div class="page-wrap" style="max-width: 460px;">
    <div class="panel">
      <h3 style="margin-top: 0;">登录</h3>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">登录</el-button>
          <el-button @click="go('/register')">注册</el-button>
          <el-button @click="go('/')">首页</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: '', password: '' })
const go = (path) => router.push(path)

const submit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  await userStore.loginAction(form)
  ElMessage.success('登录成功')
  router.push('/')
}
</script>
