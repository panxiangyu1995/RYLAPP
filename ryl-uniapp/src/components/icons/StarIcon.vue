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
      d: "M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z",
      key: "r04s7s"
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