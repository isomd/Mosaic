<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";
import {type Cube, statisticsItemName} from "@/api/plugin/pluginType";

const emit = defineEmits(['update:modelValue'])
const props = defineProps({
  value: Boolean,
  pluginData: {

  } as Cube
})
const dialog = computed({
  get() { return props.value },
  set(val) { emit('update:modelValue', val) }
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
<template>
  <v-dialog v-model="dialog" max-width="1200">
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
          <v-card-title class="subtitle-1">插件功能拓展包</v-card-title>
          <v-divider></v-divider>
          <v-expansion-panel v-for="(pkg, i) in pluginData.extensionPackages" :key="i">
            <v-expansion-panel-title>
              {{ pkg.name }} <v-chip small class="ml-2">{{ pkg.id }}</v-chip>
            </v-expansion-panel-title>
            <v-expansion-panel-text>
                <v-expansion-panels class="mb-5">
                  <v-card-title class="subtitle-2">拓展点列表</v-card-title>
                  <v-expansion-panel  v-for="(point, j) in pkg.extensionPoints" :key="j">
                    <v-expansion-panel-title>
                      {{ point.extensionName }} <v-chip small class="ml-2">{{ point.id }}</v-chip>
                    </v-expansion-panel-title>
                    <v-expansion-panel-text>
                      <div class="mt-2">
                        <p class="text-caption">入参说明: </p>
                        <v-chip v-if="point.parameterTypes.length > 0" v-for="(param, k) in point.parameterTypes" :key="k" x-small class="mr-1">
                          {{ param }}
                        </v-chip>
                        <v-chip v-else x-small class="mr-1">
                          void
                        </v-chip>
                      </div>
                      <div class="mt-2">
                        <p class="text-caption">出参说明: </p>
                        <v-chip v-for="(result, k) in point.pointResult.pointItems" :key="k" x-small class="mr-2">
                          {{ result.itemName }}: {{ result.itemClass }} | {{ result.description }}
                        </v-chip>
                      </div>
                    </v-expansion-panel-text>
                  </v-expansion-panel>
                </v-expansion-panels>
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
                <td class="font-weight-bold">{{ statisticsItemName[$t(`plugins.cube.${key}`)] }}</td>
                <td class="text-right">{{ value }}</td>
              </tr>
              </tbody>
            </v-simple-table>
          </v-card-text>
        </v-card>

        <ConfigDisplay :config="pluginData.config" />

      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="$emit('update:modelValue', false)">关闭</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.code-font {
  font-family: monospace;
  font-size: 0.9em;
}
.v-chip *{
  font-size: 10px !important;
}
</style>
