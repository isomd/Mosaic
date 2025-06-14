<template>
  <v-app>
    <!-- 顶部导航栏 -->
    <v-app-bar app elevation="0" class="minecraft-app-bar">
      <v-app-bar-nav-icon @click="drawer = !drawer">
        <v-icon>mdi-menu</v-icon>
      </v-app-bar-nav-icon>

      <v-toolbar-title>Minecraft Admin</v-toolbar-title>

      <v-spacer></v-spacer>

      <!-- 服务器状态指示器 -->
      <v-chip
          :color="serverStatus.online ? 'success' : 'error'"
          variant="elevated"
          class="minecraft-status-chip"
      >
        <v-icon start>{{ serverStatus.online ? 'mdi-server' : 'mdi-server-off' }}</v-icon>
        {{ serverStatus.online ? 'Online' : 'Offline' }}
      </v-chip>

      <v-btn icon @click="toggleTheme">
        <v-icon>mdi-brightness-6</v-icon>
      </v-btn>

      <v-btn icon>
        <v-icon>mdi-bell</v-icon>
        <v-badge color="error" content="3" floating></v-badge>
      </v-btn>

      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" class="minecraft-user-btn">
            <v-avatar size="32">
              <v-img src="https://mc-heads.net/avatar/steve" alt="Steve"></v-img>
            </v-avatar>
            <span class="ml-2 d-none d-sm-inline">Steve</span>
            <v-icon end>mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-list class="minecraft-user-menu">
          <v-list-item @click="viewProfile">
            <template v-slot:prepend>
              <v-icon>mdi-account</v-icon>
            </template>
            <v-list-item-title>Profile</v-list-item-title>
          </v-list-item>
          <v-list-item @click="openSettings">
            <template v-slot:prepend>
              <v-icon>mdi-cog</v-icon>
            </template>
            <v-list-item-title>Settings</v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <template v-slot:prepend>
              <v-icon>mdi-logout</v-icon>
            </template>
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- 侧边导航栏 -->
    <v-navigation-drawer v-model="drawer" app class="minecraft-drawer">
      <div class="minecraft-drawer-header">
        <div class="minecraft-logo">⛏️</div>
        <div class="minecraft-title">Admin Panel</div>
        <div class="minecraft-version">v{{ version }}</div>
      </div>

      <v-list nav class="minecraft-nav-list">
        <v-list-item
            v-for="item in menuItems"
            :key="item.title"
            :class="{ 'v-list-item--active': currentRoute === item.route }"
            @click="navigateTo(item.route)"
        >
          <template v-slot:prepend>
            <v-icon>{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title>{{ item.title }}</v-list-item-title>
          <template v-slot:append v-if="item.badge">
            <v-badge :color="item.badgeColor" :content="item.badge"></v-badge>
          </template>
        </v-list-item>
      </v-list>

      <template v-slot:append>
        <div class="minecraft-drawer-footer">
          <v-btn
              block
              class="minecraft-footer-btn"
              @click="toggleCreativeMode"
          >
            {{ creativeMode ? '🎨 Exit Creative' : '⚡ Creative Mode' }}
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- 主内容区 -->
    <v-main>
      <v-container fluid>
        <div class="minecraft-header minecraft-glow">
          <h1>Dashboard</h1>
        </div>

        <!-- 实时统计卡片 -->
        <v-row class="mb-8">
          <v-col
              v-for="(stat, index) in stats"
              :key="stat.title"
              cols="12"
              sm="6"
              md="3"
          >
            <v-card
                class="minecraft-stat-card"
                :class="{ 'minecraft-glow': stat.highlight }"
                :style="{ animationDelay: `${app * 0.1}s` }"
            >
              <v-card-text class="text-center">
                <div class="minecraft-stat-icon">{{ stat.icon }}</div>
                <div class="minecraft-stat-number">{{ stat.value }}</div>
                <div class="minecraft-stat-title">{{ stat.title }}</div>
                <v-progress-linear
                    v-if="stat.progress"
                    :model-value="stat.progress"
                    :color="stat.progressColor"
                    height="4"
                    class="mt-2"
                ></v-progress-linear>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>

        <!-- 主要内容区域 -->
        <v-row>
          <v-col cols="12" lg="8">
            <v-card class="minecraft-content-card">
              <v-card-title>World Overview</v-card-title>
              <v-card-text>
                <p>Welcome to your advanced Minecraft Admin Dashboard! Manage your server with precision and style.</p>

                <v-row class="mt-6">
                  <v-col cols="12" md="6">
                    <div class="minecraft-section">
                      <h3>Server Status</h3>
                      <div class="minecraft-status-item">
                        <span>🟢 Online Players:</span>
                        <strong>{{ serverInfo.onlinePlayers }} / {{ serverInfo.maxPlayers }}</strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>💾 Memory Usage:</span>
                        <strong>{{ serverInfo.memoryUsage }}GB / {{ serverInfo.maxMemory }}GB</strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>⚡ TPS:</span>
                        <strong
                            :class="{ 'text-success': serverInfo.tps >= 19, 'text-warning': serverInfo.tps < 19 && serverInfo.tps >= 15, 'text-error': serverInfo.tps < 15 }">
                          {{ serverInfo.tps }}
                        </strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>🌐 Version:</span>
                        <strong>{{ serverInfo.version }}</strong>
                      </div>
                    </div>
                  </v-col>

                  <v-col cols="12" md="6">
                    <div class="minecraft-section">
                      <h3>World Information</h3>
                      <div class="minecraft-status-item">
                        <span>🏗️ Spawn Point:</span>
                        <strong>{{ worldInfo.spawnPoint }}</strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>🌅 Game Time:</span>
                        <strong>Day {{ worldInfo.gameDay }}</strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>🌦️ Weather:</span>
                        <strong>{{ worldInfo.weather }}</strong>
                      </div>
                      <div class="minecraft-status-item">
                        <span>📏 World Size:</span>
                        <strong>{{ worldInfo.size }}</strong>
                      </div>
                    </div>
                  </v-col>
                </v-row>

                <div class="text-center mt-8">
                  <v-btn
                      class="minecraft-action-btn mr-4"
                      size="large"
                      @click="openWorldSettings"
                  >
                    ⚙️ World Settings
                  </v-btn>
                  <v-btn
                      class="minecraft-action-btn"
                      size="large"
                      @click="openConsole"
                  >
                    💻 Server Console
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-col>

          <v-col cols="12" lg="4">
            <!-- 快速操作面板 -->
            <v-card class="minecraft-content-card mb-6">
              <v-card-title>Quick Actions</v-card-title>
              <v-card-text>
                <v-btn
                    v-for="action in quickActions"
                    :key="action.title"
                    block
                    class="mb-3"
                    :color="action.color"
                    @click="executeAction(action.action)"
                >
                  {{ action.icon }} {{ action.title }}
                </v-btn>
              </v-card-text>
            </v-card>

            <!-- 最近活动 -->
            <v-card class="minecraft-content-card">
              <v-card-title>Recent Activity</v-card-title>
              <v-card-text>
                <v-timeline density="compact">
                  <v-timeline-item
                      v-for="activity in recentActivities"
                      :key="activity.id"
                      :dot-color="activity.color"
                      size="small"
                  >
                    <div class="minecraft-activity-item">
                      <div class="minecraft-activity-title">{{ activity.title }}</div>
                      <div class="minecraft-activity-time">{{ activity.time }}</div>
                    </div>
                  </v-timeline-item>
                </v-timeline>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <!-- 底部信息栏 -->
    <v-footer app class="minecraft-footer">
      <div class="w-100 text-center">
        <span class="minecraft-footer-text">
          🎮 Minecraft Server Admin Panel | Powered by Vue3 & Vuetify |
          Server Uptime: {{ serverUptime }}
        </span>
      </div>
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'

// 响应式数据
const drawer = ref(true)
const currentRoute = ref('/')
const creativeMode = ref(false)
const version = ref('1.20.4')

// 服务器状态
const serverStatus = ref({
  online: true,
  lastCheck: new Date()
})

// 服务器信息
const serverInfo = ref({
  onlinePlayers: 156,
  maxPlayers: 500,
  memoryUsage: 4.2,
  maxMemory: 8,
  tps: 19.8,
  version: '1.20.4 Paper'
})

// 世界信息
const worldInfo = ref({
  spawnPoint: '0, 64, 0',
  gameDay: 1247,
  weather: 'Clear ☀️',
  size: '2.3 GB'
})

// 菜单项
const menuItems = ref([
  {title: 'Dashboard', icon: 'mdi-view-dashboard', route: '/', badge: null},
  {title: 'Players', icon: 'mdi-account-group', route: '/players', badge: '156', badgeColor: 'success'},
  {title: 'Worlds', icon: 'mdi-earth', route: '/worlds', badge: '3', badgeColor: 'info'},
  {title: 'Server', icon: 'mdi-server', route: '/server', badge: null},
  {title: 'Plugins', icon: 'mdi-puzzle', route: '/plugins', badge: '24', badgeColor: 'warning'},
  {title: 'Analytics', icon: 'mdi-chart-line', route: '/analytics', badge: null},
  {title: 'Backups', icon: 'mdi-backup-restore', route: '/backups', badge: '5', badgeColor: 'primary'},
  {title: 'Settings', icon: 'mdi-cog', route: '/settings', badge: null},
])

// 统计数据
const stats = ref([
  {
    title: 'Players Online',
    value: '156',
    icon: '👥',
    highlight: true,
    progress: 31.2,
    progressColor: 'success'
  },
  {
    title: 'Active Worlds',
    value: '3',
    icon: '🌍',
    highlight: false,
    progress: 60,
    progressColor: 'info'
  },
  {
    title: 'Plugins Loaded',
    value: '24',
    icon: '🔧',
    highlight: false,
    progress: 100,
    progressColor: 'warning'
  },
  {
    title: 'Server Uptime',
    value: '7d 12h',
    icon: '⏰',
    highlight: false,
    progress: 85,
    progressColor: 'primary'
  },
])

// 快速操作
const quickActions = ref([
  {title: 'Restart Server', icon: '🔄', color: 'warning', action: 'restart'},
  {title: 'Backup World', icon: '💾', color: 'primary', action: 'backup'},
  {title: 'Broadcast Message', icon: '📢', color: 'info', action: 'broadcast'},
  {title: 'Emergency Stop', icon: '🛑', color: 'error', action: 'stop'},
])

// 最近活动
const recentActivities = ref([
  {id: 1, title: 'Player Steve joined the server', time: '2 minutes ago', color: 'success'},
  {id: 2, title: 'World backup completed', time: '15 minutes ago', color: 'primary'},
  {id: 3, title: 'Plugin EssentialsX updated', time: '1 hour ago', color: 'warning'},
  {id: 4, title: 'Server restart scheduled', time: '2 hours ago', color: 'info'},
  {id: 5, title: 'New player Alex registered', time: '3 hours ago', color: 'success'},
])

// 计算属性
const serverUptime = computed(() => {
  // 这里可以实现真实的服务器运行时间计算
  return '7 days, 12 hours, 34 minutes'
})

// 方法
const toggleTheme = () => {
  console.log('Toggle theme')
}

const toggleCreativeMode = () => {
  creativeMode.value = !creativeMode.value
  console.log('Creative mode:', creativeMode.value ? 'ON' : 'OFF')
}

const navigateTo = (route: string) => {
  currentRoute.value = route
  console.log('Navigate to:', route)
}

const openWorldSettings = () => {
  console.log('Opening world settings...')
}

const openConsole = () => {
  console.log('Opening server console...')
}

const executeAction = (action: string) => {
  console.log('Executing action:', action)
}

const viewProfile = () => {
  console.log('View profile')
}

const openSettings = () => {
  console.log('Open settings')
}

const logout = () => {
  console.log('Logout')
}

// 生命周期
onMounted(() => {
  console.log('Minecraft Admin Dashboard loaded! 🎮')

  // 模拟实时数据更新
  setInterval(() => {
    // 更新在线玩家数（模拟）
    const variance = Math.floor(Math.random() * 10) - 5
    const newPlayerCount = Math.max(0, Math.min(500, serverInfo.value.onlinePlayers + variance))
    serverInfo.value.onlinePlayers = newPlayerCount
    stats.value[0].value = newPlayerCount.toString()
    stats.value[0].progress = (newPlayerCount / serverInfo.value.maxPlayers) * 100

    // 更新TPS（模拟）
    const tpsVariance = (Math.random() - 0.5) * 0.4
    serverInfo.value.tps = Math.max(10, Math.min(20, serverInfo.value.tps + tpsVariance))
    serverInfo.value.tps = Math.round(serverInfo.value.tps * 10) / 10
  }, 5000)
})
</script>

<style scoped lang="scss">
.minecraft-status-chip {
  font-family: 'Press Start 2P', monospace !important;
  font-size: 9px !important;
  border-radius: 0 !important;
  border: 2px solid var(--mc-obsidian);
}

.minecraft-user-btn {
  border-radius: 0 !important;
  background: linear-gradient(145deg, var(--mc-birch), var(--mc-sand)) !important;
}

.minecraft-user-menu {
  border-radius: 0 !important;
  border: 4px solid var(--mc-oak);
  background: var(--mc-birch);
}

.minecraft-activity-item {
  .minecraft-activity-title {
    font-size: 10px;
    color: var(--mc-obsidian);
    margin-bottom: 4px;
  }

  .minecraft-activity-time {
    font-size: 8px;
    color: var(--mc-cobble);
    opacity: 0.8;
  }
}

.text-success {
  color: var(--mc-emerald) !important;
}

.text-warning {
  color: var(--mc-gold) !important;
}

.text-error {
  color: var(--mc-redstone) !important;
}

// 进入动画
.minecraft-stat-card {
  animation: slideInUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(30px);
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
