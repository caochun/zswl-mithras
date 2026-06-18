import { observer } from '@zswl/admin'
import styles from './index.less'
import { Button, Table, TableStore } from '@zswl/components'
import { useMemo, useState } from 'react'
import { amountFormat, formatPercent, getTableColumns } from '@/utils'
import ALL_COLUMNS from '../Column'
import { Space } from 'antd'
import { PageListDown } from '@/components'
import Api from '@/api/financial/orgManage'
import { Summary as TableSummary } from '@/components/Table'
import { saveServer } from '@/utils'

const Index = ({ id }) => {
  const [sumData, setSumData] = useState({})
  const table = useMemo(
    () =>
      new TableStore({
        request: async () => {
          const res = await Api.postInfoLimitDetail({ id })
          const { limitDetailList = [], limitDetailSum = {}, limitDetailListSum } = res ?? {}
          setSumData({ ...limitDetailSum, ...limitDetailListSum })
          return limitDetailList
        },
      }),
    []
  )
  const columns = getTableColumns(ALL_COLUMNS, [
    '融资编号',
    '融资机构',
    '融资金额（元）',
    '担保融资额（元）',
    '信用融资额（元）',
    '剩余本金（元）',
    '剩余担保本金（元）',
    '剩余信用本金（元）',
    '贷款日',
    '到期日',
    '融资状态',
    '创建人',
  ])
  const { usedTotalCreditAmountSum, remainingTotalCreditAmountSum } = sumData
  return (
    <div>
      <div className={styles.title}>使用详情</div>
      <Table
        resizable
        columnsFilter="guarantee-use-detail"
        onFilter={(key,val) => saveServer('guarantee-use-detail',val)}
        columnWidth={120}
        columns={columns}
        store={table}
        editable={false}
        actions={[
          <Space>
            <div>已使用额度：{amountFormat(formatPercent(usedTotalCreditAmountSum))}元</div>
            <div> 可用额度： {amountFormat(formatPercent(remainingTotalCreditAmountSum))}元</div>
          </Space>,
        ]}
        extra={[<PageListDown table={table} module="fundGuaranteeLimit" extraParams={{ id }} />]}
        summary={() => {
          return (
            <TableSummary
              title="总合计"
              columns={table.getOptimizedColumns()}
              sumData={sumData}
              initFormat={10000}
            ></TableSummary>
          )
        }}
      />
    </div>
  )
}

export default observer(Index)
