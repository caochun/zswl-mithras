import { Table, Form, Button, Page, Input, App, Select } from '@zswl/components'
import styles from './style.less'
import { AmountColumn, DateColumn } from '@/components/Format'
import baseInfoApi from '@/api/liquidity/baseInfoApi'
import { observer } from '@zswl/admin'
import { message, Space, Tag, DatePicker } from 'antd'
import TableSummary from '@/components/Table/Summary'
import { useState } from 'react'
import { PageListDown } from '@/components'
import { ImportAction } from '@/components/Actions'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import { hasValue } from '@/utils'
import math from '@/utils/math'
import { saveServer } from '@/utils'

const titleRender = (text, dateStr) => {
  return (
    <div>
      <div>{text}</div>
      <div className={styles.dateStr}>{moment(dateStr).format('MM月DD日')}</div>
    </div>
  )
}
/**
 * 账户余额明细组件
 * @returns {ReactElement} 账户余额明细表单和表格
 */
const AccountBalanceDetail = observer(() => {
  // 基础列配置
  const baseColumns = [
    {
      title: '开户银行',
      dataIndex: 'accountBank',
      width: 300,
      editable: false,
      fixed: 'left',
      render: (text, record) => (
        <div className={styles.accountBank}>
          <Tag color="#f7f7f7" style={{ color: '#4E5464' }}>
            {App.matchOption('baseDataBankAccountTypeEnum', record.accountType).label}
          </Tag>
          <div className={styles.bankInfo} style={{ color: record.color }}>
            <div className={styles.bankName}>{text}</div>
            <div className={styles.accountNumber}>{record.accountNumber}</div>
          </div>
        </div>
      ),
    },
    AmountColumn({
      title: '租金合计',
      dataIndex: 'rentReflowAmountTotal',
      width: 120,
      fixed: 'left',
      editable: false,
    }),
    AmountColumn({
      title: '还本付息（含调整）合计',
      dataIndex: 'repayEditAmountTotal',
      fixed: 'left',
      width: 200,
      editable: false,
    }),
  ]

  // 动态生成日期列
  const generateDateColumns = (dateList = []) => {
    if (!dateList.length) return []
    const columns = []
    // 生成每一天的列
    dateList.forEach((date) => {
      const dateStr = moment(date).format('YYYY-MM-DD')

      columns.push(
        AmountColumn({
          title: titleRender('提款', dateStr),
          dataIndex: `drawingsAmount_${dateStr}`,
          editable: true,
          width: 120,
        }),
        AmountColumn({
          title: titleRender('租金回流', dateStr),
          dataIndex: `rentReflowAmount_${dateStr}`,
          width: 120,
          editable: false,
        }),
        AmountColumn({
          title: titleRender('其它流入', dateStr),
          dataIndex: `otherFlowAmount_${dateStr}`,
          width: 120,
          editable: true,
        }),
        AmountColumn({
          title: titleRender('投放', dateStr),
          dataIndex: `paymentAmount_${dateStr}`,
          editable: true,
          width: 120,
        }),
        AmountColumn({
          title: titleRender('还本付息', dateStr),
          dataIndex: `repayAmount_${dateStr}`,
          editable: false,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),
        AmountColumn({
          title: titleRender('还本付息(调整)', dateStr),
          dataIndex: `repayEditAmount_${dateStr}`,
          editable: true,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),

        AmountColumn({
          title: titleRender('刚性支出', dateStr),
          dataIndex: `mustExpenseAmount_${dateStr}`,
          editable: true,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),
        AmountColumn({
          title: titleRender('其它支出', dateStr),
          dataIndex: `otherExpenseAmount_${dateStr}`,
          editable: true,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),
        AmountColumn({
          title: titleRender('结余(预估)', dateStr),
          dataIndex: `estimateBalanceAmount_${dateStr}`,
          width: 120,
          editable: false,
        }),
        AmountColumn({
          title: titleRender('结余受限(预估)', dateStr),
          dataIndex: `estimateBalanceLimitAmount_${dateStr}`,
          editable: true,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),
        AmountColumn({
          title: titleRender('结余(实际)', dateStr),
          dataIndex: `actualBalanceAmount_${dateStr}`,
          editable: true,
          width: 120,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        }),
        AmountColumn({
          title: titleRender('差异', dateStr),
          dataIndex: `diffAmount_${dateStr}`,
          width: 120,
          editable: false,
          wrapItemProps: {
            inputConfig: {
              min: -Infinity,
            },
          },
        })
      )
    })
    return columns
  }

  const [sumData, setSumData] = useState({})
  const formatListData = (list = []) => {
    // 对 list 根据 accountId 分组，将同一账户不同日期的数据合并成一行
    const groupedData = list?.reduce((acc, item) => {
      const { accountId, accountBank, accountNumber, accountType, date, color, ...rest } = item

      if (!acc[accountId]) {
        // 获取对象长度，用于排序
        var sort = Object.keys(acc).length
        // 初始化该账户的基础信息
        acc[accountId] = {
          accountId,
          accountBank,
          accountNumber,
          accountType,
          color,
          sort,
        }
      }

      // 将其他字段加上日期后缀作为新的字段名
      Object.keys(rest).forEach((key) => {
        if (key === 'rentReflowAmount') {
          acc[accountId].rentReflowAmountTotal =
            (acc[accountId].rentReflowAmountTotal ?? 0) + rest[key]
        }
        if (['repayEditAmount', 'repayAmount'].includes(key)) {
          acc[accountId].repayEditAmountTotal =
            (acc[accountId].repayEditAmountTotal ?? 0) + rest[key]
        }
        acc[accountId][`${key}_${date}`] = rest[key]
      })

      return acc
    }, {})

    // 转换为数组格式
    return Object.values(groupedData ?? {}).sort((a, b) => a.sort - b.sort)
  }
  const formatSumData = (list = []) => {
    if (!list?.length) return {}
    let allSumData = {}
    let noSupervisionSumData = {}
    let supervisionSumData = {}
    const formatSumObj = (sum, date) => {
      const sumObj = {}
      Object.entries(sum).forEach(([key, value]) => {
        if (key === 'rentReflowAmount') sumObj[`${key}Total`] = (sumObj[`${key}Total`] ?? 0) + value
        if (['repayEditAmount', 'repayAmount'].includes(key)) {
          sumObj.repayEditAmountTotal = (sumObj.repayEditAmountTotal ?? 0) + value
        }
        sumObj[`${key}_${date}`] = value
      })
      return sumObj
    }
    list.forEach(({ date, allSum, noSupervisionSum, supervisionSum }) => {
      allSumData = {
        date,
        ...allSumData,
        ...formatSumObj(allSum, date),
        repayEditAmountTotal:
          (allSumData?.repayEditAmountTotal ?? 0) +
          (allSum?.repayAmount ?? 0) +
          (allSum?.repayEditAmount ?? 0),
        rentReflowAmountTotal: math.add(
          allSumData?.rentReflowAmountTotal,
          allSum?.rentReflowAmount
        ),
      }
      noSupervisionSumData = {
        date,
        ...noSupervisionSumData,
        ...formatSumObj(noSupervisionSum, date),
        repayEditAmountTotal:
          (noSupervisionSumData?.repayEditAmountTotal ?? 0) +
          (noSupervisionSum?.repayAmount ?? 0) +
          (noSupervisionSum?.repayEditAmount ?? 0),
        rentReflowAmountTotal: math.add(
          noSupervisionSumData?.rentReflowAmountTotal,
          noSupervisionSum?.rentReflowAmount
        ),
      }
      supervisionSumData = {
        date,
        ...supervisionSumData,
        ...formatSumObj(supervisionSum, date),
        repayEditAmountTotal:
          (supervisionSumData?.repayEditAmountTotal ?? 0) +
          (supervisionSum?.repayAmount ?? 0) +
          (supervisionSum?.repayEditAmount ?? 0),
        rentReflowAmountTotal: math.add(
          supervisionSumData?.rentReflowAmountTotal,
          supervisionSum?.rentReflowAmount
        ),
      }
    })
    console.log('allSumData: ', allSumData)
    return { allSumData, noSupervisionSumData, supervisionSumData }
  }

  const [columns, setColumns] = useState(baseColumns)
  const [dates, setDates] = useState(null)
  const [editable, setEditable] = useState(false)
  const table = Table.useStore({
    request: async (params) => {
      const { sum, list } = await baseInfoApi.postAccountBalanceList(params)
      const { queryDateEnd, queryDateStart } = params
      const dateList = []
      for (let i = moment(queryDateStart); i <= moment(queryDateEnd); i.add(1, 'day')) {
        dateList.push(i.format('YYYY-MM-DD'))
      }
      const columns = [...baseColumns, ...generateDateColumns(dateList)]
      setColumns(columns)
      setSumData(formatSumData(sum))
      return formatListData(list)
    },
  })
  const disabledDate = (current) => {
    if (!dates) {
      return false
    }
    const tooLate = dates[0] && current.diff(dates[0], 'days') > 15
    const tooEarly = dates[1] && dates[1].diff(current, 'days') > 15
    return !!tooEarly || !!tooLate
  }
  const importAction = async (params) => {
    await baseInfoApi.postAccountBalanceImport(params)
    message.success('导入成功')
    table.search()
  }

  const serializeData = (list = []) => {
    const serializeList = []
    list.forEach((item) => {
      const { date, accountBank, accountNumber, accountType, color, ...rest } = item
      const dateMap = {}
      Object.entries(rest).forEach(([key, value]) => {
        const [field, date] = key.split('_')
        const isAmount = field.indexOf('Amount') > -1
        if (date) {
          const filedValue = isAmount && hasValue(value) ? value * 10000 : value
          dateMap[date] = { ...(dateMap[date] ?? {}), [field]: filedValue }
        }
      })
      Object.entries(dateMap).forEach(([date, value]) => {
        if (date) serializeList.push({ date, ...value })
      })
    })
    return serializeList
  }
  const save = async () => {
    const { list } = await table.submit()
    const serializeList = serializeData(list)

    await baseInfoApi.postAccountBalanceModify({ list: serializeList })
    table.search()
    setEditable(false)
    message.success('保存成功')
  }
  return (
    <Page className={styles.accountBalanceDetail}>
      <Table
        columnsFilter={'liquidity_accountBalanceDetail_idjs'}
        onFilter={(key,val) => saveServer('liquidity_accountBalanceDetail_idjs',val)}
        columns={columns}
        store={table}
        rowKey={'accountId'}
        scroll={{ x: 'max-content', y: 450 }}
        pagination={false}
        searchbar={{
          items: [
            { label: '开户银行', name: 'accountBank', type: 'input' },
            { label: '银行账号', name: 'accountNumber', type: 'input' },
            <Form.Item label="账户性质" name="accountType">
              <Select options={'baseDataBankAccountTypeEnum'} />
            </Form.Item>,
            <Form.Item
              label="起止日期"
              name="queryDate"
              transform={(val) => {
                return {
                  queryDate: undefined,
                  queryDateStart: val[0].format('YYYY-MM-DD'),
                  queryDateEnd: val[1].format('YYYY-MM-DD'),
                }
              }}
            >
              <DatePicker.RangePicker
                disabledDate={disabledDate}
                onCalendarChange={(val) => setDates(val)}
              />
            </Form.Item>,
          ],
          initialValues: { queryDate: [moment().subtract(1, 'day'), moment().subtract(1, 'day')] },
        }}
        actions={[
          <ImportAction upload={importAction} />,
          <DownloadTemplate
            params={{
              templateName: 'LIQUIDITY_ACCOUNT_BALANCE',
              moduleType: 'FUND_FINANCING',
            }}
          />,
          <PageListDown table={table} module="accountBalance">
            <Button.Download type="primary">导出</Button.Download>
          </PageListDown>,
          !editable && (
            <Button.Edit
              type="primary"
              onClick={() => {
                setEditable(true)
              }}
            >
              编辑
            </Button.Edit>
          ),
          editable && (
            <Button.Save type="primary" onClick={save}>
              保存
            </Button.Save>
          ),
          editable && (
            <Button.Withdraw
              onClick={() => {
                setEditable(false)
              }}
            >
              取消
            </Button.Withdraw>
          ),
        ]}
        editable={editable}
        summary={(pageData) => {
          // 添加合计行
          return (
            <>
              <TableSummary
                title="总合计"
                columns={table.getOptimizedColumns()}
                sumData={sumData.allSumData}
                initFormat={10000}
              />
              <TableSummary
                columns={table.getOptimizedColumns()}
                sumData={sumData.supervisionSumData}
                title="监管户"
                initFormat={10000}
              />
              <TableSummary
                columns={table.getOptimizedColumns()}
                sumData={sumData.noSupervisionSumData}
                title="非监管户"
                initFormat={10000}
              />
            </>
          )
        }}
      />
    </Page>
  )
})

export default AccountBalanceDetail
