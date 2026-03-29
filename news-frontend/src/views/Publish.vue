<template>
  <div class="page">
    <el-card>
      <template #header>
        <span>发布资讯</span>
      </template>

      <el-form :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>

        <el-form-item label="封面图 URL">
          <el-upload
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
            <el-button v-else class="upload-btn">
              <span class="btn-icon">📷</span>
              上传封面图
            </el-button>
          </el-upload>
          <el-input v-model="form.coverImage" placeholder="或手动输入图片URL" style="margin-top: 8px;" />
        </el-form-item>

        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 240px" clearable>
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标签（逗号分隔）">
          <el-input v-model="form.tags" placeholder="行业,观察,专题" />
        </el-form-item>

        <el-form-item label="可见范围">
          <el-radio-group v-model="form.visibleScope">
            <el-radio value="0">公开</el-radio>
            <el-radio value="1">登录后可见</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio value="1">立即发布</el-radio>
            <el-radio value="0">保存草稿</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="正文">
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

        <el-button class="submit-btn" @click="submit">
          <span class="btn-icon">📤</span>
          提交发布
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { getCategories, publishNews, uploadImage } from '@/api/news'
import { toFileUrl } from '@/utils/file-url'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const categories = ref([])
const editorRef = shallowRef()

onMounted(() => {
  if (!userStore.hasPermission('news:content:list')) {
    ElMessage.error('您没有权限访问该页面')
    router.push('/')
    return
  }
  loadCategories()
})

const form = reactive({
  title: '',
  summary: '',
  coverImage: '',
  categoryId: undefined,
  tags: '',
  visibleScope: '0',
  status: '1',
  content: ''
})

const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入正文内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await uploadImage(file)
          const url = res?.data?.url || res?.url
          if (url) {
            const fullUrl = toFileUrl(url)
            insertFn(fullUrl, '图片', fullUrl)
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
    const res = await uploadImage(option.file)
    const url = res?.data?.url || res?.url
    if (!url) {
      ElMessage.error(res?.message || '封面上传失败')
      return
    }
    form.coverImage = toFileUrl(url)
    ElMessage.success('封面上传成功')
  } catch (e) {
    ElMessage.error('封面上传失败')
  }
}

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const submit = async () => {
  if (!form.title.trim()) {
    ElMessage.error('标题不能为空')
    return
  }
  if (!form.content.trim()) {
    ElMessage.error('正文不能为空')
    return
  }

  const res = await publishNews({ ...form })
  ElMessage.success('发布成功')
  router.push(`/detail/${res.data}`)
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})
</script>

<style scoped>
.cover-preview {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
}

.upload-btn {
  border-radius: 12px;
  padding: 10px 20px;
  font-weight: 600;
  font-size: 14px;
  border: 1px solid rgba(13, 148, 136, 0.3);
  background: rgba(13, 148, 136, 0.05);
  color: var(--brand);
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(13, 148, 136, 0.1);
}

.upload-btn:hover {
  background: rgba(13, 148, 136, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
}

.submit-btn {
  border-radius: 12px;
  padding: 12px 32px;
  font-weight: 700;
  font-size: 16px;
  background: var(--brand-gradient);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  box-shadow: 0 6px 16px rgba(13, 148, 136, 0.4);
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0);
}

.btn-icon {
  margin-right: 6px;
  font-size: 16px;
}
</style>

<style src="@wangeditor/editor/dist/css/style.css"></style>
