import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { FinancialPaymentColumns as ALL_COLUMNS } from '@/components/Financial/PaymentEntries'
import { useMemo, useState } from 'react'
import { getTableColumns, getFormColumns, getSearchColumns } from '@/utils'
import AmountRange from '@/components/AmountRange'
import { CreditOrgSelect, PageListDown } from '@/components'
import { Summary as TableSummary } from '@/components/Table'
import { saveServer } from '@/utils'
import moment from 'moment'

const formNameColumns = [
  {
    title: '融资金额（元）',
    search: {
      element: <AmountRange />,
      itemProps: {
        transform: (val) => {
          const [start, end] = val || []
          return {
            financingAmount: undefined,
            financingAmountFrom: start && start * 10000,
            financingAmountTo: end && end * 10000,
          }
        },
      },
    },
  },
  // { title: '本息收付款状态', rename: '本息核销状态' },
  '融资编号',
  '创建时间',
  '还款月份',
  '是否筛选本月应还金额大于0的数据',
]
const formColumns = getSearchColumns(ALL_COLUMNS, formNameColumns)
function Index({ path }) {
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '融资编号',
        fixed: 'left',
        width: 200,
        actions: ({ financingCode: name, id }) => [{ name, to: `${path}/detail/${id}` }],
      },
      '融资机构',
      '融资金额（元）',
      '本月应还金额（元）',
      '本月应还本金（元）',
      '本月应还利息（元）',
      // { title: '本息收付款状态', rename: '本息核销状态' },
      '审批状态',
      '创建人',
      '创建时间',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [path])
  const canBatch = store.table.selectedRowKeys.length > 0
  const { sumData } = store
  console.log(' columns formColumns', columns, formColumns)
  return (
    <Page>
      <Table
        columnsFilter={'financial_payment_1'}
        onFilter={(key, val) => saveServer('financial_payment_1', val)}
        store={store.table}
        editable={false}
        selectable
        searchbar={{
          items: formColumns,
          initialValues: {
            repayMonth: moment(new Date()),
            filterAmount: 1,
          },
        }}
        extra={[<PageListDown key="1" module="payment" table={store.table} />]}
        actions={[
          <Button.Add onClick={() => store.batchApproval('BATCH')} disabled={!canBatch} key="batch">
            批量还款
          </Button.Add>,
        ]}
        scroll={{ x: 1200 }}
        columnWidth={180}
        columns={columns}
        summary={() => {
          return (
            <TableSummary
              title="总合计"
              columns={store.table.getOptimizedColumns()}
              sumData={sumData}
              initFormat={10000}
              startIndex={0}
            ></TableSummary>
          )
        }}
      />
    </Page>
  )
}

export default observer(Index)
