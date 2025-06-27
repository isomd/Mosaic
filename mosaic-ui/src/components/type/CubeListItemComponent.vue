<script lang="ts" setup>
import { defineProps, ref, onMounted, computed } from "vue";
import { type Cube } from "../../api/plugin/pluginType";

const dialog = ref(false)
const isHovered = ref(false)

const props = defineProps({
  cube: {
    type: Object as () => Cube,
    default: () => ({} as Cube)
  }
})

// 计算属性：格式化内存使用量
const formattedMemory = computed(() => {
  const bytes = props.cube.statistics?.memoryUsage || 0
  if (bytes < 1024) return `${bytes}B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)}KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)}MB`
})

// 计算属性：获取状态样式类
const statusClass = computed(() => {
  return props.cube.status?.toLowerCase() || 'unknown'
})

// 计算属性：获取模式样式类
const modelClass = computed(() => {
  return props.cube.model?.toLowerCase() || 'default'
})

// 获取模式图标
const getModelIcon = (model: string) => {
  const iconMap = {
    singleton: 'mdi-diamond-stone',
    property: 'mdi-cube-outline',
    prototype: 'mdi-content-copy'
  }
  return iconMap[model as keyof typeof iconMap] || 'mdi-package-variant'
}

// 获取状态图标
const getStatusIcon = (status: string) => {
  return status === 'ACTIVE' ? 'mdi-check-circle' : 'mdi-pause-circle'
}

onMounted(() => {
  // 组件挂载后的初始化逻辑
})
</script>

<template>
  <div class="cube-card-container">
    <v-card
        width="100%"
        class="desert-cube-card"
        :class="[
        `desert-cube-card--${statusClass}`,
        `desert-cube-card--${modelClass}`,
        { 'desert-cube-card--hovered': isHovered }
      ]"
        @mouseenter="isHovered = true"
        @mouseleave="isHovered = false"
        elevation="0"
    >
      <!-- 卡片状态指示条 -->
      <div class="status-indicator" :class="`status-indicator--${statusClass}`"></div>

      <!-- 卡片头部 -->
      <template v-slot:title>
        <div class="cube-header">
          <div class="cube-icon-wrapper">
            <v-icon
                :icon="getModelIcon(props.cube.model)"
                :class="`model-icon--${modelClass}`"
                size="36"
            ></v-icon>
            <div class="status-badge" :class="`status-badge--${statusClass}`">
              <v-icon
                  :icon="getStatusIcon(props.cube.status)"
                  size="14"
              ></v-icon>
            </div>
          </div>

          <div class="cube-title-section">
            <div class="cube-name-row">
              <span class="cube-name">{{ props.cube.name }}</span>
              <div class="cube-badges">
                <v-chip
                    size="small"
                    class="version-chip"
                    :class="`version-chip--${modelClass}`"
                >
                  v{{ props.cube.version }}
                </v-chip>
                <v-chip
                    size="small"
                    class="model-chip"
                    :class="`model-chip--${modelClass}`"
                >
                  {{ props.cube.model }}
                </v-chip>
              </div>
            </div>

            <div class="cube-id-section">
              <div class="cube-id-wrapper">
                <v-icon icon="mdi-tag-outline" size="18" class="id-icon"></v-icon>
                <span class="cube-id">{{ props.cube.id }}</span>
                <v-btn
                    icon="mdi-content-copy"
                    size="x-small"
                    variant="text"
                    class="copy-btn"
                    @click.stop="navigator.clipboard?.writeText(props.cube.id)"
                >
                </v-btn>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 卡片内容 -->
      <template v-slot:text>
        <div class="cube-content">
          <p class="cube-description">{{ props.cube.description }}</p>

          <!-- 统计信息网格 -->
          <div class="stats-grid">
            <div class="stat-item">
              <v-icon icon="mdi-package-variant" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ props.cube.statistics?.totalExtensionPackages || 0 }}</div>
                <div class="stat-label">扩展包</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-connection" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ props.cube.statistics?.totalExtensionPoints || 0 }}</div>
                <div class="stat-label">扩展点</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-memory" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ formattedMemory }}</div>
                <div class="stat-label">内存</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-speedometer" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ (props.cube.statistics?.averagePriority || 0).toFixed(1) }}</div>
                <div class="stat-label">优先级</div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 卡片操作区域 -->
      <v-card-actions class="cube-actions">
        <div class="action-buttons">

          <v-tooltip text="查看详情" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-information-outline"
                  class="details-btn icon-btn"
                  size="large"
                  @click="dialog = true"
              ></v-btn>
            </template>
          </v-tooltip>

          <v-tooltip text="移除插件" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-delete-outline"
                  class="remove-btn icon-btn"
                  size="large"
                  variant="outlined"
              ></v-btn>
            </template>
          </v-tooltip>

          <v-tooltip text="插件配置" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-cog-outline"
                  class="config-btn icon-btn"
                  size="large"
                  variant="outlined"
              ></v-btn>
            </template>
          </v-tooltip>
        </div>

        <v-spacer></v-spacer>

        <div class="cube-meta-info">
          <div class="meta-item">
            <v-icon icon="mdi-account-outline" size="16" class="meta-icon"></v-icon>
            <span class="meta-text">{{ props.cube.author || 'Unknown' }}</span>
          </div>
          <div class="meta-item">
            <v-icon icon="mdi-clock-outline" size="16" class="meta-icon"></v-icon>
            <span class="meta-text">{{ new Date(props.cube.lastUpdatedTime).toLocaleDateString('zh-CN') }}</span>
          </div>
        </div>

      </v-card-actions>
    </v-card>

    <!-- Details对话框组件 -->
    <CubeDetailsComponent v-model="dialog" :pluginData="props.cube"></CubeDetailsComponent>

  </div>
</template>