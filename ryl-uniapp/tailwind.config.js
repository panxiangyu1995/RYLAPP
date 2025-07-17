/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'primary-dark': '#0E4472',    // 主深蓝色
        'primary-medium': '#286B9C',  // 主中蓝色
        'primary-light': '#6C9DBB',   // 主浅蓝色
        'neutral-gray': '#B2B3B2',    // 中性灰色
        'neutral-light': '#DADCDF',   // 中性浅灰
        'new-primary-dark': '#0D1B2A',
        'new-primary-medium': '#2A6F97',
        'new-primary-light': '#64A6C4',
        'accent-lime': '#D4FF00',
        'neutral-bg': '#FFFFFF',
        'neutral-text': '#333333',
        'ui-blue-start': '#4F46E5',
        'ui-blue-end': '#00D4FF',
        'ui-accent-lime': '#D4FF00',
        'ui-text-black': '#111827',
        'ui-bg-white': '#FFFFFF'
      },
      backgroundImage: {
        'primary-gradient': 'linear-gradient(to right, #64A6C4, #2A6F97)',
        'ui-vibrant-gradient': 'linear-gradient(135deg, #4F46E5 0%, #00D4FF 100%)',
      },
      boxShadow: {
        'ui-glow': '0 0 8px 0 rgb(0 212 255 / 40%)',
      }
    },
  },
  corePlugins: {
    space: false,
    divideWidth: false,
  },
  plugins: [],
}; 