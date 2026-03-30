<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('auth.registerTitle') }}</span>
        </div>
      </template>
      <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-width="80px">
        <el-form-item :label="$t('auth.username')" prop="username">
          <el-input v-model="registerForm.username" :placeholder="$t('auth.username')" />
        </el-form-item>
        <el-form-item :label="$t('user.nickname')" prop="nickname">
          <el-input v-model="registerForm.nickname" :placeholder="$t('user.nickname')" />
        </el-form-item>
        <el-form-item :label="$t('user.email')" prop="email">
          <el-input v-model="registerForm.email" :placeholder="$t('user.email')" />
        </el-form-item>
        <el-form-item :label="$t('user.phone')" prop="phone">
          <el-input v-model="registerForm.phone" :placeholder="$t('user.phone')" />
        </el-form-item>
        <el-form-item :label="$t('auth.password')" prop="password">
          <el-input v-model="registerForm.password" type="password" :placeholder="$t('auth.password')" />
        </el-form-item>
        <el-form-item :label="$t('auth.confirmPassword')" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" :placeholder="$t('auth.confirmPassword')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
            {{ $t('auth.register') }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="goToLogin" style="width: 100%">
            {{ $t('auth.hasAccount') }}{{ $t('auth.goToLogin') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error(t('validation.passwordMismatch')))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: t('validation.required'), trigger: 'blur' }],
  nickname: [{ required: true, message: t('validation.required'), trigger: 'blur' }],
  email: [
    { required: true, message: t('validation.required'), trigger: 'blur' },
    { type: 'email', message: t('validation.email'), trigger: 'blur' }
  ],
  phone: [{ required: true, message: t('validation.required'), trigger: 'blur' }],
  password: [
    { required: true, message: t('validation.required'), trigger: 'blur' },
    { min: 6, message: t('validation.minLength', ['6']), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: t('validation.required'), trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success(t('auth.registerSuccess'))
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || t('auth.registerFailed'))
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
</style>