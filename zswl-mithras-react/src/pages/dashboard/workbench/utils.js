import moment from 'moment'
import ReactDOMServer from 'react-dom/server'
import { message } from 'antd'
import { amountFormat } from '@/utils'
import workbenchApi from '@/api/common/workbenchApi'

const FEIKONG_SSO_CALLBACK_URL = 'http://10.158.33.163/sso/callback'

// 小卡片合并本地配置
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

// 获取模块更新日期-默认昨日日期
export const getUpdateDate = () => {
  return moment().subtract(1, 'day').format('YYYY-MM-DD')
}

// echarts 的 toolTip,因为@zswl/charts组件库适配的是蓝底的，不写进去适配了
export const getChartsTooltip = (props) => {
  return {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    extraCssText: 'box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.3)',
    formatter: function (params) {
      return ReactDOMServer.renderToStaticMarkup(
        <div style={{ padding: '8px 16px' }}>
          <div style={{ marginBottom: 6 }}>{params?.[0]?.name}</div>
          {params?.map(({ color, seriesName, data = {} }, index) => {
            return (
              <div key={index} style={{ marginBottom: 5, display: 'flex', alignItems: 'center' }}>
                {seriesName && (
                  <>
                    <span
                      style={{
                        display: 'inline-block',
                        marginRight: '6px',
                        width: '7px',
                        height: '7px',
                        backgroundColor: color,
                      }}
                    ></span>
                    <span
                      style={{
                        fontSize: '14px',
                        color: '#5e6066',
                      }}
                    >
                      {seriesName}
                    </span>
                    <span>：</span>
                  </>
                )}

                <span
                  style={{
                    fontSize: '14px',
                    color: '#5e6066',
                  }}
                >
                  {amountFormat(data?.value ?? data)}
                </span>
                <span>{data?.unit}</span>
              </div>
            )
          })}
        </div>
      )
    },
    ...props,
  }
}

/** 费控系统：新开页跳转 SSO 回调 */
export const handleFeikongJump = async () => {
  const newTab = window.open('about:blank', '_blank')
  if (!newTab) {
    message.error('请允许浏览器弹出窗口后重试')
    return
  }
  try {
    const res = await workbenchApi.getOauthAuthorize()
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
