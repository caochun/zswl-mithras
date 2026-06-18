import {
  DashboardArrowDown2 as ArrowDown2,
  DashboardArrowUp2 as ArrowUp2,
  DashboardBoardAssets as boradAssets,
  DashboardBoardInvestment as boardInvestment,
} from '@/components/Dashboard/DashboardEntries'
import styles from './index.less'
import { useEffect, useState } from 'react'
import { Spin } from 'antd'
import { AmountFormat } from '@/components/Format'
import { hasValue } from '@/utils'
import Api from '@/api/dashboard/overview'

// 不小于0
const getPlusMinusConfig = ({ num }) => {
  if (num >= 0) {
    return {
      color: '#EB2222',
      icon: <ArrowUp2 />,
    }
  }
  return {
    color: '#00B87A',
    icon: <ArrowDown2 />,
  }
}

const DataDisplay2 = ({ num }) => {
  if (!hasValue(num)) return '-'
  const config = getPlusMinusConfig({ num })
  return (
    <>
      <div>{config.symbol}</div>
      <div style={{ color: config.color }}>
        <AmountFormat value={Math.abs(num)} initFormat={1} unit="%"></AmountFormat>
      </div>
      <div style={{ marginLeft: 5 }}>{config.icon}</div>
    </>
  )
}

export const AssetsBalance = () => {
  const [data, setData] = useState()
  const [loading, setLoading] = useState(true)

  const getData = async () => {
    setLoading(true)
    const res = await Api.postDashboardAssetsBalanceOverview()
    setData(res)
    setLoading(false)
  }

  useEffect(() => {
    getData()
  }, [])

  if (loading) {
    return (
      <Spin>
        <div className={styles.panel} />
      </Spin>
    )
  }
  return (
    <div className={styles.panel}>
      <div className={styles.left}>
        <div className={styles.block0}>
          <img src={boradAssets} className={styles.img}></img>
          <div className={styles.block1}>
            <div className={styles.block1_title}>资产余额({data.assetsBalance?.unit})</div>
            <div className={styles.block1_value}>
              <AmountFormat value={data.assetsBalance?.value} initFormat={1}></AmountFormat>
            </div>
          </div>
        </div>
        <div className={styles.block2}>
          <div className={styles.block2_item}>
            <span className={styles.block2_item_title}>本年目标</span>
            <span>{data.assetsBalanceGoalThisYear?.value || '-'}</span>
          </div>
          <div className={styles.block2_item}>
            <span className={styles.block2_item_title}>本月新增</span>
            <span>{data.assetsBalanceIncrementThisMonth?.value || '-'}</span>
          </div>
        </div>
        <div className={styles.block1}>
          <div className={styles.block1_title}>本年完成情况</div>
          <div className={styles.block1_value}>
            <AmountFormat
              value={data.completionPercentThisYear?.value}
              unit={data.completionPercentThisYear?.unit}
              initFormat={1}
            ></AmountFormat>
          </div>
        </div>
        <div className={styles.block3}>
          <div className={styles.block3_title}>同比</div>
          <div className={styles.block3_value}>
            <DataDisplay2 num={data.assetsBalanceYearOnYearBasis?.value}></DataDisplay2>
          </div>
        </div>
      </div>
    </div>
  )
}

export const AssetsLoan = () => {
  const [data, setData] = useState()
  const [loading, setLoading] = useState(true)

  const getData = async () => {
    setLoading(true)
    const res = await Api.postDashboardAssetsLoanOverview()
    setData(res)
    setLoading(false)
  }

  useEffect(() => {
    getData()
  }, [])

  if (loading) {
    return (
      <Spin>
        <div className={styles.panel} />
      </Spin>
    )
  }
  return (
    <div className={styles.panel}>
      <div className={styles.left}>
        <div className={styles.block0}>
          <img src={boardInvestment} className={styles.img}></img>
          <div className={styles.block1}>
            <div className={styles.block1_title}>本年投放({data.loanThisYear?.unit})</div>
            <div className={styles.block1_value}>
              <AmountFormat value={data.loanThisYear?.value} initFormat={1}></AmountFormat>
            </div>
          </div>
        </div>
        <div className={styles.block2}>
          <div className={styles.block2_item}>
            <span className={styles.block2_item_title}>全年目标</span>
            <span>{data.loanGoalThisYear?.value || '-'}</span>
          </div>
          <div className={styles.block2_item}>
            <span className={styles.block2_item_title}>本月新增 </span>
            <span>{data.loanIncrementThisMonth?.value || '-'}</span>
          </div>
        </div>
      </div>
      <div className={styles.right}>
        <div className={styles.block1}>
          <div className={styles.block1_title}>本年完成情况</div>
          <div className={styles.block1_value}>
            <AmountFormat
              value={data.completionPercentThisYear?.value}
              initFormat={1}
              unit={data.completionPercentThisYear?.unit}
            ></AmountFormat>
          </div>
        </div>
        <div className={styles.block3}>
          <div className={styles.block3_title}>同比</div>
          <div className={styles.block3_value}>
            <DataDisplay2 num={data.loanYearOnYearBasis?.value}></DataDisplay2>
          </div>
        </div>
      </div>
    </div>
  )
}
