<template>
  <v-app>
    <!-- 现代化顶部导航栏 -->
    <v-app-bar app elevation="0" class="minecraft-app-bar">
      <v-app-bar-nav-icon @click="drawer = !drawer" class="nav-toggle">
        <v-icon>mdi-menu</v-icon>
      </v-app-bar-nav-icon>

      <v-toolbar-title>Minecraft Admin Pro</v-toolbar-title>

      <v-spacer></v-spacer>

      <!-- 实时服务器状态 -->
      <v-chip
          :color="serverStatus.online ? 'success' : 'error'"
          variant="elevated"
          class="status-chip"
          size="small"
      >
        <v-icon start size="small">
          {{ serverStatus.online ? 'mdi-server-network' : 'mdi-server-network-off' }}
        </v-icon>
        {{ serverStatus.online ? `${serverInfo.onlinePlayers} Online` : 'Offline' }}
      </v-chip>

      <!-- 快速操作按钮 -->
      <v-btn icon variant="text" @click="toggleFullscreen">
        <v-icon>{{ isFullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen' }}</v-icon>
      </v-btn>

      <v-btn icon variant="text" @click="toggleTheme">
        <v-icon>mdi-theme-light-dark</v-icon>
      </v-btn>

      <!-- 通知中心 -->
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon variant="text" v-bind="props">
            <v-badge
                :content="notifications.length"
                :model-value="notifications.length > 0"
                color="error"
                overlap
            >
              <v-icon>mdi-bell</v-icon>
            </v-badge>
          </v-btn>
        </template>

        <v-card class="notification-card" min-width="320">
          <v-card-title class="notification-header">
            <v-icon start>mdi-bell</v-icon>
            Notifications
            <v-spacer></v-spacer>
            <v-btn size="small" variant="text" @click="clearNotifications">
              Clear All
            </v-btn>
          </v-card-title>

          <v-list class="notification-list">
            <v-list-item
                v-for="notification in notifications"
                :key="notification.id"
                class="notification-item"
            >
              <template v-slot:prepend>
                <v-avatar :color="notification.type" size="small">
                  <v-icon size="small">{{ notification.icon }}</v-icon>
                </v-avatar>
              </template>

              <v-list-item-title>{{ notification.title }}</v-list-item-title>
              <v-list-item-subtitle>{{ notification.time }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="notifications.length === 0">
              <v-list-item-title class="text-center text-medium-emphasis">
                No new notifications
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>

      <!-- 用户菜单 -->
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" class="user-menu-btn" variant="text">
            <v-avatar size="36" class="user-avatar">
              <v-img
                  src="https://mc-heads.net/avatar/steve/36"
                  alt="Steve"
                  cover
              ></v-img>
            </v-avatar>
            <div class="user-info d-none d-sm-flex">
              <div class="user-name">{{ currentUser.name }}</div>
              <div class="user-role">{{ currentUser.role }}</div>
            </div>
            <v-icon end>mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-card class="user-menu-card" min-width="240">
          <v-card-text class="user-card-header">
            <div class="d-flex align-center">
              <v-avatar size="48" class="mr-3">
                <v-img src="https://mc-heads.net/avatar/steve/48" alt="Steve"></v-img>
              </v-avatar>
              <div>
                <div class="font-weight-bold">{{ currentUser.name }}</div>
                <div class="text-caption text-medium-emphasis">{{ currentUser.email }}</div>
              </div>
            </div>
          </v-card-text>

          <v-divider></v-divider>

          <v-list density="compact">
            <v-list-item @click="viewProfile" prepend-icon="mdi-account">
              <v-list-item-title>Profile Settings</v-list-item-title>
            </v-list-item>
            <v-list-item @click="openPreferences" prepend-icon="mdi-cog">
              <v-list-item-title>Preferences</v-list-item-title>
            </v-list-item>
            <v-list-item @click="viewActivity" prepend-icon="mdi-history">
              <v-list-item-title>Activity Log</v-list-item-title>
            </v-list-item>

            <v-divider class="my-2"></v-divider>

            <v-list-item @click="logout" prepend-icon="mdi-logout" class="logout-item">
              <v-list-item-title>Sign Out</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>
    </v-app-bar>

    <!-- 现代化侧边导航栏 -->
    <v-navigation-drawer v-model="drawer" app class="minecraft-drawer">
      <div class="minecraft-drawer-header">
        <div class="minecraft-logo">⛏️</div>
        <div class="minecraft-title">Admin Console</div>
        <div class="minecraft-version">v{{ version }} • Build {{ buildNumber }}</div>
      </div>

      <v-list nav class="navigation-list">
        <v-list-item
            v-for="item in menuItems"
            :key="item.title"
            :class="{ 'v-list-item--active': currentRoute === item.route }"
            @click="navigateTo(item.route)"
            class="nav-item"
        >
          <template v-slot:prepend>
            <v-icon>{{ item.icon }}</v-icon>
          </template>

          <v-list-item-title>{{ item.title }}</v-list-item-title>

          <template v-slot:append v-if="item.badge">
            <v-badge
                :color="item.badgeColor"
                :content="item.badge"
                inline
            ></v-badge>
          </template>
        </v-list-item>
      </v-list>

      <!-- 快速统计面板 -->
      <v-card class="quick-stats-card" variant="tonal" color="primary">
        <v-card-title class="quick-stats-title">
          <v-icon start>mdi-speedometer</v-icon>
          Quick Stats
        </v-card-title>
        <v-card-text class="quick-stats-content">
          <div class="stat-row">
            <span>TPS</span>
            <v-chip
                :color="serverInfo.tps >= 19 ? 'success' : serverInfo.tps >= 15 ? 'warning' : 'error'"
                size="small"
            >
              {{ serverInfo.tps }}
            </v-chip>
          </div>
          <div class="stat-row">
            <span>Memory</span>
            <span class="text-caption">{{ serverInfo.memoryUsage }}GB / {{ serverInfo.maxMemory }}GB</span>
          </div>
          <div class="stat-row">
            <span>Uptime</span>
            <span class="text-caption">{{ formatUptime(serverUptime) }}</span>
          </div>
        </v-card-text>
      </v-card>

      <template v-slot:append>
        <div class="minecraft-drawer-footer">
          <v-btn
              block
              class="minecraft-footer-btn"
              :color="creativeMode ? 'warning' : 'error'"
              @click="toggleCreativeMode"
              size="large"
          >
            <v-icon start>{{ creativeMode ? 'mdi-pencil-off' : 'mdi-pencil' }}</v-icon>
            {{ creativeMode ? 'Exit Creative' : 'Creative Mode' }}
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- 主内容区域 -->
    <v-main>
      <v-container fluid class="main-container">
        <!-- 动态页面标题 -->
        <div class="minecraft-header minecraft-glow">
          <h1>{{ currentPageTitle }}</h1>
          <div class="header-subtitle">{{ currentPageSubtitle }}</div>
        </div>

        <!-- 高级统计卡片网格 -->
        <v-row class="stats-grid mb-8">
          <v-col
              v-for="(stat, index) in enhancedStats"
              :key="stat.title"
              cols="12"
              sm="6"
              lg="3"
          >
            <transition name="fade" :style="{ transitionDelay: `${index * 100}ms` }">
              <v-card
                  class="minecraft-stat-card"
                  :class="{ 'minecraft-glow': stat.highlight }"
                  elevation="0"
              >
                <v-card-text>
                  <div class="stat-header">
                    <div class="minecraft-stat-icon">{{ stat.icon }}</div>
                    <v-chip
                        v-if="stat.trend"
                        :color="stat.trend > 0 ? 'success' : 'error'"
                        size="x-small"
                        class="trend-chip"
                    >
                      <v-icon start size="x-small">
                        {{ stat.trend > 0 ? 'mdi-trending-up' : 'mdi-trending-down' }}
                      </v-icon>
                      {{ Math.abs(stat.trend) }}%
                    </v-chip>
                  </div>

                  <div class="minecraft-stat-number">{{ stat.value }}</div>
                  <div class="minecraft-stat-title">{{ stat.title }}</div>

                  <v-progress-linear
                      v-if="stat.progress !== undefined"
                      :model-value="stat.progress"
                      :color="stat.progressColor"
                      height="6"
                      rounded
                      class="mt-3"
                  ></v-progress-linear>

                  <div v-if="stat.subtitle" class="stat-subtitle">
                    {{ stat.subtitle }}
                  </div>
                </v-card-text>
              </v-card>
            </transition>
          </v-col>
        </v-row>

        <!-- 主要内容网格 -->
        <v-row>
          <!-- 左侧主要内容 -->
          <v-col cols="12" lg="8">
            <v-card class="minecraft-content-card main-content-card">
              <v-card-title class="content-card-title">
                <v-icon start>mdi-earth</v-icon>
                World Overview & Management
              </v-card-title>

              <v-card-text>
                <div class="content-intro">
                  <p class="intro-text">
                    Welcome to your advanced Minecraft Server Administration Console.
                    Monitor, manage, and optimize your server with precision and style.
                  </p>
                </div>

                <!-- 服务器信息网格 -->
                <v-row class="info-grid">
                  <v-col cols="12" md="6">
                    <div class="minecraft-section server-section">
                      <h3>🖥️ Server Performance</h3>

                      <div class="performance-metrics">
                        <div class="metric-item">
                          <div class="metric-label">
                            <v-icon size="small">mdi-account-group</v-icon>
                            Online Players
                          </div>
                          <div class="metric-value">
                            <span class="metric-number">{{ serverInfo.onlinePlayers }}</span>
                            <span class="metric-max">/ {{ serverInfo.maxPlayers }}</span>
                          </div>
                          <v-progress-linear
                              :model-value="(serverInfo.onlinePlayers / serverInfo.maxPlayers) * 100"
                              color="success"
                              height="4"
                              rounded
                          ></v-progress-linear>
                        </div>

                        <div class="metric-item">
                          <div class="metric-label">
                            <v-icon size="small">mdi-memory</v-icon>
                            Memory Usage
                          </div>
                          <div class="metric-value">
                            <span class="metric-number">{{ serverInfo.memoryUsage }}GB</span>
                            <span class="metric-max">/ {{ serverInfo.maxMemory }}GB</span>
                          </div>
                          <v-progress-linear
                              :model-value="(serverInfo.memoryUsage / serverInfo.maxMemory) * 100"
                              :color="getMemoryColor(serverInfo.memoryUsage / serverInfo.maxMemory)"
                              height="4"
                              rounded
                          ></v-progress-linear>
                        </div>

                        <div class="metric-item">
                          <div class="metric-label">
                            <v-icon size="small">mdi-speedometer</v-icon>
                            Server TPS
                          </div>
                          <div class="metric-value">
                            <span
                                class="metric-number"
                                :class="getTpsClass(serverInfo.tps)"
                            >
                              {{ serverInfo.tps }}
                            </span>
                            <span class="metric-max">/ 20.0</span>
                          </div>
                          <v-progress-linear
                              :model-value="(serverInfo.tps / 20) * 100"
                              :color="getTpsColor(serverInfo.tps)"
                              height="4"
                              rounded
                          ></v-progress-linear>
                        </div>
                      </div>
                    </div>
                  </v-col>

                  <v-col cols="12" md="6">
                    <div class="minecraft-section world-section">
                      <h3>🌍 World Information</h3>

                      <div class="world-info-grid">
                        <div class="world-info-item">
                          <v-icon color="primary">mdi-map-marker</v-icon>
                          <div>
                            <div class="info-label">Spawn Point</div>
                            <div class="info-value">{{ worldInfo.spawnPoint }}</div>
                          </div>
                        </div>

                        <div class="world-info-item">
                          <v-icon color="warning">mdi-weather-sunny</v-icon>
                          <div>
                            <div class="info-label">Game Time</div>
                            <div class="info-value">Day {{ worldInfo.gameDay }}</div>
                          </div>
                        </div>

                        <div class="world-info-item">
                          <v-icon color="info">mdi-weather-cloudy</v-icon>
                          <div>
                            <div class="info-label">Weather</div>
                            <div class="info-value">{{ worldInfo.weather }}</div>
                          </div>
                        </div>

                        <div class="world-info-item">
                          <v-icon color="success">mdi-harddisk</v-icon>
                          <div>
                            <div class="info-label">World Size</div>
                            <div class="info-value">{{ worldInfo.size }}</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </v-col>
                </v-row>

                <!-- 操作按钮组 -->
                <div class="action-buttons">
                  <v-btn
                      class="minecraft-action-btn"
                      size="large"
                      color="primary"
                      @click="openWorldSettings"
                  >
                    <v-icon start>mdi-cog</v-icon>
                    World Settings
                  </v-btn>

                  <v-btn
                      class="minecraft-action-btn"
                      size="large"
                      color="info"
                      @click="openConsole"
                  >
                    <v-icon start>mdi-console</v-icon>
                    Server Console
                  </v-btn>

                  <v-btn
                      class="minecraft-action-btn"
                      size="large"
                      color="success"
                      @click="createBackup"
                  >
                    <v-icon start>mdi-backup-restore</v-icon>
                    Create Backup
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- 右侧边栏 -->
          <v-col cols="12" lg="4">
            <!-- 快速操作面板 -->
            <v-card class="minecraft-content-card quick-actions-card mb-6">
              <v-card-title>
                <v-icon start>mdi-lightning-bolt</v-icon>
                Quick Actions
              </v-card-title>
              <v-card-text>
                <div class="quick-actions-grid">
                  <v-btn
                      v-for="action in quickActions"
                      :key="action.title"
                      :color="action.color"
                      :variant="action.variant || 'elevated'"
                      block
                      class="quick-action-btn"
                      @click="executeAction(action.action)"
                  >
                    <v-icon start>{{ action.icon }}</v-icon>
                    {{ action.title }}
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>

            <!-- 实时活动流 -->
            <v-card class="minecraft-content-card activity-card">
              <v-card-title>
                <v-icon start>mdi-pulse</v-icon>
                Live Activity
                <v-spacer></v-spacer>
                <v-chip size="small" color="success" variant="tonal">
                  <v-icon start size="x-small">mdi-circle</v-icon>
                  Live
                </v-chip>
              </v-card-title>

              <v-card-text class="activity-content">
                <v-timeline density="compact" class="activity-timeline">
                  <v-timeline-item
                      v-for="activity in recentActivities"
                      :key="activity.id"
                      :dot-color="activity.color"
                      size="small"
                      class="activity-item"
                  >
                    <div class="activity-content-item">
                      <div class="activity-title">{{ activity.title }}</div>
                      <div class="activity-time">{{ formatRelativeTime(activity.timestamp) }}</div>
                    </div>
                  </v-timeline-item>
                </v-timeline>

                <v-btn
                    variant="text"
                    size="small"
                    block
                    class="view-all-btn"
                    @click="viewAllActivity"
                >
                  View All Activity
                  <v-icon end>mdi-arrow-right</v-icon>
                </v-btn>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <!-- 现代化底部栏 -->
    <v-footer app class="minecraft-footer">
      <div class="footer-content">
        <div class="footer-left">
          <span class="minecraft-footer-text">
            🎮 Minecraft Server Admin Pro | Powered by Vue3 & Vuetify
          </span>
        </div>
        <div class="footer-right">
          <span class="server-uptime">
            Uptime: {{ formatUptime(serverUptime) }}
          </span>
          <v-divider vertical class="mx-3"></v-divider>
          <span class="build-info">
            Build {{ buildNumber }} • {{ lastUpdate }}
          </span>
        </div>
      </div>
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

// 响应式状态
const drawer = ref(true)
const currentRoute = ref('/')
const creativeMode = ref(false)
const isFullscreen = ref(false)
const version = ref('1.20.4')
const buildNumber = ref('2024.1.15')
const lastUpdate = ref('2024-01-15')

// 用户信息
const currentUser = ref({
  name: 'Steve',
  email: 'steve@minecraft.server',
  role: 'Administrator'
})

// 服务器状态
const serverStatus = ref({
  online: true,
  lastCheck: new Date()
})

// 增强的服务器信息
const serverInfo = ref({
  onlinePlayers: 156,
  maxPlayers: 500,
  memoryUsage: 4.2,
  maxMemory: 8,
  tps: 19.8,
  version: '1.20.4 Paper',
  plugins: 24,
  worlds: 3
})

// 世界信息
const worldInfo = ref({
  spawnPoint: '0, 64, 0',
  gameDay: 1247,
  weather: 'Clear ☀️',
  size: '2.3 GB'
})

// 通知系统
const notifications = ref([
  { id: 1, title: 'Server backup completed', time: '2 min ago', type: 'success', icon: 'mdi-check-circle' },
  { id: 2, title: 'High memory usage detected', time: '5 min ago', type: 'warning', icon: 'mdi-alert' },
  { id: 3, title: 'New player joined: Alex', time: '10 min ago', type: 'info', icon: 'mdi-account-plus' }
])

// 菜单项
const menuItems = ref([
  { title: 'Dashboard', icon: 'mdi-view-dashboard', route: '/', badge: null },
  { title: 'Players', icon: 'mdi-account-group', route: '/players', badge: '156', badgeColor: 'success' },
  { title: 'Worlds', icon: 'mdi-earth', route: '/worlds', badge: '3', badgeColor: 'info' },
  { title: 'Server', icon: 'mdi-server', route: '/server', badge: null },
  { title: 'Plugins', icon: 'mdi-puzzle', route: '/plugins', badge: '24', badgeColor: 'warning' },
  { title: 'Analytics', icon: 'mdi-chart-line', route: '/analytics', badge: null },
  { title: 'Backups', icon: 'mdi-backup-restore', route: '/backups', badge: '5', badgeColor: 'primary' },
  { title: 'Console', icon: 'mdi-console', route: '/console', badge: null },
  { title: 'Settings', icon: 'mdi-cog', route: '/settings', badge: null },
])

// 增强的统计数据
const enhancedStats = ref([
  {
    title: 'Players Online',
    value: '156',
    icon: '👥',
    highlight: true,
    progress: 31.2,
    progressColor: 'success',
    trend: 12,
    subtitle: '+18 since yesterday'
  },
  {
    title: 'Active Worlds',
    value: '3',
    icon: '🌍',
    highlight: false,
    progress: 60,
    progressColor: 'info',
    trend: 0,
    subtitle: 'All worlds online'
  },
  {
    title: 'Plugins Loaded',
    value: '24',
    icon: '🔧',
    highlight: false,
    progress: 100,
    progressColor: 'warning',
    trend: 4,
    subtitle: '2 updates available'
  },
  {
    title: 'Server Performance',
    value: '98%',
    icon: '⚡',
    highlight: false,
    progress: 98,
    progressColor: 'success',
    trend: 2,
    subtitle: 'Excellent performance'
  },
])

// 快速操作
const quickActions = ref([
  { title: 'Restart Server', icon: 'mdi-restart', color: 'warning', action: 'restart' },
  { title: 'Create Backup', icon: 'mdi-backup-restore', color: 'primary', action: 'backup' },
  { title: 'Broadcast Message', icon: 'mdi-bullhorn', color: 'info', action: 'broadcast' },
  { title: 'Emergency Stop', icon: 'mdi-stop-circle', color: 'error', action: 'stop', variant: 'outlined' },
])

// 实时活动
const recentActivities = ref([
  { id: 1, title: 'Player Steve joined the server', timestamp: Date.now() - 120000, color: 'success' },
  { id: 2, title: 'World backup completed successfully', timestamp: Date.now() - 900000, color: 'primary' },
  { id: 3, title: 'Plugin EssentialsX updated to v2.20.1', timestamp: Date.now() - 3600000, color: 'warning' },
  { id: 4, title: 'Scheduled restart in 2 hours', timestamp: Date.now() - 7200000, color: 'info' },
  { id: 5, title: 'New player Alex registered', timestamp: Date.now() - 10800000, color: 'success' },
])

// 计算属性
const serverUptime = computed(() => {
  return Date.now() - (Date.now() - 7 * 24 * 60 * 60 * 1000) // 7 days ago
})

const currentPageTitle = computed(() => {
  const route = menuItems.value.find(item => item.route === currentRoute.value)
  return route ? route.title : 'Dashboard'
})

const currentPageSubtitle = computed(() => {
  const subtitles = {
    '/': 'Monitor and manage your Minecraft server',
    '/players': 'Manage players and permissions',
    '/worlds': 'World management and configuration',
    '/server': 'Server settings and configuration',
    '/plugins': 'Plugin management and updates',
    '/analytics': 'Server analytics and reports',
    '/backups': 'Backup management and restoration',
    '/console': 'Server console and commands',
    '/settings': 'System settings and preferences'
  }
  return subtitles[currentRoute.value] || 'Welcome to your admin dashboard'
})

// 方法
const toggleTheme = () => {
  console.log('Toggle theme')
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

const toggleCreativeMode = () => {
  creativeMode.value = !creativeMode.value
  console.log('Creative mode:', creativeMode.value ? 'ON' : 'OFF')
}

const navigateTo = (route: string) => {
  currentRoute.value = route
  console.log('Navigate to:', route)
}

const clearNotifications = () => {
  notifications.value = []
}

const formatUptime = (timestamp: number) => {
  const diff = Date.now() - timestamp
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))

  if (days > 0) return `${days}d ${hours}h ${minutes}m`
  if (hours > 0) return `${hours}h ${minutes}m`
  return `${minutes}m`
}

const formatRelativeTime = (timestamp: number) => {
  const diff = Date.now() - timestamp
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days > 0) return `${days}d ago`
  if (hours > 0) return `${hours}h ago`
  if (minutes > 0) return `${minutes}m ago`
  return 'Just now'
}

const getMemoryColor = (ratio: number) => {
  if (ratio < 0.7) return 'success'
  if (ratio < 0.9) return 'warning'
  return 'error'
}

const getTpsColor = (tps: number) => {
  if (tps >= 19) return 'success'
  if (tps >= 15) return 'warning'
  return 'error'
}

const getTpsClass = (tps: number) => {
  if (tps >= 19) return 'text-success'
  if (tps >= 15) return 'text-warning'
  return 'text-error'
}

const openWorldSettings = () => {
  console.log('Opening world settings...')
}

const openConsole = () => {
  console.log('Opening server console...')
}

const createBackup = () => {
  console.log('Creating backup...')
}

const executeAction = (action: string) => {
  console.log('Executing action:', action)
}

const viewProfile = () => {
  console.log('View profile')
}

const openPreferences = () => {
  console.log('Open preferences')
}

const viewActivity = () => {
  console.log('View activity')
}

const viewAllActivity = () => {
  console.log('View all activity')
}

const logout = () => {
  console.log('Logout')
}

// 生命周期
let updateInterval: number

onMounted(() => {
  console.log('Minecraft Admin Pro Dashboard loaded! 🎮')

  // 实时数据更新
  updateInterval = setInterval(() => {
    // 更新在线玩家数（模拟）
    const variance = Math.floor(Math.random() * 10) - 5
    const newPlayerCount = Math.max(0, Math.min(500, serverInfo.value.onlinePlayers + variance))
    serverInfo.value.onlinePlayers = newPlayerCount
    enhancedStats.value[0].value = newPlayerCount.toString()
    enhancedStats.value[0].progress = (newPlayerCount / serverInfo.value.maxPlayers) * 100

    // 更新TPS（模拟）
    const tpsVariance = (Math.random() - 0.5) * 0.4
    serverInfo.value.tps = Math.max(10, Math.min(20, serverInfo.value.tps + tpsVariance))
    serverInfo.value.tps = Math.round(serverInfo.value.tps * 10) / 10

    // 更新性能指标
    const performance = Math.round(95 + Math.random() * 5)
    enhancedStats.value[3].value = `${performance}%`
    enhancedStats.value[3].progress = performance
  }, 5000)
})

onUnmounted(() => {
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})
</script>

<style scoped lang="scss">
// 组件特定样式
.status-chip {
  font-family: 'JetBrains Mono', monospace !important;
  font-size: 11px !important;
  border-radius: 16px !important;
}

.user-menu-btn {
  border-radius: 24px !important;
  padding: 8px 12px !important;

  .user-info {
    flex-direction: column;
    align-items: flex-start;
    margin: 0 8px;

    .user-name {
      font-size: 12px;
      font-weight: 600;
      line-height: 1;
    }

    .user-role {
      font-size: 10px;
      opacity: 0.7;
      line-height: 1;
    }
  }
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.notification-card,
.user-menu-card {
  border-radius: 16px !important;
  box-shadow: var(--shadow-xl) !important;
}

.notification-header {
  background: var(--mc-primary) !important;
  color: white !important;
  font-size: 14px !important;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.navigation-list {
  .nav-item {
    margin: 8px 0;
    border-radius: 16px !important;
    transition: all 0.3s var(--ease-out-cubic);

    &:hover {
      transform: translateX(8px);
    }
  }
}

.quick-stats-card {
  margin: 16px;
  border-radius: 16px !important;

  .quick-stats-title {
    font-size: 14px !important;
    padding: 16px 16px 8px 16px !important;
  }

  .quick-stats-content {
    padding: 8px 16px 16px 16px !important;
  }

  .stat-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 8px 0;
    font-size: 12px;
  }
}

.main-container {
  padding: 32px !important;
}

.stats-grid {
  .minecraft-stat-card {
    .stat-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;

      .trend-chip {
        font-size: 10px !important;
      }
    }

    .stat-subtitle {
      font-size: 11px;
      color: var(--mc-neutral-600);
      margin-top: 8px;
      text-align: center;
    }
  }
}

.main-content-card {
  .content-card-title {
    background: var(--mc-primary) !important;
    color: white !important;
    font-size: 16px !important;
    margin: -24px -24px 24px -24px !important;
    padding: 20px 24px !important;
  }

  .intro-text {
    font-size: 15px;
    line-height: 1.6;
    color: var(--mc-neutral-700);
    margin-bottom: 32px;
    text-align: center;
  }

  .info-grid {
    margin-bottom: 32px;
  }
}

.performance-metrics {
  .metric-item {
    margin-bottom: 20px;

    .metric-label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      font-weight: 500;
      color: var(--mc-neutral-700);
      margin-bottom: 8px;
    }

    .metric-value {
      display: flex;
      align-items: baseline;
      gap: 4px;
      margin-bottom: 8px;

      .metric-number {
        font-family: 'Orbitron', monospace;
        font-size: 18px;
        font-weight: 700;
      }

      .metric-max {
        font-size: 12px;
        color: var(--mc-neutral-500);
      }
    }
  }
}

.world-info-grid {
  .world-info-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .info-label {
      font-size: 12px;
      color: var(--mc-neutral-600);
      font-weight: 500;
    }

    .info-value {
      font-size: 14px;
      font-weight: 600;
      color: var(--mc-neutral-800);
    }
  }
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 32px;
}

.quick-actions-card {
  .quick-actions-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .quick-action-btn {
      border-radius: 12px !important;
      font-size: 12px !important;
      padding: 16px !important;
      min-height: 48px !important;
    }
  }
}

.activity-card {
  .activity-content {
    max-height: 400px;
    overflow-y: auto;
  }

  .activity-timeline {
    .activity-item {
      .activity-content-item {
        .activity-title {
          font-size: 13px;
          font-weight: 500;
          color: var(--mc-neutral-800);
          margin-bottom: 4px;
        }

        .activity-time {
          font-size: 11px;
          color: var(--mc-neutral-600);
        }
      }
    }
  }

  .view-all-btn {
    margin-top: 16px;
    font-size: 12px !important;
  }
}

.minecraft-footer {
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 0 24px;

    .footer-left,
    .footer-right {
      display: flex;
      align-items: center;
    }

    .server-uptime,
    .build-info {
      font-size: 11px;
      opacity: 0.8;
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .main-container {
    padding: 16px !important;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;

    .minecraft-action-btn {
      width: 100%;
      max-width: 300px;
    }
  }

  .footer-content {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }

  .user-info {
    display: none !important;
  }
}
</style>