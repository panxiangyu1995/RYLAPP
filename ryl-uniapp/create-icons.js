const fs = require('fs');
const path = require('path');

// --- 配置 ---

// 1. 需要创建的图标组件名称列表 (kebab-case)
const iconNames = [
  'chevron-left', 'file-text',  'check', 'x', 'layout-grid', 'clipboard-list',
  'log-in', 'building-2', 'phone', 'edit', 'log-out', 'download', 'star',
  'settings', 'recycle', 'package', 'book-open', 'check-circle',
  'list-filter', 'hammer', 'help-circle'
];

// 2. 模板文件的路径
// 我们使用现有的 ToolIcon.vue 作为所有新图标的基础模板
const templateFilePath = path.join(__dirname, 'src', 'components', 'icons', 'ToolIcon.vue');

// 3. 图标源文件和输出目录的路径
const outputDir = path.join(__dirname, 'src', 'components', 'icons');
const lucideDir = path.join(__dirname, 'node_modules', 'lucide-vue-next', 'dist', 'esm', 'icons');

// --- 辅助函数 ---

/**
 * 将 kebab-case 格式的字符串转换为 PascalCase 格式
 * @param {string} str - 输入字符串 (e.g., 'chevron-left')
 * @returns {string} - 输出字符串 (e.g., 'ChevronLeft')
 */
const toPascalCase = (str) => {
  return str.split('-').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join('');
};

// --- 正则表达式 ---
// 用于匹配新结构, e.g., createLucideIcon("icon-name", [ ... ]);
const regexNewStructure = /createLucideIcon\(".*?",\s*(\[[\s\S]*?\])\)/;
// 用于匹配旧结构, e.g., const iconNode = [ ... ];
const regexOldStructure = /const\s+\w+\s*=\s*(\[[\s\S]*?\]);/;


// --- 主逻辑 ---

function main() {
  console.log('--- 开始创建 Lucide 图标组件 ---');

  // 1. 确保输出目录存在
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
    console.log(`创建目录: ${outputDir}`);
  }

  // 2. 检查并读取模板文件
  if (!fs.existsSync(templateFilePath)) {
    console.error(`错误: 模板文件未找到于 ${templateFilePath}`);
    console.error('请确保 src/components/icons/ToolIcon.vue 文件存在并可作为模板。');
    return;
  }
  const templateContent = fs.readFileSync(templateFilePath, 'utf-8');
  console.log('模板文件读取成功。');

  let createdCount = 0;
  let failedCount = 0;

  // 3. 遍历图标列表并创建组件
  iconNames.forEach(iconName => {
    try {
      // a. 定义源文件和目标文件路径
      const sourceFilePath = path.join(lucideDir, `${iconName}.js`);
      const pascalCaseName = toPascalCase(iconName);
      const outputFilePath = path.join(outputDir, `${pascalCaseName}Icon.vue`);

      // b. 检查源文件是否存在
      if (!fs.existsSync(sourceFilePath)) {
        console.warn(`警告: 图标源文件未找到: ${sourceFilePath}。跳过此图标。`);
        failedCount++;
        return;
      }

      // c. 读取源文件并提取 iconNode
      const sourceContent = fs.readFileSync(sourceFilePath, 'utf-8');
      
      let iconNodeMatch;

      // 尝试使用新结构正则进行匹配
      iconNodeMatch = sourceContent.match(regexNewStructure);

      // 如果新结构未匹配, 则尝试旧结构
      if (!iconNodeMatch) {
        iconNodeMatch = sourceContent.match(regexOldStructure);
      }
      
      if (!iconNodeMatch || !iconNodeMatch[1]) {
        console.warn(`警告: 无法在 ${sourceFilePath} 中找到 iconNode。跳过此图标。`);
        failedCount++;
        return;
      }
      const iconNodeString = iconNodeMatch[1];

      // d. 将提取的 iconNode 替换到模板中
      // 使用一个更通用的正则表达式来替换模板中的 iconNode 定义
      const newContent = templateContent.replace(
        /const\s+iconNode\s*=\s*\[[\s\S]*?\];/,
        `const iconNode = ${iconNodeString};`
      );

      // e. 写入新组件文件
      fs.writeFileSync(outputFilePath, newContent, 'utf-8');
      console.log(`✔︎ 已创建: ${path.relative(__dirname, outputFilePath)}`);
      createdCount++;

    } catch (error) {
      console.error(`错误: 处理图标 ${iconName} 时失败:`, error);
      failedCount++;
    }
  });

  console.log('\n--- 操作完成 ---');
  console.log(`成功创建: ${createdCount} 个组件`);
  if (failedCount > 0) {
    console.log(`失败/跳过: ${failedCount} 个图标`);
  }
  console.log('--------------------');
}

// 执行脚本
main(); 