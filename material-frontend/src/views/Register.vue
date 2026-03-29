<template>
  <div class="register-page">
    <div class="register-container fade-in">
      <div class="register-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>

      <div class="register-card">
        <div class="register-header">
          <div class="logo-wrapper">
            <span class="logo-icon">📚</span>
          </div>
          <h1 class="register-title">创建账户</h1>
          <p class="register-subtitle">加入资料商城，开启学习之旅</p>
        </div>

        <el-form :model="form" class="register-form" @submit.prevent="submit">
          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">👤</span>
              用户名
            </label>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">🔑</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">😊</span>
              昵称
            </label>
            <el-input
              v-model="form.nickname"
              placeholder="请输入昵称"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">🏷️</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">📧</span>
              邮箱
            </label>
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">✉️</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">📱</span>
              手机号
            </label>
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">📞</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">🔒</span>
              密码
            </label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">🔐</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">✅</span>
              确认密码
            </label>
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              show-password
              class="form-input"
            >
              <template #prefix>
                <span class="input-icon">🔓</span>
              </template>
            </el-input>
          </div>

          <el-button
            type="primary"
            size="large"
            class="register-btn"
            @click="submit"
            :loading="loading"
          >
            <span class="btn-text">立即注册</span>
            <span class="btn-arrow">→</span>
          </el-button>
        </el-form>

        <div class="register-footer">
          <div class="divider">
            <span class="divider-text">或</span>
          </div>
          <RouterLink to="/login" class="login-link">
            <span class="link-icon">🔑</span>
            已有账号？立即登录
            <span class="link-arrow">→</span>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const submit = async () => {
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!form.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  if (!form.email.trim()) {
    ElMessage.warning('请输入邮箱')
    return
  }
  if (!form.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }
  if (!form.confirmPassword.trim()) {
    ElMessage.warning('请确认密码')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入密码不一致')
    return
  }

  try {
    loading.value = true
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.register-container {
  position: relative;
  width: 100%;
  max-width: 520px;
  z-index: 1;
}

.register-decoration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  height: 600px;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.2) 0%, rgba(59, 130, 246, 0.1) 100%);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(167, 139, 250, 0.1) 100%);
  bottom: 20%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(52, 211, 153, 0.1) 100%);
  top: 30%;
  right: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

.register-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 48px 40px;
  position: relative;
  overflow: hidden;
}

.register-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--brand-gradient);
}

.register-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--brand-gradient);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 36px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.register-title {
  margin: 0 0 12px;
  font-size: 32px;
  font-weight: 800;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
  margin: 0;
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.register-form {
  margin-bottom: 28px;
}

.form-item {
  margin-bottom: 18px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
  padding-left: 4px;
}

.label-icon {
  font-size: 16px;
}

.form-input {
  width: 100%;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  padding: 12px 16px;
}

.form-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.input-icon {
  font-size: 16px;
  opacity: 0.6;
}

.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  background: var(--brand-gradient);
  border: none;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.35);
  transition: all 0.3s ease;
  margin-top: 12px;
}

.register-btn:hover {
  box-shadow: 0 12px 28px rgba(37, 99, 235, 0.45);
  transform: translateY(-2px);
}

.register-btn:active {
  transform: translateY(0);
}

.btn-text {
  margin-right: 8px;
}

.btn-arrow {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.register-btn:hover .btn-arrow {
  transform: translateX(4px);
}

.register-footer {
  text-align: center;
}

.divider {
  position: relative;
  margin: 24px 0;
  text-align: center;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: rgba(226, 232, 240, 0.8);
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider-text {
  position: relative;
  display: inline-block;
  padding: 0 12px;
  background: var(--glass-bg);
  color: var(--muted);
  font-size: 13px;
  font-weight: 500;
}

.login-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--brand);
  padding: 10px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: rgba(37, 99, 235, 0.05);
  border: 1px solid rgba(37, 99, 235, 0.2);
}

.login-link:hover {
  background: rgba(37, 99, 235, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.link-icon {
  font-size: 16px;
}

.link-arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.login-link:hover .link-arrow {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .register-card {
    padding: 36px 28px;
  }

  .register-title {
    font-size: 28px;
  }

  .register-subtitle {
    font-size: 14px;
  }

  .form-item {
    margin-bottom: 16px;
  }
}

@media (max-width: 480px) {
  .register-page {
    padding: 24px 16px;
  }

  .register-card {
    padding: 28px 20px;
    border-radius: 20px;
  }

  .logo-wrapper {
    width: 64px;
    height: 64px;
    margin-bottom: 16px;
  }

  .logo-icon {
    font-size: 32px;
  }

  .register-title {
    font-size: 24px;
  }

  .register-subtitle {
    font-size: 13px;
  }

  .form-item {
    margin-bottom: 14px;
  }

  .form-label {
    font-size: 13px;
  }

  .register-btn {
    height: 48px;
    font-size: 15px;
  }
}
</style>
