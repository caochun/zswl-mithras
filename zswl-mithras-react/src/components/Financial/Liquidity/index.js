import baseInfoApi from '@/api/financial/liquidity/baseInfoApi'
import { PureAmountFormat } from '@/components/Format'
import { downLoadImg } from '@/utils'
import { history, observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Button, Spin, Tabs } from 'antd'
import moment from 'moment'
import { useRef, useState } from 'react'
import FundDailyReport from './FundDailyReport/FinancialLiquidityFundDailyReport'
import LiquidityManagement from './LiquidityManagement'
import SupervisionAccount from './SupervisionAccount'
import './style.less'

export const defaultTimes = {
  // 7 天前
  timeFrom: moment(),
  timeTo: moment().add(7, 'day'),
}

const LiquidityTabs = () => {
  const fundDailyReportRef = useRef(null)
  const [downloading, setDownloading] = useState(false)
  const [activeKey, setActiveKey] = useState('1')

  const page = Page.useStore({
    request: async () => {
      const res = await baseInfoApi.postParameterBaseDetail({})
      return res
    },
  })

  const detail = page.getData()

  const handleDownloadReport = async () => {
    if (fundDailyReportRef.current) {
      try {
        setDownloading(true)
        await fundDailyReportRef.current.setDisablePagination(true)
        await new Promise((resolve) => setTimeout(resolve, 500))
        // 将 html2canvas 操作延迟到下一个事件循环，避免阻塞 UI
        await new Promise((resolve) => setTimeout(resolve, 0))
        const today = moment().format('YYYY-MM-DD')
        await downLoadImg('fundDailyReport', `${today}_资金日报`)
        await fundDailyReportRef.current.setDisablePagination(false)
      } catch (error) {
        console.error('下载失败:', error)
        if (fundDailyReportRef.current) {
          await fundDailyReportRef.current.setDisablePagination(false)
        }
      } finally {
        setDownloading(false)
      }
    } else {
      setDownloading(true)
      try {
        // 将 html2canvas 操作延迟到下一个事件循环
        await new Promise((resolve) => setTimeout(resolve, 0))
        const today = moment().format('YYYY-MM-DD')
        await downLoadImg('fundDailyReport', `${today}_资金日报`)
      } finally {
        setDownloading(false)
      }
    }
  }

  const items = [
    {
      key: '3',
      label: '流动性管理',
      children: <LiquidityManagement detail={detail} />,
    },
    {
      key: '1',
      label: '资金日报',
      children: <FundDailyReport ref={fundDailyReportRef} detail={detail} id="fundDailyReport" />,
    },
    {
      key: '2',
      label: '监管户待转资金',
      children: <SupervisionAccount detail={detail} />,
    },
  ]

  return (
    <Page store={page}>
      <Spin spinning={downloading} tip="正在生成资金日报，请稍候...">
        <div className="liquidity-tabs">
          <div className="header-wrapper">
            <Tabs
              items={items}
              activeKey={activeKey}
              onChange={setActiveKey}
              style={{ width: '100%' }}
              tabBarExtraContent={
                <div className="right-content">
                  <div className="data-display">
                    <span className="label">安全库存</span>
                    <span className="value">
                      {PureAmountFormat(detail.saveStock, '-', 10000 * 10000)}
                    </span>
                    <span className="unit">（万元）</span>
                  </div>
                  <div className="data-display">
                    <span className="label">灵活授信</span>
                    <span className="value">
                      {PureAmountFormat(detail.flexibleCredit, '-', 10000 * 10000)}
                    </span>
                    <span className="unit">（万元）</span>
                  </div>
                  <div className="button-group">
                    <Button
                      type="primary"
                      onClick={() => history.push('/financial/liquidity/accountBalanceDetail')}
                    >
                      账户余额明细
                    </Button>
                    <Button
                      type="primary"
                      onClick={() => history.push('/financial/liquidity/predictionParameters')}
                    >
                      预测参数配置
                    </Button>
                    {activeKey === '1' && (
                      <Button type="primary" onClick={handleDownloadReport}>
                        生成资金日报
                      </Button>
                    )}
                  </div>
                </div>
              }
            />
          </div>
        </div>
      </Spin>
    </Page>
  )
}

export default observer(LiquidityTabs)
