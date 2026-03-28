<template>
  <div class="sale-category-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>售卖分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" min-width="220" />
        <el-table-column prop="parentId" label="父级ID" width="100" />
        <el-table-column prop="sortNum" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="父级ID" prop="parentId">
          <el-input-number v-model="form.parentId" :min="0" />
        </el-form-item>
        <el-form-item label="排序" prop="sortNum">
          <el-input-number v-model="form.sortNum" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saleManageApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const form = reactive({
  id: null,
  categoryName: '',
  parentId: 0,
  sortNum: 0,
  status: '0'
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请输入父级ID', trigger: 'change' }],
  sortNum: [{ required: true, message: '请输入排序', trigger: 'change' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await saleManageApi.listCategories()
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
  dialogTitle.value = '新增分类'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  form.id = row.id
  form.categoryName = row.categoryName
  form.parentId = row.parentId || 0
  form.sortNum = row.sortNum || 0
  form.status = row.status || '0'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除分类「${row.categoryName}」吗？`, '提示', { type: 'warning' })
  await saleManageApi.deleteCategory(row.id)
  ElMessage.success('删除成功')
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
    await saleManageApi.updateCategory(payload)
    ElMessage.success('修改成功')
  } else {
    await saleManageApi.createCategory(payload)
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
