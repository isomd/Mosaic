<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement,
  RadialLinearScale
} from 'chart.js'
import { Bar, Doughnut, Radar } from 'vue-chartjs'
import type { Statistics } from '@/api/plugin/pluginType'

// 注册Chart.js组件
ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement,
    PointElement,
    LineElement,
    RadialLinearScale
)

interface Props {
  statistics: Statistics
  theme?: 'light' | 'dark'
  animated?: boolean
  showDetails?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  theme: 'light',
  animated: true,
  showDetails: true
})

// 主题色彩配置
const themeColors = computed(() => ({
  light: {
    primary: '#1976D2',
    secondary: '#424242',
    success: '#4CAF50',
    warning: '#FF9800',
    error: '#F44336',
    info: '#2196F3',
    background: '#FFFFFF',
    surface: '#F5F5F5',
    text: '#212121',
    gradient: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
  },
  dark: {
    primary: '#2196F3',
    secondary: '#90A4AE',
    success: '#66BB6A',
    warning: '#FFA726',
    error: '#EF5350',
    info: '#42A5F5',
    background: '#121212',
    surface: '#1E1E1E',
    text: '#FFFFFF',
    gradient: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
  }
}))

const currentTheme = computed(() => themeColors.value[props.theme])

// 统计数据处理
const statisticsData = computed(() => {
  const stats = props.statistics
  return {
    extensionPackages: stats.totalExtensionPackages || 0,
    extensionPoints: stats.totalExtensionPoints || 0,
    syncPoints: stats.syncExtensionPoints || 0,
    asyncPoints: stats.asyncExtensionPoints || 0,
    averagePriority: Number((stats.averagePriority || 0).toFixed(2)),
    memoryUsage: stats.memoryUsage || 0
  }
})

// 柱状图配置
const barChartData = computed(() => ({
  labels: ['扩展包', '扩展点', '同步点', '异步点'],
  datasets: [{
    label: '数量统计',
    data: [
      statisticsData.value.extensionPackages,
      statisticsData.value.extensionPoints,
      statisticsData.value.syncPoints,
      statisticsData.value.asyncPoints
    ],
    backgroundColor: [
      currentTheme.value.primary + '80',
      currentTheme.value.success + '80',
      currentTheme.value.info + '80',
      currentTheme.value.warning + '80'
    ],
    borderColor: [
      currentTheme.value.primary,
      currentTheme.value.success,
      currentTheme.value.info,
      currentTheme.value.warning
    ],
    borderWidth: 2,
    borderRadius: 8,
    borderSkipped: false,
  }]
}))

const barChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: props.animated ? 1000 : 0,
    easing: 'easeInOutQuart'
  },
  plugins: {
    legend: {
      display: false
    },
    title: {
      display: true,
      text: '插件扩展统计',
      font: {
        size: 16,
        weight: 'bold'
      },
      color: currentTheme.value.text
    },
    tooltip: {
      backgroundColor: currentTheme.value.surface,
      titleColor: currentTheme.value.text,
      bodyColor: currentTheme.value.text,
      borderColor: currentTheme.value.primary,
      borderWidth: 1,
      cornerRadius: 8,
      displayColors: true,
      callbacks: {
        label: (context: any) => {
          return `${context.dataset.label}: ${context.parsed.y} 个`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1,
        color: currentTheme.value.text
      },
      grid: {
        color: currentTheme.value.text + '20'
      }
    },
    x: {
      ticks: {
        color: currentTheme.value.text
      },
      grid: {
        display: false
      }
    }
  }
}))

// 环形图配置（同步/异步分布）
const doughnutChartData = computed(() => ({
  labels: ['同步扩展点', '异步扩展点'],
  datasets: [{
    data: [statisticsData.value.syncPoints, statisticsData.value.asyncPoints],
    backgroundColor: [
      currentTheme.value.success + 'CC',
      currentTheme.value.warning + 'CC'
    ],
    borderColor: [
      currentTheme.value.success,
      currentTheme.value.warning
    ],
    borderWidth: 3,
    hoverOffset: 4
  }]
}))

const doughnutChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: props.animated ? 1200 : 0,
    animateRotate: true,
    animateScale: true
  },
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        color: currentTheme.value.text,
        usePointStyle: true,
        padding: 20,
        font: {
          size: 12
        }
      }
    },
    title: {
      display: true,
      text: '扩展点类型分布',
      font: {
        size: 14,
        weight: 'bold'
      },
      color: currentTheme.value.text
    },
    tooltip: {
      backgroundColor: currentTheme.value.surface,
      titleColor: currentTheme.value.text,
      bodyColor: currentTheme.value.text,
      borderColor: currentTheme.value.primary,
      borderWidth: 1,
      cornerRadius: 8,
      callbacks: {
        label: (context: any) => {
          const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0)
          const percentage = ((context.parsed / total) * 100).toFixed(1)
          return `${context.label}: ${context.parsed} 个 (${percentage}%)`
        }
      }
    }
  },
  cutout: '60%'
}))

// 雷达图配置（综合性能指标）
const radarChartData = computed(() => {
  const maxValues = {
    packages: 10,
    points: 20,
    priority: 10,
    memory: 10000
  }

  return {
    labels: ['扩展包数量', '扩展点数量', '平均优先级', '内存使用'],
    datasets: [{
      label: '插件性能指标',
      data: [
        (statisticsData.value.extensionPackages / maxValues.packages) * 100,
        (statisticsData.value.extensionPoints / maxValues.points) * 100,
        (statisticsData.value.averagePriority / maxValues.priority) * 100,
        (statisticsData.value.memoryUsage / maxValues.memory) * 100
      ],
      backgroundColor: currentTheme.value.primary + '30',
      borderColor: currentTheme.value.primary,
      borderWidth: 2,
      pointBackgroundColor: currentTheme.value.primary,
      pointBorderColor: currentTheme.value.background,
      pointBorderWidth: 2,
      pointRadius: 6
    }]
  }
})

const radarChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: props.animated ? 1500 : 0
  },
  plugins: {
    legend: {
      display: false
    },
    title: {
      display: true,
      text: '性能雷达图',
      font: {
        size: 14,
        weight: 'bold'
      },
      color: currentTheme.value.text
    },
    tooltip: {
      backgroundColor: currentTheme.value.surface,
      titleColor: currentTheme.value.text,
      bodyColor: currentTheme.value.text,
      borderColor: currentTheme.value.primary,
      borderWidth: 1,
      cornerRadius: 8
    }
  },
  scales: {
    r: {
      beginAtZero: true,
      max: 100,
      ticks: {
        stepSize: 20,
        color: currentTheme.value.text,
        backdropColor: 'transparent'
      },
      grid: {
        color: currentTheme.value.text + '30'
      },
      angleLines: {
        color: currentTheme.value.text + '30'
      },
      pointLabels: {
        color: currentTheme.value.text,
        font: {
          size: 11
        }
      }
    }
  }
}))

// 格式化内存使用量
const formatMemoryUsage = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 计算效率评分
const efficiencyScore = computed(() => {
  const { extensionPoints, memoryUsage, averagePriority } = statisticsData.value
  if (extensionPoints === 0) return 0

  const memoryEfficiency = Math.max(0, 100 - (memoryUsage / extensionPoints / 100))
  const priorityEfficiency = Math.min(100, averagePriority * 10)
  const overallScore = (memoryEfficiency + priorityEfficiency) / 2

  return Math.round(Math.max(0, Math.min(100, overallScore)))
})

const getScoreColor = (score: number): string => {
  if (score >= 80) return currentTheme.value.success
  if (score >= 60) return currentTheme.value.warning
  return currentTheme.value.error
}

// 动画控制
const chartKey = ref(0)
const refreshCharts = () => {
  chartKey.value++
}

watch(() => props.statistics, refreshCharts, { deep: true })
watch(() => props.theme, refreshCharts)

onMounted(() => {
  if (props.animated) {
    setTimeout(refreshCharts, 100)
  }
})
</script>

<template>
  <v-card
      :color="currentTheme.surface"
      elevation="4"
      class="statistics-container"
      rounded="lg"
  >
    <v-card-title class="d-flex align-center justify-space-between pa-6">
      <div class="d-flex align-center">
        <v-icon
            :color="currentTheme.primary"
            size="28"
            class="mr-3"
        >
          mdi-chart-box-outline
        </v-icon>
        <span class="text-h5 font-weight-bold" :style="{ color: currentTheme.text }">
          插件统计分析
        </span>
      </div>

      <v-chip
          :color="getScoreColor(efficiencyScore)"
          variant="elevated"
          size="large"
          class="font-weight-bold"
      >
        <v-icon start>mdi-speedometer</v-icon>
        效率评分: {{ efficiencyScore }}
      </v-chip>
    </v-card-title>

    <v-divider :color="currentTheme.primary + '40'"></v-divider>

    <!-- 关键指标卡片 -->
    <v-card-text class="pa-6">
      <v-row class="mb-6">
        <v-col cols="12" sm="6" md="3" v-for="(item, index) in [
          {
            title: '扩展包总数',
            value: statisticsData.extensionPackages,
            icon: 'mdi-package-variant',
            color: currentTheme.primary,
            suffix: '个'
          },
          {
            title: '扩展点总数',
            value: statisticsData.extensionPoints,
            icon: 'mdi-connection',
            color: currentTheme.success,
            suffix: '个'
          },
          {
            title: '平均优先级',
            value: statisticsData.averagePriority,
            icon: 'mdi-priority-high',
            color: currentTheme.info,
            suffix: ''
          },
          {
            title: '内存使用量',
            value: formatMemoryUsage(statisticsData.memoryUsage),
            icon: 'mdi-memory',
            color: currentTheme.warning,
            suffix: ''
          }
        ]" :key="index">
          <v-card
              :color="item.color + '10'"
              elevation="2"
              class="metric-card"
              rounded="lg"
              hover
          >
            <v-card-text class="pa-4">
              <div class="d-flex align-center justify-space-between">
                <div>
                  <div class="text-caption text-medium-emphasis mb-1">
                    {{ item.title }}
                  </div>
                  <div class="text-h4 font-weight-bold" :style="{ color: item.color }">
                    {{ typeof item.value === 'number' && item.suffix ? item.value + item.suffix : item.value }}
                  </div>
                </div>
                <v-avatar :color="item.color" size="48">
                  <v-icon :color="currentTheme.background" size="24">
                    {{ item.icon }}
                  </v-icon>
                </v-avatar>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- 图表区域 -->
      <v-row>
        <!-- 柱状图 -->
        <v-col cols="12" lg="6">
          <v-card
              :color="currentTheme.background"
              elevation="2"
              rounded="lg"
              class="chart-card"
          >
            <v-card-text class="pa-4">
              <div class="chart-container" style="height: 300px;">
                <Bar
                    :key="`bar-${chartKey}`"
                    :data="barChartData"
                    :options="barChartOptions"
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 环形图 -->
        <v-col cols="12" md="6" lg="3">
          <v-card
              :color="currentTheme.background"
              elevation="2"
              rounded="lg"
              class="chart-card"
          >
            <v-card-text class="pa-4">
              <div class="chart-container" style="height: 300px;">
                <Doughnut
                    :key="`doughnut-${chartKey}`"
                    :data="doughnutChartData"
                    :options="doughnutChartOptions"
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 雷达图 -->
        <v-col cols="12" md="6" lg="3">
          <v-card
              :color="currentTheme.background"
              elevation="2"
              rounded="lg"
              class="chart-card"
          >
            <v-card-text class="pa-4">
              <div class="chart-container" style="height: 300px;">
                <Radar
                    :key="`radar-${chartKey}`"
                    :data="radarChartData"
                    :options="radarChartOptions"
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- 详细信息表格 -->
      <v-row v-if="showDetails" class="mt-4">
        <v-col cols="12">
          <v-card
              :color="currentTheme.background"
              elevation="2"
              rounded="lg"
          >
            <v-card-title class="text-h6 pa-4">
              <v-icon :color="currentTheme.primary" class="mr-2">
                mdi-table-large
              </v-icon>
              详细统计信息
            </v-card-title>
            <v-divider></v-divider>
            <v-card-text class="pa-0">
              <v-table density="comfortable">
                <thead>
                <tr>
                  <th class="text-left font-weight-bold">统计项目</th>
                  <th class="text-right font-weight-bold">数值</th>
                  <th class="text-center font-weight-bold">占比/状态</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in [
                    {
                      name: '扩展包总数',
                      value: `${statisticsData.extensionPackages} 个`,
                      status: statisticsData.extensionPackages > 0 ? '正常' : '无数据'
                    },
                    {
                      name: '扩展点总数',
                      value: `${statisticsData.extensionPoints} 个`,
                      status: statisticsData.extensionPoints > 0 ? '正常' : '无数据'
                    },
                    {
                      name: '同步扩展点',
                      value: `${statisticsData.syncPoints} 个`,
                      status: `${statisticsData.extensionPoints > 0 ? ((statisticsData.syncPoints / statisticsData.extensionPoints) * 100).toFixed(1) : 0}%`
                    },
                    {
                      name: '异步扩展点',
                      value: `${statisticsData.asyncPoints} 个`,
                      status: `${statisticsData.extensionPoints > 0 ? ((statisticsData.asyncPoints / statisticsData.extensionPoints) * 100).toFixed(1) : 0}%`
                    },
                    {
                      name: '平均优先级',
                      value: statisticsData.averagePriority.toString(),
                      status: statisticsData.averagePriority >= 5 ? '高优先级' : '低优先级'
                    },
                    {
                      name: '内存使用量',
                      value: formatMemoryUsage(statisticsData.memoryUsage),
                      status: statisticsData.memoryUsage < 5000 ? '轻量级' : '重量级'
                    }
                  ]" :key="index">
                  <td class="font-weight-medium">{{ item.name }}</td>
                  <td class="text-right font-weight-bold" :style="{ color: currentTheme.primary }">
                    {{ item.value }}
                  </td>
                  <td class="text-center">
                    <v-chip
                        size="small"
                        :color="index < 2 ? currentTheme.success : currentTheme.info"
                        variant="tonal"
                    >
                      {{ item.status }}
                    </v-chip>
                  </td>
                </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.statistics-container {
  transition: all 0.3s ease;
}

.metric-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.metric-card:hover {
  transform: translateY(-2px);
}

.chart-card {
  transition: all 0.3s ease;
}

.chart-container {
  position: relative;
  width: 100%;
}

.v-table {
  background: transparent !important;
}

.v-table th {
  background: rgba(var(--v-theme-primary), 0.1) !important;
}

.v-table tbody tr:hover {
  background: rgba(var(--v-theme-primary), 0.05) !important;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.statistics-container {
  animation: fadeInUp 0.6s ease-out;
}
</style>