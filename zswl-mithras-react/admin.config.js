/** @type {import('@zswl/admin').Config} */

module.exports = (config) => {
  const { env } = config
  const themeMap = new Map([
    ['preSvc', '#008c8c'],
    ['uat', '#942ce1'],
  ])
  const themeColor = themeMap.get(env)
  const newConfig = {
    ...config,
    // proxy: {
    //   '/zswl': {
    //     target: 'http://192.168.0.59:7003',
    //     changeOrigin: true,
    //     ws: true,
    //   },
    // },
    yapi2ts: {
      serverUrl: 'http://yapi.zswltec.com:3000',
      token: '9c181faa6ae020be94c597b6eb5a68ba701a4a244f8e97f8697fd4eeb27c4ba3',
      dataKey: 'data',
    },
    lazyCompilation: false,
    // ESLint配置：构建时禁用ESLint检查（不中断构建），但编辑器中的ESLint扩展仍会显示错误提示
    // 编辑器中的错误提示由 .vscode/settings.json 和 .eslintrc.js 控制
    eslint: false,
    doctor: false,
  }
  if (themeColor) {
    newConfig.lessOptions = {
      modifyVars: {
        'primary-color': themeColor,
      },
    }
  }

  return newConfig
}
