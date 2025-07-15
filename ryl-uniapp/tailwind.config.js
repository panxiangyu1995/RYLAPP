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
      },
    },
  },
  corePlugins: {
    space: false,
    divideWidth: false,
  },
  plugins: [],
}; 