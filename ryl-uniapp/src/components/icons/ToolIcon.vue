<template>
  <image v-if="base64Src" :src="base64Src" :style="{ width: size + 'px', height: size + 'px' }" />
</template>

<script setup>
import { computed } from 'vue';

// --- Start of btoa polyfill from youngjuning/wxBase64 ---
const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
function btoa(input = '') {
  let str = input;
  let output = '';
  for (let block = 0, charCode, i = 0, map = chars;
  str.charAt(i | 0) || (map = '=', i % 1);
  output += map.charAt(63 & block >> 8 - i % 1 * 8)) {
    charCode = str.charCodeAt(i += 3/4);
    if (charCode > 0xFF) {
      throw new Error("'btoa' failed: The string to be encoded contains characters outside of the Latin1 range.");
    }
    block = block << 8 | charCode;
  }
  return output;
}
// --- End of btoa polyfill ---

const props = defineProps({
  size: {
    type: [Number, String],
    default: 24
  },
  color: {
    type: String,
    default: 'currentColor'
  },
  strokeWidth: {
    type: [Number, String],
    default: 2
  }
});

const iconNode = [
  [
    "path",
    {
      d: "M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z",
      key: "cbrjhi"
    }
  ]
];

const svgContent = computed(() => {
  const nodeString = iconNode.map(([tag, attrs]) => {
    const attrsString = Object.entries(attrs).map(([key, value]) => `${key}="${value}"`).join(' ');
    return `<${tag} ${attrsString}></${tag}>`;
  }).join('');

  return `<svg xmlns="http://www.w3.org/2000/svg" width="${props.size}" height="${props.size}" viewBox="0 0 24 24" fill="none" stroke="${props.color}" stroke-width="${props.strokeWidth}" stroke-linecap="round" stroke-linejoin="round">${nodeString}</svg>`;
});

const base64Src = computed(() => {
  return 'data:image/svg+xml;base64,' + btoa(svgContent.value);
});
</script> 