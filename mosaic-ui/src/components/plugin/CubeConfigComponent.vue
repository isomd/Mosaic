<template>
  <v-dialog v-model="localDialog" max-width="1600" persistent class="config-dialog">
    <v-card class="config-dialog-card">
      <!-- 对话框标题栏 -->
      <v-card-title class="config-dialog-header">
        <div class="dialog-header-content">
          <div class="header-icon-wrapper">
            <v-icon size="32" color="white">mdi-cog</v-icon>
          </div>
          <div class="header-text-section">
            <h2 class="dialog-title">配置管理</h2>
            <p class="dialog-subtitle">{{ pluginData?.name || '插件配置详情' }}</p>
          </div>

          <v-chip
              small
              color="rgba(255,255,255,0.2)"
              text-color="white"
              class="config-count-chip"
          >
            {{ structuredConfigItems.length }} 项配置
          </v-chip>

          <v-spacer></v-spacer>

          <!-- 操作按钮组 -->
          <div class="header-actions">
            <v-btn
                color="success"
                :loading="saving"
                :disabled="!hasChanges || !isFormValid"
                @click="saveConfiguration"
                class="save-btn"
            >
              <v-icon class="mr-2">mdi-content-save</v-icon>
              保存配置
            </v-btn>

            <v-btn
                color="warning"
                :disabled="!hasChanges"
                @click="resetConfiguration"
                class="reset-btn"
            >
              <v-icon class="mr-2">mdi-refresh</v-icon>
              重置
            </v-btn>

            <v-btn
                icon
                @click="closeDialog"
                class="close-button"
                color="white"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </div>
        </div>
      </v-card-title>

      <!-- 对话框内容区域 -->
      <v-card-text class="config-dialog-content">
        <div v-if="hasStructuredConfig" class="config-main-layout">
          <!-- 左侧：配置说明书 -->
          <div class="config-documentation-panel">
            <v-card class="documentation-card" elevation="2">
              <v-card-title class="documentation-header">
                <div class="header-content">
                  <div class="icon-wrapper">
                    <v-icon size="24" color="white">mdi-book-open-variant</v-icon>
                  </div>
                  <span class="header-text">配置说明书</span>
                </div>
              </v-card-title>

              <v-card-text class="documentation-content">
                <div class="documentation-grid">
                  <v-card
                      v-for="(item, index) in structuredConfigItems"
                      :key="index"
                      class="doc-item-card"
                      :class="{ 'doc-item-active': selectedConfigIndex === index }"
                      elevation="1"
                      @click="selectConfigItem(index)"
                  >
                    <v-card-text class="doc-item-content">
                      <!-- 配置名称和类型 -->
                      <div class="doc-item-header">
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
                        <code class="config-value-display">
                          {{ formatConfigValue(item.defaultValue) }}
                        </code>
                      </div>

                      <!-- 验证规则 -->
                      <div v-if="item.validation" class="validation-section">
                        <v-chip
                            v-if="item.validation.allowedValues"
                            x-small
                            color="orange"
                            outlined
                            @click.stop="showValidationDialog(item)"
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
          </div>

          <!-- 右侧：配置编辑表单 -->
          <div class="config-editor-panel">
            <v-card class="editor-card" elevation="2">
              <v-card-title class="editor-header">
                <div class="header-content">
                  <div class="icon-wrapper">
                    <v-icon size="24" color="white">mdi-pencil-box</v-icon>
                  </div>
                  <span class="header-text">配置编辑</span>
                </div>
                <v-spacer></v-spacer>
                <v-chip
                    small
                    :color="isFormValid ? 'success' : 'error'"
                    dark
                    class="validation-status-chip"
                >
                  <v-icon small class="mr-1">
                    {{ isFormValid ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                  </v-icon>
                  {{ isFormValid ? '验证通过' : '验证失败' }}
                </v-chip>
              </v-card-title>

              <v-card-text class="editor-content">
                <v-form ref="configForm" v-model="isFormValid" class="config-form">
                  <div
                      v-for="(item, index) in structuredConfigItems"
                      :key="index"
                      class="config-form-item"
                      :class="{ 'form-item-highlighted': selectedConfigIndex === index }"
                  >
                    <!-- 字符串类型 -->
                    <v-text-field
                        v-if="item.type === 'string'"
                        v-model="configValues[item.name]"
                        :label="item.name"
                        :placeholder="formatConfigValue(item.defaultValue)"
                        :required="item.required"
                        :rules="getValidationRules(item)"
                        :error-messages="getFieldErrors(item.name)"
                        outlined
                        dense
                        class="config-input"
                        @focus="selectConfigItem(index)"
                        @input="onConfigChange"
                    >
                      <template v-slot:prepend-inner>
                        <v-icon :color="item.required ? 'error' : 'grey'">
                          {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                        </v-icon>
                      </template>
                    </v-text-field>

                    <!-- 数字类型 (integer/double) -->
                    <v-text-field
                        v-else-if="item.type === 'integer' || item.type === 'double'"
                        v-model.number="configValues[item.name]"
                        :label="item.name"
                        :placeholder="String(item.defaultValue)"
                        :required="item.required"
                        :rules="getValidationRules(item)"
                        :error-messages="getFieldErrors(item.name)"
                        :type="item.type === 'integer' ? 'number' : 'number'"
                        :step="item.type === 'double' ? '0.01' : '1'"
                        outlined
                        dense
                        class="config-input"
                        @focus="selectConfigItem(index)"
                        @input="onConfigChange"
                    >
                      <template v-slot:prepend-inner>
                        <v-icon :color="item.required ? 'error' : 'grey'">
                          {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                        </v-icon>
                      </template>
                    </v-text-field>

                    <!-- 布尔类型 -->
                    <v-switch
                        v-else-if="item.type === 'boolean'"
                        v-model="configValues[item.name]"
                        :label="item.name"
                        :required="item.required"
                        :rules="getValidationRules(item)"
                        class="config-switch"
                        @focus="selectConfigItem(index)"
                        @change="onConfigChange"
                    >
                      <template v-slot:prepend>
                        <v-icon :color="item.required ? 'error' : 'grey'" class="mr-2">
                          {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                        </v-icon>
                      </template>
                    </v-switch>

                    <!-- 枚举选择 -->
                    <v-select
                        v-else-if="item.validation && item.validation.allowedValues"
                        v-model="configValues[item.name]"
                        :label="item.name"
                        :items="item.validation.allowedValues"
                        :placeholder="formatConfigValue(item.defaultValue)"
                        :required="item.required"
                        :rules="getValidationRules(item)"
                        :error-messages="getFieldErrors(item.name)"
                        outlined
                        dense
                        class="config-select"
                        @focus="selectConfigItem(index)"
                        @change="onConfigChange"
                    >
                      <template v-slot:prepend-inner>
                        <v-icon :color="item.required ? 'error' : 'grey'">
                          {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                        </v-icon>
                      </template>
                    </v-select>

                    <!-- 对象/数组类型 -->
                    <v-textarea
                        v-else-if="item.type === 'object' || item.type === 'array'"
                        v-model="configValues[item.name]"
                        :label="item.name"
                        :placeholder="formatConfigValue(item.defaultValue)"
                        :required="item.required"
                        :rules="getValidationRules(item)"
                        :error-messages="getFieldErrors(item.name)"
                        outlined
                        rows="3"
                        class="config-textarea"
                        @focus="selectConfigItem(index)"
                        @input="onConfigChange"
                    >
                      <template v-slot:prepend-inner>
                        <v-icon :color="item.required ? 'error' : 'grey'">
                          {{ item.required ? 'mdi-star' : 'mdi-star-outline' }}
                        </v-icon>
                      </template>
                    </v-textarea>

                    <!-- 配置项描述 -->
                    <div class="config-item-description">
                      <v-icon small color="grey" class="mr-1">mdi-information-outline</v-icon>
                      <span class="description-text">{{ item.desc || '暂无描述' }}</span>
                    </div>
                  </div>
                </v-form>
              </v-card-text>
            </v-card>
          </div>
        </div>

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

    <!-- 保存确认对话框 -->
    <v-dialog v-model="saveConfirmDialog" max-width="500">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="warning" class="mr-2">mdi-alert</v-icon>
          确认保存配置
        </v-card-title>
        <v-card-text>
          <p>您即将保存以下配置更改：</p>
          <ul class="change-list">
            <li v-for="(change, key) in configChanges" :key="key" class="change-item">
              <strong>{{ key }}:</strong>
              <span class="old-value">{{ formatConfigValue(change.oldValue) }}</span>
              <v-icon small class="mx-2">mdi-arrow-right</v-icon>
              <span class="new-value">{{ formatConfigValue(change.newValue) }}</span>
            </li>
          </ul>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="saveConfirmDialog = false">
            取消
          </v-btn>
          <v-btn color="primary" @click="confirmSave" :loading="saving">
            确认保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted,defineProps,defineEmits } from 'vue'
import { getCubeConfiguration, updateCubeConfiguration } from '@/api/plugin/pluginApi.js'

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
const emit = defineEmits(['update:modelValue', 'configUpdated'])

// 响应式数据
const validationDialog = ref(false)
const saveConfirmDialog = ref(false)
const selectedConfig = ref(null)
const selectedConfigIndex = ref(-1)
const configValues = ref({})
const originalConfigValues = ref({})
const fieldErrors = ref({})
const isFormValid = ref(true)
const saving = ref(false)
const loading = ref(false)

// 双向绑定处理
const localDialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 计算属性
const configData = computed(() => {
  return props.pluginData?.config || {}
})

const hasStructuredConfig = computed(() => {
  const configInfo = configData.value?.configInfo
  return configInfo &&
      Array.isArray(configInfo.config) &&
      configInfo.config.length > 0
})

const structuredConfigItems = computed(() => {
  return configData.value?.configInfo?.config || []
})

const hasAnyConfig = computed(() => {
  return hasStructuredConfig.value
})

const hasChanges = computed(() => {
  return Object.keys(configChanges.value).length > 0
})

const configChanges = computed(() => {
  const changes = {}
  for (const key in configValues.value) {
    if (configValues.value[key] !== originalConfigValues.value[key]) {
      changes[key] = {
        oldValue: originalConfigValues.value[key],
        newValue: configValues.value[key]
      }
    }
  }
  return changes
})

// 监听对话框打开状态
watch(localDialog, (newVal) => {
  if (newVal && props.pluginData?.id) {
    loadCurrentConfiguration()
  }
})

// 生命周期
onMounted(() => {
  if (localDialog.value && props.pluginData?.id) {
    loadCurrentConfiguration()
  }
})

// 方法定义
const loadCurrentConfiguration = async () => {
  if (!props.pluginData?.id) return

  loading.value = true
  try {
    const response = await getCubeConfiguration(props.pluginData.id)
    if (response.code === 200) {
      const currentConfig = response.data || {}

      // 初始化配置值
      const initialValues = {}
      structuredConfigItems.value.forEach(item => {
        initialValues[item.name] = currentConfig[item.name] !== undefined
            ? currentConfig[item.name]
            : item.defaultValue
      })

      configValues.value = { ...initialValues }
      originalConfigValues.value = { ...initialValues }
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    // 使用默认值
    const defaultValues = {}
    structuredConfigItems.value.forEach(item => {
      defaultValues[item.name] = item.defaultValue
    })
    configValues.value = { ...defaultValues }
    originalConfigValues.value = { ...defaultValues }
  } finally {
    loading.value = false
  }
}

const saveConfiguration = () => {
  if (!isFormValid.value || !hasChanges.value) return
  saveConfirmDialog.value = true
}

const confirmSave = async () => {
  saving.value = true
  saveConfirmDialog.value = false

  try {
    const updateRequest = {
      cubeId: props.pluginData.id,
      configurations: { ...configValues.value }
    }

    const response = await updateCubeConfiguration(updateRequest)

    if (response.code === 200) {
      originalConfigValues.value = { ...configValues.value }
      emit('configUpdated', response.data)

      // 显示成功消息
      showSuccessMessage('配置保存成功')
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    showErrorMessage('保存配置失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

const resetConfiguration = () => {
  configValues.value = { ...originalConfigValues.value }
  fieldErrors.value = {}
}

const closeDialog = () => {
  if (hasChanges.value) {
    if (confirm('有未保存的更改，确定要关闭吗？')) {
      resetConfiguration()
      localDialog.value = false
    }
  } else {
    localDialog.value = false
  }
}

const selectConfigItem = (index) => {
  selectedConfigIndex.value = index
}
const configForm = ref()
const onConfigChange = () => {
  // 清除字段错误
  fieldErrors.value = {}

  // 触发表单验证
  setTimeout(() => {
    if (configForm.value) {
      configForm.value.validate()
    }
  }, 100)
}

const getValidationRules = (item) => {
  const rules = []

  // 必填验证
  if (item.required) {
    rules.push(v => {
      if (v === null || v === undefined || v === '') {
        return `${item.name} 为必填项`
      }
      return true
    })
  }

  // 类型验证
  if (item.type === 'integer') {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        const num = Number(v)
        if (!Number.isInteger(num)) {
          return `${item.name} 必须为整数`
        }
      }
      return true
    })
  }

  if (item.type === 'double') {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        const num = Number(v)
        if (isNaN(num)) {
          return `${item.name} 必须为数字`
        }
      }
      return true
    })
  }

  // 枚举值验证
  if (item.validation?.allowedValues) {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        if (!item.validation.allowedValues.includes(v)) {
          return `${item.name} 必须为: ${item.validation.allowedValues.join(', ')}`
        }
      }
      return true
    })
  }

  // 数值范围验证
  if (item.validation?.minValue !== undefined) {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        const num = Number(v)
        if (num < item.validation.minValue) {
          return `${item.name} 不能小于 ${item.validation.minValue}`
        }
      }
      return true
    })
  }

  if (item.validation?.maxValue !== undefined) {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        const num = Number(v)
        if (num > item.validation.maxValue) {
          return `${item.name} 不能大于 ${item.validation.maxValue}`
        }
      }
      return true
    })
  }

  // 正则验证
  if (item.validation?.pattern) {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        const regex = new RegExp(item.validation.pattern)
        if (!regex.test(v)) {
          return `${item.name} 格式不正确`
        }
      }
      return true
    })
  }

  // JSON格式验证（对象/数组类型）
  if (item.type === 'object' || item.type === 'array') {
    rules.push(v => {
      if (v !== null && v !== undefined && v !== '') {
        try {
          JSON.parse(v)
        } catch (e) {
          return `${item.name} 必须为有效的JSON格式`
        }
      }
      return true
    })
  }

  return rules
}

const getFieldErrors = (fieldName) => {
  return fieldErrors.value[fieldName] || []
}

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

const showValidationDialog = (config) => {
  selectedConfig.value = config
  validationDialog.value = true
}

const showSuccessMessage = (message) => {
  // 这里可以集成您的消息提示组件
  console.log('Success:', message)
}

const showErrorMessage = (message) => {
  // 这里可以集成您的消息提示组件
  console.error('Error:', message)
}
</script>

<style scoped lang="scss">

/* 基础对话框样式保持不变 */
.config-dialog :deep(.v-overlay__content) {
  margin: 24px;
}

.config-dialog-card {
  border-radius: 16px !important;
  overflow: hidden !important;
  background: #FEFEFE;
  box-shadow: 0 8px 32px rgba(139, 115, 85, 0.15);
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%) !important;
  border: 2px solid #E6D3A3 !important;
}

.config-dialog-header {
  border-radius: 16px 16px 0 0 !important;
  background: linear-gradient(135deg, #F4E4BC 0%, #E6D3A3 50%, #D4A574 100%);
  color: #2D5A27;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.dialog-header-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
  width: 100%;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.save-btn, .reset-btn {
  min-width: 120px;
  font-weight: 600;
  border-radius: 8px;
}

.save-btn {
  background: linear-gradient(135deg, #4CAF50, #45A049) !important;
  color: white !important;
}

.reset-btn {
  background: linear-gradient(135deg, #FF9800, #F57C00) !important;
  color: white !important;
}

/* 主布局样式 */
.config-main-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  min-height: 70vh;
}

.config-documentation-panel,
.config-editor-panel {
  height: 100%;
}

.documentation-card,
.editor-card {
  height: 100%;
  border-radius: 12px !important;
  border: 1px solid #E6D3A3;
  overflow: hidden;
}

.documentation-header,
.editor-header {
  background: linear-gradient(135deg, #E6D3A3 0%, #F4E4BC 100%);
  color: #2D5A27;
  padding: 16px 20px;
}

.validation-status-chip {
  font-size: 0.8rem;
  font-weight: 600;
}

/* 文档面板样式 */
.documentation-content {
  padding: 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  height: calc(100% - 80px);
  overflow: hidden;
}

.documentation-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.doc-item-card {
  transition: all 0.3s ease;
  border: 2px solid #E6D3A3;
  cursor: pointer;
  border-radius: 8px !important;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%) !important;
}

.doc-item-card:hover,
.doc-item-active {
  border-color: #D4A574;
  background: linear-gradient(135deg, #F4E4BC 0%, #E6D3A3 100%) !important;
  transform: translateX(4px);
}

.doc-item-content {
  padding: 16px;
}

/* 编辑器面板样式 */
.editor-content {
  padding: 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  height: calc(100% - 80px);
  overflow:hidden;
}

.config-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.config-form-item {
  padding: 16px;
  border-radius: 8px;
  border: 2px solid #E6D3A3;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.5);
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%) !important;
}

.form-item-highlighted {
  border-color: #D4A574;
  background: rgba(244, 228, 188, 0.3);
  box-shadow: 0 4px 12px rgba(212, 165, 116, 0.2);
}

.config-input,
.config-select,
.config-textarea,
.config-switch {
  margin-bottom: 8px;
}

.config-item-description {
  display: flex;
  align-items: center;
  margin-top: 8px;
  padding: 8px 12px;
  background: rgba(230, 211, 163, 0.2);
  border-radius: 6px;
  border-left: 4px solid #E6D3A3;
}

.description-text {
  font-size: 0.9rem;
  color: #8B7355;
  font-style: italic;
}

/* 保存确认对话框样式 */
.change-list {
  margin: 16px 0;
  padding-left: 20px;
}

.change-item {
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.old-value {
  color: #F44336;
  text-decoration: line-through;
}

.new-value {
  color: #4CAF50;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .config-main-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .config-documentation-panel {
    order: 2;
  }

  .config-editor-panel {
    order: 1;
  }
}
.config-dialog-content{
  overflow-y: scroll;

}
@media (max-width: 768px) {
  .config-dialog-content {
    overflow-y: scroll;
    padding: 16px;
  }

  .header-actions {
    flex-direction: column;
    gap: 8px;
  }

  .save-btn, .reset-btn {
    min-width: 100px;
    font-size: 0.8rem;
  }
}

/* 滚动条样式 */
.documentation-content::-webkit-scrollbar,
.editor-content::-webkit-scrollbar {
  width: 8px;
}

.documentation-content::-webkit-scrollbar-track,
.editor-content::-webkit-scrollbar-track {
  background: #F4E4BC;
  border-radius: 4px;
}

.documentation-content::-webkit-scrollbar-thumb,
.editor-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #D4A574, #8B7355);
  border-radius: 4px;
}

/* 其他样式保持原有设计 */
.header-icon-wrapper,
.icon-wrapper {
  background: linear-gradient(135deg, #4A9B8E, #6BB6B0);
  border-radius: 25%;
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

.config-count-chip {
  background: rgba(255, 255, 255, 0.3) !important;
  color: inherit !important;
  font-size: 0.8rem;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.4);
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

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-text {
  font-size: 1.2rem;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
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
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
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

/* 验证对话框样式保持原有设计 */
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
</style>
