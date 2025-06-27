<template>
  <v-dialog v-model="localDialog" max-width="1200" persistent class="config-dialog">
    <v-card class="config-dialog-card">
      <!-- 对话框标题栏 -->
      <v-card-title class="config-dialog-header">
        <div class="dialog-header-content">
          <div class="header-icon-wrapper">
            <v-icon size="32" color="white">mdi-cog</v-icon>
          </div>
          <div class="header-text-section">
            <h2 class="dialog-title">配置信息</h2>
            <p class="dialog-subtitle">{{ pluginData?.name || '插件配置详情' }}</p>
          </div>
        </div>
        <v-spacer></v-spacer>
        <v-btn
            icon
            @click="closeDialog"
            class="close-button"
            color="white"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <!-- 对话框内容区域 -->
      <v-card-text class="config-dialog-content">
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
        </v-card>
      </v-card-text>

      <!-- 对话框操作区域 -->
      <v-card-actions class="config-dialog-actions">
        <v-spacer></v-spacer>
        <v-btn
            color="grey"
            text
            @click="closeDialog"
            class="action-button"
        >
          <v-icon class="mr-1">mdi-close</v-icon>
          关闭
        </v-btn>
      </v-card-actions>
    </v-card>

    <!-- 验证规则弹窗 - 独立的对话框 -->
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
  </v-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

// Props定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  pluginData: {
    type: Object,
    default: () => ({})
  }
})

// Emits定义
const emit = defineEmits(['update:modelValue'])

// 响应式数据
const validationDialog = ref(false)
const selectedConfig = ref(null)

// 双向绑定处理
const localDialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算属性 - 配置数据处理
const configData = computed(() => {
  return props.pluginData?.config || props.pluginData?.configuration || {}
})

const hasConfig = computed(() => {
  const config = configData.value
  if (!config) return false

  if (Array.isArray(config)) return config.length > 0
  if (typeof config === 'object') return Object.keys(config).length > 0

  return false
})

const isStructuredConfig = computed(() => {
  const config = configData.value
  return Array.isArray(config) && config.length > 0 &&
      typeof config[0] === 'object' && config[0].name
})

const structuredConfigItems = computed(() => {
  return isStructuredConfig.value ? configData.value : []
})

const simpleConfig = computed(() => {
  return !isStructuredConfig.value && typeof configData.value === 'object'
      ? configData.value : {}
})

// 方法定义
const closeDialog = () => {
  localDialog.value = false
}

const getTypeColor = (type) => {
  const colorMap = {
    'string': '#4CAF50',
    'number': '#2196F3',
    'boolean': '#FF9800',
    'object': '#9C27B0',
    'array': '#F44336',
    'function': '#795548'
  }
  return colorMap[type?.toLowerCase()] || '#607D8B'
}

const formatConfigValue = (value) => {
  if (value === null) return 'null'
  if (value === undefined) return 'undefined'
  if (typeof value === 'string') return `"${value}"`
  if (typeof value === 'object') return JSON.stringify(value, null, 2)
  return String(value)
}

const getValueTooltip = (value) => {
  if (value === null) return '空值'
  if (value === undefined) return '未定义'
  if (typeof value === 'object') return '对象类型，点击查看详情'
  return `类型: ${typeof value}`
}

const showValidationDialog = (config) => {
  selectedConfig.value = config
  validationDialog.value = true
}
</script>

<style scoped>
/* 对话框样式 */
.config-dialog :deep(.v-overlay__content) {
  margin: 24px;
}

.config-dialog-card {
  border-radius: 12px;
  overflow: hidden;
}

.config-dialog-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
}

.dialog-header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text-section {
  flex: 1;
}

.dialog-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.dialog-subtitle {
  margin: 4px 0 0 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

.close-button {
  background: rgba(255, 255, 255, 0.1);

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
}

.config-dialog-content {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.config-dialog-actions {
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
  background: #fafafa;
}

.action-button {
  min-width: 100px;
}

/* 配置显示卡片样式 */
.config-display-card {
  border-radius: 8px;
  overflow: hidden;
}

.config-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text {
  font-size: 1.1rem;
  font-weight: 600;
}

.config-count-chip {
  font-size: 0.75rem;
}

.config-content {
  padding: 20px;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 40px 20px;

  .empty-icon {
    margin-bottom: 16px;
  }

  .empty-text {
    font-size: 1.1rem;
    color: #666;
    margin-bottom: 8px;
  }

  .empty-subtext {
    font-size: 0.9rem;
    color: #999;
    margin: 0;
  }
}

/* 结构化配置样式 */
.structured-config {
  margin-top: 16px;

  .config-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 16px;
  }

  .config-item-card {
    border-radius: 8px;
    transition: all 0.3s ease;
    border: 1px solid #e0e0e0;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }

  .config-item-content {
    padding: 16px;
  }

  .config-item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
  }

  .config-name-section {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
  }

  .config-name {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: #333;
  }

  .type-chip {
    font-size: 0.7rem;
    height: 20px;
  }

  .required-icon {
    margin-left: 8px;
  }

  .config-description {
    color: #666;
    font-size: 0.9rem;
    margin: 0 0 12px 0;
    line-height: 1.4;
  }

  .config-value-section {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .value-label {
      font-size: 0.8rem;
      color: #888;
      font-weight: 500;
    }

    .config-value-display {
      background: #f5f5f5;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 0.8rem;
      color: #333;
      border: 1px solid #e0e0e0;
      cursor: help;

      &.null-value {
        color: #999;
        font-style: italic;
      }
    }
  }

  .validation-section {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;

    .validation-chip {
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }
    }
  }
}

/* 简单配置样式 */
.simple-config {
  margin-top: 16px;

  .simple-config-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 12px;
  }

  .simple-config-item {
    background: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    padding: 12px;
    transition: all 0.2s ease;

    &:hover {
      background: #e9ecef;
      transform: translateY(-1px);
    }
  }

  .simple-item-header {
    margin-bottom: 8px;

    .config-key-chip {
      font-size: 0.8rem;
      height: 24px;
    }
  }

  .simple-config-code {
    background: white;
    padding: 6px 10px;
    border-radius: 4px;
    font-size: 0.85rem;
    color: #333;
    border: 1px solid #dee2e6;
    display: block;
    word-break: break-all;
  }
}

/* 验证对话框样式 */
.validation-dialog :deep(.v-overlay__content) {
  margin: 24px;
}

.validation-card {
  border-radius: 8px;
}

.validation-header {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: white;
  padding: 16px 20px;

  .validation-title-content {
    display: flex;
    align-items: center;
  }

  .validation-title {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
  }

  .validation-subtitle {
    margin: 4px 0 0 0;
    opacity: 0.9;
    font-size: 0.9rem;
  }
}

.validation-content {
  padding: 20px;

  .allowed-values-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .allowed-value-chip {
      margin: 2px;
    }
  }
}

.validation-actions {
  padding: 12px 20px;
  border-top: 1px solid #e0e0e0;

  .close-btn {
    min-width: 80px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .config-grid {
    grid-template-columns: 1fr;
  }

  .simple-config-grid {
    grid-template-columns: 1fr;
  }

  .config-dialog-content {
    padding: 16px;
    max-height: 60vh;
  }

  .dialog-header-content {
    gap: 12px;
  }

  .dialog-title {
    font-size: 1.3rem;
  }
}

@media (max-width: 480px) {
  .config-item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .config-name-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .allowed-values-grid {
    flex-direction: column;

    .allowed-value-chip {
      align-self: flex-start;
    }
  }
}
</style>