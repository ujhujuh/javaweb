<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.userManagement') }}</span>
          <div>
            <el-button type="success" @click="handleExport">{{ $t('common.export') }}</el-button>
            <el-button type="primary" @click="handleAdd">{{ $t('common.add') }}</el-button>
          </div>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('user.username')" :label-width="isEnglish ? '90px' : 'auto'">
          <el-input v-model="queryForm.username" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.nickname')" :label-width="isEnglish ? '90px' : 'auto'">
          <el-input v-model="queryForm.nickname" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.phone')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-input v-model="queryForm.phone" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.status')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-select v-model="queryForm.status" :placeholder="$t('common.query')" clearable style="width: 150px">
            <el-option :label="$t('user.statusNormal')" value="0" />
            <el-option :label="$t('user.statusDisabled')" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" :label="$t('user.username')" />
        <el-table-column prop="nickname" :label="$t('user.nickname')" />
        <el-table-column prop="email" :label="$t('user.email')" />
        <el-table-column prop="phone" :label="$t('user.phone')" />
        <el-table-column prop="status" :label="$t('user.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? $t('user.statusNormal') : $t('user.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('user.createTime')" />
        <el-table-column :label="$t('common.operation')" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-dropdown @command="(command) => handleCommandAction(command, row)">
              <el-button size="small" type="primary" style="margin-left: 8px;">
                {{ $t('common.more') }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="resetPassword">{{ $t('system.resetPassword') }}</el-dropdown-item>
                  <el-dropdown-item command="authRole">{{ $t('system.authRole') }}</el-dropdown-item>
                  <el-dropdown-item command="delete" divided style="color: #f56c6c;">{{ $t('common.delete') }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item :label="$t('user.username')" prop="username">
          <el-input v-model="form.username" :placeholder="$t('common.query')" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item :label="$t('user.nickname')" prop="nickname">
          <el-input v-model="form.nickname" :placeholder="$t('user.nickname')" />
        </el-form-item>
        <el-form-item :label="$t('user.password')" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" :placeholder="$t('user.password')" />
        </el-form-item>
        <el-form-item :label="$t('user.email')" prop="email">
          <el-input v-model="form.email" :placeholder="$t('user.email')" />
        </el-form-item>
        <el-form-item :label="$t('user.phone')" prop="phone">
          <el-input v-model="form.phone" :placeholder="$t('user.phone')" />
        </el-form-item>
        <el-form-item :label="$t('user.status')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">{{ $t('user.statusNormal') }}</el-radio>
            <el-radio label="1">{{ $t('user.statusDisabled') }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordVisible" :title="$t('system.resetPassword')" width="400px">
      <el-form :model="resetPasswordForm" :rules="resetPasswordRules" ref="resetPasswordRef" label-width="80px">
        <el-form-item :label="$t('user.username')">
          <el-input v-model="resetPasswordForm.username" disabled />
        </el-form-item>
        <el-form-item :label="$t('user.password')" prop="newPassword">
          <el-input v-model="resetPasswordForm.newPassword" type="password" :placeholder="$t('user.password')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleResetPasswordSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 角色授权对话框 -->
    <el-dialog v-model="authRoleVisible" :title="$t('system.authRole')" width="400px">
      <el-form label-width="80px">
        <el-form-item :label="$t('user.username')">
          <el-input v-model="authRoleForm.username" disabled />
        </el-form-item>
        <el-form-item :label="$t('system.selectRole')">
          <el-checkbox-group v-model="authRoleForm.roleIds">
            <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="authRoleVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleAuthRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi, roleApi } from '@/api/system'
import { ArrowDown } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const queryForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  status: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  status: '0'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const resetPasswordVisible = ref(false)
const resetPasswordRef = ref(null)
const resetPasswordForm = reactive({
  userId: null,
  username: '',
  newPassword: ''
})

const resetPasswordRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const authRoleVisible = ref(false)
const authRoleForm = reactive({
  userId: null,
  username: '',
  roleIds: []
})
const roleList = ref([])

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await userApi.list(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.nickname = ''
  queryForm.phone = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, {
    id: null,
    username: '',
    nickname: '',
    password: '',
    email: '',
    phone: '',
    status: '0'
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑用户'
  try {
    const res = await userApi.getById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = form.id ? await userApi.update(form) : await userApi.add(form)
        if (res.code === 200) {
          ElMessage.success(form.id ? '修改成功' : '新增成功')
          dialogVisible.value = false
          handleQuery()
        }
      } catch (error) {
        ElMessage.error(form.id ? '修改失败' : '新增失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await userApi.delete([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleResetPassword = (row) => {
  Object.assign(resetPasswordForm, {
    userId: row.id,
    username: row.username,
    newPassword: ''
  })
  resetPasswordVisible.value = true
}

const handleResetPasswordSubmit = async () => {
  if (!resetPasswordRef.value) return
  await resetPasswordRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.resetPwd({
          userId: resetPasswordForm.userId,
          newPassword: resetPasswordForm.newPassword
        })
        if (res.code === 200) {
          ElMessage.success('密码重置成功')
          resetPasswordVisible.value = false
        }
      } catch (error) {
        ElMessage.error('密码重置失败')
      }
    }
  })
}

const handleAuthRole = async (row) => {
  Object.assign(authRoleForm, {
    userId: row.id,
    username: row.username,
    roleIds: []
  })
  try {
    const res = await userApi.getById(row.id)
    if (res.code === 200) {
      authRoleForm.roleIds = res.data.roleIds || []
    }
  } catch (error) {
    ElMessage.error('获取用户角色失败')
  }
  authRoleVisible.value = true
}

const handleAuthRoleSubmit = async () => {
  try {
    const res = await userApi.authRole(authRoleForm.userId, authRoleForm.roleIds)
    if (res.code === 200) {
      ElMessage.success('角色授权成功')
      authRoleVisible.value = false
      handleQuery()
    }
  } catch (error) {
    ElMessage.error('角色授权失败')
  }
}

const handleExport = async () => {
  try {
    const res = await userApi.export(queryForm)
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `用户列表_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleCommandAction = (command, row) => {
  switch (command) {
    case 'resetPassword':
      handleResetPassword(row)
      break
    case 'authRole':
      handleAuthRole(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const loadRoleList = async () => {
  try {
    const res = await roleApi.all()
    if (res.code === 200) {
      roleList.value = res.data
    }
  } catch (error) {
    console.error('加载角色列表失败', error)
  }
}

onMounted(() => {
  handleQuery()
  loadRoleList()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>