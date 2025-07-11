<script lang="ts" setup>
import { defineProps, ref, onMounted, computed } from "vue";
import { type Cube } from "../../api/plugin/pluginType";

const dialog = ref(false)
const isHovered = ref(false)
const configDialog = ref(false)

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
    function: 'mdi-diamond-stone',
    default: 'mdi-cube-outline',
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
                  {{ props.cube.scope }}
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

<!--          <v-tooltip text="移除插件" location="top">-->
<!--            <template v-slot:activator="{ props: tooltipProps }">-->
<!--              <v-btn-->
<!--                  v-bind="tooltipProps"-->
<!--                  icon="mdi-delete-outline"-->
<!--                  class="remove-btn icon-btn"-->
<!--                  size="large"-->
<!--                  variant="outlined"-->
<!--              ></v-btn>-->
<!--            </template>-->
<!--          </v-tooltip>-->

          <v-tooltip text="插件配置" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-cog-outline"
                  class="config-btn icon-btn"
                  size="large"
                  @click="configDialog = true"
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

    <!-- 配置对话框组件 -->
    <CubeConfigComponent v-model="configDialog" :pluginData="props.cube"></CubeConfigComponent>

  </div>
</template>

<style scoped lang="scss">
// 沙漠绿洲配色变量
$desert-sand:   #F4E4BC;        // 沙漠沙子色
$desert-dune:   #E6D3A3;        // 沙丘色
$oasis-green:   #4A9B8E;        // 绿洲绿色
$oasis-blue:    #6BB6B0;         // 绿洲蓝绿色
$palm-green:    #2D5A27;         // 棕榈绿
$sunset-orange: #D4A574;      // 日落橙
$clear-white:   #FEFEFE;        // 清爽白
$shadow-brown:  #8B7355;       // 阴影棕

// 圆角系统
$radius-large: 24px;          // 主卡片圆角
$radius-medium: 16px;         // 中等圆角
$radius-small: 12px;          // 小圆角
$radius-tiny: 8px;            // 微小圆角

.cube-card-container {
  margin-bottom: 1.5rem;
  position: relative;
}

.desert-cube-card {
  position: relative;
  background: linear-gradient(135deg, $clear-white 0%, $desert-sand 100%) !important;
  border: 3px solid transparent !important;
  border-radius: $radius-large !important;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
      0 8px 32px rgba(139, 115, 85, 0.12),
      0 2px 8px rgba(139, 115, 85, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.8) !important;

  // 渐变边框效果
  &::before {
    content: '';
    position: absolute;
    top: -3px;
    left: -3px;
    right: -3px;
    bottom: -3px;
    background: linear-gradient(135deg, $desert-dune, $oasis-green, $desert-dune);
    border-radius: $radius-large;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.4s ease;
  }

  // 顶部状态指示条
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 5px;
    background: linear-gradient(90deg, $oasis-green, $oasis-blue, $palm-green);
    border-radius: $radius-large $radius-large 0 0;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: 1;
  }

  &:hover,
  &--hovered {
    transform: translateY(-12px) scale(1.02);
    box-shadow:
        0 20px 60px rgba(139, 115, 85, 0.25),
        0 8px 24px rgba(139, 115, 85, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.9) !important;

    &::before {
      opacity: 1;
    }

    &::after {
      opacity: 1;
    }

    .icon-btn {
      transform: scale(1.15) rotate(5deg);
    }

    .cube-id-wrapper {
      transform: scale(1.02);
      box-shadow: 0 4px 16px rgba(139, 115, 85, 0.2);
    }
  }

  // 状态变体
  &--active {
    &::before {
      background: linear-gradient(135deg, $oasis-green, $oasis-blue, $oasis-green);
    }
  }

  &--inactive {
    opacity: 0.9;

    &::before {
      background: linear-gradient(135deg, $sunset-orange, #E6B887, $sunset-orange);
    }
  }

  // 模式变体
  &--singleton {
    .model-icon--singleton {
      color: $oasis-blue !important;
    }
  }

  &--property {
    .model-icon--property {
      color: $oasis-green !important;
    }
  }

  &--prototype {
    .model-icon--prototype {
      color: $palm-green !important;
    }
  }
}

.status-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  z-index: 2;
  border-radius: $radius-large $radius-large 0 0;

  &--active {
    background: linear-gradient(90deg, $oasis-green, $oasis-blue);
    box-shadow: 0 0 12px rgba(74, 155, 142, 0.5);
  }

  &--inactive {
    background: linear-gradient(90deg, $sunset-orange, #E6B887);
    box-shadow: 0 0 12px rgba(212, 165, 116, 0.5);
  }
}

.cube-header {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1rem 0;
}

.cube-icon-wrapper {
  position: relative;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: $radius-medium;
  padding: 1rem;
  box-shadow:
      0 4px 16px rgba(139, 115, 85, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.4s ease;

  &:hover {
    box-shadow:
        0 8px 24px rgba(139, 115, 85, 0.25),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    transform: translateY(-4px) rotate(-2deg);
  }
}

.status-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid $clear-white;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.2);

  &--active {
    background: linear-gradient(135deg, $oasis-green, $oasis-blue);
    color: white;
  }

  &--inactive {
    background: linear-gradient(135deg, $sunset-orange, #E6B887);
    color: white;
  }
}

.cube-title-section {
  flex: 1;
  min-width: 0;
}

.cube-name-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1.25rem;
  margin-bottom: 1rem;
}

.cube-name {
  font-size: 1.4rem !important;
  font-weight: 700 !important;
  color: $shadow-brown !important;
  line-height: 1.3;
  flex: 1;
  min-width: 0;
  letter-spacing: -0.025em;
}

.cube-badges {
  display: flex;
  gap: 0.75rem;
  flex-shrink: 0;
}

.version-chip {
  font-weight: 600 !important;
  border-radius: $radius-small !important;

  &--singleton {
    background: rgba(107, 182, 176, 0.25) !important;
    color: $oasis-blue !important;
  }

  &--property {
    background: rgba(74, 155, 142, 0.25) !important;
    color: $oasis-green !important;
  }

  &--prototype {
    background: rgba(45, 90, 39, 0.25) !important;
    color: $palm-green !important;
  }
}

.model-chip {
  font-weight: 500 !important;
  border-radius: $radius-small !important;

  &--singleton {
    background: rgba(107, 182, 176, 0.18) !important;
    color: $oasis-blue !important;
  }

  &--property {
    background: rgba(74, 155, 142, 0.18) !important;
    color: $oasis-green !important;
  }

  &--prototype {
    background: rgba(45, 90, 39, 0.18) !important;
    color: $palm-green !important;
  }
}

.cube-id-section {
  margin-top: 0.5rem;
}

.cube-id-wrapper {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(244, 228, 188, 0.8));
  padding: 0.75rem 1rem;
  border-radius: $radius-small;
  border: 2px solid rgba(230, 211, 163, 0.6);
  box-shadow:
      0 2px 8px rgba(139, 115, 85, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
    transition: left 0.6s ease;
  }

  &:hover {
    background: linear-gradient(135deg, rgba(255, 255, 255, 1), rgba(244, 228, 188, 0.9));
    border-color: $oasis-green;
    box-shadow:
        0 4px 16px rgba(139, 115, 85, 0.2),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);

    &::before {
      left: 100%;
    }

    .copy-btn {
      opacity: 1;
      transform: scale(1);
    }
  }
}

.id-icon {
  color: $oasis-green !important;
  flex-shrink: 0;
}

.cube-id {
  font-family: 'JetBrains Mono', 'Fira Code', 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Source Code Pro', 'Consolas', monospace;
  font-size: 1rem;
  font-weight: 600;
  color: $shadow-brown;
  background: transparent;
  border: none;
  padding: 0;
  letter-spacing: 0.05em;
  flex: 1;
  min-width: 0;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  line-height: 1.4;
}

.copy-btn {
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.3s ease;
  color: rgba(139, 115, 85, 0.6) !important;

  &:hover {
    color: $oasis-green !important;
    background: rgba(74, 155, 142, 0.1) !important;
  }
}

.cube-content {
  padding: 1rem 0;
}

.cube-description {
  font-size: 1rem !important;
  color: rgba(139, 115, 85, 0.85) !important;
  line-height: 1.7 !important;
  margin-bottom: 2rem !important;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: 400;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(244, 228, 188, 0.6));
  padding: 1.5rem;
  border-radius: $radius-medium;
  border: 2px solid rgba(230, 211, 163, 0.5);
  box-shadow:
      0 3px 12px rgba(139, 115, 85, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  text-align: center;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);

    .stat-icon {
      transform: scale(1.15) rotate(5deg);
      box-shadow: 0 6px 16px rgba(74, 155, 142, 0.3);
    }
  }
}

.stat-icon {
  color: $oasis-green !important;
  background: linear-gradient(135deg, rgba(74, 155, 142, 0.15), rgba(74, 155, 142, 0.08));
  padding: 0.75rem;
  border-radius: $radius-small;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(74, 155, 142, 0.15);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: $shadow-brown;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 0.85rem;
  color: rgba(139, 115, 85, 0.75);
  margin-top: 0.25rem;
  font-weight: 500;
}

.cube-actions {
  background: linear-gradient(135deg, rgba(244, 228, 188, 0.5), rgba(230, 211, 163, 0.3)) !important;
  border-top: 2px solid rgba(230, 211, 163, 0.6) !important;
  padding: 1.5rem 2rem !important;
  border-radius: 0 0 $radius-large $radius-large;
}

.action-buttons {
  display: flex;
  gap: 1.25rem;
}

.icon-btn {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
  border-radius: $radius-medium !important;

  &:hover {
    transform: translateY(-4px) scale(1.1);
  }

  &:active {
    transform: translateY(-2px) scale(1.05);
  }
}

.details-btn {
  background: linear-gradient(135deg, $oasis-green, $oasis-blue) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(74, 155, 142, 0.4) !important;

  &:hover {
    box-shadow: 0 10px 30px rgba(74, 155, 142, 0.5) !important;
  }
}

.remove-btn {
  border-color: $sunset-orange !important;
  color: $sunset-orange !important;
  background: rgba(212, 165, 116, 0.08) !important;

  &:hover {
    background: rgba(212, 165, 116, 0.2) !important;
    border-color: darken($sunset-orange, 15%) !important;
    box-shadow: 0 8px 24px rgba(212, 165, 116, 0.35) !important;
  }
}

.config-btn {
  border-color: rgba(139, 115, 85, 0.5) !important;
  color: $shadow-brown !important;
  background: rgba(139, 115, 85, 0.08) !important;

  &:hover {
    background: rgba(139, 115, 85, 0.15) !important;
    border-color: rgba(139, 115, 85, 0.7) !important;
    box-shadow: 0 8px 24px rgba(139, 115, 85, 0.25) !important;
  }
}

.cube-meta-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: right;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  justify-content: flex-end;
  transition: all 0.3s ease;

  &:hover {
    transform: translateX(-3px);

    .meta-icon {
      color: $oasis-green !important;
    }
  }
}

.meta-icon {
  color: rgba(139, 115, 85, 0.6) !important;
  transition: color 0.3s ease;
}

.meta-text {
  font-size: 0.85rem;
  color: rgba(139, 115, 85, 0.85);
  font-weight: 500;
  letter-spacing: 0.02em;
}

// 响应式设计
@media (max-width: 768px) {
  .desert-cube-card {
    border-radius: $radius-medium !important;

    &::before, &::after {
      border-radius: $radius-medium;
    }
  }

  .cube-name-row {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .cube-name {
    font-size: 1.25rem !important;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.25rem;
    padding: 1.25rem;
    border-radius: $radius-small;
  }

  .cube-actions {
    flex-direction: column;
    gap: 1.5rem;
    align-items: stretch;
    padding: 1.25rem 1.5rem !important;
    border-radius: 0 0 $radius-medium $radius-medium;
  }

  .action-buttons {
    width: 100%;
    justify-content: center;
  }

  .cube-meta-info {
    text-align: center;
  }

  .meta-item {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .desert-cube-card {
    border-radius: $radius-small !important;

    &::before, &::after {
      border-radius: $radius-small;
    }
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
    border-radius: $radius-tiny;
  }

  .action-buttons {
    flex-direction: row;
    justify-content: space-around;
  }

  .cube-header {
    gap: 1.25rem;
  }

  .cube-icon-wrapper {
    padding: 0.875rem;
    border-radius: $radius-small;
  }

  .cube-actions {
    border-radius: 0 0 $radius-small $radius-small;
  }
}
</style>