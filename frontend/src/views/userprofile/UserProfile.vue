<template>
  <div class="user-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('userProfile.userProfile') }}</span>
          <div>
            <el-button type="primary" @click="handleGenerate" :loading="generating">{{ $t('userProfile.generateProfile') }}</el-button>
          </div>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('userProfile.user')">
          <el-select v-model="queryForm.userId" :placeholder="$t('userProfile.selectUser')" clearable style="width: 200px">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.nickname || user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('userProfile.date')">
          <el-date-picker
            v-model="queryForm.date"
            type="date"
            :placeholder="$t('userProfile.selectDate')"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <!-- 画像展示区域 -->
      <div v-if="profile" class="profile-content">
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">{{ $t('userProfile.softwareStartCount') }}</div>
                <div class="stat-value">{{ profile.softwareStartCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">{{ $t('userProfile.fileOperationCount') }}</div>
                <div class="stat-value">{{ profile.fileOperationCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">{{ $t('userProfile.browserVisitCount') }}</div>
                <div class="stat-value">{{ profile.browserVisitCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">{{ $t('userProfile.totalUsageDuration') }}</div>
                <div class="stat-value">{{ formatDuration(profile.totalUsageDuration) }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 基础属性画像 -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <el-icon><User /></el-icon>
              <span>{{ $t('userProfile.basicAttributes') }}</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.activityLevel') }}</div>
                <el-tag :type="getActivityTagType(profile.activityLevel)" size="large">
                  {{ getActivityLabel(profile.activityLevel) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.usagePeriod') }}</div>
                <el-tag type="primary" size="large">
                  {{ getPeriodLabel(profile.usagePeriod) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.deviceDependency') }}</div>
                <el-tag :type="getDependencyTagType(profile.deviceDependency)" size="large">
                  {{ getDependencyLabel(profile.deviceDependency) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.workRhythm') }}</div>
                <el-tag type="info" size="large">{{ profile.workRhythm }}</el-tag>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 软件使用画像 -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <el-icon><Monitor /></el-icon>
              <span>{{ $t('userProfile.softwareUsageProfile') }}</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.softwareDiversity') }}</div>
                <div class="profile-value">{{ profile.softwareDiversity }} {{ $t('userProfile.types') }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.professionalLevel') }}</div>
                <el-tag :type="getProfessionalTagType(profile.professionalLevel)" size="large">
                  {{ getProfessionalLabel(profile.professionalLevel) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.browserPreference') }}</div>
                <el-tag type="warning" size="large">{{ profile.browserPreference }}</el-tag>
              </div>
            </el-col>
          </el-row>

          <el-divider />

          <div class="profile-item">
            <div class="profile-label">{{ $t('userProfile.softwarePreferenceTop5') }}</div>
            <div class="software-list" v-if="softwarePreferenceList.length > 0">
              <div v-for="(item, index) in softwarePreferenceList" :key="index" class="software-item">
                <span class="software-rank">{{ index + 1 }}</span>
                <span class="software-name">{{ item.name }}</span>
                <span class="software-count">{{ item.count }} {{ $t('userProfile.times') }}</span>
                <span class="software-duration">{{ formatDuration(item.duration) }}</span>
              </div>
            </div>
            <div v-else class="empty-text">{{ $t('common.noData') }}</div>
          </div>
        </el-card>

        <!-- 网络行为画像 -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <el-icon><Connection /></el-icon>
              <span>{{ $t('userProfile.networkBehaviorProfile') }}</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.onlineDuration') }}</div>
                <div class="profile-value">{{ formatDuration(profile.onlineDuration) }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="profile-item">
                <div class="profile-label">{{ $t('userProfile.avgVisitDuration') }}</div>
                <div class="profile-value">
                  {{ profile.browserVisitCount > 0 ? formatDuration(profile.onlineDuration / profile.browserVisitCount) : '0' }}
                </div>
              </div>
            </el-col>
          </el-row>

          <el-divider />

          <div class="profile-item">
            <div class="profile-label">{{ $t('userProfile.visitCategoryTop5') }}</div>
            <div class="category-list" v-if="visitCategoryList.length > 0">
              <el-progress
                v-for="(item, index) in visitCategoryList"
                :key="index"
                :percentage="getCategoryPercentage(item.count)"
                :color="getCategoryColor(index)"
              >
                <template #default="{ percentage }">
                  <span class="category-name">{{ item.category }}</span>
                  <span class="category-count">{{ item.count }} {{ $t('userProfile.times') }} ({{ percentage }}%)</span>
                </template>
              </el-progress>
            </div>
            <div v-else class="empty-text">{{ $t('common.noData') }}</div>
          </div>
        </el-card>
      </div>

      <el-empty v-else-if="!loading" :description="$t('userProfile.selectUserAndDateQuery')" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Monitor, Connection } from '@element-plus/icons-vue'
import { userProfileApi } from '@/api/userprofile'
import { userApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const loading = ref(false)
const generating = ref(false)
const profile = ref(null)
const userList = ref([])

const queryForm = reactive({
  userId: null,
  date: new Date().toISOString().split('T')[0]
})

// 解析软件偏好JSON
const softwarePreferenceList = computed(() => {
  if (!profile.value?.softwarePreference) return []
  try {
    return JSON.parse(profile.value.softwarePreference)
  } catch (e) {
    return []
  }
})

// 解析访问类别JSON
const visitCategoryList = computed(() => {
  if (!profile.value?.visitCategory) return []
  try {
    return JSON.parse(profile.value.visitCategory)
  } catch (e) {
    return []
  }
})

// 获取类别百分比
const getCategoryPercentage = (count) => {
  const total = visitCategoryList.value.reduce((sum, item) => sum + item.count, 0)
  return total > 0 ? Math.round((count / total) * 100) : 0
}

// 获取类别颜色
const getCategoryColor = (index) => {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  return colors[index % colors.length]
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '0'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  if (hours > 0) {
    return `${hours}${t('userProfile.hours')}${minutes}${t('userProfile.minutes')}`
  }
  return `${minutes}${t('userProfile.minutes')}`
}

// 获取活跃度标签
const getActivityLabel = (level) => {
  const labels = {
    high: t('userProfile.highActivity'),
    medium: t('userProfile.mediumActivity'),
    low: t('userProfile.lowActivity')
  }
  return labels[level] || t('userProfile.unknown')
}

const getActivityTagType = (level) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[level] || ''
}

// 获取使用时段标签
const getPeriodLabel = (period) => {
  const labels = {
    morning: t('userProfile.morning'),
    afternoon: t('userProfile.afternoon'),
    evening: t('userProfile.evening')
  }
  return labels[period] || t('userProfile.unknown')
}

// 获取设备依赖度标签
const getDependencyLabel = (level) => {
  const labels = {
    high: t('userProfile.highDependency'),
    medium: t('userProfile.mediumDependency'),
    low: t('userProfile.lowDependency')
  }
  return labels[level] || t('userProfile.unknown')
}

const getDependencyTagType = (level) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'success'
  }
  return types[level] || ''
}

// 获取专业程度标签
const getProfessionalLabel = (level) => {
  const labels = {
    high: t('userProfile.professional'),
    medium: t('userProfile.intermediate'),
    low: t('userProfile.beginner')
  }
  return labels[level] || t('userProfile.unknown')
}

const getProfessionalTagType = (level) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[level] || ''
}

// 获取用户列表
const getUserList = async () => {
  try {
    const res = await userApi.list({ current: 1, size: 1000 })
    if (res.code === 200) {
      userList.value = res.data.records
    }
  } catch (error) {
    console.error(t('userProfile.getUserListFailed'), error)
  }
}

// 查询画像
const handleQuery = async () => {
  if (!queryForm.userId || !queryForm.date) {
    ElMessage.warning(t('userProfile.selectUserAndDate'))
    return
  }

  loading.value = true
  try {
    const res = await userProfileApi.getByDate(queryForm.userId, queryForm.date)
    if (res.code === 200) {
      profile.value = res.data
      if (!profile.value) {
        ElMessage.info(t('userProfile.noProfileData'))
      }
    }
  } catch (error) {
    console.error(t('userProfile.queryProfileFailed'), error)
    ElMessage.error(t('userProfile.queryProfileFailed'))
  } finally {
    loading.value = false
  }
}

// 生成画像
const handleGenerate = async () => {
  if (!queryForm.userId || !queryForm.date) {
    ElMessage.warning(t('userProfile.selectUserAndDate'))
    return
  }

  generating.value = true
  try {
    const res = await userProfileApi.generate(queryForm.userId, queryForm.date)
    if (res.code === 200) {
      ElMessage.success(t('userProfile.profileGenerated'))
      handleQuery()
    }
  } catch (error) {
    console.error(t('userProfile.generateProfileFailed'), error)
    ElMessage.error(t('userProfile.generateProfileFailed'))
  } finally {
    generating.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.userId = null
  queryForm.date = new Date().toISOString().split('T')[0]
  profile.value = null
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-item {
  text-align: center;
  color: white;
}

.stat-label {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.section-card {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
}

.profile-item {
  margin-bottom: 20px;
}

.profile-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.profile-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.software-list {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.software-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e4e7ed;
}

.software-item:last-child {
  border-bottom: none;
}

.software-rank {
  width: 24px;
  height: 24px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 10px;
}

.software-name {
  flex: 1;
  font-weight: 500;
}

.software-count {
  margin-left: 20px;
  color: #909399;
  font-size: 13px;
}

.software-duration {
  margin-left: 20px;
  color: #67c23a;
  font-size: 13px;
}

.category-list {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.category-list :deep(.el-progress) {
  margin-bottom: 15px;
}

.category-list :deep(.el-progress:last-child) {
  margin-bottom: 0;
}

.category-name {
  font-weight: 500;
  margin-right: 10px;
}

.category-count {
  color: #909399;
  font-size: 13px;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>