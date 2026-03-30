<template>
  <div class="news-content-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('news.contentManagement') }}</span>
          <el-button type="primary" @click="handleAdd">{{ $t('news.addContent') }}</el-button>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('common.query')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-input v-model="queryForm.keyword" :placeholder="$t('news.newsTitle')" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item :label="$t('news.newsCategory')" :label-width="isEnglish ? '120px' : 'auto'">
          <el-select v-model="queryForm.categoryId" clearable :placeholder="$t('common.query')" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('news.status')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-select v-model="queryForm.status" clearable :placeholder="$t('common.query')" style="width: 140px">
            <el-option :label="$t('news.draft')" value="0" />
            <el-option :label="$t('news.published')" value="1" />
            <el-option :label="$t('news.offline')" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" :label="$t('news.newsTitle')" min-width="260" show-overflow-tooltip />
        <el-table-column prop="categoryId" :label="$t('news.newsCategory')" width="140">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="visibleScope" :label="$t('news.visibleScope')" width="110">
          <template #default="{ row }">
            <el-tag :type="row.visibleScope === '1' ? 'warning' : 'success'">{{ row.visibleScope === '1' ? $t('news.loginVisible') : $t('news.public') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('news.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : row.status === '0' ? 'info' : 'danger'">
              {{ row.status === '1' ? $t('news.published') : row.status === '0' ? $t('news.draft') : $t('news.offline') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" :label="$t('news.viewCount')" width="80" />
        <el-table-column prop="likeCount" :label="$t('news.likeCount')" width="80" />
        <el-table-column prop="favoriteCount" :label="$t('news.favoriteCount')" width="80" />
        <el-table-column prop="commentCount" :label="$t('news.commentCount')" width="80" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="900px">
      <el-form :model="form" :rules="rules" ref="formRef" :label-width="$t('common.labelWidth')">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('news.newsTitle')" prop="title">
              <el-input v-model="form.title" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('news.newsCategory')">
              <el-select v-model="form.categoryId" clearable :placeholder="$t('news.selectCategory')" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('news.summary')">
          <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('news.coverImage')">
              <el-upload
                class="cover-uploader"
                :show-file-list="false"
                :http-request="handleCoverUpload"
                accept="image/*"
              >
                <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
                <el-button v-else type="primary" plain>{{ $t('news.uploadCover') }}</el-button>
              </el-upload>
              <div style="margin-top: 8px;">
                <el-input v-model="form.coverImage" :placeholder="$t('news.orManualUrl')" />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('news.tags')">
              <el-input v-model="form.tags" :placeholder="$t('news.tagsPlaceholder')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('news.visibleScope')">
              <el-radio-group v-model="form.visibleScope">
                <el-radio value="0">{{ $t('news.public') }}</el-radio>
                <el-radio value="1">{{ $t('news.loginVisible') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('news.status')">
              <el-radio-group v-model="form.status">
                <el-radio value="0">{{ $t('news.draft') }}</el-radio>
                <el-radio value="1">{{ $t('news.published') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('news.content')" prop="content">
          <div style="border: 1px solid #dcdfe6; width: 100%;">
            <Toolbar
              style="border-bottom: 1px solid #dcdfe6"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 360px; overflow-y: hidden;"
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleEditorCreated"
            />
          </div>
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
import { onBeforeUnmount, onMounted, reactive, ref, shallowRef, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { fileApi, newsManageApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

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
const editorRef = shallowRef()
const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  tags: '',
  categoryId: undefined,
  visibleScope: '0',
  status: '1'
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
}

const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入正文内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await fileApi.uploadImage(file)
          const url = res?.data?.url || res?.url
          if (url) {
            insertFn(url, '图片', url)
          } else {
            ElMessage.error(res?.message || '图片上传失败')
          }
        } catch (e) {
          ElMessage.error('图片上传失败')
        }
      },
      maxFileSize: 10 * 1024 * 1024,
      allowedFileTypes: ['image/*']
    }
  }
}

const handleEditorCreated = (editor) => {
  editorRef.value = editor
}

const handleCoverUpload = async (option) => {
  try {
    const res = await fileApi.uploadImage(option.file)
    const url = res?.data?.url || res?.url
    if (!url) {
      ElMessage.error(res?.message || '上传失败')
      return
    }
    form.coverImage = url
    ElMessage.success('封面上传成功')
  } catch (e) {
    ElMessage.error('封面上传失败')
  }
}

const getCategoryName = (id) => {
  const item = categoryOptions.value.find(i => i.id === id)
  return item ? item.categoryName : '-'
}

const loadCategories = async () => {
  const res = await newsManageApi.categories()
  categoryOptions.value = res.data || []
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await newsManageApi.list(queryForm)
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
  form.content = ''
  form.coverImage = ''
  form.tags = ''
  form.categoryId = undefined
  form.visibleScope = '0'
  form.status = '1'
}

const handleAdd = () => {
  dialogTitle.value = '新增内容'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑内容'
  form.id = row.id
  form.title = row.title
  form.summary = row.summary
  form.content = row.content
  form.coverImage = row.coverImage
  form.tags = row.tags
  form.categoryId = row.categoryId
  form.visibleScope = row.visibleScope || '0'
  form.status = row.status || '1'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」吗？`, '提示', { type: 'warning' })
  await newsManageApi.delete(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  const payload = {
    title: form.title,
    summary: form.summary,
    content: form.content,
    coverImage: form.coverImage,
    tags: form.tags,
    categoryId: form.categoryId,
    visibleScope: form.visibleScope,
    status: form.status
  }
  if (form.id) {
    await newsManageApi.update(form.id, payload)
    ElMessage.success('修改成功')
  } else {
    await newsManageApi.publish(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

onMounted(async () => {
  await loadCategories()
  await loadList()
})

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-preview {
  width: 180px;
  height: 110px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
}
</style>

<style src="@wangeditor/editor/dist/css/style.css"></style>
