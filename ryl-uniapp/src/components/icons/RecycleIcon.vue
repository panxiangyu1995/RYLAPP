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
      d: "M7 19H4.815a1.83 1.83 0 0 1-1.57-.881 1.785 1.785 0 0 1-.004-1.784L7.196 9.5",
      key: "x6z5xu"
    }
  ],
  [
    "path",
    {
      d: "M11 19h8.203a1.83 1.83 0 0 0 1.556-.89 1.784 1.784 0 0 0 0-1.775l-1.226-2.12",
      key: "1x4zh5"
    }
  ],
  ["path", { d: "m14 16-3 3 3 3", key: "f6jyew" }],
  ["path", { d: "M8.293 13.596 7.196 9.5 3.1 10.598", key: "wf1obh" }],
  [
    "path",
    {
      d: "m9.344 5.811 1.093-1.892A1.83 1.83 0 0 1 11.985 3a1.784 1.784 0 0 1 1.546.888l3.943 6.843",
      key: "9tzpgr"
    }
  ],
  ["path", { d: "m13.378 9.633 4.096 1.098 1.097-4.096", key: "1oe83g" }]
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