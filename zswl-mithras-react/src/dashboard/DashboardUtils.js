import moment from 'moment'
import { message } from 'antd'
import feikongSsoApi from '@/api/dashboard/feikongSsoApi'
import { getUserInfo } from '@/utils/auth'

export { getChartsTooltip } from '@/components/Chart/TooltipEntries'

const FEIKONG_SSO_CALLBACK_URL = 'http://10.158.33.163/sso/callback'

export const mergeArray = (objValue = [], srcValue = [], dataIndex = 'groupCode') => {
  const mergeConfig = objValue?.filter(Boolean).map((item) => {
    let findConfig = null
    findConfig = srcValue.find((srcItem) => srcItem[dataIndex] === item[dataIndex])
    if (findConfig) {
      return {
        ...findConfig,
        ...item,
      }
    }
    return item
  })
  return mergeConfig
}

export const getUpdateDate = () => {
  return moment().subtract(1, 'day').format('YYYY-MM-DD')
}

export const handleFeikongJump = async () => {
  const newTab = window.open('about:blank', '_blank')
  if (!newTab) {
    message.error('请允许浏览器弹出窗口后重试')
    return
  }
  try {
    const res = await feikongSsoApi.getOauthAuthorize()
    if (!res) {
      newTab.close()
      message.error('未获取到授权码')
      return
    }
    try {
      newTab.location.href = `${FEIKONG_SSO_CALLBACK_URL}?code=${encodeURIComponent(
        res
      )}&client_name=zheshangleasing`
    } catch {
      newTab.close()
      message.error('无法打开新页面')
    }
  } catch {
    newTab.close()
    message.error('获取授权码失败')
  }
}

export const canSeeDetailFn = () => {
  const { id } = getUserInfo()
  return ![52, 198].includes(id)
}
