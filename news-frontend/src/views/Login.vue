<template>
  <div class="login-page">
    <div class="login-container fade-in">
      <div class="login-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>

      <div class="login-card">
        <div class="login-header">
          <div class="logo-wrapper">
            <span class="logo-icon">📰</span>
          </div>
          <h1 class="login-title">欢迎回来</h1>
          <p class="login-subtitle">登录资讯站，发现更多精彩内容</p>
        </div>

        <el-form :model="form" class="login-form" @submit.prevent="submit">
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

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            @click="submit"
            :loading="loading"
          >
            <span class="btn-text">立即登录</span>
            <span class="btn-arrow">→</span>
          </el-button>
        </el-form>

        <div class="login-footer">
          <div class="divider">
            <span class="divider-text">或</span>
          </div>
          <RouterLink to="/register" class="register-link">
            <span class="link-icon">✨</span>
            没有账号？立即注册
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
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const submit = async () => {
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!form.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }

  try {
    loading.value = true
    await userStore.loginAction(form)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
}

.login-container {
  position: relative;
  width: 100%;
  max-width: 480px;
  z-index: 1;
}

.login-decoration {
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
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.2) 0%, rgba(20, 184, 166, 0.1) 100%);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.2) 0%, rgba(139, 92, 246, 0.1) 100%);
  bottom: 20%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(99, 102, 241, 0.1) 100%);
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

.login-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 56px 48px;
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--brand-gradient);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--brand-gradient);
  box-shadow: 0 8px 24px rgba(13, 148, 136, 0.4);
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 36px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.login-title {
  margin: 0 0 12px;
  font-size: 32px;
  font-weight: 800;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  margin: 0;
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.login-form {
  margin-bottom: 32px;
}

.form-item {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 10px;
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
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.15);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.25);
}

.input-icon {
  font-size: 16px;
  opacity: 0.6;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  background: var(--brand-gradient);
  border: none;
  box-shadow: 0 8px 20px rgba(13, 148, 136, 0.35);
  transition: all 0.3s ease;
  margin-top: 8px;
}

.login-btn:hover {
  box-shadow: 0 12px 28px rgba(13, 148, 136, 0.45);
  transform: translateY(-2px);
}

.login-btn:active {
  transform: translateY(0);
}

.btn-text {
  margin-right: 8px;
}

.btn-arrow {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-arrow {
  transform: translateX(4px);
}

.login-footer {
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

.register-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--brand);
  padding: 10px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: rgba(13, 148, 136, 0.05);
  border: 1px solid rgba(13, 148, 136, 0.2);
}

.register-link:hover {
  background: rgba(13, 148, 136, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
}

.link-icon {
  font-size: 16px;
}

.link-arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.register-link:hover .link-arrow {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .login-page {
    padding: 40px 24px;
  }

  .login-card {
    padding: 40px 32px;
  }

  .login-title {
    font-size: 28px;
  }

  .login-subtitle {
    font-size: 14px;
  }
}
</style>
