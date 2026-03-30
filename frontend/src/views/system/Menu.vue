<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.menuManagement') }}</span>
          <el-button type="primary" @click="handleAdd">{{ $t('common.add') }}</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('menu.menuName')" :label-width="isEnglish ? '120px' : 'auto'">
          <el-input v-model="queryForm.menuName" :placeholder="$t('common.query')" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item :label="$t('menu.status')" :label-width="isEnglish ? '100px' : 'auto'">
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
      <el-table
        :data="tableData"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
        default-expand-all
      >
        <el-table-column prop="menuName" :label="$t('menu.menuName')" min-width="180" />
        <el-table-column prop="icon" :label="$t('menu.icon')" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon && row.icon !== '#'"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" :label="$t('menu.menuType')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 'M'" type="primary">{{ $t('menu.menuTypeDirectory') }}</el-tag>
            <el-tag v-else-if="row.menuType === 'C'" type="success">{{ $t('menu.menuTypeMenu') }}</el-tag>
            <el-tag v-else-if="row.menuType === 'F'" type="info">{{ $t('menu.menuTypeButton') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" :label="$t('menu.sort')" width="80" />
        <el-table-column prop="perms" :label="$t('menu.perms')" min-width="120" />
        <el-table-column prop="path" :label="$t('menu.path')" min-width="150" />
        <el-table-column prop="component" :label="$t('menu.component')" min-width="150" />
        <el-table-column prop="status" :label="$t('menu.status')" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? $t('user.statusNormal') : $t('user.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('common.operation')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-dropdown @command="(command) => handleCommandAction(command, row)">
              <el-button size="small" type="primary" style="margin-left: 8px;">
                {{ $t('common.more') }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="add" v-if="row.menuType !== 'F'">{{ $t('menu.addSubMenu') }}</el-dropdown-item>
                  <el-dropdown-item command="delete" divided style="color: #f56c6c;">{{ $t('common.delete') }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" :label-width="$t('common.labelWidth')">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('menu.parentMenu')">
              <el-tree-select
                v-model="form.parentId"
                :data="menuTree"
                :props="{ label: 'menuName', value: 'id' }"
                :placeholder="$t('menu.selectParentMenu')"
                check-strictly
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.menuType')" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">{{ $t('menu.menuTypeDirectory') }}</el-radio>
                <el-radio label="C">{{ $t('menu.menuTypeMenu') }}</el-radio>
                <el-radio label="F">{{ $t('menu.menuTypeButton') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('menu.menuName')" prop="menuName">
              <el-input v-model="form.menuName" :placeholder="$t('menu.menuName')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.menuNameEn')">
              <el-input v-model="form.menuNameEn" :placeholder="$t('menu.menuNameEn')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('menu.sort')" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'F'">
          <el-col :span="12">
            <el-form-item :label="$t('menu.path')" prop="path">
              <el-input v-model="form.path" :placeholder="$t('menu.path')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.component')" prop="component">
              <el-input v-model="form.component" :placeholder="$t('menu.component')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType === 'C'">
          <el-col :span="12">
            <el-form-item :label="$t('menu.isFrame')">
              <el-radio-group v-model="form.isFrame">
                <el-radio label="0">{{ $t('common.no') }}</el-radio>
                <el-radio label="1">{{ $t('common.yes') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.isCache')">
              <el-radio-group v-model="form.isCache">
                <el-radio label="0">{{ $t('menu.cache') }}</el-radio>
                <el-radio label="1">{{ $t('menu.noCache') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('menu.icon')" prop="icon">
              <el-input v-model="form.icon" :placeholder="$t('menu.icon')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.perms')" prop="perms">
              <el-input v-model="form.perms" :placeholder="$t('menu.perms')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('menu.visible')">
              <el-radio-group v-model="form.visible">
                <el-radio label="0">{{ $t('menu.show') }}</el-radio>
                <el-radio label="1">{{ $t('menu.hide') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('menu.status')" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">{{ $t('common.normal') }}</el-radio>
                <el-radio label="1">{{ $t('common.disabled') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi } from '@/api/system'
import { ArrowDown } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const queryForm = reactive({
  menuName: '',
  status: ''
})

const tableData = ref([])
const loading = ref(false)
const menuTree = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuNameEn: '',
  menuType: 'M',
  orderNum: 0,
  path: '',
  component: '',
  isFrame: '0',
  isCache: '0',
  icon: '',
  perms: '',
  visible: '0',
  status: '0'
})

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  orderNum: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择菜单状态', trigger: 'change' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await menuApi.tree(queryForm)
    if (res.code === 200) {
      tableData.value = res.data
      menuTree.value = res.data
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.menuName = ''
  queryForm.status = ''
  handleQuery()
}

const handleAdd = (row) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, {
    id: null,
    parentId: row ? row.id : 0,
    menuName: '',
    menuNameEn: '',
    menuType: row && row.menuType === 'M' ? 'C' : 'M',
    orderNum: 0,
    path: '',
    component: '',
    isFrame: '0',
    isCache: '0',
    icon: '',
    perms: '',
    visible: '0',
    status: '0'
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑菜单'
  try {
    const res = await menuApi.getById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取菜单信息失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = form.id ? await menuApi.update(form) : await menuApi.add(form)
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
  ElMessageBox.confirm('确认删除该菜单吗？删除后该菜单下的子菜单也会被删除。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await menuApi.delete(row.id)
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
    case 'add':
      handleAdd(row)
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
.menu-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>