<script setup>
import { onMounted, ref } from 'vue'
import { StagewiseToolbar } from '@stagewise/toolbar-vue'
import { VuePlugin } from '@stagewise-plugins/vue'

const isClient = ref(false)
const stagewiseConfig = {
  plugins: [VuePlugin]
}

onMounted(() => {
  isClient.value = true
})
</script>

<template>
  <StagewiseToolbar v-if="isClient" :config="stagewiseConfig" />
</template> 