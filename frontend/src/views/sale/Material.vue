<template>
  <div class="sale-material-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('sale.saleMaterialManagement') }}</span>
          <el-button type="primary" @click="handleAdd">{{ $t('sale.addMaterial') }}</el-button>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('common.query')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-input v-model="queryForm.keyword" :placeholder="$t('news.newsTitle')" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item :label="$t('sale.materialCategory')" :label-width="isEnglish ? '130px' : 'auto'">
          <el-select v-model="queryForm.categoryId" clearable :placeholder="$t('common.query')" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('news.status')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-select v-model="queryForm.status" clearable :placeholder="$t('common.query')" style="width: 140px">
            <el-option :label="$t('news.draft')" value="0" />
            <el-option :label="$t('sale.published')" value="1" />
            <el-option :label="$t('sale.cancelled')" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" :label="$t('sale.materialTitle')" min-width="220" show-overflow-tooltip />
        <el-table-column prop="categoryId" :label="$t('sale.materialCategory')" width="140">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="price" :label="$t('sale.materialPrice')" width="110" />
        <el-table-column prop="salesCount" :label="$t('sale.salesCount')" width="90" />
        <el-table-column prop="status" :label="$t('news.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : row.status === '0' ? 'info' : 'danger'">
              {{ row.status === '1' ? $t('news.published') : row.status === '0' ? $t('news.draft') : $t('sale.cancelled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" :label="$t('news.publishTime')" width="180" />
        <el-table-column :label="$t('common.operation')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="860px">
      <el-form :model="form" :rules="rules" ref="formRef" :label-width="$t('common.labelWidth')">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('sale.materialTitle')" prop="title">
              <el-input v-model="form.title" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('sale.materialCategory')" prop="categoryId">
              <el-select v-model="form.categoryId" clearable :placeholder="$t('news.selectCategory')" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('sale.materialSummary')">
          <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item :label="$t('sale.materialDetail')" prop="detailContent">
          <el-input v-model="form.detailContent" type="textarea" :rows="4" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('sale.coverImage')">
              <el-input v-model="form.coverImage" :placeholder="$t('sale.coverUrl')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('sale.previewUrl')">
              <el-input v-model="form.previewUrl" :placeholder="$t('sale.previewUrl')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('sale.fileUrl')" prop="fileUrl">
              <el-input v-model="form.fileUrl" :placeholder="$t('sale.fileUrl')" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('sale.fileFormat')">
              <el-input v-model="form.fileFormat" placeholder="pdf/zip" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('sale.fileSize')">
              <el-input v-model="form.fileSize" placeholder="20MB" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item :label="$t('sale.materialPrice')" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('sale.downloadCount')" prop="maxDownloadCount">
              <el-input-number v-model="form.maxDownloadCount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('news.status')">
              <el-radio-group v-model="form.status">
                <el-radio value="0">{{ $t('news.draft') }}</el-radio>
                <el-radio value="1">{{ $t('news.published') }}</el-radio>
                <el-radio value="2">{{ $t('sale.cancelled') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('sale.tags')">
          <el-input v-model="form.tags" :placeholder="$t('sale.tagsPlaceholder')" />
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
import { useI18n } from 'vue-i18n'
import { saleManageApi } from '@/api/system'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryOptions = ref([])

const queryForm = reactive({
  keyword: '',
  categoryId: undefined,
  status: '',
  current: 1,
  size: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null,
  title: '',
  summary: '',
  detailContent: '',
  coverImage: '',
  previewUrl: '',
  fileUrl: '',
  fileFormat: '',
  fileSize: '',
  price: 0,
  tags: '',
  categoryId: undefined,
  status: '1',
  maxDownloadCount: 99,
  isFree: '0'
})

const rules = {
  title: [{ required: true, message: '请输入资料标题', trigger: 'blur' }],
  detailContent: [{ required: true, message: '请输入资料详情', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  fileUrl: [{ required: true, message: '请输入文件地址', trigger: 'blur' }]
}

const getCategoryName = (id) => {
  const item = categoryOptions.value.find(i => i.id === id)
  return item ? item.categoryName : '-'
}

const loadCategories = async () => {
  const res = await saleManageApi.listCategories()
  categoryOptions.value = res.data || []
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await saleManageApi.listMaterials(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  loadList()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.categoryId = undefined
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.summary = ''
  form.detailContent = ''
  form.coverImage = ''
  form.previewUrl = ''
  form.fileUrl = ''
  form.fileFormat = ''
  form.fileSize = ''
  form.price = 0
  form.tags = ''
  form.categoryId = undefined
  form.status = '1'
  form.maxDownloadCount = 99
  form.isFree = '0'
}

const handleAdd = () => {
  dialogTitle.value = '新增资源'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资源'
  form.id = row.id
  form.title = row.title
  form.summary = row.summary || ''
  form.detailContent = row.detailContent || ''
  form.coverImage = row.coverImage || ''
  form.previewUrl = row.previewUrl || ''
  form.fileUrl = row.fileUrl || ''
  form.fileFormat = row.fileFormat || ''
  form.fileSize = row.fileSize || ''
  form.price = Number(row.price || 0)
  form.tags = row.tags || ''
  form.categoryId = row.categoryId
  form.status = row.status || '1'
  form.maxDownloadCount = row.maxDownloadCount || 99
  form.isFree = row.isFree || '0'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除资源「${row.title}」吗？`, '提示', { type: 'warning' })
  await saleManageApi.deleteMaterial(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  form.isFree = Number(form.price) === 0 ? '1' : '0'
  const payload = { ...form }
  if (form.id) {
    await saleManageApi.updateMaterial(form.id, payload)
    ElMessage.success('修改成功')
  } else {
    await saleManageApi.createMaterial(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

onMounted(async () => {
  await loadCategories()
  await loadList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
