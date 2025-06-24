<template>
  <v-card class="config-display-card" elevation="2">
    <!-- 优化的标题区域 -->
    <v-card-title class="config-header">
      <div class="header-content">
        <div class="icon-wrapper">
          <v-icon size="24" color="white">mdi-cog</v-icon>
        </div>
        <span class="header-text">配置信息</span>
      </div>
      <v-spacer></v-spacer>
      <v-chip
          v-if="hasConfig"
          small
          color="rgba(255,255,255,0.2)"
          text-color="white"
          class="config-count-chip"
      >
        {{ isStructuredConfig ? structuredConfigItems.length : Object.keys(simpleConfig).length }} 项配置
      </v-chip>
    </v-card-title>

    <v-card-text class="config-content">
      <!-- 无配置信息 - 优化版 -->
      <div v-if="!hasConfig" class="empty-state">
        <div class="empty-icon">
          <v-icon size="48" color="#E0E0E0">mdi-cog-off-outline</v-icon>
        </div>
        <p class="empty-text">该插件暂无配置信息</p>
        <p class="empty-subtext">插件可能使用默认配置或运行时配置</p>
      </div>

      <!-- 结构化配置信息 - 卡片式布局 -->
      <div v-else-if="isStructuredConfig" class="structured-config">
        <div class="config-grid">
          <v-card
              v-for="(item, index) in structuredConfigItems"
              :key="index"
              class="config-item-card"
              elevation="1"
              hover
          >
            <v-card-text class="config-item-content">
              <!-- 配置名称和类型 -->
              <div class="config-item-header">
                <div class="config-name-section">
                  <h4 class="config-name">{{ item.name }}</h4>
                  <v-chip
                      x-small
                      :color="getTypeColor(item.type)"
                      dark
                      class="type-chip"
                  >
                    {{ item.type }}
                  </v-chip>
                </div>
                <v-icon
                    :color="item.required ? '#4CAF50' : '#9E9E9E'"
                    size="20"
                    class="required-icon"
                >
                  {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                </v-icon>
              </div>

              <!-- 描述 -->
              <p class="config-description">{{ item.desc || '暂无描述' }}</p>

              <!-- 默认值 -->
              <div class="config-value-section">
                <span class="value-label">默认值:</span>
                <v-tooltip location="right">
                  <template v-slot:activator="{ props }">
                    <code
                        v-bind="props"
                        class="config-value-display"
                        :class="{ 'null-value': item.defaultValue === null }"
                    >
                      {{ formatConfigValue(item.defaultValue) }}
                    </code>
                  </template>
                  <span>{{ getValueTooltip(item.defaultValue) }}</span>
                </v-tooltip>
              </div>

              <!-- 验证规则 -->
              <div v-if="item.validation" class="validation-section">
                <v-chip
                    v-if="item.validation.allowedValues"
                    x-small
                    color="orange"
                    outlined
                    @click="showValidationDialog(item)"
                    class="validation-chip"
                >
                  <v-icon x-small class="mr-1">mdi-format-list-bulleted</v-icon>
                  查看枚举值
                </v-chip>
                <v-chip
                    v-else
                    x-small
                    color="blue"
                    outlined
                    class="validation-chip"
                >
                  <v-icon x-small class="mr-1">mdi-shield-check</v-icon>
                  有验证规则
                </v-chip>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </div>

      <!-- 简单键值对配置 - 优化版 -->
      <div v-else class="simple-config">
        <div class="simple-config-grid">
          <div
              v-for="(value, key) in simpleConfig"
              :key="key"
              class="simple-config-item"
          >
            <div class="simple-item-header">
              <v-chip
                  small
                  color="primary"
                  outlined
                  class="config-key-chip"
              >
                {{ key }}
              </v-chip>
            </div>
            <div class="simple-item-value">
              <code class="simple-config-code">{{ formatConfigValue(value) }}</code>
            </div>
          </div>
        </div>
      </div>
    </v-card-text>

    <!-- 优化的验证规则弹窗 -->
    <v-dialog v-model="validationDialog" max-width="500" class="validation-dialog">
      <v-card class="validation-card">
        <v-card-title class="validation-header">
          <div class="validation-title-content">
            <v-icon class="mr-3" color="orange" size="28">mdi-format-list-bulleted</v-icon>
            <div>
              <h3 class="validation-title">{{ selectedConfig?.name }}</h3>
              <p class="validation-subtitle">允许的配置值</p>
            </div>
          </div>
        </v-card-title>

        <v-card-text class="validation-content">
          <div class="allowed-values-grid">
            <v-chip
                v-for="value in selectedConfig?.validation?.allowedValues"
                :key="value"
                class="allowed-value-chip"
                color="primary"
                outlined
                large
            >
              {{ value }}
            </v-chip>
          </div>
        </v-card-text>

        <v-card-actions class="validation-actions">
          <v-spacer></v-spacer>
          <v-btn
              color="primary"
              text
              @click="validationDialog = false"
              class="close-btn"
          >
            <v-icon class="mr-1">mdi-close</v-icon>
            关闭
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import type { Config, ConfigItem } from '@/api/plugin/pluginType'

const props = defineProps({
  config: {

  } as Config
})

// 响应式数据
const validationDialog = ref(false)
const selectedConfig = ref<ConfigItem | null>(null)

// 计算属性
const hasConfig = computed(() => {
  return props.config && (
      props.config.configInfo ||
      Object.keys(props.config).length > 0
  )
})

const isStructuredConfig = computed(() => {
  return props.config?.configInfo?.config &&
      Array.isArray(props.config.configInfo.config)
})

const structuredConfigItems = computed(() => {
  if (!isStructuredConfig.value) return []
  return props.config.configInfo.config
})

const simpleConfig = computed(() => {
  if (isStructuredConfig.value || !props.config) return {}
  const { configInfo, ...simpleConfigData } = props.config
  return simpleConfigData
})

// 方法定义
const formatConfigValue = (value: any): string => {
  if (value === null || value === undefined) return 'null'
  if (Array.isArray(value)) {
    return value.length > 0 ? value.join(', ') : '空数组'
  }
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2)
  }
  if (typeof value === 'string' && value === '') return '空字符串'
  return String(value)
}

const getValueTooltip = (value: any): string => {
  if (value === null || value === undefined) return '未设置默认值'
  if (Array.isArray(value)) return `数组包含 ${value.length} 个元素`
  if (typeof value === 'object') return '复杂对象配置'
  return `当前值: ${value}`
}

const getTypeColor = (type: string): string => {
  const typeColorMap: Record<string, string> = {
    'string': '#4CAF50',
    'integer': '#2196F3',
    'double': '#9C27B0',
    'boolean': '#FF9800',
    'array': '#009688',
    'object': '#3F51B5'
  }
  return typeColorMap[type] || '#757575'
}

const showValidationDialog = (item: ConfigItem) => {
  selectedConfig.value = item
  validationDialog.value = true
}
</script>

<style scoped>
/* 主卡片样式 */
.config-display-card {
  border-radius: 12px !important;
  overflow: hidden;
  border: 1px solid #E8EAF6;
}

/* 标题区域样式 */
.config-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
}

.header-content {
  display: flex;
  align-items: center;
}

.icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.header-text {
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.config-count-chip {
  backdrop-filter: blur(10px);
  font-weight: 500;
}

/* 内容区域 */
.config-content {
  padding: 24px;
  background: #FAFAFA;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 48px 24px;
  background: white;
  border-radius: 8px;
}

.empty-icon {
  margin-bottom: 16px;
}

.empty-text {
  font-size: 1.1rem;
  color: #424242;
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-subtext {
  color: #757575;
  font-size: 0.9rem;
  margin: 0;
}

/* 结构化配置网格 */
.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.config-item-card {
  border-radius: 10px !important;
  border: 1px solid #E3F2FD;
  transition: all 0.3s ease;
  background: white;
}

.config-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1) !important;
  border-color: #BBDEFB;
}

.config-item-content {
  padding: 20px;
}

.config-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.config-name-section {
  flex: 1;
}

.config-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1A237E;
  margin: 0 0 8px 0;
}

.type-chip {
  font-weight: 600;
  letter-spacing: 0.5px;
}

.required-icon {
  margin-top: 2px;
}

.config-description {
  color: #616161;
  font-size: 0.9rem;
  line-height: 1.4;
  margin: 0 0 16px 0;
}

.config-value-section {
  margin-bottom: 12px;
}

.value-label {
  font-size: 0.85rem;
  color: #757575;
  font-weight: 500;
  display: block;
  margin-bottom: 4px;
}

.config-value-display {
  background: #F5F5F5;
  border: 1px solid #E0E0E0;
  border-radius: 6px;
  padding: 8px 12px;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #2E7D32;
  display: inline-block;
  max-width: 100%;
  word-break: break-all;
  cursor: help;
  transition: all 0.2s ease;
}

.config-value-display:hover {
  background: #EEEEEE;
  border-color: #BDBDBD;
}

.config-value-display.null-value {
  color: #757575;
  font-style: italic;
}

.validation-section {
  margin-top: 12px;
}

.validation-chip {
  cursor: pointer;
  transition: all 0.2s ease;
}

.validation-chip:hover {
  transform: scale(1.05);
}

/* 简单配置样式 */
.simple-config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.simple-config-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #E8EAF6;
  transition: all 0.2s ease;
}

.simple-config-item:hover {
  border-color: #C5CAE9;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.simple-item-header {
  margin-bottom: 12px;
}

.config-key-chip {
  font-weight: 600;
}

.simple-config-code {
  background: #F8F9FA;
  border: 1px solid #DEE2E6;
  border-radius: 4px;
  padding: 8px 12px;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #495057;
  display: block;
  word-break: break-all;
}

/* 验证弹窗样式 */
.validation-card {
  border-radius: 12px !important;
}

.validation-header {
  background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
  color: white;
  padding: 20px 24px;
}

.validation-title-content {
  display: flex;
  align-items: center;
}

.validation-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.validation-subtitle {
  font-size: 0.9rem;
  opacity: 0.9;
  margin: 4px 0 0 0;
}

.validation-content {
  padding: 24px;
}

.allowed-values-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.allowed-value-chip {
  font-weight: 500;
  font-size: 0.9rem !important;
}

.validation-actions {
  padding: 16px 24px;
  background: #FAFAFA;
}

.close-btn {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .config-grid {
    grid-template-columns: 1fr;
  }

  .simple-config-grid {
    grid-template-columns: 1fr;
  }

  .config-content {
    padding: 16px;
  }
}
</style>
