<template>
  <div class="notice-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知公告</span>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item label="公告标题">
          <el-input v-model="queryForm.noticeTitle" placeholder="请输入公告标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="queryForm.noticeType" placeholder="请选择公告类型" clearable style="width: 150px">
            <el-option label="通知" value="1" />
            <el-option label="公告" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="正常" value="0" />
            <el-option label="关闭" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="noticeTitle" label="公告标题" min-width="200" />
        <el-table-column prop="noticeType" label="公告类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.noticeType === '1'" type="primary">通知</el-tag>
            <el-tag v-else-if="row.noticeType === '2'" type="success">公告</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建者" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-dropdown @command="(command) => handleCommandAction(command, row)">
              <el-button size="small" type="primary" style="margin-left: 8px;">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="delete" divided style="color: #f56c6c;">删除</el-dropdown-item>
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
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option label="通知" value="1" />
                <el-option label="公告" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="公告内容" prop="noticeContent">
          <div style="border: 1px solid #ccc">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="form.noticeContent"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">关闭</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewVisible" title="公告详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="公告标题">{{ viewData.noticeTitle }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag v-if="viewData.noticeType === '1'" type="primary">通知</el-tag>
          <el-tag v-else-if="viewData.noticeType === '2'" type="success">公告</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="公告状态">
          <el-tag :type="viewData.status === '0' ? 'success' : 'danger'">
            {{ viewData.status === '0' ? '正常' : '关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="公告内容">
          <div v-html="viewData.noticeContent"></div>
        </el-descriptions-item>
        <el-descriptions-item label="创建者">{{ viewData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { noticeApi, fileApi } from '@/api/system'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 工具栏配置
const toolbarConfig = {
  // 排除某些菜单项
  // excludeKeys: ['group-video']
}

// 编辑器配置
const editorConfig = {
  placeholder: '请输入公告内容...',
  MENU_CONF: {
    // 配置上传图片
    uploadImage: {
      // 自定义上传
      async customUpload(file, insertFn) {
        try {
          // 调用文件上传API
          const res = await fileApi.uploadImage(file)

          // 检查上传结果
          if (res.errno === 0 && res.data && res.data.url) {
            // 插入图片到编辑器
            insertFn(res.data.url, res.data.alt || '图片', res.data.href || res.data.url)
          } else {
            ElMessage.error(res.message || '图片上传失败')
          }
        } catch (error) {
          console.error('图片上传失败:', error)
          ElMessage.error('图片上传失败')
        }
      },
      // 限制图片大小（10MB）
      maxFileSize: 10 * 1024 * 1024,
      // 限制图片类型
      allowedFileTypes: ['image/*']
    }
  }
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

const queryForm = reactive({
  noticeTitle: '',
  noticeType: '',
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
  noticeTitle: '',
  noticeType: '1',
  noticeContent: '',
  status: '0'
})

const viewVisible = ref(false)
const viewData = ref({})

const rules = {
  noticeTitle: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  noticeContent: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await noticeApi.list(queryForm)
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
  queryForm.noticeTitle = ''
  queryForm.noticeType = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增公告'
  Object.assign(form, {
    id: null,
    noticeTitle: '',
    noticeType: '1',
    noticeContent: '',
    status: '0'
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑公告'
  try {
    const res = await noticeApi.getById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取公告信息失败')
  }
}

const handleView = (row) => {
  viewData.value = row
  viewVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = form.id ? await noticeApi.update(form) : await noticeApi.add(form)
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
  ElMessageBox.confirm('确认删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await noticeApi.delete([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleCommandAction = (command, row) => {
  switch (command) {
    case 'edit':
      handleEdit(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.notice-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

<style src="@wangeditor/editor/dist/css/style.css"></style>