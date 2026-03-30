<template>
  <div class="news-category-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('news.categoryManagement') }}</span>
          <el-button type="primary" @click="handleAdd">{{ $t('common.add') }}</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" :label="$t('news.categoryName')" min-width="220" />
        <el-table-column prop="parentId" :label="$t('news.parentId')" width="100" />
        <el-table-column prop="sortNum" :label="$t('menu.sort')" width="100" />
        <el-table-column prop="status" :label="$t('user.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? $t('user.statusNormal') : $t('user.statusDisabled') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('common.operation')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" :label-width="isEnglish ? '100px' : '80px'">
        <el-form-item :label="$t('news.categoryName')" prop="categoryName">
          <el-input v-model="form.categoryName" :placeholder="$t('news.categoryName')" />
        </el-form-item>
        <el-form-item :label="$t('news.parentId')">
          <el-input-number v-model="form.parentId" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('menu.sort')">
          <el-input-number v-model="form.sortNum" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('user.status')">
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { newsManageApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const $t = t

const form = reactive({
  id: null,
  categoryName: '',
  parentId: 0,
  sortNum: 0,
  status: '0'
})

const rules = {
  categoryName: [{ required: true, message: t('validation.required'), trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await newsManageApi.categories()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.categoryName = ''
  form.parentId = 0
  form.sortNum = 0
  form.status = '0'
}

const handleAdd = () => {
  dialogTitle.value = $t('news.addDictType')
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = $t('news.editDictType')
  form.id = row.id
  form.categoryName = row.categoryName
  form.parentId = row.parentId || 0
  form.sortNum = row.sortNum || 0
  form.status = row.status || '0'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm($t('common.confirmDelete'), '提示', { type: 'warning' })
  await newsManageApi.deleteCategory(row.id)
  ElMessage.success($t('common.success'))
  await loadList()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  const payload = {
    id: form.id,
    categoryName: form.categoryName,
    parentId: form.parentId,
    sortNum: form.sortNum,
    status: form.status
  }
  if (form.id) {
    await newsManageApi.updateCategory(payload)
    ElMessage.success('修改成功')
  } else {
    await newsManageApi.addCategory(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

onMounted(loadList)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
