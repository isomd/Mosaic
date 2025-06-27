<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";
import {type Cube, statisticsItemName} from "@/api/plugin/pluginType";

const emit = defineEmits(['update:modelValue'])
const props = defineProps({
  value: Boolean,
  pluginData: {

  } as Cube
})

const dialog = computed({
  get() { return props.value },
  set(val) { emit('update:modelValue', val) }
})

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}
const formatConfigValue = (value) => {
  if (value === null) return 'null'
  if (Array.isArray(value)) return value.join(', ') || '空数组'
  if (typeof value === 'object') return JSON.stringify(value)
  return value.toString()
}

// 添加到 script 部分
const getStatusText = (status: string) => {
  const statusTextMap = {
    'active': '运行中',
    'running': '运行中',
    'enabled': '已启用',
    'inactive': '已停止',
    'stopped': '已停止',
    'disabled': '已禁用',
    'pending': '等待中',
    'loading': '加载中'
  }
  return statusTextMap[status?.toLowerCase()] || status || '未知'
}

const getStatusClass = (status: string) => {
  const statusMap = {
    'active': 'status-active',
    'running': 'status-active',
    'enabled': 'status-active',
    'inactive': 'status-inactive',
    'stopped': 'status-inactive',
    'disabled': 'status-inactive',
    'pending': 'status-pending',
    'loading': 'status-pending'
  }
  return statusMap[status?.toLowerCase()] || 'status-pending'
}
const isDark = ref(true)
</script>
<template>

  <v-dialog v-model="dialog" max-width="1200">

    <v-card>

      <v-toolbar
          class="plugin-header-toolbar"
          height="90"
          flat
          elevation="0"
      >
        <v-container fluid class="pa-0">
          <div class="header-content">
            <div class="title-section">
              <div class="title-wrapper">
                <h1 class="plugin-main-title">{{ pluginData.name }}</h1>
                <div class="title-meta">
                  <v-icon class="id-icon" size="small">mdi-identifier</v-icon>
                  <span class="plugin-id-text">{{ pluginData.id }}</span>
                </div>
              </div>
            </div>

            <div class="status-section">
              <div class="status-wrapper">
                <v-chip
                    :class="getStatusClass(pluginData.status)"
                    class="status-chip-enhanced"
                    size="large"
                    variant="elevated"
                    prepend-icon="mdi-circle"
                >
                  {{ getStatusText(pluginData.status) }}
                </v-chip>
              </div>
            </div>
          </div>
        </v-container>
      </v-toolbar>

      <v-card-text class="plugin-content">
        <v-row class="plugin-info-grid" no-gutters>
          <v-col cols="12" md="6" class="pr-md-3">
            <v-card class="info-card version-card" elevation="2">
              <v-card-text class="pa-4">
                <div class="info-item">
                  <span class="info-label">版本</span>
                  <span class="info-value">{{ pluginData.version }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">模型类型</span>
                  <span class="info-value">{{ pluginData.model }}</span>
                </div>
              </v-card-text>
            </v-card>
          </v-col>

          <v-col cols="12" md="6" class="pl-md-3">
            <v-card class="info-card time-card" elevation="2">
              <v-card-text class="pa-4">
                <div class="info-item">
                  <span class="info-label">注册时间</span>
                  <span class="info-value">{{ formatDate(pluginData.registeredTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">更新时间</span>
                  <span class="info-value">{{ formatDate(pluginData.lastUpdatedTime) }}</span>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>

        <v-card class="description-card mt-4" elevation="1">
          <v-card-text class="pa-4">
            <div class="description-content">
              <v-icon class="description-icon">mdi-information-outline</v-icon>
              <span class="description-text">
          {{ pluginData.description || '暂无描述信息' }}
        </span>
            </div>
          </v-card-text>
        </v-card>

        <ExtensionPackageDisplay
            :extension-packages="pluginData.extensionPackages"
            class="mt-4"
        />

        <ConfigDisplay :config="pluginData.config" />

        <PluginStatisticsChart
            :statistics="pluginData.statistics"
            :theme="isDark ? 'dark' : 'light'"
            :animated="true"
            :show-details="true"
        />

      </v-card-text>

    </v-card>
  </v-dialog>
</template>
