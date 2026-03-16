<template>
  <div class="us-sentiment">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">美股情绪指标监控</span>
            <div class="condition-toolbar">
              <el-tag type="info" size="small" class="condition-tag">VIX > 30</el-tag>
              <el-tag type="warning" size="small" class="condition-tag">Fear & Greed < 20</el-tag>
              <el-tag type="danger" size="small" class="condition-tag">NAAIM < 40</el-tag>
              <el-tag type="primary" size="small" class="condition-tag">RSI < 30</el-tag>
              <el-tag type="success" size="small" class="condition-note">满足3-4个条件触发预警</el-tag>
            </div>
          </div>
          <el-button type="primary" @click="handleCollect" :loading="collectLoading">手动收集数据</el-button>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon vix">
                <el-icon :size="30"><TrendCharts /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">VIX指数</div>
                <div class="stat-value">{{ latestData.vix || '--' }}</div>
                <div class="stat-desc">恐慌指数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon fear-greed">
                <el-icon :size="30"><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">Fear & Greed</div>
                <div class="stat-value">{{ latestData.fearGreed || '--' }}</div>
                <div class="stat-desc">恐惧贪婪指数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon naaim">
                <el-icon :size="30"><Coin /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">NAAIM</div>
                <div class="stat-value">{{ latestData.naaim || '--' }}</div>
                <div class="stat-desc">机构仓位指数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon rsi">
                <el-icon :size="30"><DataAnalysis /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">RSI (标普500)</div>
                <div class="stat-value">{{ latestData.rsiSp500 || '--' }}</div>
                <div class="stat-desc">相对强弱指数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline style="margin-bottom: 20px;">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="满足条件">
          <el-select v-model="queryForm.satisfiedCount" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="0个条件" value="0" />
            <el-option label="1个条件" value="1" />
            <el-option label="2个条件" value="2" />
            <el-option label="3个条件" value="3" />
            <el-option label="4个条件" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryForm.notificationSent" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="已发送" value="1" />
            <el-option label="未发送" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

<!-- 数据表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column label="VIX > 30" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.vixCondition === '1' ? 'satisfied' : '']">
              {{ row.vix || '--' }}
              <el-tag v-if="row.vixCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Fear & Greed < 20" width="140">
          <template #default="{ row }">
            <div :class="['condition-cell', row.fearGreedCondition === '1' ? 'satisfied' : '']">
              {{ row.fearGreed || '--' }}
              <el-tag v-if="row.fearGreedCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="NAAIM < 40" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.naaimCondition === '1' ? 'satisfied' : '']">
              {{ row.naaim || '--' }}
              <el-tag v-if="row.naaimCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="RSI < 30" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.rsiCondition === '1' ? 'satisfied' : '']">
              {{ row.rsiSp500 || '--' }}
              <el-tag v-if="row.rsiCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="满足条件" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.satisfiedCount >= 3 ? 'danger' : row.satisfiedCount > 0 ? 'warning' : 'info'">
              {{ row.satisfiedCount || 0 }}/4
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.notificationSent === '1' ? 'success' : 'info'">
              {{ row.notificationSent === '1' ? '已发送' : '未发送' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { TrendCharts, Warning, Coin, DataAnalysis } from '@element-plus/icons-vue'
import request from '@/utils/request'

const queryForm = reactive({
  current: 1,
  size: 10,
  startDate: '',
  endDate: '',
  satisfiedCount: '',
  notificationSent: ''
})

const dateRange = ref([])

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const collectLoading = ref(false)
const latestData = ref({})

const handleDateChange = (value) => {
  if (value && value.length === 2) {
    queryForm.startDate = value[0]
    queryForm.endDate = value[1]
  } else {
    queryForm.startDate = ''
    queryForm.endDate = ''
  }
}

const handleReset = () => {
  dateRange.value = []
  queryForm.startDate = ''
  queryForm.endDate = ''
  queryForm.satisfiedCount = ''
  queryForm.notificationSent = ''
  queryForm.current = 1
  handleQuery()
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await request.get('/toolbox/us-sentiment/list', {
      params: queryForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
      if (tableData.value.length > 0) {
        latestData.value = tableData.value[0]
      }
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleCollect = async () => {
  collectLoading.value = true
  try {
    const res = await request.post('/toolbox/us-sentiment/collect')
    if (res.code === 200) {
      ElMessage.success('数据收集成功')
      handleQuery()
    } else {
      ElMessage.error(res.message || '数据收集失败')
    }
  } catch (error) {
    ElMessage.error('数据收集失败')
  } finally {
    collectLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该条数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/toolbox/us-sentiment/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.us-sentiment {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.condition-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.condition-tag {
  font-size: 12px;
  font-weight: 500;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
}

.stat-icon.vix {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.fear-greed {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.naaim {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rsi {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
}

.condition-cell {
  display: flex;
  align-items: center;
}

.condition-cell.satisfied {
  color: #67c23a;
  font-weight: bold;
}
</style>