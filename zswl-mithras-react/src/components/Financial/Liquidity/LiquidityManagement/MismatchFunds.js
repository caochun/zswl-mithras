import { Button, Table } from '@zswl/components'
import { DatePicker } from 'antd'
import { useState } from 'react'
import { observer } from '@zswl/admin'
import styles from './style.less'
import liquidityRiskApi from '@/api/financial/liquidity/liquidityRiskApi'
import { AmountColumn } from '@/components/Format'
import { defaultTimes } from '../index'
import PageListDown from '@/components/PageListDown'
import { Tooltip } from 'antd'
import { uniqueId } from 'lodash'
import { saveServer } from '@/utils'

const MismatchFunds = ({ detail }) => {
  const [time, setTime] = useState(defaultTimes)

  const table = Table.useStore({
    request: async (params) => {
      const requestParams = {
        queryDateStart: time.timeFrom.format('YYYY-MM-DD'),
        queryDateEnd: time.timeTo.format('YYYY-MM-DD'),
        ...params,
      }
      const res = await liquidityRiskApi.postManageMismatch(requestParams)
      return (res ?? []).map(({ cashInFlowList, ...item }) => ({
        ...item,
        id: uniqueId(),
        children: cashInFlowList.map((item) => ({ ...item, id: uniqueId() })),
      }))
    },
  })

  const timeChange = (time) => {
    setTime({
      timeFrom: time[0],
      timeTo: time[1],
    })
    table.search({
      queryDateStart: time[0].format('YYYY-MM-DD'),
      queryDateEnd: time[1].format('YYYY-MM-DD'),
    })
  }
  const columns = [
    {
      title: '融资编号',
      dataIndex: 'financingCode',
      width: 220,
    },
    {
      title: '融资机构',
      dataIndex: 'organizationName',
      render: (text, record) => {
        const title = text?.join('、') ?? '-'
        return <Tooltip title={title}>{title}</Tooltip>
      },
      width: 180,
    },
    {
      title: '期项',
      dataIndex: 'phase',
      width: 120,
    },
    {
      title: '监管/质押合同编号',
      dataIndex: 'pledgeContractCode',
      width: 300,
    },
    {
      title: '现金流出时间',
      dataIndex: 'cashOutflowTime',
      width: 160,
    },
    AmountColumn({
      title: '现金流出金额',
      dataIndex: 'cashOutflowAmount',
      width: 160,
    }),
    {
      title: '现金流入时间',
      dataIndex: 'cashInflowTime',
      width: 160,
    },
    AmountColumn({
      title: '现金流入金额',
      dataIndex: 'cashInflowAmount',
      width: 160,
    }),
  ]

  return (
    <div className={styles['liquidity-panel']}>
      <div className={styles['section-header']}>
        <div className={'z-sub-title'}>资金错配明细</div>
        <div className={styles['update-time']}>
          账户余额更新时间：{detail.accountBalanceUpdateTime}
        </div>
      </div>
      <div className={styles['date-range']}>
        <span className={styles.label}>现金流出区间</span>
        <DatePicker.RangePicker
          value={[time.timeFrom, time.timeTo]}
          onChange={timeChange}
          style={{ marginRight: 12 }}
        />
        <PageListDown
          table={table}
          module={'liquidityMismatch'}
          extraParams={{
            queryDateStart: time.timeFrom.format('YYYY-MM-DD'),
            queryDateEnd: time.timeTo.format('YYYY-MM-DD'),
          }}
        >
          <Button.Download type="primary">导出</Button.Download>
        </PageListDown>
      </div>
      <Table         columnsFilter={'LiquidityManagement_MismatchFunds_1'}
              onFilter={(key, val) => saveServer('LiquidityManagement_MismatchFunds_1', val)} store={table} columns={columns} pagination={false} />
    </div>
  )
}

export default observer(MismatchFunds)
