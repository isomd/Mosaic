<template>
  <v-dialog v-model="dialog" max-width="800">
    <v-card>
      <v-toolbar color="primary" dark>
        <v-toolbar-title>{{ pluginData.name }} ({{ pluginData.id }})</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-chip label color="green" text-color="white">
          {{ pluginData.status }}
        </v-chip>
      </v-toolbar>

      <v-card-text class="mt-4">
        <v-row>
          <v-col cols="6">
            <v-list dense>
              <v-list-item>
                <v-list-item-subtitle>
                  <v-list-item-title>版本</v-list-item-title>
                  <v-list-item-title>{{ pluginData.version }}</v-list-item-title>
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-subtitle>
                  <v-list-item-title>模型类型</v-list-item-title>
                  <v-list-item-title>{{ pluginData.model }}</v-list-item-title>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-col>
          <v-col cols="6">
            <v-list dense>
              <v-list-item>
                <v-list-item-subtitle>
                  <v-list-item-title>注册时间</v-list-item-title>
                  <v-list-item-title>{{ formatDate(pluginData.registeredTime) }}</v-list-item-title>
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-subtitle>
                  <v-list-item-title>更新时间</v-list-item-title>
                  <v-list-item-title>{{ formatDate(pluginData.lastUpdatedTime) }}</v-list-item-title>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>

        <v-alert border="left" color="blue-grey" dark class="my-4">
          {{ pluginData.description || '暂无描述信息' }}
        </v-alert>

        <v-expansion-panels class="mb-4">
          <v-expansion-panel v-for="(pkg, i) in pluginData.extensionPackages" :key="i">
            <v-expansion-panel-title>
              {{ pkg.name }} <v-chip small class="ml-2">{{ pkg.id }}</v-chip>
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-list dense>
                <v-list-item>
                    <v-list-item-title>类名</v-list-item-title>
                    <v-list-item-subtitle class="code-font">{{ pkg.className }}</v-list-item-subtitle>
                </v-list-item>
                <v-divider></v-divider>

                <v-list-item v-for="(point, j) in pkg.extensionPoints" :key="j">
                    <v-list-item-title>{{ point.extensionName }}</v-list-item-title>
                    <v-list-item-subtitle class="d-flex align-center">
                      <v-chip x-small label class="mr-2">{{ point.methodName }}()</v-chip>
                      <v-chip x-small label color="indigo" text-color="white">
                        {{ point.returnType }}
                      </v-chip>
                    </v-list-item-subtitle>

                    <div class="mt-2">
                      <span class="text-caption">参数: </span>
                      <v-chip v-for="(param, k) in point.parameterTypes" :key="k" x-small class="mr-1">
                        {{ param }}
                      </v-chip>
                    </div>
                </v-list-item>
              </v-list>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>

        <v-card outlined class="mb-4">
          <v-card-title class="subtitle-1">统计信息</v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-simple-table dense>
              <tbody>
              <tr v-for="(value, key) in pluginData.statistics" :key="key">
                <td class="font-weight-bold">{{ $t(`plugins.cube.${key}`) }}</td>
                <td class="text-right">{{ value }}</td>
              </tr>
              </tbody>
            </v-simple-table>
          </v-card-text>
        </v-card>

        <v-card outlined>
          <v-card-title class="subtitle-1">配置信息</v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-simple-table dense>
              <tbody>
              <tr v-for="(value, key) in pluginData.config" :key="key">
                <td class="font-weight-bold">{{ $t(`plugins.cube.${key}`) }}</td>
                <td class="text-right">{{ formatConfigValue(value) }}</td>
              </tr>
              </tbody>
            </v-simple-table>
          </v-card-text>
        </v-card>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="dialog = false">关闭</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";
const emit = defineEmits(['input'])
const props = defineProps({
  value: Boolean,
  pluginData: Object
})
const dialog = computed({
  get() { return props.value },
  set(val) { emit('input', val) }
})

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}
const formatConfigValue = (value) => {
  if (value === null) return 'null'
  if (Array.isArray(value)) return value.join(', ') || '空数组'
  if (typeof value === 'object') return JSON.stringify(value)
  return value.toString()
}

</script>

<style scoped>
.code-font {
  font-family: monospace;
  font-size: 0.9em;
}
.v-chip *{
  font-size: 10px !important;
}
</style>
