<template>
  <v-card class="extension-display-card" elevation="2">
    <!-- 优化的标题区域 -->
    <v-card-title class="extension-header">
      <div class="header-content">
        <div class="icon-wrapper">
          <v-icon size="24" color="white">mdi-puzzle</v-icon>
        </div>
        <span class="header-text">扩展包信息</span>
      </div>
      <v-spacer></v-spacer>
      <v-chip
          v-show="hasExtensions"
          small
          color="rgba(255,255,255,0.2)"
          text-color="white"
          class="extension-count-chip"
      >
        {{ extensionPackages.length }} 个扩展包
      </v-chip>
    </v-card-title>

    <v-card-text class="extension-content">
      <!-- 加载状态 - 防止闪屏 -->
      <div v-if="isLoading" class="loading-state">
        <v-skeleton-loader
            type="card"
            class="mb-4"
        ></v-skeleton-loader>
        <v-skeleton-loader
            type="card"
            class="mb-4"
        ></v-skeleton-loader>
      </div>

      <!-- 无扩展包信息 -->
      <div v-else-if="!hasExtensions" class="empty-state">
        <div class="empty-icon">
          <v-icon size="48" color="#E0E0E0">mdi-puzzle-remove-outline</v-icon>
        </div>
        <p class="empty-text">该插件暂无扩展包</p>
        <p class="empty-subtext">插件可能使用核心功能或静态配置</p>
      </div>

      <!-- 扩展包列表 -->
      <div v-else class="extension-packages">
        <v-card
            v-for="(pkg, index) in extensionPackages"
            :key="`pkg-${pkg.id}-${index}`"
            class="extension-package-card"
            elevation="1"
            hover
        >
          <!-- 扩展包头部 - 可点击折叠 -->
          <v-card-title
              class="package-header clickable-header"
              @click="togglePackage(pkg.id)"
          >
            <div class="package-title-section">
              <div class="package-icon-wrapper">
                <v-icon size="20" color="white">mdi-package-variant</v-icon>
              </div>
              <div class="package-info">
                <h3 class="package-name">{{ pkg.name }}</h3>
                <p class="package-class">{{ pkg.className }}</p>
              </div>
            </div>
            <div class="package-actions">
              <v-chip
                  small
                  :color="getExtensionCountColor(pkg.extensionPointCount)"
                  dark
                  class="extension-count-badge"
              >
                {{ pkg.extensionPointCount }} 个扩展点
              </v-chip>
              <v-btn
                  icon
                  small
                  color="white"
                  class="expand-btn ml-2"
              >
                <v-icon size="20">
                  {{ isPackageExpanded(pkg.id) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                </v-icon>
              </v-btn>
            </div>
          </v-card-title>

          <!-- 扩展包内容 - 支持折叠动画 -->
          <v-expand-transition>
            <v-card-text
                v-show="isPackageExpanded(pkg.id)"
                class="package-content"
            >
              <div class="package-description">
                <v-icon size="16" color="#757575" class="desc-icon">mdi-text</v-icon>
                <span class="description-text">{{ pkg.description || '暂无描述' }}</span>
              </div>

              <!-- 扩展点列表 -->
              <div v-if="pkg.extensionPoints && pkg.extensionPoints.length > 0" class="extension-points-section">
                <div
                    class="section-header clickable-header"
                    @click="toggleAllPoints(pkg.id)"
                >
                  <v-icon size="18" color="#1976D2" class="mr-2">mdi-connection</v-icon>
                  <span class="section-title">扩展点详情</span>
                  <v-spacer></v-spacer>
                  <v-chip
                      x-small
                      color="primary"
                      outlined
                      class="points-count-chip mr-2"
                  >
                    {{ pkg.extensionPoints.length }} 个扩展点
                  </v-chip>
                  <v-btn
                      icon
                      x-small
                      color="primary"
                      class="expand-all-btn"
                  >
                    <v-icon size="16">
                      {{ areAllPointsExpanded(pkg.id) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                    </v-icon>
                  </v-btn>
                </div>

                <v-expand-transition>
                  <div
                      v-show="isPointsSectionExpanded(pkg.id)"
                      class="extension-points-grid"
                  >
                    <v-card
                        v-for="(point, pointIndex) in pkg.extensionPoints"
                        :key="`point-${point.id}-${pointIndex}`"
                        class="extension-point-card"
                        elevation="0"
                        outlined
                    >
                      <!-- 扩展点头部 - 可点击折叠 -->
                      <div
                          class="point-header-clickable"
                          @click="togglePoint(pkg.id, point.id)"
                      >
                        <div class="point-header">
                          <div class="point-name-section">
                            <h4 class="point-name">{{ point.extensionName }}</h4>
                            <div class="point-badges">
                              <v-chip
                                  x-small
                                  :color="point.asyncFlag ? '#FF9800' : '#4CAF50'"
                                  dark
                                  class="async-badge"
                              >
                                <v-icon x-small class="mr-1">
                                  {{ point.asyncFlag ? 'mdi-clock-fast' : 'mdi-clock' }}
                                </v-icon>
                                {{ point.asyncFlag ? '异步' : '同步' }}
                              </v-chip>
                              <v-chip
                                  x-small
                                  color="primary"
                                  outlined
                                  class="priority-badge"
                              >
                                <v-icon x-small class="mr-1">mdi-sort-numeric-ascending</v-icon>
                                优先级: {{ point.priority }}
                              </v-chip>
                            </div>
                          </div>
                          <v-btn
                              icon
                              x-small
                              color="primary"
                              class="point-expand-btn"
                          >
                            <v-icon size="14">
                              {{ isPointExpanded(pkg.id, point.id) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                            </v-icon>
                          </v-btn>
                        </div>
                      </div>

                      <!-- 扩展点详细内容 -->
                      <v-expand-transition>
                        <v-card-text
                            v-show="isPointExpanded(pkg.id, point.id)"
                            class="extension-point-content"
                        >
                          <!-- 方法信息 -->
                          <div class="method-info">
                            <div class="method-signature">
                              <code class="method-code">
                                <span class="return-type">{{ formatReturnType(point.returnType) }}</span>
                                <span class="method-name">{{ point.methodName }}</span>
                                <span class="parameters">({{ formatParameters(point.parameterTypes) }})</span>
                              </code>
                            </div>
                          </div>

                          <!-- 描述信息 -->
                          <div v-if="point.description" class="point-description">
                            <v-icon size="14" color="#757575" class="mr-2">mdi-information</v-icon>
                            {{ point.description }}
                          </div>

                          <!-- 返回结果信息 -->
                          <div v-if="point.pointResult && hasPointItems(point.pointResult)" class="result-info">
                            <div class="result-header">
                              <v-icon size="16" color="#9C27B0" class="mr-2">mdi-keyboard-return</v-icon>
                              <span class="result-label">返回结果</span>
                              <v-chip
                                  x-small
                                  color="#9C27B0"
                                  outlined
                                  class="result-count-chip"
                              >
                                {{ point.pointResult.pointItems.length }} 个字段
                              </v-chip>
                            </div>

                            <div class="result-items-grid">
                              <div
                                  v-for="(item, itemIndex) in point.pointResult.pointItems"
                                  :key="`item-${itemIndex}`"
                                  class="result-item"
                              >
                                <div class="result-item-header">
                                  <div class="result-item-name">
                                    <v-icon size="12" color="#9C27B0" class="mr-1">mdi-variable</v-icon>
                                    <code class="item-name-code">{{ item.itemName }}</code>
                                  </div>
                                  <v-chip
                                      x-small
                                      :color="getTypeColor(item.itemClass)"
                                      dark
                                      class="item-type-chip"
                                  >
                                    {{ formatItemClass(item.itemClass) }}
                                  </v-chip>
                                </div>
                                <p class="result-item-desc">{{ item.description || '暂无说明' }}</p>
                              </div>
                            </div>
                          </div>

                          <!-- 无返回结果的情况 -->
                          <div v-else-if="point.returnType === 'void'" class="void-result">
                            <v-icon size="14" color="#757575" class="mr-2">mdi-null</v-icon>
                            <span class="void-text">该方法无返回值</span>
                          </div>
                        </v-card-text>
                      </v-expand-transition>
                    </v-card>
                  </div>
                </v-expand-transition>
              </div>

              <!-- 无扩展点的情况 -->
              <div v-else class="no-extension-points">
                <v-icon color="#BDBDBD" class="mr-2">mdi-information-outline</v-icon>
                <span class="no-points-text">该扩展包暂无扩展点</span>
              </div>
            </v-card-text>
          </v-expand-transition>
        </v-card>
      </div>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import {computed, ref, watch, nextTick, defineProps} from 'vue'
import type { ExtensionPackage, PointResult } from '@/api/plugin/pluginType'


const props = defineProps({
  extensionPackages: {
    default: [] as ExtensionPackage[]
  },
  loading: {
    default: false
  }
})

// 响应式状态管理
const expandedPackages = ref<Set<string>>(new Set())
const expandedPoints = ref<Map<string, Set<string>>>(new Map())
const pointsSectionExpanded = ref<Set<string>>(new Set())
const isLoading = ref(props.loading)

// 监听loading状态变化
watch(() => props.loading, (newVal) => {
  isLoading.value = newVal
}, { immediate: true })

// 监听数据变化，防止闪屏
watch(() => props.extensionPackages, async (newPackages) => {
  if (newPackages && newPackages.length > 0) {
    await nextTick()
    // 默认展开第一个扩展包
    if (newPackages[0]?.id) {
      expandedPackages.value.add(newPackages[0].id)
      pointsSectionExpanded.value.add(newPackages[0].id)
    }
  }
}, { immediate: true, deep: true })

// 计算属性
const hasExtensions = computed(() => {
  return props.extensionPackages && props.extensionPackages.length > 0
})

// 扩展包折叠控制方法
const togglePackage = async (packageId: string) => {
  if (expandedPackages.value.has(packageId)) {
    expandedPackages.value.delete(packageId)
    // 同时收起所有扩展点
  } else {
    expandedPackages.value.add(packageId)
  }
}

const isPackageExpanded = (packageId: string): boolean => {
  return expandedPackages.value.has(packageId)
}

// 扩展点区域折叠控制
const toggleAllPoints = (packageId: string) => {
  if (pointsSectionExpanded.value.has(packageId)) {
    pointsSectionExpanded.value.delete(packageId)
    // 收起所有扩展点
    // expandedPoints.value.delete(packageId)
  } else {
    pointsSectionExpanded.value.add(packageId)
  }
}

const isPointsSectionExpanded = (packageId: string): boolean => {
  return pointsSectionExpanded.value.has(packageId)
}

// 单个扩展点折叠控制
const togglePoint = (packageId: string, pointId: string) => {
  if (!expandedPoints.value.has(packageId)) {
    expandedPoints.value.set(packageId, new Set())
  }

  const packagePoints = expandedPoints.value.get(packageId)!
  if (packagePoints.has(pointId)) {
    packagePoints.delete(pointId)
  } else {
    packagePoints.add(pointId)
  }
}

const isPointExpanded = (packageId: string, pointId: string): boolean => {
  const packagePoints = expandedPoints.value.get(packageId)
  return packagePoints ? packagePoints.has(pointId) : false
}

const areAllPointsExpanded = (packageId: string): boolean => {
  const pkg = props.extensionPackages.find(p => p.id === packageId)
  if (!pkg || !pkg.extensionPoints) return false

  const packagePoints = expandedPoints.value.get(packageId)
  if (!packagePoints) return false

  return pkg.extensionPoints.every(point => packagePoints.has(point.id))
}

// 工具方法
const getExtensionCountColor = (count: number): string => {
  if (count === 0) return '#757575'
  if (count <= 2) return '#4CAF50'
  if (count <= 5) return '#FF9800'
  return '#F44336'
}

const formatParameters = (parameterTypes: string[]): string => {
  if (!parameterTypes || parameterTypes.length === 0) return ''
  return parameterTypes.map(type => {
    const simplifiedType = type.split('.').pop() || type
    return simplifiedType
  }).join(', ')
}

const formatReturnType = (returnType: string): string => {
  if (!returnType) return 'void'
  return returnType.split('.').pop() || returnType
}

const formatItemClass = (itemClass: string): string => {
  if (!itemClass) return 'unknown'
  const typeMap: Record<string, string> = {
    'java.lang.String': 'String',
    'java.lang.Integer': 'Integer',
    'java.lang.Long': 'Long',
    'java.lang.Boolean': 'Boolean',
    'java.lang.Double': 'Double',
    'java.util.List': 'List',
    'java.util.Map': 'Map',
    'com.alibaba.fastjson.JSONObject': 'JSONObject',
    'void': 'void'
  }

  return typeMap[itemClass] || itemClass.split('.').pop() || itemClass
}

const getTypeColor = (itemClass: string): string => {
  const typeColorMap: Record<string, string> = {
    'java.lang.String': '#4CAF50',
    'java.lang.Integer': '#2196F3',
    'java.lang.Long': '#2196F3',
    'java.lang.Boolean': '#FF9800',
    'java.lang.Double': '#9C27B0',
    'java.util.List': '#009688',
    'java.util.Map': '#795548',
    'com.alibaba.fastjson.JSONObject': '#3F51B5',
    'void': '#757575'
  }

  return typeColorMap[itemClass] || '#607D8B'
}

const hasPointItems = (pointResult: PointResult): boolean => {
  return pointResult &&
      pointResult.pointItems &&
      Array.isArray(pointResult.pointItems) &&
      pointResult.pointItems.length > 0
}
</script>

<style scoped>
/* 防闪屏优化 */
.extension-display-card {
  border-radius: 12px !important;
  overflow: hidden;
  border: 1px solid #E8EAF6;
  min-height: 200px; /* 防止高度突变 */
}

/* 加载状态 */
.loading-state {
  padding: 24px;
}

/* 可点击头部样式 */
.clickable-header {
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.clickable-header:hover {
  background: linear-gradient(135deg, #E8F5E8 0%, #F3E5F5 100%) !important;
}

.point-header-clickable {
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  padding: 12px 16px;
  border-radius: 6px;
}

.point-header-clickable:hover {
  background: #F8F9FA;
}

/* 扩展按钮样式 */
.expand-btn, .expand-all-btn, .point-expand-btn {
  transition: transform 0.2s ease;
}

.expand-btn:hover, .expand-all-btn:hover, .point-expand-btn:hover {
  transform: scale(1.1);
}

/* 包头部布局优化 */
.package-header {
  background: linear-gradient(135deg, #E3F2FD 0%, #F3E5F5 100%);
  padding: 16px 20px;
  border-bottom: 1px solid #E8EAF6;
}

.package-actions {
  display: flex;
  align-items: center;
}

/* 动画优化 */
.v-expand-transition-enter-active,
.v-expand-transition-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-expand-transition-enter-from,
.v-expand-transition-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 扩展点网格优化 */
.extension-points-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

/* 扩展点卡片优化 */
.extension-point-card {
  border-radius: 8px !important;
  border: 1px solid #E8EAF6 !important;
  transition: all 0.2s ease;
  overflow: hidden;
}

.extension-point-card:hover {
  border-color: #C5CAE9 !important;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08) !important;
}

/* 点头部布局 */
.point-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0;
}

.point-name-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.point-name {
  font-size: 1rem;
  font-weight: 600;
  color: #1A237E;
  margin: 0;
}

.point-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

/* 保持原有样式 */
.extension-header {
  background: linear-gradient(135deg, #42A5F5 0%, #1E88E5 100%);
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

.extension-count-chip {
  backdrop-filter: blur(10px);
  font-weight: 500;
}

.extension-content {
  padding: 24px;
  background: #FAFAFA;
}

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

.extension-packages {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.extension-package-card {
  border-radius: 12px !important;
  border: 1px solid #E3F2FD;
  background: white;
  transition: all 0.3s ease;
}

.extension-package-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1) !important;
  border-color: #BBDEFB;
}

.package-title-section {
  display: flex;
  align-items: center;
  flex: 1;
}

.package-icon-wrapper {
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
  border-radius: 8px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.package-info {
  flex: 1;
}

.package-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1A237E;
  margin: 0 0 4px 0;
}

.package-class {
  font-size: 0.85rem;
  color: #757575;
  margin: 0;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
}

.extension-count-badge {
  font-weight: 600;
}

.package-content {
  padding: 20px;
}

.package-description {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 12px;
  background: #F8F9FA;
  border-radius: 6px;
  border-left: 4px solid #2196F3;
}

.desc-icon {
  margin-right: 8px;
  margin-top: 2px;
}

.description-text {
  color: #424242;
  line-height: 1.4;
  font-size: 0.9rem;
}

.extension-points-section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 8px 12px;
  border-radius: 6px;
  border-bottom: 2px solid #E3F2FD;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1976D2;
}

.points-count-chip {
  font-weight: 500;
}

.extension-point-content {
  padding: 16px;
}

.method-info {
  margin-bottom: 12px;
}

.method-signature {
  background: #F5F5F5;
  border: 1px solid #E0E0E0;
  border-radius: 6px;
  padding: 10px 12px;
  overflow-x: auto;
}

.method-code {
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 0.85rem;
  line-height: 1.4;
  white-space: nowrap;
}

.return-type {
  color: #1976D2;
  font-weight: 600;
}

.method-name {
  color: #7B1FA2;
  font-weight: 600;
  margin: 0 4px;
}

.parameters {
  color: #388E3C;
}

.point-description {
  display: flex;
  align-items: flex-start;
  color: #616161;
  font-size: 0.9rem;
  line-height: 1.4;
  margin: 0 0 12px 0;
  padding: 8px 12px;
  background: #FAFAFA;
  border-radius: 4px;
  border-left: 3px solid #E0E0E0;
}

.result-info {
  background: #F3E5F5;
  border: 1px solid #E1BEE7;
  border-radius: 6px;
  padding: 12px;
  margin-top: 12px;
}

.result-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  gap: 6px;
}

.result-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #7B1FA2;
  flex: 1;
}

.result-count-chip {
  font-weight: 500;
}

.result-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.result-item {
  background: #FAFAFA;
  border: 1px solid #E1BEE7;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.2s ease;
}

.result-item:hover {
  background: #F8F9FA;
  border-color: #CE93D8;
  transform: translateY(-1px);
}

.result-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.result-item-name {
  display: flex;
  align-items: center;
  flex: 1;
}

.item-name-code {
  background: #E8EAF6;
  border: 1px solid #C5CAE9;
  border-radius: 4px;
  padding: 2px 6px;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 0.8rem;
  color: #3F51B5;
  font-weight: 600;
}

.item-type-chip {
  font-weight: 600;
  font-size: 0.7rem !important;
}

.result-item-desc {
  font-size: 0.85rem;
  color: #424242;
  margin: 0;
  line-height: 1.3;
  font-style: italic;
}

.void-result {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #F5F5F5;
  border: 1px dashed #E0E0E0;
  border-radius: 6px;
  margin-top: 12px;
}

.void-text {
  color: #757575;
  font-size: 0.85rem;
  font-style: italic;
}

.no-extension-points {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 6px;
  border: 1px dashed #E0E0E0;
}

.no-points-text {
  color: #757575;
  font-size: 0.9rem;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .extension-points-grid {
    grid-template-columns: 1fr;
  }

  .extension-content {
    padding: 16px;
  }

  .package-content {
    padding: 16px;
  }

  .point-name-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .method-code {
    font-size: 0.8rem;
  }

  .result-items-grid {
    grid-template-columns: 1fr;
  }

  .result-item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
}

@media (max-width: 480px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .package-title-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .package-actions {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .point-badges {
    width: 100%;
  }
}
</style>
