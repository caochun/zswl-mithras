import { DatePicker, Input, InputNumber, Space, Tooltip } from 'antd'
import { observer } from '@zswl/admin'
import { useState, useEffect } from 'react'
import styles from './style.less'
import { Button, Table } from '@zswl/components'
import { hasValue } from '@/utils'
import { AmountColumn, PureAmountFormat } from '@/components/Format'
import { InfoCircleOutlined } from '@ant-design/icons'
import { Summary as TableSummary } from '@/components/Table'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'

const divorcer = 10000 * 10000
const LiquidityTable = ({ dataSource = {}, params, setPredictDay, time }) => {
  const { list = [], sum = {} } = dataSource

  const [sumData, setSumData] = useState({})

  const table = Table.useStore({
    request: async () => {},
  })

  const formatData = () => {
    if (!list?.length) return []

    // 定义需要展示的指标及其对应的属性名

    const indicators = [
      {
        key: 'initialBalance',
        name: '期初余额',
        initFormat: 10000,
        tooltip: '上日末资金余额',
        level: 1,
      },
      {
        key: 'expectedRentRecovery',
        name: '预计回收租金',
        initFormat: 10000,
        level: 1,
        tooltip: '测算每日租金回流金额',
      },
      {
        key: 'cashFlowExpenditure',
        name: '现金流支出',
        initFormat: 10000,
        level: 1,
        tooltip: '测算每日支出合计',
      },
      {
        key: 'debtRepayment',
        name: '（1）债务偿还',
        initFormat: 10000,
        tooltip: '测算每日还款金额',
        level: 2,
      },
      {
        key: 'nonAbsRepayment',
        name: '（1.1）非ABS还款',
        initFormat: 10000,
        tooltip: '测算每日非ABS还款金额',
        level: 3,
      },
      {
        key: 'absRepayment',
        name: '（1.2）ABS还款',
        initFormat: 10000,
        level: 3,
        tooltip: '测算每日ABS还款金额，流动性紧张时，考虑是否可通过延迟转付来缓解',
      },
      {
        key: 'rigidExpenditure',
        name: '（2）刚性支出',
        initFormat: 10000,
        level: 2,
        tooltip: '人力费用+财务费用',
      },
      {
        key: 'endingBalance',
        name: '期末余额',
        initFormat: 10000,
        tooltip: '预计当日资金余额',
        level: 1,
      },
      {
        key: 'supervisedAccountFunds',
        name: '（1）监管户资金',
        initFormat: 10000,
        tooltip: '在考虑租金回收压力的前提下，预计当日监管户余额',
        level: 2,
      },
      {
        key: 'negativeSupervisedAccountFunds',
        name: '监管户资金的负值',
        initFormat: 10000,
        tooltip: '监管户之间无法互转，一旦出现负数，需自有资金补足',
        level: 3,
      },
      {
        key: 'nonSupervisedAccountFunds',
        name: '（2）非监管户资金',
        initFormat: 10000,
        tooltip: '在考虑租金回收压力的前提下，预计当日一般户余额',
        level: 2,
      },
      // {
      //   key: 'dailyLiquidityGap',
      //   name: '流动性缺口（当天）',
      //   initFormat: 10000,
      //   tooltip: '租金回流-支出合计',
      //   level: 1,
      // },
      // {
      //   key: 'thirtyDayLiquidityCoverageRatio',
      //   name: '流动性覆盖率（30天）',
      //   initFormat: 1 / 100,
      //   tooltip: '（不受限银行存款+租金回流）/（还本付息+还本付息（调整值）+刚性支出）',
      //   level: 1,
      // },
      {
        key: 'periodActualOrPlannedExpenditure',
        name: '期间实际或计划投放等支出金额',
        initFormat: 10000,
        level: 1,
      },
      {
        key: 'periodActualOrPlannedFinancingReceipts',
        name: '期间实际或计划融资等收款金额',
        initFormat: 10000,
        level: 1,
      },
      // {
      //   key: 'dailyMaxAvailableBalance',
      //   name: '当日最大可用余额',
      //   initFormat: 10000,
      // },
    ]

    const newSumData = {}
    list.forEach((item) => {
      newSumData[item.date] = item?.dailyMaxAvailableBalance?.value
    })
    setSumData(newSumData)

    // 生成每一行的数据
    const newData = indicators.map(({ key, name, tooltip, initFormat, level }) => {
      const rowData = { name, tooltip, level }
      list.forEach((item) => {
        rowData[item.date] = item[key]?.value / initFormat
        rowData.total = sum[key]?.value / initFormat
      })
      return rowData
    })
    return newData
  }

  const generateColumns = () => {
    const baseColumns = [
      {
        title: '指标',
        dataIndex: 'name',
        width: 250,
        fixed: 'left',
        render: (value, { tooltip, level }, index) => {
          return (
            <div
              className={styles['indicator-name']}
              style={{
                fontWeight: 600,
                paddingLeft: (level - 1) * 20,
              }}
            >
              <div className={styles['indicator-name-text']}>{value}</div>
              {tooltip && (
                <Tooltip title={tooltip}>
                  <InfoCircleOutlined className={styles['info-icon']} />
                </Tooltip>
              )}
            </div>
          )
        },
      },
      AmountColumn({
        title: '合计值',
        dataIndex: 'total',
        width: 250,
        initFormat: 1,
      }),
    ]

    const dateColumns = list?.map((item) =>
      AmountColumn({
        title: item.date,
        dataIndex: item.date,
        width: 180,
        initFormat: 1,
      })
    )

    return [...baseColumns, ...dateColumns]
  }
  useEffect(() => {
    table.setList(formatData())
  }, [list])
  return (
    <div className={styles['table-container']}>
      <Space>
        <DatePicker.RangePicker value={[time.timeFrom, time.timeTo]} disabled />
        <PageListDown table={table} module={'liquidityBoard'} extraParams={params}>
          <Button.Download type="primary">导出</Button.Download>
        </PageListDown>
      </Space>
      <Table
        columnsFilter={'LiquidityManagement_LiquidityTable_1'}
        onFilter={(key, val) => saveServer('LiquidityManagement_LiquidityTable_1', val)}
        columns={generateColumns()}
        store={table}
        pagination={false}
        scroll={{ x: 1000 }}
        style={{ marginTop: 12 }}
        bordered
        className={styles.table}
        rowClassName={(record, index) => {
          return index % 2 === 0 ? styles['even-row'] : styles['odd-row']
        }}
        summary={() => {
          return (
            <>
              <TableSummary
                columns={table.getOptimizedColumns()}
                sumData={sumData}
                initFormat={10000}
                title={
                  <Space className={styles['current-available-max-amount']}>
                    当前可用最大金额
                    <InputNumber
                      addonAfter="天内"
                      size="small"
                      min={1}
                      style={{ width: 90 }}
                      value={params.predictDay}
                      onChange={(value) => setPredictDay(value)}
                    />
                  </Space>
                }
              />
            </>
          )
        }}
      />
    </div>
  )
}

export default observer(LiquidityTable)
