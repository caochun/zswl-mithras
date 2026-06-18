import { observer } from '@zswl/admin'
import styles from './index.less'
import { Button, Table, TableStore } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import {
  amountFormat,
  formatPercent,
  getLocalColumnsFilter,
  getTableColumns,
  setLocalColumnsFilter,
} from '@/utils'
import ALL_COLUMNS from '../Column'
import { Space } from 'antd'
import { Summary as TableSummary } from '@/components/Table'
import Api from '@/api/financial/creditManage'
import { PageListDown } from '@/components'
import _ from 'lodash'
import { saveServer } from '@/utils'

export const columnsFilterKey = 'credit-use-detail'
const Index = ({ id }) => {
  const [sumData, setSumData] = useState({})
  const table = useMemo(
    () =>
      new TableStore({
        request: async () => {
          const res = await Api.postLimitDetail({ id })
          const { limitDetailList = [], limitDetailSum = {}, limitDetailListSum } = res ?? {}
          setSumData({ ...limitDetailSum, ...limitDetailListSum })
          return limitDetailList
        },
      }),
    []
  )
  const columns = getTableColumns(ALL_COLUMNS, [
    '融资编号',
    '融资金额（元）',
    '担保融资额（元）',
    '信用融资额（元）',
    { title: '占用额度（元）', filter: false },
    { title: '占用担保额度（元）', filter: false },
    { title: '占用信用额度（元）', filter: false },
    '剩余本金（元）',
    '剩余担保本金（元）',
    '剩余信用本金（元）',
    '合同利率',
    '贷款日',
    '到期日',
    '融资状态',
    '创建人',
  ])
  const {
    usedTotalCreditAmountSum = 0,
    usedCreditAmountSum = 0,
    usedGuaranteeAmountSum = 0,
    remainingTotalCreditAmountSum = 0,
    remainingCreditAmountSum = 0,
    remainingGuaranteeAmountSum = 0,
    ...rest
  } = sumData

  return (
    <div>
      <div className={styles.title}>使用详情</div>
      <Table
        resizable
        columnsFilter={columnsFilterKey}
        onFilter={(key,val) => saveServer(columnsFilterKey,val)}
        columnWidth={120}
        columns={columns}
        store={table}
        actions={[
          <Space>
            <div>
              已占用额度：{amountFormat(formatPercent(usedTotalCreditAmountSum))}元（担保：
              {amountFormat(formatPercent(usedGuaranteeAmountSum))}元，信用：
              {amountFormat(formatPercent(usedCreditAmountSum))}元）
            </div>
            <div>
              可用额度： {amountFormat(formatPercent(remainingTotalCreditAmountSum))}元（担保：
              {amountFormat(formatPercent(remainingGuaranteeAmountSum))}元，信用：
              {amountFormat(formatPercent(remainingCreditAmountSum))}元）
            </div>
          </Space>,
        ]}
        extra={[<PageListDown table={table} module="fundCreditLimit" extraParams={{ id }} />]}
        summary={() => {
          return (
            <TableSummary
              title="总合计"
              columns={table.getOptimizedColumns()}
              sumData={rest}
              initFormat={10000}
            ></TableSummary>
          )
        }}
      />
    </div>
  )
}

export default observer(Index)
