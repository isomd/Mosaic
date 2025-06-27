<template>
  <v-dialog v-model="localDialog" max-width="1400" persistent class="config-dialog">
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
  return hasStructuredConfig.value
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

<style scoped lang="scss">
/* 对话框基础样式 - 采用暖黄色调主题 */
.config-dialog :deep(.v-overlay__content) {
  margin: 24px;
}

.config-dialog-card {
  border-radius: 16px;
  overflow: hidden;
  background: #FEFEFE;
  box-shadow: 0 8px 32px rgba(139, 115, 85, 0.15);
  border: 2px solid #E6D3A3;
}

/* 对话框标题栏 - 暖黄渐变主题 */
.config-dialog-header {
  background: linear-gradient(135deg, #F4E4BC 0%, #E6D3A3 50%, #D4A574 100%);
  color: #2D5A27;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.config-dialog-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23D4A574' fill-opacity='0.1'%3E%3Cpath d='M20 20c0 11.046-8.954 20-20 20v20h40V20H20z'/%3E%3C/g%3E%3C/svg%3E") repeat;
  pointer-events: none;
}

.dialog-header-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.header-icon-wrapper {
  background: linear-gradient(135deg, #4A9B8E, #6BB6B0);
  border-radius: 50%;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(74, 155, 142, 0.3);
}

.header-text-section {
  flex: 1;
}

.dialog-title {
  margin: 0;
  font-size: 1.6rem;
  font-weight: 700;
  color: #2D5A27;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.dialog-subtitle {
  margin: 6px 0 0 0;
  color: #8B7355;
  font-size: 1rem;
  font-weight: 500;
}

.close-button {
  background: rgba(45, 90, 39, 0.1);
  border: 2px solid rgba(45, 90, 39, 0.2);
  color: #2D5A27;
  transition: all 0.3s ease;
}

.close-button:hover {
  background: rgba(45, 90, 39, 0.2);
  transform: scale(1.05);
}

.config-dialog-content {
  padding: 28px;
  max-height: 70vh;
  overflow-y: auto;
  background: linear-gradient(180deg, #FEFEFE 0%, #F4E4BC 100%);
}

/* 配置卡片通用样式 - 暖色调主题 */
.config-structured-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(139, 115, 85, 0.12);
  border: 1px solid #E6D3A3;
  transition: all 0.3s ease;
}

.config-structured-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(139, 115, 85, 0.18);
}

/* 结构化配置卡片头部 */
.config-structured-header {
  background: linear-gradient(135deg, #E6D3A3 0%, #F4E4BC 100%);
  color: #2D5A27;
  padding: 16px 20px;
  position: relative;
}

.config-structured-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #8B7355, #E6D3A3, #8B7355);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-wrapper {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-text {
  font-size: 1.2rem;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.config-count-chip {
  background: rgba(255, 255, 255, 0.3) !important;
  color: inherit !important;
  font-size: 0.8rem;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

/* 结构化配置样式 */
.structured-content {
  padding: 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
}

.structured-config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.config-item-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid #E6D3A3;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  position: relative;
  overflow: hidden;
}

.config-item-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #E6D3A3, #D4A574, #E6D3A3);
}

.config-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(139, 115, 85, 0.2);
  background: linear-gradient(135deg, #F4E4BC 0%, #E6D3A3 100%);
}

.config-item-content {
  padding: 20px;
}

.config-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.config-name-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.config-name {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #2D5A27;
}

.type-chip {
  font-size: 0.75rem;
  height: 22px;
  font-weight: 600;
}

.required-icon {
  margin-left: 12px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.config-description {
  color: #8B7355;
  font-size: 0.95rem;
  margin: 0 0 16px 0;
  line-height: 1.5;
  font-weight: 500;
}

.config-value-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.value-label {
  font-size: 0.85rem;
  color: #8B7355;
  font-weight: 600;
}

.config-value-display {
  background: linear-gradient(135deg, #FEFEFE, #F4E4BC);
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #2D5A27;
  border: 2px solid #E6D3A3;
  cursor: help;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
  transition: all 0.2s ease;
}

.config-value-display:hover {
  background: linear-gradient(135deg, #F4E4BC, #E6D3A3);
  transform: scale(1.02);
}

.config-value-display.null-value {
  color: #8B7355;
  font-style: italic;
}

.validation-section {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.validation-chip {
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.validation-chip:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.3);
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  border-radius: 12px;
  border: 2px dashed #E6D3A3;
}

.empty-icon {
  margin-bottom: 24px;
}

.empty-title {
  font-size: 1.4rem;
  color: #8B7355;
  margin-bottom: 16px;
  font-weight: 600;
}

.empty-text {
  font-size: 1.1rem;
  color: #8B7355;
  margin: 0;
  font-weight: 500;
}

/* 验证对话框样式 */
.validation-dialog :deep(.v-overlay__content) {
  margin: 24px;
}

.validation-card {
  border-radius: 12px;
  border: 2px solid #E6D3A3;
  background: #FEFEFE;
}

.validation-header {
  background: linear-gradient(135deg, #D4A574 0%, #E6D3A3 100%);
  color: #2D5A27;
  padding: 20px 24px;
}

.validation-title-content {
  display: flex;
  align-items: center;
}

.validation-title {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: #2D5A27;
}

.validation-subtitle {
  margin: 6px 0 0 0;
  color: #8B7355;
  font-size: 1rem;
  font-weight: 500;
}

.validation-content {
  padding: 24px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
}

.validation-section {
  margin-bottom: 24px;
}

.validation-section:last-child {
  margin-bottom: 0;
}

.validation-section-title {
  margin: 0 0 16px 0;
  font-size: 1.1rem;
  color: #2D5A27;
  font-weight: 700;
}

.allowed-values-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.allowed-value-chip {
  background: linear-gradient(135deg, #4A9B8E, #6BB6B0) !important;
  color: #FEFEFE !important;
  font-weight: 600;
  border: none !important;
}

.range-display {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.pattern-code {
  background: linear-gradient(135deg, #FEFEFE, #F4E4BC);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #2D5A27;
  border: 2px solid #E6D3A3;
  display: block;
  word-break: break-all;
  font-weight: 500;
}

.validation-actions {
  padding: 16px 24px;
  border-top: 2px solid #E6D3A3;
  background: linear-gradient(135deg, #F4E4BC, #E6D3A3);
}

.close-btn {
  background: linear-gradient(135deg, #4A9B8E, #6BB6B0) !important;
  color: #FEFEFE !important;
  min-width: 100px;
  font-weight: 600;
  border: none !important;
}

/* 响应式设计优化 */
@media (max-width: 768px) {
  .structured-config-grid {
    grid-template-columns: 1fr;
  }

  .config-dialog-content {
    padding: 20px;
    max-height: 65vh;
  }

  .dialog-header-content {
    gap: 16px;
  }

  .dialog-title {
    font-size: 1.4rem;
  }
}

@media (max-width: 480px) {
  .config-item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .config-name-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .property-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
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

  .config-dialog-content {
    padding: 16px;
  }
}

/* 滚动条样式优化 */
.config-dialog-content::-webkit-scrollbar {
  width: 8px;
}

.config-dialog-content::-webkit-scrollbar-track {
  background: #F4E4BC;
  border-radius: 4px;
}

.config-dialog-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #D4A574, #8B7355);
  border-radius: 4px;
}

.config-dialog-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #8B7355, #D4A574);
}
</style>