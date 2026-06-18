import { observer } from '@zswl/admin'
import { AmountColumn } from '@/components/Format'
import { Button, DatePicker, Table } from '@zswl/components'
import styles from './style.less'
import liquidityRiskApi from '@/api/financial/liquidity/liquidityRiskApi'
import { useEffect, useState } from 'react'
import moment from 'moment'
import { debounce } from 'lodash'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'

const defaultTimes = {
  // 7 天前
  expireDateFrom: moment(),
  expireDateTo: moment().add(7, 'day'),
}
const FORMAT_NUMBER = 10000 * 10000
const Index = ({ queryTime }) => {
  const [time, setTime] = useState(defaultTimes)
  const [repayTime, setRepayTime] = useState(defaultTimes)
  const incomeTable = Table.useStore({
    request: async (params) => {
      return await liquidityRiskApi.postRentIncome(params)
    },
  })
  const repaymentPrincipalTable = Table.useStore({
    request: async (params) => {
      return await liquidityRiskApi.postManageRepay(params)
    },
  })

  const dateChange = (value) => {
    setTime({ expireDateFrom: value[0], expireDateTo: value[1] })
    const newQuery = getNewQuery({
      expireDateFrom: value[0],
      expireDateTo: value[1],
    })
    incomeTable.search(newQuery)
  }
  const repayDateChange = (value) => {
    setRepayTime({ expireDateFrom: value[0], expireDateTo: value[1] })
    const newQuery = getNewQuery({
      expireDateFrom: value[0],
      expireDateTo: value[1],
    })
    repaymentPrincipalTable.search(newQuery)
  }
  const columns = [
    { title: '承租人名称', dataIndex: 'tenantName', width: 250 },
    { title: '合同编号', dataIndex: 'contractCode', width: 250 },
    { title: '本期到期日', dataIndex: 'expireDate' },
    AmountColumn({
      title: '本期应还金额（万元）',
      dataIndex: 'shouldPayAmount',
      initFormat: FORMAT_NUMBER,
    }),
    { title: '流入账户', dataIndex: 'incomeAccount' },
    { title: '流入账户性质', dataIndex: 'incomeAccountProperty' },
  ]

  const repayColumns = [
    { title: '融资机构', dataIndex: 'organizationName', render: (value) => value?.join('、') },
    { title: '融资编号', dataIndex: 'financingCode' },
    { title: '本期到期日', dataIndex: 'expireDate' },
    AmountColumn({
      title: '本期应还金额(万元）',
      dataIndex: 'shouldPayAmount',
      initFormat: FORMAT_NUMBER,
    }),
    AmountColumn({
      title: '本期应还本金(万元）',
      dataIndex: 'shouldPayPrincipal',
      initFormat: FORMAT_NUMBER,
    }),
    AmountColumn({
      title: '本期应还利息(万元）',
      dataIndex: 'shouldPayInterest',
      initFormat: FORMAT_NUMBER,
    }),
    { title: '本金流出账户', dataIndex: 'principalOutflowAccount' },
    { title: '本金流出账户性质', dataIndex: 'principalOutflowAccountProperty' },
    { title: '利息流出账户', dataIndex: 'interestOutflowAccount' },
    { title: '利息流出账户性质', dataIndex: 'interestOutflowAccountProperty' },
  ]
  const getNewQuery = (newTime, newParams = {}) => {
    return {
      queryDateStart: queryTime?.timeFrom.format('yyyy-MM-DD'),
      queryDateEnd: queryTime?.timeTo.format('yyyy-MM-DD'),
      expireDateFrom: newTime.expireDateFrom?.format('yyyy-MM-DD'),
      expireDateTo: newTime.expireDateTo?.format('yyyy-MM-DD'),
      ...newParams,
    }
  }

  useEffect(() => {
    incomeTable.search(getNewQuery(time))
    repaymentPrincipalTable.search(getNewQuery(repayTime))
  }, [queryTime])
  return (
    <div className={styles['table-container']}>
      <div className={styles['date-range']}>
        <span className={styles.label}>租金流入</span>
        <DatePicker.RangePicker
          value={[time.expireDateFrom, time.expireDateTo]}
          onChange={debounce(dateChange, 500)}
          // 在queryTime时间范围内
          disabledDate={(current) =>
            current.isAfter(queryTime?.timeTo, 'day') ||
            current.isBefore(queryTime?.timeFrom, 'day')
          }
        />
        <PageListDown
          table={incomeTable}
          module={'liquidityRepayIncome'}
          extraParams={getNewQuery(time)}
        >
          <Button.Download type="primary">导出</Button.Download>
        </PageListDown>
      </div>
      <Table
        columns={columns}
        store={incomeTable}
        scroll={{ x: 'auto' }}
        style={{ marginTop: 12 }}
        autoRequest={false}
        bordered
        className={styles.table}
      />
      <div className={styles['date-range']}>
        <span className={styles.label}>还本付息</span>
        <DatePicker.RangePicker
          value={[repayTime.expireDateFrom, repayTime.expireDateTo]}
          onChange={debounce(repayDateChange, 500)}
          disabledDate={(current) =>
            current.isAfter(queryTime?.timeTo, 'day') ||
            current.isBefore(queryTime?.timeFrom, 'day')
          }
        />
        <PageListDown
          table={repaymentPrincipalTable}
          module={'liquidityRepay'}
          extraParams={getNewQuery(repayTime)}
        >
          <Button.Download type="primary">导出</Button.Download>
        </PageListDown>
      </div>
      <Table
        columns={repayColumns}
        store={repaymentPrincipalTable}
        scroll={{ x: 'auto' }}
        autoRequest={false}
        style={{ marginTop: 12 }}
        bordered
        className={styles.table}
      />
    </div>
  )
}

export default observer(Index)
