<template>
  <div class="us-sentiment">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">{{ $t('sentiment.sentimentMonitoring') }}</span>
            <div class="condition-toolbar">
              <el-tag type="info" size="small" class="condition-tag">{{ $t('sentiment.conditionVix') }}</el-tag>
              <el-tag type="warning" size="small" class="condition-tag">{{ $t('sentiment.conditionFearGreed') }}</el-tag>
              <el-tag type="danger" size="small" class="condition-tag">{{ $t('sentiment.conditionNaaim') }}</el-tag>
              <el-tag type="primary" size="small" class="condition-tag">{{ $t('sentiment.conditionRsi') }}</el-tag>
              <el-tag type="success" size="small" class="condition-note">{{ $t('sentiment.conditionNote') }}</el-tag>
            </div>
          </div>
          <el-button type="primary" @click="handleCollect" :loading="collectLoading">{{ $t('sentiment.manualCollect') }}</el-button>
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
                <div class="stat-label">{{ $t('sentiment.vixIndex') }}</div>
                <div class="stat-value">{{ latestData.vix || '--' }}</div>
                <div class="stat-desc">{{ $t('sentiment.fearIndex') }}</div>
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
                <div class="stat-label">{{ $t('sentiment.fearGreedIndex') }}</div>
                <div class="stat-value">{{ latestData.fearGreed || '--' }}</div>
                <div class="stat-desc">{{ $t('sentiment.fearGreedDesc') }}</div>
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
                <div class="stat-label">{{ $t('sentiment.naaimIndex') }}</div>
                <div class="stat-value">{{ latestData.naaim || '--' }}</div>
                <div class="stat-desc">{{ $t('sentiment.naaimDesc') }}</div>
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
                <div class="stat-label">{{ $t('sentiment.rsiIndex') }}</div>
                <div class="stat-value">{{ latestData.rsiSp500 || '--' }}</div>
                <div class="stat-desc">{{ $t('sentiment.rsiDesc') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline style="margin-bottom: 20px;">
        <el-form-item :label="$t('sentiment.dateRange')" :label-width="isEnglish ? '100px' : 'auto'">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            :range-separator="$t('sentiment.to')"
            :start-placeholder="$t('sentiment.startDate')"
            :end-placeholder="$t('sentiment.endDate')"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item :label="$t('sentiment.satisfiedConditions')" :label-width="isEnglish ? '150px' : 'auto'">
          <el-select v-model="queryForm.satisfiedCount" :placeholder="$t('common.query')" clearable style="width: 150px">
            <el-option :label="$t('common.all')" value="" />
            <el-option :label="$t('sentiment.zeroCondition')" value="0" />
            <el-option :label="$t('sentiment.oneCondition')" value="1" />
            <el-option :label="$t('sentiment.twoConditions')" value="2" />
            <el-option :label="$t('sentiment.threeConditions')" value="3" />
            <el-option :label="$t('sentiment.fourConditions')" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('sentiment.notificationStatus')">
          <el-select v-model="queryForm.notificationSent" :placeholder="$t('common.query')" clearable style="width: 150px">
            <el-option :label="$t('common.all')" value="" />
            <el-option :label="$t('sentiment.sent')" value="1" />
            <el-option :label="$t('sentiment.notSent')" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

<!-- 数据表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="recordDate" :label="$t('sentiment.date')" width="120" />
        <el-table-column :label="$t('sentiment.conditionVix')" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.vixCondition === '1' ? 'satisfied' : '']">
              {{ row.vix || '--' }}
              <el-tag v-if="row.vixCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sentiment.conditionFearGreed')" width="140">
          <template #default="{ row }">
            <div :class="['condition-cell', row.fearGreedCondition === '1' ? 'satisfied' : '']">
              {{ row.fearGreed || '--' }}
              <el-tag v-if="row.fearGreedCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sentiment.conditionNaaim')" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.naaimCondition === '1' ? 'satisfied' : '']">
              {{ row.naaim || '--' }}
              <el-tag v-if="row.naaimCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sentiment.conditionRsi')" width="120">
          <template #default="{ row }">
            <div :class="['condition-cell', row.rsiCondition === '1' ? 'satisfied' : '']">
              {{ row.rsiSp500 || '--' }}
              <el-tag v-if="row.rsiCondition === '1'" type="success" size="small" style="margin-left: 5px">✓</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sentiment.satisfiedConditions')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.satisfiedCount >= 3 ? 'danger' : row.satisfiedCount > 0 ? 'warning' : 'info'">
              {{ row.satisfiedCount || 0 }}/4
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sentiment.notificationStatus')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.notificationSent === '1' ? 'success' : 'info'">
              {{ row.notificationSent === '1' ? $t('sentiment.sent') : $t('sentiment.notSent') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('sentiment.createTime')" width="180" />
        <el-table-column :label="$t('common.operation')" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { TrendCharts, Warning, Coin, DataAnalysis } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import request from '@/utils/request'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

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
    ElMessage.error(t('common.queryFailed'))
  } finally {
    loading.value = false
  }
}

const handleCollect = async () => {
  collectLoading.value = true
  try {
    const res = await request.post('/toolbox/us-sentiment/collect')
    if (res.code === 200) {
      ElMessage.success(t('sentiment.collectSuccess'))
      handleQuery()
    } else {
      ElMessage.error(res.message || t('sentiment.collectFailed'))
    }
  } catch (error) {
    ElMessage.error(t('sentiment.collectFailed'))
  } finally {
    collectLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    t('sentiment.confirmDelete'),
    t('common.tip'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete(`/toolbox/us-sentiment/${row.id}`)
      if (res.code === 200) {
        ElMessage.success(t('common.deleteSuccess'))
        handleQuery()
      }
    } catch (error) {
      ElMessage.error(t('common.deleteFailed'))
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