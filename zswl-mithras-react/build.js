const { execSync } = require('child_process')
const fs = require('fs')
const path = require('path')
const archiver = require('archiver')

// 生成控制台打印输出
function generateLog({ env }) {
  // 获取当前分支名
  let branch = 'unknown'
  try {
    branch = execSync('git rev-parse --abbrev-ref HEAD').toString().trim()
  } catch (error) {
    console.error('获取分支名失败:', error)
  }

  const logTable = {
    打包环境: env,
    打包时间: new Date().toLocaleString(),
    打包分支名称: branch,
  }
  // 获取最后一次提交的作者和提交时间
  try {
    const logInfo = execSync('git log -1 --pretty=format:"%an|%cd"').toString().trim().split('|')
    const authorName = logInfo[0].trim()
    const commitDate = new Date(logInfo[1].trim()).toLocaleString()
    logTable['最后一次提交的作者'] = authorName
    logTable['最后一次提交的时间'] = commitDate
  } catch (error) {
    console.error('执行命令时出错:', error)
  }
  return logTable
}

// 写入打包信息到html
function writeLogToHtml({ logTable }) {
  const htmlFilePath = path.join(__dirname, './dist/index.html')
  const file = fs.readFileSync(htmlFilePath, 'UTF-8')
  const logText = JSON.stringify(logTable, null, 2)
  const updatedData = file.replace(
    /<script id="buildInfo">[^<]*<\/script>/,
    `<script id="buildInfo">
    console.table(${logText})
   </script>`
  )
  fs.writeFileSync(htmlFilePath, updatedData)
  console.log('已成功写入打包信息到HTML文件。')
  console.table(logTable)
}

const args = parseCliKVArgs();

// 打包环境
const ENV_LIST = args && args.env ? [args.env] : ['uat', 'preSvc', 'prod']
// const ENV_LIST = ['uat']

// 打包产物文件夹
const BUNDLE_DIR = 'mithras-react'

// 删除打包产物
execSync(`rm -rf ${BUNDLE_DIR}*`, { stdio: 'inherit' })
fs.mkdirSync(BUNDLE_DIR)

execSync(`git remote set-url origin http://yaogan:Zszl%40123456@10.158.32.114/frontend/mithras-react.git`, { stdio: 'inherit' })
// 拉取代码
if(args && args.branch){
  execSync(`git pull origin ${args.branch}`, { stdio: 'inherit' })
}else{
  execSync(`git pull`, { stdio: 'inherit' })
}

// 安装依赖
execSync(`yarn`, { stdio: 'inherit' })

const generate = async (env) => {
  console.log(`开始打包${env}环境`)
  execSync(`admin build env=${env}`, { stdio: 'inherit' })
  await prepareBuild({ env })
}

// 顺序执行每个环境的打包
const buildAll = async () => {
  for (const env of ENV_LIST) {
    await generate(env)
  }
  console.log('所有环境打包完成！')
}

buildAll().catch(console.error)

function prepareBuild({ env }) {
  return new Promise((resolve, reject) => {
    const logTable = generateLog({ env })
    writeLogToHtml({
      logTable,
    })

    // 重命名dist目录为环境特定目录
    const envDir = `${BUNDLE_DIR}/${env}`
    fs.renameSync('dist', envDir)

    // 为每个环境生成单独的zip文件
    const envZipName = `${BUNDLE_DIR}-${env}.zip`
    const archive = archiver('zip', { zlib: { level: 9 } })
    const output = fs.createWriteStream(`./${BUNDLE_DIR}/${envZipName}`)

    // 监听压缩完成事件
    output.on('close', () => {
      console.log(`已生成 ${envZipName} (${archive.pointer()} bytes)`)
      resolve()
    })

    // 监听错误事件
    archive.on('error', (err) => {
      console.error(`压缩 ${envZipName} 时出错:`, err)
      reject(err)
    })

    archive.pipe(output)
    archive.directory(envDir, 'dist') // 将环境目录添加到压缩包
    archive.finalize()
  })
}

function parseCliKVArgs() {
  // process.argv 结构说明：
  // [0] = node 执行路径（如 /usr/local/bin/node）
  // [1] = 当前脚本路径（如 /xxx/build.js）
  // [2...] = 自定义参数（如 env=prod、branch=refs/heads/feature/20251211）
  const cliArgs = process.argv.slice(2); 
  const argsMap = {};

  cliArgs.forEach(arg => {
    // 拆分 key 和 value（仅按第一个 = 拆分，避免 value 含 = 的情况）
    const [key, ...valueParts] = arg.split('=');
    if (key && valueParts.length > 0) {
      const value = valueParts.join('='); // 还原 value 中可能含有的 =
      argsMap[key] = value;
    }
  });

  return argsMap;
}