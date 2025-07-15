<template>
  <v-dialog v-model="localDialog" max-width="1800" persistent class="config-dialog">
    <v-card class="config-dialog-card">
      <!-- 对话框标题栏 -->
      <v-card-title class="config-dialog-header">
        <div class="dialog-header-content">
          <div class="header-icon-wrapper">
            <v-icon size="32" color="white">mdi-cog-multiple</v-icon>
          </div>
          <div class="header-text-section">
            <h2 class="dialog-title">配置管理中心</h2>
            <p class="dialog-subtitle">{{ pluginData?.name || '插件配置详情' }}</p>
          </div>

          <v-chip
              small
              color="rgba(255,255,255,0.2)"
              text-color="white"
              class="config-count-chip"
          >
            <v-icon small class="mr-1">mdi-database</v-icon>
            {{ allConfigIds.length }} 个配置实例
          </v-chip>

          <v-spacer></v-spacer>

          <!-- 操作按钮组 -->
          <div class="header-actions">
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="info"
                    @click="showAddConfigDialog"
                    class="add-btn"
                    v-bind="attrs"
                    v-on="on"
                    :disabled="loadingConfigs"
                >
                  <v-icon class="mr-2">mdi-plus-circle</v-icon>
                  新建配置
                </v-btn>
              </template>
              <span>基于默认配置创建新的配置实例</span>
            </v-tooltip>

            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="secondary"
                    :disabled="!currentConfigId || loadingCurrentConfig"
                    @click="showCloneDialog"
                    class="clone-btn"
                    v-bind="attrs"
                    v-on="on"
                >
                  <v-icon class="mr-2">mdi-content-duplicate</v-icon>
                  复制配置
                </v-btn>
              </template>
              <span>复制当前配置创建新实例</span>
            </v-tooltip>

            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="success"
                    :loading="saving"
                    :disabled="!hasChanges || !isFormValid || loadingCurrentConfig"
                    @click="saveConfiguration"
                    class="save-btn"
                    v-bind="attrs"
                    v-on="on"
                >
                  <v-icon class="mr-2">mdi-content-save</v-icon>
                  保存更改
                </v-btn>
              </template>
              <span>保存当前配置的修改</span>
            </v-tooltip>

            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="warning"
                    :disabled="!hasChanges || loadingCurrentConfig"
                    @click="resetConfiguration"
                    class="reset-btn"
                    v-bind="attrs"
                    v-on="on"
                >
                  <v-icon class="mr-2">mdi-backup-restore</v-icon>
                  撤销更改
                </v-btn>
              </template>
              <span>撤销所有未保存的修改</span>
            </v-tooltip>

            <v-btn
                icon
                @click="handleCloseDialog"
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
          <!-- 左侧：配置列表面板 -->
          <div class="config-list-panel">
            <v-card class="config-list-card" elevation="2">
              <v-card-title class="config-list-header">
                <div class="header-content">
                  <div class="icon-wrapper">
                    <v-icon size="24" color="white">mdi-view-list</v-icon>
                  </div>
                  <span class="header-text">配置实例</span>
                </div>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        icon
                        small
                        @click="refreshConfigList"
                        :loading="loadingConfigs"
                        color="white"
                        v-bind="attrs"
                        v-on="on"
                    >
                      <v-icon>mdi-refresh</v-icon>
                    </v-btn>
                  </template>
                  <span>刷新配置列表</span>
                </v-tooltip>
              </v-card-title>

              <v-card-text class="config-list-content">
                <!-- 搜索框 -->
                <v-text-field
                    v-model="configSearchQuery"
                    prepend-inner-icon="mdi-magnify"
                    label="搜索配置..."
                    clearable
                    dense
                    outlined
                    class="config-search mb-4"
                    hide-details
                ></v-text-field>

                <div class="config-items-list">
                  <v-card
                      v-for="configId in filteredConfigIds"
                      :key="configId"
                      class="config-item-card"
                      :class="{
                        'config-item-active': currentConfigId === configId,
                        'config-item-modified': hasConfigChanges(configId)
                      }"
                      elevation="1"
                      @click="selectConfiguration(configId)"
                  >
                    <v-card-text class="config-item-content">
                      <div class="config-item-header">
                        <div class="config-info">
                          <div class="config-name-row">
                            <h4 class="config-id">{{ getConfigDisplayName(configId) }}</h4>
                            <v-icon
                                v-if="hasConfigChanges(configId)"
                                small
                                color="orange"
                                class="modified-indicator"
                                title="有未保存的修改"
                            >
                              mdi-circle
                            </v-icon>
                          </div>
                          <div class="config-chips">
                            <v-chip
                                x-small
                                :color="getConfigStatusColor(configId)"
                                dark
                                class="config-status-chip"
                            >
                              <v-icon x-small class="mr-1">{{ getConfigStatusIcon(configId) }}</v-icon>
                              {{ getConfigStatus(configId) }}
                            </v-chip>
                            <v-chip
                                v-if="configId === 'default'"
                                x-small
                                color="primary"
                                dark
                                class="config-type-chip"
                            >
                              <v-icon x-small class="mr-1">mdi-star</v-icon>
                              系统默认
                            </v-chip>
                          </div>
                        </div>
                        <div class="config-actions">
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn
                                  icon
                                  x-small
                                  @click.stop="cloneConfiguration(configId)"
                                  :disabled="loadingConfigs"
                                  color="primary"
                                  v-bind="attrs"
                                  v-on="on"
                              >
                                <v-icon small>mdi-content-duplicate</v-icon>
                              </v-btn>
                            </template>
                            <span>复制此配置</span>
                          </v-tooltip>

                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn
                                  v-if="configId !== 'default'"
                                  icon
                                  x-small
                                  @click.stop="showDeleteConfirmDialog(configId)"
                                  :disabled="loadingConfigs || currentConfigId === configId"
                                  color="error"
                                  v-bind="attrs"
                                  v-on="on"
                              >
                                <v-icon small>mdi-delete</v-icon>
                              </v-btn>
                            </template>
                            <span>删除此配置</span>
                          </v-tooltip>
                        </div>
                      </div>

                      <div class="config-meta">
                        <div class="config-summary">
                          <v-icon small color="grey" class="mr-1">mdi-information-outline</v-icon>
                          <span>{{ getConfigSummary(configId) }}</span>
                        </div>
                        <div v-if="getConfigLastModified(configId)" class="config-time">
                          <v-icon x-small color="grey" class="mr-1">mdi-clock-outline</v-icon>
                          <span>{{ getConfigLastModified(configId) }}</span>
                        </div>
                      </div>
                    </v-card-text>
                  </v-card>

                  <!-- 空状态 -->
                  <div v-if="filteredConfigIds.length === 0 && configSearchQuery" class="no-search-results">
                    <v-icon color="grey" size="32">mdi-magnify-close</v-icon>
                    <p class="grey--text mt-2">未找到匹配的配置</p>
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </div>

          <!-- 中间：配置说明书 -->
          <div class="config-documentation-panel">
            <v-card class="documentation-card" elevation="2">
              <v-card-title class="documentation-header">
                <div class="header-content">
                  <div class="icon-wrapper">
                    <v-icon size="24" color="white">mdi-book-open-variant</v-icon>
                  </div>
                  <span class="header-text">配置说明</span>
                </div>
                <v-spacer></v-spacer>
                <v-chip
                    x-small
                    color="rgba(255,255,255,0.3)"
                    text-color="white"
                >
                  {{ structuredConfigItems.length }} 项配置
                </v-chip>
              </v-card-title>

              <v-card-text class="documentation-content">
                <!-- 搜索框 -->
                <v-text-field
                    v-model="docSearchQuery"
                    prepend-inner-icon="mdi-magnify"
                    label="搜索配置项..."
                    clearable
                    dense
                    outlined
                    class="doc-search mb-4"
                    hide-details
                ></v-text-field>

                <div class="documentation-grid">
                  <v-card
                      v-for="(item, index) in filteredDocItems"
                      :key="index"
                      class="doc-item-card"
                      :class="{ 'doc-item-active': selectedConfigIndex === getOriginalIndex(item) }"
                      elevation="1"
                      @click="selectConfigItem(getOriginalIndex(item))"
                  >
                    <v-card-text class="doc-item-content">
                      <!-- 配置名称和类型 -->
                      <div class="doc-item-header">
                        <div class="config-name-section">
                          <h4 class="config-name">{{ item.name }}</h4>
                          <div class="config-badges">
                            <v-chip
                                x-small
                                :color="getTypeColor(item.type)"
                                dark
                                class="type-chip"
                            >
                              {{ item.type }}
                            </v-chip>
                            <v-chip
                                v-if="item.required"
                                x-small
                                color="error"
                                dark
                                class="required-chip"
                            >
                              <v-icon x-small class="mr-1">mdi-asterisk</v-icon>
                              必填
                            </v-chip>
                          </div>
                        </div>
                        <v-btn
                            icon
                            x-small
                            @click.stop="focusConfigField(item.name)"
                            color="primary"
                            title="跳转到编辑字段"
                        >
                          <v-icon small>mdi-cursor-pointer</v-icon>
                        </v-btn>
                      </div>

                      <!-- 描述 -->
                      <p class="config-description">{{ item.desc || '暂无描述信息' }}</p>

                      <!-- 默认值 -->
                      <div class="config-value-section">
                        <span class="value-label">默认值:</span>
                        <code class="config-value-display">
                          {{ formatConfigValue(item.defaultValue) }}
                        </code>
                      </div>

                      <!-- 当前值对比 -->
                      <div v-if="getCurrentValue(item.name) !== item.defaultValue" class="current-value-section">
                        <span class="value-label">当前值:</span>
                        <code class="config-current-value">
                          {{ formatConfigValue(getCurrentValue(item.name)) }}
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
                          枚举值 ({{ item.validation.allowedValues.length }})
                        </v-chip>
                        <v-chip
                            v-if="item.validation.pattern"
                            x-small
                            color="purple"
                            outlined
                            class="validation-chip"
                            @click.stop="showValidationDialog(item)"
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
                            @click.stop="showValidationDialog(item)"
                        >
                          <v-icon x-small class="mr-1">mdi-numeric</v-icon>
                          数值范围
                        </v-chip>
                      </div>
                    </v-card-text>
                  </v-card>

                  <!-- 搜索无结果 -->
                  <div v-if="filteredDocItems.length === 0 && docSearchQuery" class="no-search-results">
                    <v-icon color="grey" size="48">mdi-file-search-outline</v-icon>
                    <p class="grey--text mt-2">未找到匹配的配置项</p>
                  </div>
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
                  <span class="header-text">配置编辑器</span>
                  <v-chip
                      x-small
                      color="primary"
                      dark
                      class="current-config-chip"
                  >
                    <v-icon x-small class="mr-1">mdi-cog</v-icon>
                    {{ getConfigDisplayName(currentConfigId) }}
                  </v-chip>
                </div>
                <v-spacer></v-spacer>
                <div class="editor-status">
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
                  <v-chip
                      v-if="hasChanges"
                      small
                      color="orange"
                      dark
                      class="changes-status-chip ml-2"
                  >
                    <v-icon small class="mr-1">mdi-pencil</v-icon>
                    {{ Object.keys(configChanges).length }} 项待保存
                  </v-chip>
                </div>
              </v-card-title>

              <v-card-text class="editor-content">
                <div v-if="loadingCurrentConfig" class="loading-state">
                  <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
                  <p class="loading-text">正在加载配置数据...</p>
                </div>

                <div v-else-if="!currentConfigId" class="empty-selection-state">
                  <v-icon size="64" color="grey lighten-1">mdi-cursor-pointer</v-icon>
                  <h3 class="empty-title">请选择配置实例</h3>
                  <p class="empty-text">从左侧配置列表中选择一个配置进行编辑</p>
                </div>

                <v-form v-else ref="configForm" v-model="isFormValid" class="config-form">
                  <!-- 快速操作栏 -->
                  <div class="quick-actions mb-4">
                    <v-btn
                        small
                        color="info"
                        outlined
                        @click="resetToDefaults"
                        :disabled="loadingCurrentConfig"
                        class="mr-2"
                    >
                      <v-icon small class="mr-1">mdi-backup-restore</v-icon>
                      <div style="color: #1a1a1a">恢复默认值</div>
                    </v-btn>
                    <v-btn
                        small
                        color="secondary"
                        outlined
                        @click="showJsonEditor = !showJsonEditor"
                        class="mr-2"
                    >
                      <v-icon small class="mr-1">mdi-code-json</v-icon>
                      <div style="color: #1a1a1a">{{ showJsonEditor ? '表单模式' : 'JSON模式' }}</div>

                    </v-btn>
                    <v-btn
                        small
                        color="success"
                        outlined
                        @click="validateAllFields"
                        :loading="validating"
                    >
                      <v-icon small class="mr-1">mdi-check-all</v-icon>
                      <div style="color: #1a1a1a">验证所有字段</div>
                    </v-btn>
                  </div>

                  <!-- JSON编辑器 -->
                  <div v-if="showJsonEditor" class="json-editor-section">
                    <v-textarea
                        v-model="jsonConfigString"
                        label="JSON配置"
                        outlined
                        rows="20"
                        :error-messages="jsonError"
                        @input="onJsonChange"
                        class="json-textarea"
                    >
                      <template v-slot:prepend-inner>
                        <v-icon color="primary">mdi-code-json</v-icon>
                      </template>
                    </v-textarea>
                  </div>

                  <!-- 表单编辑器 -->
                  <div v-else class="form-editor-section">
                    <div
                        v-for="(item, index) in structuredConfigItems"
                        :key="index"
                        :ref="`configField_${item.name}`"
                        class="config-form-item"
                        :class="{
                          'form-item-highlighted': selectedConfigIndex === index,
                          'form-item-error': hasFieldError(item.name),
                          'form-item-changed': hasFieldChanged(item.name)
                        }"
                    >
                      <!-- 字段标题 -->
                      <div class="field-header">
                        <div class="field-title">
                          <v-icon
                              :color="item.required ? 'error' : 'grey'"
                              small
                              class="mr-2"
                          >
                            {{ item.required ? 'mdi-asterisk' : 'mdi-circle-outline' }}
                          </v-icon>
                          <span class="field-name">{{ item.name }}</span>
                          <v-chip
                              v-if="hasFieldChanged(item.name)"
                              x-small
                              color="orange"
                              dark
                              class="ml-2"
                          >
                            已修改
                          </v-chip>
                        </div>
                        <div class="field-actions">
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn
                                  icon
                                  x-small
                                  @click="resetField(item.name, item.defaultValue)"
                                  v-bind="attrs"
                                  v-on="on"
                                  :disabled="!hasFieldChanged(item.name)"
                              >
                                <v-icon small>mdi-undo</v-icon>
                              </v-btn>
                            </template>
                            <span>重置为默认值</span>
                          </v-tooltip>
                        </div>
                      </div>

                      <!-- 字符串类型 -->
                      <v-text-field
                          v-if="item.type === 'string'"
                          v-model="configValues[item.name]"
                          :label="`输入 ${item.name}`"
                          :placeholder="formatConfigValue(item.defaultValue)"
                          :required="item.required"
                          :rules="getValidationRules(item)"
                          :error-messages="getFieldErrors(item.name)"
                          outlined
                          dense
                          class="config-input"
                          @focus="selectConfigItem(index)"
                          @input="onConfigChange"
                          clearable
                      >
                        <template v-slot:append>
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-icon
                                  v-if="item.validation?.pattern"
                                  small
                                  color="purple"
                                  v-bind="attrs"
                                  v-on="on"
                                  @click="showValidationDialog(item)"
                              >
                                mdi-help-circle
                              </v-icon>
                            </template>
                            <span>查看验证规则</span>
                          </v-tooltip>
                        </template>
                      </v-text-field>

                      <!-- 数字类型 (integer/double) -->
                      <v-text-field
                          v-else-if="item.type === 'integer' || item.type === 'double'"
                          v-model.number="configValues[item.name]"
                          :label="`输入 ${item.name}`"
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
                          clearable
                      >
                        <template v-slot:append>
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-icon
                                  v-if="item.validation?.minValue !== undefined || item.validation?.maxValue !== undefined"
                                  small
                                  color="blue"
                                  v-bind="attrs"
                                  v-on="on"
                                  @click="showValidationDialog(item)"
                              >
                                mdi-help-circle
                              </v-icon>
                            </template>
                            <span>查看数值范围</span>
                          </v-tooltip>
                        </template>
                      </v-text-field>

                      <!-- 布尔类型 -->
                      <div v-else-if="item.type === 'boolean'" class="boolean-field">
                        <v-switch
                            v-model="configValues[item.name]"
                            :label="`${item.name} ${configValues[item.name] ? '(开启)' : '(关闭)'}`"
                            :required="item.required"
                            :rules="getValidationRules(item)"
                            class="config-switch"
                            @focus="selectConfigItem(index)"
                            @change="onConfigChange"
                            color="success"
                        ></v-switch>
                      </div>

                      <!-- 枚举选择 -->
                      <v-select
                          v-else-if="item.validation && item.validation.allowedValues"
                          v-model="configValues[item.name]"
                          :label="`选择 ${item.name}`"
                          :items="item.validation.allowedValues"
                          :placeholder="`请选择...`"
                          :required="item.required"
                          :rules="getValidationRules(item)"
                          :error-messages="getFieldErrors(item.name)"
                          outlined
                          dense
                          class="config-select"
                          @focus="selectConfigItem(index)"
                          @change="onConfigChange"
                          clearable
                      >
                        <template v-slot:append-outer>
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn
                                  icon
                                  small
                                  @click="showValidationDialog(item)"
                                  v-bind="attrs"
                                  v-on="on"
                              >
                                <v-icon small color="orange">mdi-format-list-bulleted</v-icon>
                              </v-btn>
                            </template>
                            <span>查看所有选项</span>
                          </v-tooltip>
                        </template>
                      </v-select>

                      <!-- 对象/数组类型 -->
                      <v-textarea
                          v-else-if="item.type === 'object' || item.type === 'array'"
                          v-model="configValues[item.name]"
                          :label="`编辑 ${item.name} (JSON格式)`"
                          :placeholder="formatConfigValue(item.defaultValue)"
                          :required="item.required"
                          :rules="getValidationRules(item)"
                          :error-messages="getFieldErrors(item.name)"
                          outlined
                          rows="4"
                          class="config-textarea"
                          @focus="selectConfigItem(index)"
                          @input="onConfigChange"
                          auto-grow
                      >
                        <template v-slot:append>
                          <v-tooltip bottom>
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn
                                  icon
                                  small
                                  @click="formatJsonField(item.name)"
                                  v-bind="attrs"
                                  v-on="on"
                              >
                                <v-icon small color="primary">mdi-code-braces</v-icon>
                              </v-btn>
                            </template>
                            <span>格式化JSON</span>
                          </v-tooltip>
                        </template>
                      </v-textarea>

                      <!-- 配置项描述 -->
                      <div class="config-item-description">
                        <v-icon small color="grey" class="mr-1">mdi-information-outline</v-icon>
                        <span class="description-text">{{ item.desc || '暂无描述信息' }}</span>
                      </div>
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

    <!-- 新建配置对话框 -->
    <v-dialog v-model="addConfigDialog" max-width="600">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="primary" class="mr-2">mdi-plus-circle</v-icon>
          新建配置实例
        </v-card-title>
        <v-card-text>
          <v-form ref="addConfigForm" v-model="addConfigValid">
            <div class="add-config-content">
              <v-text-field
                  v-model="newConfigName"
                  label="配置名称 (可选)"
                  placeholder="为新配置起个名字..."
                  outlined
                  clearable
                  :rules="configNameRules"
                  class="mb-4"
              ></v-text-field>

              <v-select
                  v-model="newConfigSource"
                  :items="configSourceOptions"
                  label="基于配置"
                  outlined
                  :rules="[v => !!v || '请选择基础配置']"
                  class="mb-4"
              ></v-select>

              <v-alert type="info" outlined class="mb-4">
                新配置将复制所选配置的所有值，您可以在创建后进行修改
              </v-alert>

              <div class="preview-section">
                <h4 class="preview-title">配置预览</h4>
                <div class="preview-content">
                  <div class="preview-item">
                    <span class="preview-label">配置名称:</span>
                    <span class="preview-value">{{ newConfigName || '自动生成' }}</span>
                  </div>
                  <div class="preview-item">
                    <span class="preview-label">基于配置:</span>
                    <span class="preview-value">{{ getConfigDisplayName(newConfigSource) }}</span>
                  </div>
                  <div class="preview-item">
                    <span class="preview-label">配置项数:</span>
                    <span class="preview-value">{{ structuredConfigItems.length }} 项</span>
                  </div>
                </div>
              </div>
            </div>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="cancelAddConfig">
            取消
          </v-btn>
          <v-btn
              color="primary"
              @click="confirmAddConfiguration"
              :loading="addingConfig"
              :disabled="!addConfigValid"
          >
            <v-icon class="mr-1">mdi-plus</v-icon>
            创建配置
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 复制配置对话框 -->
    <v-dialog v-model="cloneConfigDialog" max-width="600">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="secondary" class="mr-2">mdi-content-duplicate</v-icon>
          复制配置实例
        </v-card-title>
        <v-card-text>
          <v-form ref="cloneConfigForm" v-model="cloneConfigValid">
            <div class="clone-config-content">
              <v-text-field
                  v-model="cloneConfigName"
                  label="新配置名称 (可选)"
                  placeholder="为复制的配置起个名字..."
                  outlined
                  clearable
                  :rules="configNameRules"
                  class="mb-4"
              ></v-text-field>

              <v-alert type="info" outlined class="mb-4">
                将复制 "{{ getConfigDisplayName(currentConfigId) }}" 的所有配置项
              </v-alert>

              <div class="clone-preview-section">
                <h4 class="preview-title">复制预览</h4>
                <div class="preview-content">
                  <div class="preview-item">
                    <span class="preview-label">源配置:</span>
                    <span class="preview-value">{{ getConfigDisplayName(currentConfigId) }}</span>
                  </div>
                  <div class="preview-item">
                    <span class="preview-label">新配置名:</span>
                    <span class="preview-value">{{ cloneConfigName || '自动生成' }}</span>
                  </div>
                  <div class="preview-item">
                    <span class="preview-label">包含修改:</span>
                    <span class="preview-value">{{ hasChanges ? '是' : '否' }}</span>
                  </div>
                </div>
              </div>

              <v-alert v-if="hasChanges" type="warning" outlined>
                注意：当前配置有未保存的修改，复制将基于已保存的版本
              </v-alert>
            </div>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="cancelCloneConfig">
            取消
          </v-btn>
          <v-btn
              color="secondary"
              @click="confirmCloneConfiguration"
              :loading="cloningConfig"
              :disabled="!cloneConfigValid"
          >
            <v-icon class="mr-1">mdi-content-duplicate</v-icon>
            复制配置
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除配置确认对话框 -->
    <v-dialog v-model="deleteConfigDialog" max-width="500">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="error" class="mr-2">mdi-delete-alert</v-icon>
          确认删除配置
        </v-card-title>
        <v-card-text>
          <div class="delete-warning">
            <p class="mb-3">您确定要删除以下配置吗？</p>
            <div class="config-info-card">
              <div class="config-name">{{ getConfigDisplayName(configToDelete) }}</div>
              <div class="config-details">{{ getConfigSummary(configToDelete) }}</div>
            </div>
            <v-alert type="error" outlined class="mt-4">
              <div class="warning-content">
                <v-icon color="error" class="mr-2">mdi-alert</v-icon>
                <div>
                  <strong>此操作不可撤销！</strong><br>
                  删除后所有配置数据将永久丢失
                </div>
              </div>
            </v-alert>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="cancelDeleteConfig">
            取消
          </v-btn>
          <v-btn
              color="error"
              @click="confirmDeleteConfiguration"
              :loading="deletingConfig"
          >
            <v-icon class="mr-1">mdi-delete</v-icon>
            确认删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 验证规则详情弹窗 -->
    <v-dialog v-model="validationDialog" max-width="700" class="validation-dialog">
      <v-card class="validation-card">
        <v-card-title class="validation-header">
          <div class="validation-title-content">
            <v-icon class="mr-3" color="orange" size="28">mdi-shield-check</v-icon>
            <div>
              <h3 class="validation-title">{{ selectedConfig?.name }}</h3>
              <p class="validation-subtitle">配置项验证规则详情</p>
            </div>
          </div>
        </v-card-title>

        <v-card-text class="validation-content">
          <!-- 基本信息 -->
          <div class="validation-section">
            <h4 class="validation-section-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">数据类型:</span>
                <v-chip small :color="getTypeColor(selectedConfig?.type)" dark>
                  {{ selectedConfig?.type }}
                </v-chip>
              </div>
              <div class="info-item">
                <span class="info-label">是否必填:</span>
                <v-chip small :color="selectedConfig?.required ? 'error' : 'grey'" dark>
                  {{ selectedConfig?.required ? '是' : '否' }}
                </v-chip>
              </div>
              <div class="info-item">
                <span class="info-label">默认值:</span>
                <code class="default-value">{{ formatConfigValue(selectedConfig?.defaultValue) }}</code>
              </div>
            </div>
          </div>

          <!-- 允许值列表 -->
          <div v-if="selectedConfig?.validation?.allowedValues" class="validation-section">
            <h4 class="validation-section-title">
              <v-icon color="orange" class="mr-2">mdi-format-list-bulleted</v-icon>
              允许的值 ({{ selectedConfig.validation.allowedValues.length }} 个)
            </h4>
            <div class="allowed-values-grid">
              <v-chip
                  v-for="(value, index) in selectedConfig.validation.allowedValues"
                  :key="index"
                  class="allowed-value-chip"
                  color="primary"
                  outlined
                  large
                  @click="selectEnumValue(value)"
              >
                {{ value }}
                <v-icon right small>mdi-cursor-pointer</v-icon>
              </v-chip>
            </div>
            <p class="help-text mt-2">
              <v-icon small color="grey">mdi-lightbulb-outline</v-icon>
              点击任意选项可快速设置为当前值
            </p>
          </div>

          <!-- 数值范围 -->
          <div v-if="selectedConfig?.validation?.minValue !== undefined || selectedConfig?.validation?.maxValue !== undefined"
               class="validation-section">
            <h4 class="validation-section-title">
              <v-icon color="blue" class="mr-2">mdi-numeric</v-icon>
              数值范围限制
            </h4>
            <div class="range-display">
              <v-chip color="blue" outlined large>
                <v-icon left small>mdi-greater-than-or-equal</v-icon>
                最小值: {{ selectedConfig.validation.minValue ?? '无限制' }}
              </v-chip>
              <v-chip color="blue" outlined large>
                <v-icon left small>mdi-less-than-or-equal</v-icon>
                最大值: {{ selectedConfig.validation.maxValue ?? '无限制' }}
              </v-chip>
            </div>
          </div>

          <!-- 正则表达式 -->
          <div v-if="selectedConfig?.validation?.pattern" class="validation-section">
            <h4 class="validation-section-title">
              <v-icon color="purple" class="mr-2">mdi-regex</v-icon>
              格式验证规则
            </h4>
            <div class="pattern-container">
              <code class="pattern-code">{{ selectedConfig.validation.pattern }}</code>
              <v-btn
                  small
                  color="primary"
                  outlined
                  @click="testPattern"
                  class="mt-2"
              >
                <v-icon small class="mr-1">mdi-test-tube</v-icon>
                测试正则表达式
              </v-btn>
            </div>
          </div>
        </v-card-text>

        <v-card-actions class="validation-actions">
          <v-spacer></v-spacer>
          <v-btn
              color="primary"
              @click="validationDialog = false"
              class="close-btn"
          >
            <v-icon class="mr-1">mdi-check</v-icon>
            确定
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 保存确认对话框 -->
    <v-dialog v-model="saveConfirmDialog" max-width="600">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="success" class="mr-2">mdi-content-save</v-icon>
          确认保存配置
        </v-card-title>
        <v-card-text>
          <div class="save-confirm-content">
            <p class="mb-3">您即将保存配置 <strong>{{ getConfigDisplayName(currentConfigId) }}</strong> 的以下更改：</p>

            <div class="changes-summary">
              <div class="changes-header">
                <v-icon color="orange" class="mr-2">mdi-pencil</v-icon>
                <span>修改摘要 ({{ Object.keys(configChanges).length }} 项)</span>
              </div>

              <div class="changes-list">
                <div v-for="(change, key) in configChanges" :key="key" class="change-item">
                  <div class="change-header">
                    <strong class="field-name">{{ key }}</strong>
                    <v-chip x-small color="grey" outlined>
                      {{ getFieldType(key) }}
                    </v-chip>
                  </div>
                  <div class="change-values">
                    <div class="old-value">
                      <span class="value-label">原值:</span>
                      <code class="value-code old">{{ formatConfigValue(change.oldValue) }}</code>
                    </div>
                    <v-icon small class="mx-2">mdi-arrow-right</v-icon>
                    <div class="new-value">
                      <span class="value-label">新值:</span>
                      <code class="value-code new">{{ formatConfigValue(change.newValue) }}</code>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <v-alert type="info" outlined class="mt-4">
              <div class="save-info">
                <v-icon color="info" class="mr-2">mdi-information</v-icon>
                <div>
                  保存后配置将立即生效，请确认所有修改都是正确的
                </div>
              </div>
            </v-alert>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="saveConfirmDialog = false">
            取消
          </v-btn>
          <v-btn
              color="success"
              @click="confirmSave"
              :loading="saving"
          >
            <v-icon class="mr-1">mdi-content-save</v-icon>
            确认保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 未保存更改警告对话框 -->
    <v-dialog v-model="unsavedChangesDialog" max-width="500">
      <v-card>
        <v-card-title class="headline">
          <v-icon color="warning" class="mr-2">mdi-alert</v-icon>
          有未保存的更改
        </v-card-title>
        <v-card-text>
          <p>当前配置有 {{ Object.keys(configChanges).length }} 项未保存的更改，您要：</p>
          <div class="unsaved-actions mt-4">
            <v-btn
                color="success"
                @click="saveAndContinue"
                :loading="saving"
                class="mb-2 mr-2"
                block
            >
              <v-icon class="mr-1">mdi-content-save</v-icon>
              <div style="color: #1a1a1a">保存更改并继续</div>

            </v-btn>
            <v-btn
                color="warning"
                @click="discardAndContinue"
                class="mb-2"
                block
                outlined
            >
              <v-icon class="mr-1">mdi-delete-sweep</v-icon>
              <div style="color: #1a1a1a">放弃更改并继续</div>

            </v-btn>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" text @click="cancelAction">
            取消操作
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 成功/错误提示 Snackbar -->
    <v-snackbar
        v-model="showSnackbar"
        :color="snackbarColor"
        :timeout="snackbarTimeout"
        top
        right
    >
      <v-icon class="mr-2">{{ snackbarIcon }}</v-icon>
      {{ snackbarMessage }}
      <template v-slot:action="{ attrs }">
        <v-btn
            text
            v-bind="attrs"
            @click="showSnackbar = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, defineProps, defineEmits, nextTick } from 'vue'
import {
  getCubeConfiguration,
  getAllCubeConfigurations,
  getCubeConfigurationById,
  updateCubeConfiguration,
  updateCubeConfigurationMulti,
  removeCubeConfiguration,
  cloneCubeConfiguration
} from '@/api/plugin/pluginApi.js'

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
const addConfigDialog = ref(false)
const cloneConfigDialog = ref(false)
const deleteConfigDialog = ref(false)
const unsavedChangesDialog = ref(false)
const selectedConfig = ref(null)
const selectedConfigIndex = ref(-1)
const configValues = ref({})
const originalConfigValues = ref({})
const fieldErrors = ref({})
const isFormValid = ref(true)
const addConfigValid = ref(true)
const cloneConfigValid = ref(true)
const saving = ref(false)
const loading = ref(false)
const loadingConfigs = ref(false)
const loadingCurrentConfig = ref(false)
const addingConfig = ref(false)
const cloningConfig = ref(false)
const deletingConfig = ref(false)
const validating = ref(false)

// 搜索相关
const configSearchQuery = ref('')
const docSearchQuery = ref('')

// 多配置相关数据
const allConfigIds = ref(['default'])
const currentConfigId = ref('default')
const allConfigurations = ref({})
const configToDelete = ref('')

// 新增配置相关
const newConfigName = ref('')
const newConfigSource = ref('default')
const cloneConfigName = ref('')

// JSON编辑器
const showJsonEditor = ref(false)
const jsonConfigString = ref('')
const jsonError = ref('')

// 消息提示
const showSnackbar = ref(false)
const snackbarMessage = ref('')
const snackbarColor = ref('success')
const snackbarTimeout = ref(4000)
const snackbarIcon = ref('mdi-check')

// 未保存更改处理
const pendingAction = ref(null)

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

// 搜索过滤
const filteredConfigIds = computed(() => {
  if (!configSearchQuery.value) return allConfigIds.value
  return allConfigIds.value.filter(id =>
      getConfigDisplayName(id).toLowerCase().includes(configSearchQuery.value.toLowerCase())
  )
})

const filteredDocItems = computed(() => {
  if (!docSearchQuery.value) return structuredConfigItems.value
  return structuredConfigItems.value.filter(item =>
      item.name.toLowerCase().includes(docSearchQuery.value.toLowerCase()) ||
      (item.desc && item.desc.toLowerCase().includes(docSearchQuery.value.toLowerCase()))
  )
})

// 配置源选项
const configSourceOptions = computed(() => {
  return allConfigIds.value.map(id => ({
    text: getConfigDisplayName(id),
    value: id
  }))
})

// 配置名称验证规则
const configNameRules = [
  v => !v || v.length <= 50 || '配置名称不能超过50个字符',
  v => !v || /^[a-zA-Z0-9\u4e00-\u9fa5_-\s]*$/.test(v) || '配置名称只能包含字母、数字、中文、下划线和横线'
]

// 监听对话框打开状态
watch(localDialog, (newVal) => {
  if (newVal && props.pluginData?.id) {
    loadAllConfigurations()
  }
})

// 监听当前配置ID变化
watch(currentConfigId, (newConfigId) => {
  if (newConfigId && props.pluginData?.id) {
    loadSpecificConfiguration(newConfigId)
  }
})

// 监听配置值变化，更新JSON字符串
watch(configValues, (newValues) => {
  if (showJsonEditor.value) {
    try {
      jsonConfigString.value = JSON.stringify(newValues, null, 2)
      jsonError.value = ''
    } catch (error) {
      jsonError.value = '无法序列化配置为JSON'
    }
  }
}, { deep: true })

// 生命周期
onMounted(() => {
  if (localDialog.value && props.pluginData?.id) {
    loadAllConfigurations()
  }
})

// 方法定义

// 加载所有配置列表
const loadAllConfigurations = async () => {
  if (!props.pluginData?.id) return

  loadingConfigs.value = true
  try {
    const response = await getAllCubeConfigurations(props.pluginData.id)
    if (response.code === 200) {
      const configs = response.data || {}
      allConfigurations.value = configs
      allConfigIds.value = Object.keys(configs).length > 0 ? Object.keys(configs) : ['default']

      // 如果当前选择的配置不存在，选择第一个可用配置
      if (!allConfigIds.value.includes(currentConfigId.value)) {
        currentConfigId.value = allConfigIds.value[0] || 'default'
      }
    }
  } catch (error) {
    console.error('加载配置列表失败:', error)
    showErrorMessage('加载配置列表失败: ' + error.message)
    allConfigIds.value = ['default']
    currentConfigId.value = 'default'
  } finally {
    loadingConfigs.value = false
  }
}

// 加载特定配置
const loadSpecificConfiguration = async (configId) => {
  if (!props.pluginData?.id || !configId) return

  loadingCurrentConfig.value = true
  try {
    let response
    if (configId === 'default') {
      response = await getCubeConfiguration(props.pluginData.id)
    } else {
      response = await getCubeConfigurationById(props.pluginData.id, configId)
    }

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

      // 更新JSON编辑器
      if (showJsonEditor.value) {
        jsonConfigString.value = JSON.stringify(initialValues, null, 2)
      }
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    showErrorMessage('加载配置失败: ' + error.message)
    // 使用默认值
    const defaultValues = {}
    structuredConfigItems.value.forEach(item => {
      defaultValues[item.name] = item.defaultValue
    })
    configValues.value = { ...defaultValues }
    originalConfigValues.value = { ...defaultValues }
  } finally {
    loadingCurrentConfig.value = false
  }
}

// 选择配置
const selectConfiguration = (configId) => {
  if (hasChanges.value) {
    pendingAction.value = () => {
      currentConfigId.value = configId
    }
    unsavedChangesDialog.value = true
  } else {
    currentConfigId.value = configId
  }
}

// 刷新配置列表
const refreshConfigList = () => {
  loadAllConfigurations()
  showSuccessMessage('配置列表已刷新')
}

// 新增配置
const showAddConfigDialog = () => {
  newConfigName.value = ''
  newConfigSource.value = 'default'
  addConfigDialog.value = true
}

const cancelAddConfig = () => {
  addConfigDialog.value = false
  newConfigName.value = ''
  newConfigSource.value = 'default'
}

const confirmAddConfiguration = async () => {
  addingConfig.value = true
  try {
    const response = await cloneCubeConfiguration({
      cubeId: props.pluginData.id,
      sourceConfigId: newConfigSource.value
    })

    if (response.code === 200) {
      const newConfigId = response.data
      showSuccessMessage(`新配置 "${getConfigDisplayName(newConfigId)}" 创建成功`)

      // 刷新配置列表并选择新配置
      await loadAllConfigurations()
      currentConfigId.value = newConfigId

      addConfigDialog.value = false
      cancelAddConfig()
    } else {
      throw new Error(response.message || '创建配置失败')
    }
  } catch (error) {
    console.error('创建配置失败:', error)
    showErrorMessage('创建配置失败: ' + error.message)
  } finally {
    addingConfig.value = false
  }
}

// 复制配置
const showCloneDialog = () => {
  cloneConfigName.value = ''
  cloneConfigDialog.value = true
}

const cancelCloneConfig = () => {
  cloneConfigDialog.value = false
  cloneConfigName.value = ''
}

const cloneConfiguration = async (sourceConfigId) => {
  try {
    const response = await cloneCubeConfiguration({
      cubeId: props.pluginData.id,
      sourceConfigId: sourceConfigId
    })

    if (response.code === 200) {
      const newConfigId = response.data
      showSuccessMessage(`配置复制成功，新配置ID: ${getConfigDisplayName(newConfigId)}`)

      // 刷新配置列表
      await loadAllConfigurations()
    } else {
      throw new Error(response.message || '复制配置失败')
    }
  } catch (error) {
    console.error('复制配置失败:', error)
    showErrorMessage('复制配置失败: ' + error.message)
  }
}

const confirmCloneConfiguration = async () => {
  cloningConfig.value = true
  try {
    await cloneConfiguration(currentConfigId.value)
    cloneConfigDialog.value = false
    cancelCloneConfig()
  } finally {
    cloningConfig.value = false
  }
}

// 删除配置
const showDeleteConfirmDialog = (configId) => {
  configToDelete.value = configId
  deleteConfigDialog.value = true
}

const cancelDeleteConfig = () => {
  deleteConfigDialog.value = false
  configToDelete.value = ''
}

const confirmDeleteConfiguration = async () => {
  deletingConfig.value = true
  try {
    const response = await removeCubeConfiguration(props.pluginData.id, configToDelete.value)

    if (response.code === 200) {
      showSuccessMessage(`配置 "${getConfigDisplayName(configToDelete.value)}" 删除成功`)

      // 如果删除的是当前配置，切换到默认配置
      if (currentConfigId.value === configToDelete.value) {
        currentConfigId.value = 'default'
      }

      // 刷新配置列表
      await loadAllConfigurations()

      deleteConfigDialog.value = false
    } else {
      throw new Error(response.message || '删除配置失败')
    }
  } catch (error) {
    console.error('删除配置失败:', error)
    showErrorMessage('删除配置失败: ' + error.message)
  } finally {
    deletingConfig.value = false
  }
}

// 保存配置
const saveConfiguration = () => {
  if (!isFormValid.value || !hasChanges.value) return
  saveConfirmDialog.value = true
}

const confirmSave = async () => {
  saving.value = true
  saveConfirmDialog.value = false

  try {
    let response
    if (currentConfigId.value === 'default') {
      // 更新默认配置
      const updateRequest = {
        cubeId: props.pluginData.id,
        configurations: { ...configValues.value }
      }
      response = await updateCubeConfiguration(updateRequest)
    } else {
      // 更新特定配置
      const updateRequest = {
        cubeId: props.pluginData.id,
        configId: currentConfigId.value,
        configurations: { ...configValues.value }
      }
      response = await updateCubeConfigurationMulti(updateRequest)
    }

    if (response.code === 200) {
      originalConfigValues.value = { ...configValues.value }
      emit('configUpdated', response.data)
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

// 重置配置
const resetConfiguration = () => {
  configValues.value = { ...originalConfigValues.value }
  fieldErrors.value = {}
  if (showJsonEditor.value) {
    jsonConfigString.value = JSON.stringify(configValues.value, null, 2)
  }
  showSuccessMessage('已重置为原始值')
}

// 恢复默认值
const resetToDefaults = () => {
  const defaultValues = {}
  structuredConfigItems.value.forEach(item => {
    defaultValues[item.name] = item.defaultValue
  })
  configValues.value = { ...defaultValues }
  if (showJsonEditor.value) {
    jsonConfigString.value = JSON.stringify(defaultValues, null, 2)
  }
  showSuccessMessage('已恢复为默认值')
}

// 关闭对话框
const handleCloseDialog = () => {
  if (hasChanges.value) {
    pendingAction.value = () => {
      resetConfiguration()
      localDialog.value = false
    }
    unsavedChangesDialog.value = true
  } else {
    localDialog.value = false
  }
}

// 未保存更改处理
const saveAndContinue = async () => {
  await confirmSave()
  if (pendingAction.value) {
    pendingAction.value()
    pendingAction.value = null
  }
  unsavedChangesDialog.value = false
}

const discardAndContinue = () => {
  resetConfiguration()
  if (pendingAction.value) {
    pendingAction.value()
    pendingAction.value = null
  }
  unsavedChangesDialog.value = false
}

const cancelAction = () => {
  pendingAction.value = null
  unsavedChangesDialog.value = false
}

const selectConfigItem = (index) => {
  selectedConfigIndex.value = index
}

// 聚焦到配置字段
const focusConfigField = async (fieldName) => {
  selectedConfigIndex.value = structuredConfigItems.value.findIndex(item => item.name === fieldName)

  // 如果在JSON模式，切换到表单模式
  if (showJsonEditor.value) {
    showJsonEditor.value = false
  }

  await nextTick()

  // 滚动到对应字段
  const fieldElement = document.querySelector(`[ref="configField_${fieldName}"]`)
  if (fieldElement) {
    fieldElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

// JSON编辑器相关
const onJsonChange = () => {
  try {
    const parsed = JSON.parse(jsonConfigString.value)
    configValues.value = { ...parsed }
    jsonError.value = ''
    onConfigChange()
  } catch (error) {
    jsonError.value = 'JSON格式错误: ' + error.message
  }
}

// 验证所有字段
const validateAllFields = async () => {
  validating.value = true
  await nextTick()

  if (configForm.value) {
    const isValid = configForm.value.validate()
    if (isValid) {
      showSuccessMessage('所有字段验证通过')
    } else {
      showErrorMessage('存在验证错误的字段')
    }
  }

  validating.value = false
}

// 格式化JSON字段
const formatJsonField = (fieldName) => {
  try {
    const value = configValues.value[fieldName]
    if (typeof value === 'string') {
      const parsed = JSON.parse(value)
      configValues.value[fieldName] = JSON.stringify(parsed, null, 2)
    }
    showSuccessMessage('JSON格式化完成')
  } catch (error) {
    showErrorMessage('JSON格式化失败: ' + error.message)
  }
}

// 重置单个字段
const resetField = (fieldName, defaultValue) => {
  configValues.value[fieldName] = defaultValue
  onConfigChange()
  showSuccessMessage(`已重置 ${fieldName} 为默认值`)
}

// 选择枚举值
const selectEnumValue = (value) => {
  if (selectedConfig.value) {
    configValues.value[selectedConfig.value.name] = value
    onConfigChange()
    validationDialog.value = false
    showSuccessMessage(`已设置 ${selectedConfig.value.name} 为 ${value}`)
  }
}

// 测试正则表达式
const testPattern = () => {
  if (selectedConfig.value?.validation?.pattern) {
    const currentValue = configValues.value[selectedConfig.value.name] || ''
    const regex = new RegExp(selectedConfig.value.validation.pattern)
    const isMatch = regex.test(currentValue)

    if (isMatch) {
      showSuccessMessage('当前值匹配正则表达式')
    } else {
      showErrorMessage('当前值不匹配正则表达式')
    }
  }
}

const configForm = ref()
const addConfigForm = ref()
const cloneConfigForm = ref()

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

// 工具方法
const getConfigDisplayName = (configId) => {
  if (configId === 'default') return '默认配置'
  return `配置-${configId.substring(0, 8)}`
}

const getConfigStatus = (configId) => {
  if (configId === 'default') return '系统默认'
  if (configId === currentConfigId.value) return '当前使用'
  return '备用配置'
}

const getConfigStatusColor = (configId) => {
  if (configId === 'default') return 'primary'
  if (configId === currentConfigId.value) return 'success'
  return 'grey'
}

const getConfigStatusIcon = (configId) => {
  if (configId === 'default') return 'mdi-star'
  if (configId === currentConfigId.value) return 'mdi-check-circle'
  return 'mdi-circle'
}

const getConfigSummary = (configId) => {
  const config = allConfigurations.value[configId]
  if (!config) return '暂无配置数据'

  const configCount = Object.keys(config).length
  return `${configCount} 个配置项`
}

const getConfigLastModified = (configId) => {
  // 这里可以根据实际情况返回最后修改时间
  return ''
}

const hasConfigChanges = (configId) => {
  return configId === currentConfigId.value && hasChanges.value
}

const getCurrentValue = (fieldName) => {
  return configValues.value[fieldName]
}

const getOriginalIndex = (item) => {
  return structuredConfigItems.value.findIndex(original => original.name === item.name)
}

const hasFieldError = (fieldName) => {
  return fieldErrors.value[fieldName] && fieldErrors.value[fieldName].length > 0
}

const hasFieldChanged = (fieldName) => {
  return configValues.value[fieldName] !== originalConfigValues.value[fieldName]
}

const getFieldType = (fieldName) => {
  const item = structuredConfigItems.value.find(item => item.name === fieldName)
  return item?.type || 'unknown'
}

// 原有方法保持不变
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
  snackbarMessage.value = message
  snackbarColor.value = 'success'
  snackbarIcon.value = 'mdi-check-circle'
  snackbarTimeout.value = 3000
  showSnackbar.value = true
}

const showErrorMessage = (message) => {
  snackbarMessage.value = message
  snackbarColor.value = 'error'
  snackbarIcon.value = 'mdi-alert-circle'
  snackbarTimeout.value = 5000
  showSnackbar.value = true
}
</script>

<style scoped lang="scss">
/* 保持原有基础样式，新增以下优化样式 */

/* 主布局保持三列 */
.config-main-layout {
  display: grid;
  grid-template-columns: 350px 1fr 1fr;
  gap: 24px;
  min-height: 70vh;
}

/* 搜索框样式 */
.config-search,
.doc-search {
  margin-bottom: 16px;
}

/* 配置列表项增强 */
.config-item-card {
  transition: all 0.3s ease;
  border: 2px solid #E6D3A3;
  cursor: pointer;
  border-radius: 8px !important;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%) !important;
}

.config-item-card:hover,
.config-item-active {
  border-color: #D4A574;
  background: linear-gradient(135deg, #F4E4BC 0%, #E6D3A3 100%) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 165, 116, 0.3);
}

.config-item-modified {
  border-left: 4px solid #FF9800 !important;
}

.config-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.modified-indicator {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.config-chips {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.config-meta {
  margin-top: 12px;
  font-size: 0.85rem;
}

.config-summary,
.config-time {
  display: flex;
  align-items: center;
  color: #8B7355;
  margin-bottom: 4px;
}

/* 无搜索结果样式 */
.no-search-results {
  text-align: center;
  padding: 40px 20px;
  color: #9E9E9E;
}

/* 编辑器增强样式 */
.editor-status {
  display: flex;
  align-items: center;
}

.empty-selection-state {
  text-align: center;
  padding: 80px 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  border-radius: 12px;
  border: 2px dashed #E6D3A3;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px;
  background: rgba(230, 211, 163, 0.1);
  border-radius: 8px;
  border: 1px solid #E6D3A3;
}

.json-editor-section {
  margin-top: 16px;
}

.json-textarea {
  font-family: 'Courier New', monospace;
}

/* 字段增强样式 */
.config-form-item {
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #E6D3A3;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%) !important;
  margin-bottom: 20px;
}

.form-item-highlighted {
  border-color: #D4A574;
  background: rgba(244, 228, 188, 0.3);
  box-shadow: 0 4px 12px rgba(212, 165, 116, 0.2);
}

.form-item-error {
  border-color: #F44336;
  background: rgba(244, 67, 54, 0.05);
}

.form-item-changed {
  border-left: 4px solid #FF9800;
}

.field-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.field-title {
  display: flex;
  align-items: center;
}

.field-name {
  font-weight: 600;
  color: #2D5A27;
  font-size: 1rem;
}

.field-actions {
  display: flex;
  gap: 4px;
}

.boolean-field {
  margin: 16px 0;
}

/* 文档项增强 */
.config-badges {
  display: flex;
  gap: 6px;
  align-items: center;
}

.required-chip {
  font-size: 0.7rem;
  height: 20px;
}

.current-value-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.config-current-value {
  background: linear-gradient(135deg, #E8F5E8, #C8E6C9);
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #2E7D32;
  border: 2px solid #4CAF50;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

/* 对话框增强样式 */
.add-config-content,
.clone-config-content {
  padding: 16px 0;
}

.preview-section,
.clone-preview-section {
  background: rgba(230, 211, 163, 0.1);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #E6D3A3;
}

.preview-title {
  margin: 0 0 12px 0;
  color: #2D5A27;
  font-weight: 600;
}

.preview-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-label {
  font-weight: 500;
  color: #8B7355;
}

.preview-value {
  font-weight: 600;
  color: #2D5A27;
}

/* 删除确认对话框样式 */
.delete-warning {
  padding: 8px 0;
}

.config-info-card {
  background: rgba(244, 67, 54, 0.05);
  border: 1px solid #F44336;
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0;
}

.config-name {
  font-weight: 600;
  color: #F44336;
  font-size: 1.1rem;
  margin-bottom: 4px;
}

.config-details {
  color: #8B7355;
  font-size: 0.9rem;
}

.warning-content {
  display: flex;
  align-items: flex-start;
}

/* 验证对话框增强 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 0.9rem;
  color: #8B7355;
  font-weight: 500;
}

.default-value {
  background: linear-gradient(135deg, #FEFEFE, #F4E4BC);
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #2D5A27;
  border: 1px solid #E6D3A3;
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.allowed-value-chip {
  cursor: pointer;
  transition: all 0.3s ease;
}

.allowed-value-chip:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(74, 155, 142, 0.4);
}

.help-text {
  font-size: 0.85rem;
  color: #8B7355;
  font-style: italic;
  display: flex;
  align-items: center;
}

.pattern-container {
  background: rgba(230, 211, 163, 0.1);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #E6D3A3;
}

/* 保存确认对话框增强 */
.save-confirm-content {
  padding: 8px 0;
}

.changes-summary {
  background: rgba(255, 152, 0, 0.05);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #FF9800;
  margin: 16px 0;
}

.changes-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #2D5A27;
}

.changes-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.change-item {
  background: #FEFEFE;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #E6D3A3;
}

.change-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.field-name {
  font-weight: 600;
  color: #2D5A27;
}

.change-values {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 12px;
  align-items: center;
}

.old-value,
.new-value {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.value-label {
  font-size: 0.8rem;
  color: #8B7355;
  font-weight: 500;
}

.value-code {
  font-family: 'Courier New', monospace;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
}

.value-code.old {
  background: rgba(244, 67, 54, 0.1);
  color: #F44336;
  text-decoration: line-through;
}

.value-code.new {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  font-weight: 600;
}

.save-info {
  display: flex;
  align-items: flex-start;
}

/* 未保存更改对话框 */
.unsaved-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* Snackbar位置调整 */
.v-snackbar {
  z-index: 9999;
}

/* 响应式设计调整 */
@media (max-width: 1400px) {
  .config-main-layout {
    grid-template-columns: 300px 1fr 1fr;
  }
}

@media (max-width: 1200px) {
  .config-main-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .config-list-panel {
    order: 1;
  }

  .config-editor-panel {
    order: 2;
  }

  .config-documentation-panel {
    order: 3;
  }
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    gap: 8px;
  }

  .add-btn, .clone-btn, .save-btn, .reset-btn {
    min-width: 100px;
    font-size: 0.8rem;
  }

  .quick-actions {
    flex-direction: column;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .change-values {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}

/* 原有样式保持不变... */
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

.add-btn {
  background: linear-gradient(135deg, #2196F3, #1976D2) !important;
  color: white !important;
  min-width: 120px;
  font-weight: 600;
  border-radius: 8px;
}

.clone-btn {
  background: linear-gradient(135deg, #9C27B0, #7B1FA2) !important;
  color: white !important;
  min-width: 120px;
  font-weight: 600;
  border-radius: 8px;
}

.config-list-card,
.documentation-card,
.editor-card {
  height: 100%;
  border-radius: 12px !important;
  border: 1px solid #E6D3A3;
  overflow: hidden;
}

.config-list-header,
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

.config-list-content,
.documentation-content {
  padding: 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  height: calc(100% - 80px);
  overflow-y: auto;
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

.editor-content {
  padding: 20px;
  background: linear-gradient(135deg, #FEFEFE 0%, #F4E4BC 100%);
  height: calc(100% - 80px);
  overflow-y: auto;
}

.config-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.config-dialog-content {
  overflow-y: auto;
}

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

.doc-item-header {
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

/* 滚动条样式 */
.config-list-content::-webkit-scrollbar,
.documentation-content::-webkit-scrollbar,
.editor-content::-webkit-scrollbar {
  width: 8px;
}

.config-list-content::-webkit-scrollbar-track,
.documentation-content::-webkit-scrollbar-track,
.editor-content::-webkit-scrollbar-track {
  background: #F4E4BC;
  border-radius: 4px;
}

.config-list-content::-webkit-scrollbar-thumb,
.documentation-content::-webkit-scrollbar-thumb,
.editor-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #D4A574, #8B7355);
  border-radius: 4px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-text {
  font-size: 1.1rem;
  color: #8B7355;
  margin: 0;
}

/* 验证对话框样式保持原有设计... */
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
  display: flex;
  align-items: center;
}

.allowed-values-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
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
  font-family: 'Courier New', monospace;
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