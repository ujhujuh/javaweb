<template>
  <div class="dict-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.dictManagement') }}</span>
          <el-button type="primary" @click="handleAddType">{{ $t('dict.addDictType') }}</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('dict.dictName')">
          <el-input v-model="queryForm.dictName" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('dict.dictType')">
          <el-input v-model="queryForm.dictType" :placeholder="$t('common.query')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.status')">
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
        <el-table-column prop="dictName" :label="$t('dict.dictName')" />
        <el-table-column prop="dictType" :label="$t('dict.dictType')" />
        <el-table-column prop="status" :label="$t('user.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? $t('user.statusNormal') : $t('user.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" :label="$t('dict.remark')" />
        <el-table-column prop="createTime" :label="$t('user.createTime')" />
        <el-table-column :label="$t('common.operation')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditType(row)">{{ $t('common.edit') }}</el-button>
            <el-dropdown @command="(command) => handleCommandAction(command, row)">
              <el-button size="small" type="primary" style="margin-left: 8px;">
                {{ $t('common.more') }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="viewData">{{ $t('dict.viewData') }}</el-dropdown-item>
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

    <!-- 字典类型新增/编辑对话框 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="500px">
      <el-form :model="typeForm" :rules="typeRules" ref="typeFormRef" label-width="90px">
        <el-form-item :label="$t('dict.dictName')" prop="dictName">
          <el-input v-model="typeForm.dictName" :placeholder="$t('dict.dictName')" />
        </el-form-item>
        <el-form-item :label="$t('dict.dictType')" prop="dictType">
          <el-input v-model="typeForm.dictType" :placeholder="$t('dict.dictType')" :disabled="!!typeForm.id" />
        </el-form-item>
        <el-form-item :label="$t('user.status')" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio label="0">{{ $t('user.statusNormal') }}</el-radio>
            <el-radio label="1">{{ $t('user.statusDisabled') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('config.remark')">
          <el-input v-model="typeForm.remark" type="textarea" :placeholder="$t('config.remark')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmitType">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据配置对话框 -->
    <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="900px">
      <div class="data-dialog-header">
        <el-form inline>
          <el-form-item label="字典类型">
            <el-input v-model="currentDictType" disabled style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAddData">新增</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="dataTableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dictLabel" label="字典标签" />
        <el-table-column prop="dictValue" label="字典值" />
        <el-table-column prop="dictSort" label="排序" />
        <el-table-column prop="cssClass" label="样式属性" />
        <el-table-column prop="listClass" label="表格回显">
          <template #default="{ row }">
            <el-tag v-if="row.listClass === 'primary'" type="primary">主要</el-tag>
            <el-tag v-else-if="row.listClass === 'success'" type="success">成功</el-tag>
            <el-tag v-else-if="row.listClass === 'info'" type="info">信息</el-tag>
            <el-tag v-else-if="row.listClass === 'warning'" type="warning">警告</el-tag>
            <el-tag v-else-if="row.listClass === 'danger'" type="danger">危险</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="是否默认">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 'Y' ? 'success' : 'info'">
              {{ row.isDefault === 'Y' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditData(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 字典数据新增/编辑对话框 -->
    <el-dialog v-model="dataEditDialogVisible" :title="dataEditDialogTitle" width="500px">
      <el-form :model="dataForm" :rules="dataRules" ref="dataFormRef" label-width="90px">
        <el-form-item label="字典类型">
          <el-input v-model="dataForm.dictType" disabled />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" :placeholder="字典标签" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" :placeholder="字典值" />
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="dataForm.dictSort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="样式属性">
          <el-input v-model="dataForm.cssClass" :placeholder="样式属性" />
        </el-form-item>
        <el-form-item label="表格回显">
          <el-select v-model="dataForm.listClass" placeholder="请选择回显样式" clearable>
            <el-option label="主要" value="primary" />
            <el-option label="成功" value="success" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否默认">
          <el-radio-group v-model="dataForm.isDefault">
            <el-radio label="Y">是</el-radio>
            <el-radio label="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { dictApi } from '@/api/system'

const queryForm = reactive({
  dictName: '',
  dictType: '',
  status: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const typeFormRef = ref(null)
const typeForm = reactive({
  id: null,
  dictName: '',
  dictType: '',
  status: '0',
  remark: ''
})

const typeRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const dataDialogVisible = ref(false)
const dataDialogTitle = ref('')
const currentDictType = ref('')
const dataTableData = ref([])

const dataEditDialogVisible = ref(false)
const dataEditDialogTitle = ref('')
const dataFormRef = ref(null)
const dataForm = reactive({
  id: null,
  dictType: '',
  dictLabel: '',
  dictValue: '',
  dictSort: 0,
  cssClass: '',
  listClass: '',
  isDefault: 'N',
  status: '0'
})

const dataRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }],
  dictSort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await dictApi.typeList(queryForm)
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
  queryForm.dictName = ''
  queryForm.dictType = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleAddType = () => {
  typeDialogTitle.value = '新增字典类型'
  Object.assign(typeForm, {
    id: null,
    dictName: '',
    dictType: '',
    status: '0',
    remark: ''
  })
  typeDialogVisible.value = true
}

const handleEditType = async (row) => {
  typeDialogTitle.value = '编辑字典类型'
  try {
    const res = await dictApi.getTypeById(row.id)
    if (res.code === 200) {
      Object.assign(typeForm, res.data)
      typeDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取字典类型信息失败')
  }
}

const handleSubmitType = async () => {
  if (!typeFormRef.value) return
  await typeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = typeForm.id ? await dictApi.updateType(typeForm) : await dictApi.addType(typeForm)
        if (res.code === 200) {
          ElMessage.success(typeForm.id ? '修改成功' : '新增成功')
          typeDialogVisible.value = false
          handleQuery()
        }
      } catch (error) {
        ElMessage.error(typeForm.id ? '修改失败' : '新增失败')
      }
    }
  })
}

const handleDeleteType = (row) => {
  ElMessageBox.confirm('确认删除该字典类型吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await dictApi.deleteType([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleViewData = async (row) => {
  dataDialogTitle.value = '字典配置'
  currentDictType.value = row.dictType
  try {
    const res = await dictApi.dataByType(row.dictType)
    if (res.code === 200) {
      dataTableData.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取字典数据失败')
  }
  dataDialogVisible.value = true
}

const handleAddData = () => {
  dataEditDialogTitle.value = '新增字典数据'
  Object.assign(dataForm, {
    id: null,
    dictType: currentDictType.value,
    dictLabel: '',
    dictValue: '',
    dictSort: 0,
    cssClass: '',
    listClass: '',
    isDefault: 'N',
    status: '0'
  })
  dataEditDialogVisible.value = true
}

const handleEditData = async (row) => {
  dataEditDialogTitle.value = '编辑字典数据'
  try {
    const res = await dictApi.getDataById(row.id)
    if (res.code === 200) {
      Object.assign(dataForm, res.data)
      dataEditDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取字典数据信息失败')
  }
}

const handleSubmitData = async () => {
  if (!dataFormRef.value) return
  await dataFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = dataForm.id ? await dictApi.updateData(dataForm) : await dictApi.addData(dataForm)
        if (res.code === 200) {
          ElMessage.success(dataForm.id ? '修改成功' : '新增成功')
          dataEditDialogVisible.value = false
          handleViewData({ dictType: dataForm.dictType })
        }
      } catch (error) {
        ElMessage.error(dataForm.id ? '修改失败' : '新增失败')
      }
    }
  })
}

const handleDeleteData = (row) => {
  ElMessageBox.confirm('确认删除该字典数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await dictApi.deleteData([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleViewData({ dictType: currentDictType.value })
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleCommandAction = (command, row) => {
  switch (command) {
    case 'viewData':
      handleViewData(row)
      break
    case 'delete':
      handleDeleteType(row)
      break
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.dict-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.data-dialog-header {
  margin-bottom: 15px;
}
</style>