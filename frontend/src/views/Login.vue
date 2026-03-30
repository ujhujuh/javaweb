<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.systemName') }}</span>
        </div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="80px">
        <el-form-item :label="$t('auth.username')" prop="username">
          <el-input v-model="loginForm.username" :placeholder="$t('auth.username')" />
        </el-form-item>
        <el-form-item :label="$t('auth.password')" prop="password">
          <el-input v-model="loginForm.password" type="password" :placeholder="$t('auth.password')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            {{ $t('auth.login') }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="goToRegister" style="width: 100%">
            {{ $t('auth.noAccount') }}{{ $t('auth.goToRegister') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: t('validation.required'), trigger: 'blur' }],
  password: [{ required: true, message: t('validation.required'), trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    await userStore.loginAction(loginForm)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
</style>