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
      d: "M11 21.73a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73z",
      key: "1a0edw"
    }
  ],
  ["path", { d: "M12 22V12", key: "d0xqtd" }],
  ["polyline", { points: "3.29 7 12 12 20.71 7", key: "ousv84" }],
  ["path", { d: "m7.5 4.27 9 5.15", key: "1c824w" }]
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