<template>
  <el-dialog
    v-model="visible"
    title="个人中心"
    width="700px"
    :close-on-click-modal="false"
    class="profile-dialog"
  >
    <div class="profile-container">
      <!-- 左侧：个人信息卡片 -->
      <div class="profile-left">
        <div class="profile-card">
          <div class="avatar-section">
            <div class="avatar">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="avatar-info">
              <div class="username">{{ userInfo.nickname || userInfo.username }}</div>
              <div class="user-id">ID: {{ userInfo.id }}</div>
            </div>
          </div>

          <el-divider />

          <div class="info-list">
            <div class="info-item">
              <el-icon class="info-icon"><User /></el-icon>
              <div class="info-content">
                <div class="info-label">用户名</div>
                <div class="info-value">{{ userInfo.username }}</div>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><Message /></el-icon>
              <div class="info-content">
                <div class="info-label">邮箱</div>
                <div class="info-value">{{ userInfo.email || '未设置' }}</div>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><Phone /></el-icon>
              <div class="info-content">
                <div class="info-label">手机号</div>
                <div class="info-value">{{ userInfo.phone || '未设置' }}</div>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><Male v-if="userInfo.sex === '1'" /><Female v-else-if="userInfo.sex === '2'" /><QuestionFilled v-else /></el-icon>
              <div class="info-content">
                <div class="info-label">性别</div>
                <div class="info-value">
                  <span v-if="userInfo.sex === '1'">男</span>
                  <span v-else-if="userInfo.sex === '2'">女</span>
                  <span v-else>未知</span>
                </div>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><CircleCheck v-if="userInfo.status === '0'" /><CircleClose v-else /></el-icon>
              <div class="info-content">
                <div class="info-label">状态</div>
                <div class="info-value">
                  <el-tag :type="userInfo.status === '0' ? 'success' : 'danger'" size="small">
                    {{ userInfo.status === '0' ? '正常' : '停用' }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><Clock /></el-icon>
              <div class="info-content">
                <div class="info-label">创建时间</div>
                <div class="info-value">{{ userInfo.createTime }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：密码修改卡片 -->
      <div class="profile-right">
        <div class="password-card">
          <div class="card-header">
            <el-icon class="header-icon"><Lock /></el-icon>
            <span class="header-title">修改密码</span>
          </div>

          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="80px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码（至少6位）"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="password-tips">
              <el-icon><InfoFilled /></el-icon>
              <span>密码修改成功后需要重新登录</span>
            </div>

            <div class="form-actions">
              <el-button size="large" @click="handleCancel">取消</el-button>
              <el-button type="primary" size="large" @click="handleUpdatePassword">
                <el-icon><Check /></el-icon>
                确认修改
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Message,
  Phone,
  Male,
  Female,
  QuestionFilled,
  CircleCheck,
  CircleClose,
  Clock,
  InfoFilled,
  Check
} from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'updatePassword'])

const visible = ref(props.modelValue)
const passwordFormRef = ref(null)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (val) {
    // 重置表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  }
})

const handleCancel = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordFormRef.value?.clearValidate()
}

const handleClose = () => {
  visible.value = false
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        emit('updatePassword', passwordForm.value)
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        passwordFormRef.value?.clearValidate()
      } catch (error) {
        ElMessage.error(error.message || '修改失败')
      }
    }
  })
}
</script>

<style scoped>
.profile-dialog :deep(.el-dialog__body) {
  padding: 16px;
}

.profile-container {
  display: flex;
  gap: 16px;
  min-height: 420px;
}

.profile-left {
  flex: 1;
}

.profile-right {
  flex: 1;
}

/* 个人信息卡片 */
.profile-card {
  background: var(--theme-card-bg);
  border: 1px solid var(--theme-card-border);
  border-radius: 12px;
  padding: 18px;
  height: 100%;
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.1);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px 0 rgba(102, 126, 234, 0.4);
}

.avatar-info {
  flex: 1;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: var(--theme-text-color);
  margin-bottom: 2px;
}

.user-id {
  font-size: 13px;
  color: var(--theme-text-color-secondary);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: var(--theme-bg-color-page);
}

.info-icon {
  width: 18px;
  height: 18px;
  color: var(--theme-primary);
  margin-top: 1px;
  flex-shrink: 0;
}

.info-content {
  flex: 1;
  min-width: 0;
}

.info-label {
  font-size: 11px;
  color: var(--theme-text-color-secondary);
  margin-bottom: 2px;
}

.info-value {
  font-size: 13px;
  color: var(--theme-text-color);
  font-weight: 500;
  word-break: break-all;
}

/* 密码修改卡片 */
.password-card {
  background: var(--theme-card-bg);
  border: 1px solid var(--theme-card-border);
  border-radius: 12px;
  padding: 18px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--theme-border-color-light);
}

.header-icon {
  font-size: 22px;
  color: var(--theme-primary);
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-color);
}

.password-form {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.password-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.password-form :deep(.el-input__wrapper) {
  padding: 8px 12px;
}

.password-form :deep(.el-input__prefix) {
  color: var(--theme-text-color-secondary);
}

.password-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px;
  background: var(--theme-primary-light);
  border-radius: 6px;
  color: var(--theme-primary);
  font-size: 12px;
  margin-top: 6px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.form-actions .el-button {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

/* 暗色主题优化 */
.theme-dark .profile-card,
.theme-dark .password-card {
  background: rgba(30, 30, 46, 0.6);
  border: 1px solid rgba(51, 65, 85, 0.4);
  backdrop-filter: blur(10px);
}

.theme-dark .avatar {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  box-shadow: 0 4px 12px 0 rgba(99, 102, 241, 0.4);
}

.theme-dark .info-item:hover {
  background: rgba(99, 102, 241, 0.1);
}

.theme-dark .password-tips {
  background: rgba(99, 102, 241, 0.1);
}

/* 清新主题优化 */
.theme-fresh .avatar {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 4px 12px 0 rgba(82, 196, 26, 0.4);
}

.theme-fresh .info-icon {
  color: #52c41a;
}
</style>