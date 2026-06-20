import { Table, Form, DatePicker, Button, Block } from '@zswl/components'
import styles from './styles.less'
import { AmountColumn, DateColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Summary as TableSummary } from '@/components/Table'
import { useState } from 'react'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'
import fundTransferApi from '@/api/financial/fundTransfer'
import { saveServer } from '@/utils'

const titleRender = (text, dateStr) => {
  return (
    <div>
      <div>{moment(dateStr).format('MM月DD日')}</div>
      <div className={styles.dateStr}>{text}</div>
    </div>
  )
}
/**
 * 账户余额明细组件
 * @returns {ReactElement} 账户余额明细表单和表格
 */
const AccountBalanceDetail = ({ store }) => {
  const commonQueryParams = store.getCommonQueryParams() ?? {}

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
          <div className={styles.bankInfo} style={{ color: 'blue' }}>
            <div className={styles.bankName}>{text}</div>
            <div className={styles.accountNumber} onClick={() => store.bankInfoModal.open(record)}>
              {record.accountNumber}
            </div>
          </div>
        </div>
      ),
    },
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
          title: titleRender('待转余额', dateStr),
          dataIndex: `pendingBalanceAmount_${dateStr}`,
          editable: false,
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
        // 初始化该账户的基础信息
        acc[accountId] = {
          accountId,
          accountBank,
          accountNumber,
        }
      }

      // 将其他字段加上日期后缀作为新的字段名
      Object.keys(rest).forEach((key) => {
        acc[accountId][`${key}_${date}`] = rest[key]
      })

      return acc
    }, {})

    // 转换为数组格式
    return Object.values(groupedData ?? {})
  }
  const formatSumData = (list = []) => {
    if (!list?.length) return {}
    let allSumData = {}
    const formatSumObj = (sum, date) => {
      const sumObj = {}
      Object.entries(sum).forEach(([key, value]) => {
        sumObj[`${key}_${date}`] = value
      })
      return sumObj
    }
    list.forEach(({ date, allSum }) => {
      allSumData = { date, ...allSumData, ...formatSumObj(allSum, date) }
    })
    return { allSumData }
  }

  const [columns, setColumns] = useState(baseColumns)
  const table = Table.useStore(
    {
      request: async () => {
        const { queryDateStart, queryDateEnd } = commonQueryParams
        const { sum, list } = await fundTransferApi.postAccountList({
          ...commonQueryParams,
        })
        const dateList = []
        for (let i = moment(queryDateStart); i <= moment(queryDateEnd); i.add(1, 'day')) {
          dateList.push(i.format('YYYY-MM-DD'))
        }
        // const dateList = list.map((item) => item.date)
        const columns = [...baseColumns, ...generateDateColumns(dateList)]
        setColumns(columns)
        setSumData(formatSumData(sum))
        return formatListData(list)
      },
    },
    [JSON.stringify(commonQueryParams)]
  )

  return (
    <Block>
      <Table
        columnsFilter={'Component_BankInfoTable_1'}
        onFilter={(key, val) => saveServer('Component_BankInfoTable_1', val)}
        columns={columns}
        store={table}
        rowKey={'accountId'}
        scroll={{ x: 'max-content' }}
        pagination={false}
        actions={[
          <PageListDown
            table={table}
            module="fundTransfer"
            extraParams={{
              ...store.getCommonQueryParams(),
            }}
          >
            <Button.Download type="primary">导出</Button.Download>
          </PageListDown>,
        ]}
        editable={false}
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
            </>
          )
        }}
      />
    </Block>
  )
}

export default observer(AccountBalanceDetail)
