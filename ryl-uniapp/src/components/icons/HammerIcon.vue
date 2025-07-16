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
  ["path", { d: "m15 12-8.373 8.373a1 1 0 1 1-3-3L12 9", key: "eefl8a" }],
  ["path", { d: "m18 15 4-4", key: "16gjal" }],
  [
    "path",
    {
      d: "m21.5 11.5-1.914-1.914A2 2 0 0 1 19 8.172V7l-2.26-2.26a6 6 0 0 0-4.202-1.756L9 2.96l.92.82A6.18 6.18 0 0 1 12 8.4V10l2 2h1.172a2 2 0 0 1 1.414.586L18.5 14.5",
      key: "b7pghm"
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