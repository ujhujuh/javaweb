<template>
  <div class="news-content-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>内容管理</span>
          <el-button type="primary" @click="handleAdd">新增内容</el-button>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="标题/摘要" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" clearable placeholder="全部分类" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="草稿" value="0" />
            <el-option label="发布" value="1" />
            <el-option label="下线" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="categoryId" label="分类" width="140">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="visibleScope" label="可见范围" width="110">
          <template #default="{ row }">
            <el-tag :type="row.visibleScope === '1' ? 'warning' : 'success'">{{ row.visibleScope === '1' ? '登录可见' : '公开' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : row.status === '0' ? 'info' : 'danger'">
              {{ row.status === '1' ? '发布' : row.status === '0' ? '草稿' : '下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="favoriteCount" label="收藏" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" clearable placeholder="选择分类" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="封面图">
              <el-upload
                class="cover-uploader"
                :show-file-list="false"
                :http-request="handleCoverUpload"
                accept="image/*"
              >
                <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
                <el-button v-else type="primary" plain>上传封面图</el-button>
              </el-upload>
              <div style="margin-top: 8px;">
                <el-input v-model="form.coverImage" placeholder="或手动输入图片URL" />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-input v-model="form.tags" placeholder="多个标签逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="可见范围">
              <el-radio-group v-model="form.visibleScope">
                <el-radio value="0">公开</el-radio>
                <el-radio value="1">登录可见</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">草稿</el-radio>
                <el-radio value="1">发布</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="正文" prop="content">
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { fileApi, newsManageApi } from '@/api/system'

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
