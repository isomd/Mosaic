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
<style scoped lang="scss">
// 沙漠主题配色变量
$desert-sand: #F4E4BC;
$desert-dune: #E6D3A3;
$oasis-green: #4A9B8E;
$oasis-blue: #6BB6B0;
$palm-green: #2D5A27;
$sunset-orange: #D4A574;
$clear-white: #FEFEFE;
$shadow-brown: #8B7355;
$warm-gold: #E6C068;
$deep-sand: #D4C4A8;

// 强制覆盖 Vuetify 工具栏样式
.plugin-header-toolbar {
  background: linear-gradient(135deg, $sunset-orange 0%, $warm-gold 50%, $desert-dune 100%) !important;
  border-bottom: 4px solid $oasis-green !important;
  position: relative;
  overflow: hidden;

  // 添加纹理效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
        radial-gradient(circle at 20% 20%, rgba(255,255,255,0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(255,255,255,0.05) 0%, transparent 50%);
    pointer-events: none;
  }

  // 覆盖 Vuetify 的默认样式
  :deep(.v-toolbar__content) {
    padding: 0 !important;
    height: 100% !important;
  }
}

// 头部内容布局
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 1rem 1.5rem;
  position: relative;
  z-index: 2;
}

// 标题区域样式
.title-section {
  flex: 1;

  .title-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .plugin-main-title {
    color: $shadow-brown !important;
    font-weight: 700 !important;
    font-size: 1.75rem !important;
    margin: 0 !important;
    line-height: 1.2 !important;
    text-shadow: 0 1px 2px rgba(255,255,255,0.3);
    letter-spacing: -0.02em;
  }

  .title-meta {
    display: flex;
    align-items: center;
    gap: 0.5rem;

    .id-icon {
      color: $palm-green !important;
      opacity: 0.8;
    }

    .plugin-id-text {
      color: $palm-green !important;
      font-size: 0.9rem !important;
      font-weight: 600 !important;
      background: rgba(255,255,255,0.2);
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      border: 1px solid rgba(45, 90, 39, 0.2);
    }
  }
}

// 状态区域样式
.status-section {
  .status-wrapper {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 0.5rem;

    .status-label {
      color: $shadow-brown !important;
      font-size: 0.8rem !important;
      font-weight: 500 !important;
      opacity: 0.8;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }
}

// 增强的状态芯片样式
.status-chip-enhanced {
  font-weight: 700 !important;
  letter-spacing: 0.3px !important;
  border-radius: 20px !important;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;

  &:hover {
    transform: translateY(-1px) !important;
    box-shadow: 0 6px 16px rgba(0,0,0,0.2) !important;
  }

  // 强制覆盖 Vuetify 芯片样式
  &.status-active {
    background: linear-gradient(45deg, $oasis-green, $oasis-blue) !important;
    color: $clear-white !important;
    border: 2px solid rgba(255,255,255,0.3) !important;

    :deep(.v-icon) {
      color: $clear-white !important;
    }
  }

  &.status-inactive {
    background: linear-gradient(45deg, $shadow-brown, #A0896B) !important;
    color: $clear-white !important;
    border: 2px solid rgba(255,255,255,0.2) !important;

    :deep(.v-icon) {
      color: $clear-white !important;
    }
  }

  &.status-pending {
    background: linear-gradient(45deg, $sunset-orange, $warm-gold) !important;
    color: $shadow-brown !important;
    border: 2px solid rgba(139, 115, 85, 0.3) !important;

    :deep(.v-icon) {
      color: $shadow-brown !important;
    }
  }

  // 覆盖 Vuetify 芯片内部样式
  :deep(.v-chip__content) {
    font-weight: 700 !important;
    font-size: 0.9rem !important;
  }

  :deep(.v-chip__prepend) {
    margin-right: 0.5rem !important;
  }
}

// 内容区域样式优化
.plugin-content {
  background: linear-gradient(180deg, $clear-white 0%, $desert-sand 100%) !important;
  min-height: 200px;
  padding: 1.5rem !important;
}

.plugin-info-grid {
  margin-top: 1.5rem;
}

// 信息卡片样式增强
.info-card {
  background: $clear-white !important;
  border: 1px solid rgba(139, 115, 85, 0.12) !important;
  border-radius: 16px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  overflow: hidden;

  &:hover {
    transform: translateY(-3px) !important;
    box-shadow: 0 12px 28px rgba(139, 115, 85, 0.18) !important;
  }

  &.version-card {
    border-left: 5px solid $oasis-green !important;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 3px;
      background: linear-gradient(90deg, $oasis-green, $oasis-blue);
    }
  }

  &.time-card {
    border-left: 5px solid $sunset-orange !important;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 3px;
      background: linear-gradient(90deg, $sunset-orange, $warm-gold);
    }
  }
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid rgba(139, 115, 85, 0.08);
  position: relative;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  .info-label {
    color: $shadow-brown !important;
    font-weight: 600 !important;
    font-size: 0.95rem !important;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      width: 20px;
      height: 2px;
      background: $oasis-green;
      border-radius: 1px;
    }
  }

  .info-value {
    color: $palm-green !important;
    font-weight: 700 !important;
    font-size: 1rem !important;
    text-align: right;
    max-width: 65%;
    word-break: break-word;
    background: rgba(74, 155, 142, 0.05);
    padding: 0.5rem 0.75rem;
    border-radius: 8px;
    border: 1px solid rgba(74, 155, 142, 0.1);
  }
}

// 描述卡片样式增强
.description-card {
  background: linear-gradient(135deg, $desert-sand 0%, $desert-dune 100%) !important;
  border: 2px solid rgba(74, 155, 142, 0.15) !important;
  border-radius: 16px !important;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, $oasis-green, $oasis-blue, $sunset-orange);
  }
}

.description-content {
  display: flex;
  align-items: flex-start;
  gap: 1.25rem;
  padding-top: 0.5rem;

  .description-icon {
    color: $oasis-green !important;
    font-size: 1.75rem !important;
    margin-top: 0.125rem;
    flex-shrink: 0;
    background: rgba(74, 155, 142, 0.1);
    padding: 0.5rem;
    border-radius: 50%;
  }

  .description-text {
    color: $shadow-brown !important;
    font-size: 1.05rem !important;
    line-height: 1.7 !important;
    flex: 1;
    font-weight: 500;
  }
}

// 响应式设计增强
@media (max-width: 960px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
    padding: 1rem;
  }

  .title-section {
    width: 100%;

    .plugin-main-title {
      font-size: 1.5rem !important;
    }
  }

  .status-section {
    width: 100%;

    .status-wrapper {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  .plugin-info-grid .v-col {
    margin-bottom: 1.5rem;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;

    .info-value {
      text-align: left;
      max-width: 100%;
      width: 100%;
    }
  }
}

@media (max-width: 600px) {
  .plugin-header-toolbar {
    height: auto !important;
    min-height: 100px !important;
  }

  .header-content {
    padding: 0.75rem;
  }

  .plugin-main-title {
    font-size: 1.25rem !important;
  }

  .description-content {
    flex-direction: column;
    gap: 1rem;

    .description-icon {
      align-self: flex-start;
    }
  }
}
</style>
