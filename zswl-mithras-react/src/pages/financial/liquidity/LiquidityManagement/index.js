import { Card, DatePicker } from 'antd'
import styles from './style.less'
import LiquidityChart from './LiquidityChart'
import { useEffect, useState } from 'react'
import { defaultTimes } from '../index.js'
import LiquidityTable from './LiquidityTable'
import MismatchFunds from './MismatchFunds'
import liquidityRiskApi from '@/api/financial/liquidity/liquidityRiskApi'
import { PureAmountFormat } from '@/components/Format'
import LiquidityInflow from './LiquidityInflow'
import { debounce } from 'lodash'

const LiquidityCards = ({ detail }) => {
  const [timeRange, setTimeRange] = useState({
    queryDateStart: moment(),
    // 往后 30 天
    queryDateEnd: moment().add(30, 'day'),
  })

  const [dataSource, setDataSource] = useState({})
  const fetchIndicators = async () => {
    try {
      const params = {
        queryDateStart: timeRange.queryDateStart.format('YYYY-MM-DD'),
        queryDateEnd: timeRange.queryDateEnd.format('YYYY-MM-DD'),
      }
      const res = await liquidityRiskApi.postManageIndex(params)
      setDataSource(res)
    } catch (error) {
      console.error('获取流动性指标失败:', error)
    }
  }

  useEffect(() => {
    fetchIndicators()
  }, [timeRange])

  const handleTimeChange = (dates) => {
    if (dates) {
      setTimeRange({
        queryDateStart: dates[0],
        queryDateEnd: dates[1],
      })
    }
  }
  const indicators = [
    {
      label: '负债久期',
      icon: require('/public/assets/liquidity/debt-duration.png'),
      dataIndex: 'durationLiability',
    },
    {
      label: '资产久期',
      icon: require('/public/assets/liquidity/asset-duration.png'),
      dataIndex: 'durationAssets',
    },
    {
      label: '资产久期',
      suffix: '(质押/监管)',
      icon: require('/public/assets/liquidity/asset-duration-ratio.png'),
      dataIndex: 'durationAssetsPledgedSupervised',
    },
    {
      label: '资产负债久期比',
      icon: require('/public/assets/liquidity/asset-debt-ratio.png'),
      dataIndex: 'assetLiabilityDurationRatio',
    },
    {
      label: '高流动性资产',
      suffix: '(万元)',
      initFormat: 10000 * 10000,
      icon: require('/public/assets/liquidity/high-liquidity-asset.png'),
      dataIndex: 'highLiquidityAssets',
    },
    {
      label: '高流动性负债',
      suffix: '(万元)',
      initFormat: 10000 * 10000,
      icon: require('/public/assets/liquidity/high-liquidity-debt.png'),
      dataIndex: 'highLiquidityLiability',
    },
    {
      label: '流动性覆盖率',
      icon: require('/public/assets/liquidity/liquidity-coverage.png'),
      dataIndex: 'liquidityCoverageRatio',
      initFormat: 1 / 100,
      suffix: '%',
    },
    {
      label: '流动性缺口',
      dataIndex: 'liquidityGap',
      icon: require('/public/assets/liquidity/liquidity-gap.png'),
      initFormat: 10000 * 10000,
      suffix: '(万元)',
    },
    {
      label: '流动性缺口率',
      dataIndex: 'liquidityGapRate',
      icon: require('/public/assets/liquidity/liquidity-gap-ratio.png'),
      initFormat: 1 / 100,
      suffix: '%',
    },
    {
      label: '可用授信比',
      dataIndex: 'availableCreditRatio',
      icon: require('/public/assets/liquidity/available-credit-ratio.png'),
      initFormat: 1 / 100,
      suffix: '%',
    },
  ]
  return (
    <div className={styles['liquidity-management']}>
      <div className={styles['section-header']}>
        <div className={'z-sub-title'}>流动性指标</div>
        <div className={styles['update-time']}>
          账户余额更新时间：{detail.accountBalanceUpdateTime}
        </div>
      </div>

      <div className={styles['date-range']}>
        <span className={styles.label}>预测区间</span>
        <DatePicker.RangePicker
          value={[timeRange.queryDateStart, timeRange.queryDateEnd]}
          onChange={handleTimeChange}
        />
      </div>

      <div className={styles['indicators-grid']}>
        {indicators.map((item, index) => (
          <div key={index} className={styles['indicator-card']}>
            <div className={styles['indicator-content']}>
              <div className="icon">
                <img src={item.icon} alt={item.label} />
              </div>
              <div className={styles.info}>
                <div className={styles.label}>{item.label}</div>
                <div className={styles['value-wrapper']}>
                  <span
                    className={styles.value}
                    style={{ color: dataSource[item.dataIndex]?.color }}
                  >
                    {PureAmountFormat(dataSource[item.dataIndex]?.value, '-', item.initFormat ?? 1)}
                  </span>
                  {item.suffix && <span className={styles.suffix}>{item.suffix}</span>}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
const LiquidityPanel = ({ detail }) => {
  const [time, setTime] = useState(defaultTimes)
  const [data, setData] = useState([])
  const [predictDay, setPredictDay] = useState(10)
  const getList = async (params) => {
    if (!predictDay) return
    const res = await liquidityRiskApi.postManageBoard({
      queryDateStart: time.timeFrom.format('YYYY-MM-DD'),
      queryDateEnd: time.timeTo.format('YYYY-MM-DD'),

      predictDay,
      ...params,
    })
    setData(res)
  }
  const dateChange = async (value) => {
    setTime({ timeFrom: value[0], timeTo: value[1] })
    getList({
      queryDateStart: value[0].format('YYYY-MM-DD'),
      queryDateEnd: value[1].format('YYYY-MM-DD'),
    })
  }
  useEffect(() => {
    dateChange([defaultTimes.timeFrom, defaultTimes.timeTo])
  }, [])
  useEffect(() => {
    getList()
  }, [predictDay])

  return (
    <div className={styles['liquidity-panel']}>
      <div className={styles['section-header']}>
        <div className={'z-sub-title'}>流动性看板</div>
        <div className={styles['update-time']}>
          账户余额更新时间：{detail.accountBalanceUpdateTime}
        </div>
      </div>
      <div className={styles['date-range']}>
        <span className={styles.label}>预测区间</span>
        <DatePicker.RangePicker
          value={[time.timeFrom, time.timeTo]}
          onChange={debounce(dateChange, 1000)}
        />
      </div>
      <LiquidityChart dataSource={data?.list} />
      <LiquidityInflow queryTime={time} />
      <LiquidityTable
        dataSource={data}
        params={{
          queryDateStart: time.timeFrom.format('YYYY-MM-DD'),
          queryDateEnd: time.timeTo.format('YYYY-MM-DD'),
          predictDay,
        }}
        time={time}
        setPredictDay={setPredictDay}
      />
    </div>
  )
}
const LiquidityManagement = ({ detail }) => {
  return (
    <>
      <LiquidityCards detail={detail} />
      <LiquidityPanel detail={detail} />
      <MismatchFunds detail={detail} />
    </>
  )
}

export default LiquidityManagement
