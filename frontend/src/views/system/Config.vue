<template>
  <div class="config-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.configManagement') }}</span>
          <div>
            <el-button type="warning" @click="handleRefresh">{{ $t('config.refreshCache') }}</el-button>
            <el-button type="primary" @click="handleAdd">{{ $t('common.add') }}</el-button>
          </div>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('config.configName')">
          <el-input v-model="queryForm.configName" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('config.configKey')">
          <el-input v-model="queryForm.configKey" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('config.systemBuiltIn')">
          <el-select v-model="queryForm.configType" :placeholder="$t('common.query')" clearable style="width: 150px">
            <el-option :label="$t('common.yes')" value="Y" />
            <el-option :label="$t('common.no')" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="configName" :label="$t('config.configName')" />
        <el-table-column prop="configKey" :label="$t('config.configKey')" />
        <el-table-column prop="configValue" :label="$t('config.configValue')" />
        <el-table-column prop="configType" :label="$t('config.systemBuiltIn')">
          <template #default="{ row }">
            <el-tag :type="row.configType === 'Y' ? 'warning' : 'info'">
              {{ row.configType === 'Y' ? $t('common.yes') : $t('common.no') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" :label="$t('config.remark')" />
        <el-table-column prop="createTime" :label="$t('user.createTime')" />
        <el-table-column :label="$t('common.operation')" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)" :disabled="row.configType === 'Y'">{{ $t('common.edit') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :disabled="row.configType === 'Y'">{{ $t('common.delete') }}</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item :label="$t('config.configName')" prop="configName">
          <el-input v-model="form.configName" :placeholder="$t('config.configName')" />
        </el-form-item>
        <el-form-item :label="$t('config.configKey')" prop="configKey">
          <el-input v-model="form.configKey" :placeholder="$t('config.configKey')" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item :label="$t('config.configValue')" prop="configValue">
          <el-input v-model="form.configValue" :placeholder="$t('config.configValue')" />
        </el-form-item>
        <el-form-item :label="$t('config.systemBuiltIn')" prop="configType">
          <el-radio-group v-model="form.configType" :disabled="!!form.id">
            <el-radio label="Y">{{ $t('common.yes') }}</el-radio>
            <el-radio label="N">{{ $t('common.no') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('config.remark')">
          <el-input v-model="form.remark" type="textarea" :placeholder="$t('config.remark')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { configApi } from '@/api/system'

const queryForm = reactive({
  configName: '',
  configKey: '',
  configType: '',
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
  configName: '',
  configKey: '',
  configValue: '',
  configType: 'N',
  remark: ''
})

const rules = {
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数键值', trigger: 'blur' }],
  configType: [{ required: true, message: '请选择系统内置', trigger: 'change' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await configApi.list(queryForm)
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
  queryForm.configName = ''
  queryForm.configKey = ''
  queryForm.configType = ''
  queryForm.current = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增参数'
  Object.assign(form, {
    id: null,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'N',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑参数'
  try {
    const res = await configApi.getById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取参数信息失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = form.id ? await configApi.update(form) : await configApi.add(form)
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
  ElMessageBox.confirm('确认删除该参数吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await configApi.delete([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleRefresh = () => {
  ElMessageBox.confirm('确认刷新参数缓存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await configApi.refresh()
      if (res.code === 200) {
        ElMessage.success('刷新成功')
      }
    } catch (error) {
      ElMessage.error('刷新失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.config-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>