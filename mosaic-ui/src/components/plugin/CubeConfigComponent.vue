<template>
  <v-dialog v-model="localDialog" max-width="1350" persistent class="config-dialog">
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
          <v-btn
              icon
              @click="closeDialog"
              class="close-button"
              color="white"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </div>

        <v-spacer></v-spacer>

      </v-card-title>

      <!-- 对话框内容区域 -->
      <v-card-text class="config-dialog-content">
        <!-- 基础配置信息卡片 -->
        <v-card class="config-basic-card" elevation="2" v-if="hasBasicConfig">
          <v-card-title class="config-basic-header">
            <div class="header-content">
              <div class="icon-wrapper">
                <v-icon size="24" color="white">mdi-settings</v-icon>
              </div>
              <span class="header-text">基础配置</span>
            </div>
          </v-card-title>

          <v-card-text class="config-basic-content">
            <div class="basic-config-grid">
              <div v-if="configData.singleton !== undefined" class="basic-config-item">
                <div class="basic-item-header">
                  <v-chip small color="primary" outlined class="config-key-chip">
                    singleton
                  </v-chip>
                </div>
                <div class="basic-item-value">
                  <v-chip
                      :color="configData.singleton ? 'success' : 'warning'"
                      small
                      class="boolean-chip"
                  >
                    {{ configData.singleton ? '单例模式' : '多例模式' }}
                  </v-chip>
                </div>
              </div>

              <div v-if="configData.lazyInit !== undefined" class="basic-config-item">
                <div class="basic-item-header">
                  <v-chip small color="primary" outlined class="config-key-chip">
                    lazyInit
                  </v-chip>
                </div>
                <div class="basic-item-value">
                  <v-chip
                      :color="configData.lazyInit ? 'info' : 'default'"
                      small
                      class="boolean-chip"
                  >
                    {{ configData.lazyInit ? '延迟初始化' : '立即初始化' }}
                  </v-chip>
                </div>
              </div>

              <div v-if="configData.initMethod" class="basic-config-item">
                <div class="basic-item-header">
                  <v-chip small color="primary" outlined class="config-key-chip">
                    initMethod
                  </v-chip>
                </div>
                <div class="basic-item-value">
                  <code class="method-code">{{ configData.initMethod }}</code>
                </div>
              </div>

              <div v-if="configData.destroyMethod" class="basic-config-item">
                <div class="basic-item-header">
                  <v-chip small color="primary" outlined class="config-key-chip">
                    destroyMethod
                  </v-chip>
                </div>
                <div class="basic-item-value">
                  <code class="method-code">{{ configData.destroyMethod }}</code>
                </div>
              </div>
            </div>
          </v-card-text>
        </v-card>

        <!-- 依赖配置卡片 -->
        <v-card class="config-dependencies-card" elevation="2" v-if="hasDependencies">
          <v-card-title class="config-dependencies-header">
            <div class="header-content">
              <div class="icon-wrapper">
                <v-icon size="24" color="white">mdi-package-variant</v-icon>
              </div>
              <span class="header-text">依赖配置</span>
            </div>
            <v-spacer></v-spacer>
            <v-chip
                small
                color="rgba(255,255,255,0.2)"
                text-color="white"
                class="config-count-chip"
            >
              {{ configData.dependencies.length }} 个依赖
            </v-chip>
          </v-card-title>

          <v-card-text class="dependencies-content">
            <div class="dependencies-grid">
              <v-chip
                  v-for="(dependency, index) in configData.dependencies"
                  :key="index"
                  class="dependency-chip"
                  color="primary"
                  outlined
                  large
              >
                <v-icon left small>mdi-link</v-icon>
                {{ dependency }}
              </v-chip>
            </div>
          </v-card-text>
        </v-card>

        <!-- 属性配置卡片 -->
        <v-card class="config-properties-card" elevation="2" v-if="hasProperties">
          <v-card-title class="config-properties-header">
            <div class="header-content">
              <div class="icon-wrapper">
                <v-icon size="24" color="white">mdi-code-json</v-icon>
              </div>
              <span class="header-text">属性配置</span>
            </div>
            <v-spacer></v-spacer>
            <v-chip
                small
                color="rgba(255,255,255,0.2)"
                text-color="white"
                class="config-count-chip"
            >
              {{ Object.keys(configData.properties).length }} 个属性
            </v-chip>
          </v-card-title>

          <v-card-text class="properties-content">
            <div class="properties-grid">
              <div
                  v-for="(value, key) in configData.properties"
                  :key="key"
                  class="property-item"
              >
                <div class="property-header">
                  <v-chip
                      small
                      color="secondary"
                      outlined
                      class="property-key-chip"
                  >
                    {{ key }}
                  </v-chip>
                  <v-chip
                      x-small
                      :color="getValueTypeColor(value)"
                      dark
                      class="property-type-chip"
                  >
                    {{ getValueType(value) }}
                  </v-chip>
                </div>
                <div class="property-value">
                  <code class="property-code">{{ formatPropertyValue(value) }}</code>
                </div>
              </div>
            </div>
          </v-card-text>
        </v-card>

        <!-- 结构化配置信息卡片 -->
        <v-card class="config-structured-card" elevation="2" v-if="hasStructuredConfig">
          <v-card-title class="config-structured-header">
            <div class="header-content">
              <div class="icon-wrapper">
                <v-icon size="24" color="white">mdi-cog</v-icon>
              </div>
              <span class="header-text">详细配置项</span>
            </div>
            <v-spacer></v-spacer>
            <v-chip
                small
                color="rgba(255,255,255,0.2)"
                text-color="white"
                class="config-count-chip"
            >
              {{ structuredConfigItems.length }} 项配置
            </v-chip>
          </v-card-title>

          <v-card-text class="structured-content">
            <div class="structured-config-grid">
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
                        :title="item.required ? '必填项' : '可选项'"
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
                        v-if="item.validation.pattern"
                        x-small
                        color="purple"
                        outlined
                        class="validation-chip"
                        :title="item.validation.pattern"
                    >
                      <v-icon x-small class="mr-1">mdi-regex</v-icon>
                      正则验证
                    </v-chip>
                    <v-chip
                        v-if="item.validation.minValue !== undefined || item.validation.maxValue !== undefined"
                        x-small
                        color="blue"
                        outlined
                        class="validation-chip"
                        :title="`范围: ${item.validation.minValue || '∞'} ~ ${item.validation.maxValue || '∞'}`"
                    >
                      <v-icon x-small class="mr-1">mdi-numeric</v-icon>
                      数值范围
                    </v-chip>
                  </div>
                </v-card-text>
              </v-card>
            </div>
          </v-card-text>
        </v-card>

        <!-- 无配置信息状态 -->
        <div v-if="!hasAnyConfig" class="empty-state">
          <div class="empty-icon">
            <v-icon size="64" color="#E0E0E0">mdi-cog-off-outline</v-icon>
          </div>
          <h3 class="empty-title">暂无配置信息</h3>
          <p class="empty-text">该插件可能使用默认配置或运行时动态配置</p>
        </div>
      </v-card-text>
    </v-card>

    <!-- 验证规则详情弹窗 -->
    <v-dialog v-model="validationDialog" max-width="600" class="validation-dialog">
      <v-card class="validation-card">
        <v-card-title class="validation-header">
          <div class="validation-title-content">
            <v-icon class="mr-3" color="orange" size="28">mdi-format-list-bulleted</v-icon>
            <div>
              <h3 class="validation-title">{{ selectedConfig?.name }}</h3>
              <p class="validation-subtitle">配置项验证规则</p>
            </div>
          </div>
        </v-card-title>

        <v-card-text class="validation-content">
          <!-- 允许值列表 -->
          <div v-if="selectedConfig?.validation?.allowedValues" class="validation-section">
            <h4 class="validation-section-title">允许的值</h4>
            <div class="allowed-values-grid">
              <v-chip
                  v-for="value in selectedConfig.validation.allowedValues"
                  :key="value"
                  class="allowed-value-chip"
                  color="primary"
                  outlined
                  large
              >
                {{ value }}
              </v-chip>
            </div>
          </div>

          <!-- 数值范围 -->
          <div v-if="selectedConfig?.validation?.minValue !== undefined || selectedConfig?.validation?.maxValue !== undefined"
               class="validation-section">
            <h4 class="validation-section-title">数值范围</h4>
            <div class="range-display">
              <v-chip color="blue" outlined>
                最小值: {{ selectedConfig.validation.minValue ?? '无限制' }}
              </v-chip>
              <v-chip color="blue" outlined>
                最大值: {{ selectedConfig.validation.maxValue ?? '无限制' }}
              </v-chip>
            </div>
          </div>

          <!-- 正则表达式 -->
          <div v-if="selectedConfig?.validation?.pattern" class="validation-section">
            <h4 class="validation-section-title">格式验证</h4>
            <code class="pattern-code">{{ selectedConfig.validation.pattern }}</code>
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

// Props定义 - 严格按照TypeScript接口
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  pluginData: {
    type: Object, // 对应 Cube 接口
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

// 计算属性 - 严格按照Config接口结构
const configData = computed(() => {
  return props.pluginData?.config || {}
})

// 基础配置检查
const hasBasicConfig = computed(() => {
  const config = configData.value
  return config.singleton !== undefined ||
      config.lazyInit !== undefined ||
      config.initMethod ||
      config.destroyMethod
})

// 依赖配置检查
const hasDependencies = computed(() => {
  return Array.isArray(configData.value?.dependencies) &&
      configData.value.dependencies.length > 0
})

// 属性配置检查
const hasProperties = computed(() => {
  const properties = configData.value?.properties
  return properties &&
      typeof properties === 'object' &&
      Object.keys(properties).length > 0
})

// 结构化配置检查 - 基于ConfigInfo接口
const hasStructuredConfig = computed(() => {
  const configInfo = configData.value?.configInfo
  return configInfo &&
      Array.isArray(configInfo.config) &&
      configInfo.config.length > 0
})

const structuredConfigItems = computed(() => {
  return configData.value?.configInfo?.config || []
})

// 总体配置检查
const hasAnyConfig = computed(() => {
  return hasBasicConfig.value ||
      hasDependencies.value ||
      hasProperties.value ||
      hasStructuredConfig.value
})

// 方法定义
const closeDialog = () => {
  localDialog.value = false
}

// 类型颜色映射 - 基于ConfigItem的type字段
const getTypeColor = (type) => {
  const colorMap = {
    'string': '#4CAF50',
    'integer': '#2196F3',
    'double': '#03A9F4',
    'boolean': '#FF9800',
    'object': '#9C27B0',
    'array': '#F44336'
  }
  return colorMap[type] || '#607D8B'
}

// 获取值类型
const getValueType = (value) => {
  if (value === null) return 'null'
  if (Array.isArray(value)) return 'array'
  return typeof value
}

// 值类型颜色
const getValueTypeColor = (value) => {
  const type = getValueType(value)
  const colorMap = {
    'string': '#4CAF50',
    'number': '#2196F3',
    'boolean': '#FF9800',
    'object': '#9C27B0',
    'array': '#F44336',
    'null': '#9E9E9E'
  }
  return colorMap[type] || '#607D8B'
}

// 格式化配置值
const formatConfigValue = (value) => {
  if (value === null) return 'null'
  if (value === undefined) return 'undefined'
  if (typeof value === 'string') return `"${value}"`
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value, null, 2)
    } catch {
      return '[Object]'
    }
  }
  return String(value)
}

// 格式化属性值
const formatPropertyValue = (value) => {
  if (value === null) return 'null'
  if (value === undefined) return 'undefined'
  if (typeof value === 'string') return value
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value, null, 2)
    } catch {
      return '[Complex Object]'
    }
  }
  return String(value)
}

// 获取值提示信息
const getValueTooltip = (value) => {
  if (value === null) return '空值'
  if (value === undefined) return '未定义'
  if (typeof value === 'object') return '对象类型，点击查看详情'
  return `类型: ${typeof value}, 值: ${value}`
}

// 显示验证规则对话框
const showValidationDialog = (config) => {
  selectedConfig.value = config
  validationDialog.value = true
}
</script>

<style scoped>
/* 对话框基础样式 */
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
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.config-dialog-content {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.action-button {
  min-width: 100px;
}

/* 配置卡片通用样式 */
.config-basic-card,
.config-dependencies-card,
.config-properties-card,
.config-structured-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.config-basic-header {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  padding: 12px 16px;
}

.config-dependencies-header {
  background: linear-gradient(135deg, #FF9800 0%, #f57c00 100%);
  color: white;
  padding: 12px 16px;
}

.config-properties-header {
  background: linear-gradient(135deg, #9C27B0 0%, #7b1fa2 100%);
  color: white;
  padding: 12px 16px;
}

.config-structured-header {
  background: linear-gradient(135deg, #2196F3 0%, #1976d2 100%);
  color: white;
  padding: 12px 16px;
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

/* 基础配置样式 */
.config-basic-content {
  padding: 16px;
}

.basic-config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.basic-config-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.2s ease;
}

.basic-config-item:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.basic-item-header {
  margin-bottom: 8px;
}

.config-key-chip {
  font-size: 0.8rem;
  height: 24px;
}

.basic-item-value {
  display: flex;
  align-items: center;
}

.boolean-chip {
  font-size: 0.8rem;
}

.method-code {
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  color: #333;
  border: 1px solid #dee2e6;
}

/* 依赖配置样式 */
.dependencies-content {
  padding: 16px;
}

.dependencies-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.dependency-chip {
  margin: 2px;
}

/* 属性配置样式 */
.properties-content {
  padding: 16px;
}

.properties-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
}

.property-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.2s ease;
}

.property-item:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.property-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.property-key-chip {
  font-size: 0.8rem;
  height: 24px;
}

.property-type-chip {
  font-size: 0.7rem;
  height: 18px;
}

.property-code {
  background: white;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 0.85rem;
  color: #333;
  border: 1px solid #dee2e6;
  display: block;
  word-break: break-all;
  white-space: pre-wrap;
}

/* 结构化配置样式 */
.structured-content {
  padding: 16px;
}

.structured-config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

.config-item-card {
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid #e0e0e0;
}

.config-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
}

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
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.config-value-display.null-value {
  color: #999;
  font-style: italic;
}

.validation-section {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.validation-chip {
  cursor: pointer;
  transition: all 0.2s ease;
}

.validation-chip:hover {
  transform: scale(1.05);
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  margin-bottom: 20px;
}

.empty-title {
  font-size: 1.3rem;
  color: #666;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 1rem;
  color: #999;
  margin: 0;
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
}

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

.validation-content {
  padding: 20px;
}

.validation-section {
  margin-bottom: 20px;
}

.validation-section:last-child {
  margin-bottom: 0;
}

.validation-section-title {
  margin: 0 0 12px 0;
  font-size: 1rem;
  color: #333;
}

.allowed-values-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.allowed-value-chip {
  margin: 2px;
}

.range-display {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.pattern-code {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #333;
  border: 1px solid #e0e0e0;
  display: block;
  word-break: break-all;
}

.validation-actions {
  padding: 12px 20px;
  border-top: 1px solid #e0e0e0;
}

.close-btn {
  min-width: 80px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .basic-config-grid,
  .properties-grid,
  .structured-config-grid {
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

  .property-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .range-display {
    flex-direction: column;
  }

  .allowed-values-grid {
    flex-direction: column;
  }

  .allowed-value-chip {
    align-self: flex-start;
  }
}
</style>