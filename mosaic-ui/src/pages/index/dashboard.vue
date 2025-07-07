<script lang="ts" setup>
import {computed, onMounted, ref} from "vue";
import {useCubeStore} from "@/store/data/useCubeStore";
import {useSlotStore} from "@/store/data/useSlotStore";
import {useWorldStore} from "@/store/data/useWorldStore";
const cubeStore = useCubeStore()
const slotStore = useSlotStore()
const worldStore = useWorldStore()
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
const slotNums = computed(()=>{
  return slotStore.slotList.length
})
const cubeNums = computed(()=>{
  return cubeStore.cubeList.length
})
const worldNums = computed(()=>{
  return worldStore.worldList.length
})
const stats = ref([
  {
    title: 'dashboard.slotNum',
    value: slotNums.value,
    icon: 'mdi-shovel',
    highlight: true,
    progress: 31.2,
    progressColor: 'success'
  },
  {
    title: 'dashboard.worldNum',
    value: worldNums.value,
    icon: 'mdi-earth-box',
    highlight: false,
    progress: 60,
    progressColor: 'info'
  },
  {
    title: 'dashboard.pluginNum',
    value: cubeNums.value,
    icon: 'mdi-puzzle',
    highlight: false,
    progress: 100,
    progressColor: 'warning'
  },
  // {
  //   title: 'dashboard.runningTime',
  //   value: '7d 12h',
  //   icon: 'mdi-clock-time-five-outline',
  //   highlight: false,
  //   progress: 85,
  //   progressColor: 'primary'
  // },
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


// 生命周期
onMounted(() => {
  // console.log('Minecraft Admin Dashboard loaded! 🎮')

  // 模拟实时数据更新
  // setInterval(() => {
  //   // 更新在线玩家数（模拟）
  //   const variance = Math.floor(Math.random() * 10) - 5
  //   const newPlayerCount = Math.max(0, Math.min(500, serverInfo.value.onlinePlayers + variance))
  //   serverInfo.value.onlinePlayers = newPlayerCount
  //   stats.value[0].value = newPlayerCount.toString()
  //   stats.value[0].progress = (newPlayerCount / serverInfo.value.maxPlayers) * 100
  //
  //   // 更新TPS（模拟）
  //   const tpsVariance = (Math.random() - 0.5) * 0.4
  //   serverInfo.value.tps = Math.max(10, Math.min(20, serverInfo.value.tps + tpsVariance))
  //   serverInfo.value.tps = Math.round(serverInfo.value.tps * 10) / 10
  // }, 5000)
})

</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.dashboard')}}</h1>
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
            :style="{ animationDelay: `${3 * 0.1}s` }"
        >
          <v-card-text class="text-center">
            <div class="minecraft-stat-icon"><v-icon>{{ stat.icon }}</v-icon></div>
            <div class="minecraft-stat-number">{{ stat.value }}</div>
            <div class="minecraft-stat-title">{{ $t(stat.title) }}</div>
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
<!--    <v-row>-->
<!--      <v-col cols="12" lg="8">-->
<!--        <v-card class="minecraft-content-card">-->
<!--          <v-card-title>World Overview</v-card-title>-->
<!--          <v-card-text>-->
<!--            <p>Welcome to your advanced Minecraft Admin Dashboard! Manage your server with precision and style.</p>-->

<!--            <v-row class="mt-6">-->
<!--              <v-col cols="12" md="6">-->
<!--                <div class="minecraft-section">-->
<!--                  <h3>Server Status</h3>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>🟢 Online Players:</span>-->
<!--                    <strong>{{ serverInfo.onlinePlayers }} / {{ serverInfo.maxPlayers }}</strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>💾 Memory Usage:</span>-->
<!--                    <strong>{{ serverInfo.memoryUsage }}GB / {{ serverInfo.maxMemory }}GB</strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>⚡ TPS:</span>-->
<!--                    <strong-->
<!--                        :class="{ 'text-success': serverInfo.tps >= 19, 'text-warning': serverInfo.tps < 19 && serverInfo.tps >= 15, 'text-error': serverInfo.tps < 15 }">-->
<!--                      {{ serverInfo.tps }}-->
<!--                    </strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>🌐 Version:</span>-->
<!--                    <strong>{{ serverInfo.version }}</strong>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </v-col>-->

<!--              <v-col cols="12" md="6">-->
<!--                <div class="minecraft-section">-->
<!--                  <h3>World Information</h3>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>🏗️ Spawn Point:</span>-->
<!--                    <strong>{{ worldInfo.spawnPoint }}</strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>🌅 Game Time:</span>-->
<!--                    <strong>Day {{ worldInfo.gameDay }}</strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>🌦️ Weather:</span>-->
<!--                    <strong>{{ worldInfo.weather }}</strong>-->
<!--                  </div>-->
<!--                  <div class="minecraft-status-item">-->
<!--                    <span>📏 World Size:</span>-->
<!--                    <strong>{{ worldInfo.size }}</strong>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </v-col>-->
<!--            </v-row>-->

<!--            <div class="text-center mt-8">-->
<!--              <v-btn-->
<!--                  class="minecraft-action-btn mr-4"-->
<!--                  size="large"-->
<!--                  @click="openWorldSettings"-->
<!--              >-->
<!--                ⚙️ World Settings-->
<!--              </v-btn>-->
<!--              <v-btn-->
<!--                  class="minecraft-action-btn"-->
<!--                  size="large"-->
<!--                  @click="openConsole"-->
<!--              >-->
<!--                💻 Server Console-->
<!--              </v-btn>-->
<!--            </div>-->
<!--          </v-card-text>-->
<!--        </v-card>-->
<!--      </v-col>-->

<!--      <v-col cols="12" lg="4">-->
<!--        &lt;!&ndash; 快速操作面板 &ndash;&gt;-->
<!--        <v-card class="minecraft-content-card mb-6">-->
<!--          <v-card-title>Quick Actions</v-card-title>-->
<!--          <v-card-text>-->
<!--            <v-btn-->
<!--                v-for="action in quickActions"-->
<!--                :key="action.title"-->
<!--                block-->
<!--                class="mb-3"-->
<!--                :color="action.color"-->
<!--                @click="executeAction(action.action)"-->
<!--            >-->
<!--              {{ action.icon }} {{ action.title }}-->
<!--            </v-btn>-->
<!--          </v-card-text>-->
<!--        </v-card>-->

<!--        &lt;!&ndash; 最近活动 &ndash;&gt;-->
<!--        <v-card class="minecraft-content-card">-->
<!--          <v-card-title>Recent Activity</v-card-title>-->
<!--          <v-card-text>-->
<!--            <v-timeline density="compact">-->
<!--              <v-timeline-item-->
<!--                  v-for="activity in recentActivities"-->
<!--                  :key="activity.id"-->
<!--                  :dot-color="activity.color"-->
<!--                  size="small"-->
<!--              >-->
<!--                <div class="minecraft-activity-item">-->
<!--                  <div class="minecraft-activity-title">{{ activity.title }}</div>-->
<!--                  <div class="minecraft-activity-time">{{ activity.time }}</div>-->
<!--                </div>-->
<!--              </v-timeline-item>-->
<!--            </v-timeline>-->
<!--          </v-card-text>-->
<!--        </v-card>-->
<!--      </v-col>-->
<!--    </v-row>-->
</template>
<style scoped lang="scss">

</style>
