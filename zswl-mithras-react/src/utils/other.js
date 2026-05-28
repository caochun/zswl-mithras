import { getLocalStorage } from '@zswl/admin'

export function appendParamsToUrl(url, params) {
  try {
    const urlObj = new URL(url)
    const searchParams = urlObj.searchParams
    for (const key in params) {
      if (params.hasOwnProperty(key) && params[key]) {
        searchParams.append(key, params[key])
      }
    }
    return urlObj.href
  } catch (error) {
    console.error('处理URL参数拼接出现错误:', error)
    return url
  }
}

export const jumpZhongDeng = (props = {}) => {
  const userInfo = getLocalStorage('userInfo')
  const queryParams = {
    userId: userInfo.id,
    userNames: props.userNames,
  }
  let url = 'http://10.158.33.114:8001/sso-redirect'
  if (['prod', 'preSvc', 'uat'].includes(__ENV__)) {
    url = 'http://10.158.33.114:80/sso-redirect'
  }
  window.open(appendParamsToUrl(url, queryParams))
}
