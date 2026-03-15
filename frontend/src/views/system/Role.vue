<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <div>
            <el-button type="success" @click="handleExport">导出</el-button>
            <el-button type="primary" @click="handleAdd">新增</el-button>
          </div>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryForm.roleKey" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleKey" label="角色编码" />
        <el-table-column prop="roleSort" label="显示顺序" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="(command) => handleCommandAction(command, row)">
              <el-button size="small" type="primary" style="margin-left: 8px;">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="authMenu">菜单授权</el-dropdown-item>
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
        style="margin-top: 20px"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜单授权对话框 -->
    <el-dialog v-model="authMenuVisible" title="菜单授权" width="500px">
      <el-form label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="authMenuForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="{ label: 'menuName', children: 'children' }"
            show-checkbox
            node-key="id"
            :check-strictly="false"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="authMenuVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuthMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleApi, menuApi } from '@/api/system'
import { ArrowDown } from '@element-plus/icons-vue'

const queryForm = reactive({
  roleName: '',
  roleKey: '',
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
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: '0',
  remark: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleSort: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }]
}

const authMenuVisible = ref(false)
const authMenuForm = reactive({
  roleId: null,
  roleName: ''
})
const menuTreeRef = ref(null)
const menuTree = ref([])

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await roleApi.list(queryForm)
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
  queryForm.roleName = ''
  queryForm.roleKey = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, {
    id: null,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: '0',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑角色'
  try {
    const res = await roleApi.getById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = form.id ? await roleApi.update(form) : await roleApi.add(form)
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
  ElMessageBox.confirm('确认删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await roleApi.delete([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleAuthMenu = async (row) => {
  Object.assign(authMenuForm, {
    roleId: row.id,
    roleName: row.roleName
  })
  authMenuVisible.value = true
  
  try {
    const res = await roleApi.getMenuIds(row.id)
    if (res.code === 200) {
      // 使用 nextTick 确保对话框渲染完成后再设置选中状态
      await nextTick()
      if (menuTreeRef.value) {
        // 获取所有菜单节点的ID，用于识别父子关系
        const allMenuIds = getAllMenuIds(menuTree.value)
        // 过滤出非叶子节点（有children的节点）
        const parentIds = menuTree.value.reduce((acc, node) => {
          if (node.children && node.children.length > 0) {
            acc.push(node.id)
          }
          return acc
        }, [])
        // 只设置叶子节点的选中状态
        const leafMenuIds = (res.data || []).filter(id => !parentIds.includes(id))
        menuTreeRef.value.setCheckedKeys(leafMenuIds)
      }
    }
  } catch (error) {
    ElMessage.error('获取角色菜单失败')
  }
}

// 递归获取所有菜单ID
const getAllMenuIds = (menus) => {
  const ids = []
  menus.forEach(menu => {
    ids.push(menu.id)
    if (menu.children && menu.children.length > 0) {
      ids.push(...getAllMenuIds(menu.children))
    }
  })
  return ids
}

const handleAuthMenuSubmit = async () => {
  if (!menuTreeRef.value) return
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  const menuIds = [...checkedKeys, ...halfCheckedKeys]
  try {
    const res = await roleApi.authMenu(authMenuForm.roleId, menuIds)
    if (res.code === 200) {
      ElMessage.success('菜单授权成功')
      authMenuVisible.value = false
    }
  } catch (error) {
    ElMessage.error('菜单授权失败')
  }
}

const handleExport = async () => {
  try {
    const res = await roleApi.export(queryForm)
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `角色列表_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleCommandAction = (command, row) => {
  switch (command) {
    case 'authMenu':
      handleAuthMenu(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const loadMenuTree = async () => {
  try {
    const res = await menuApi.tree({})
    if (res.code === 200) {
      menuTree.value = res.data
    }
  } catch (error) {
    console.error('加载菜单树失败', error)
  }
}

onMounted(() => {
  handleQuery()
  loadMenuTree()
})
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>