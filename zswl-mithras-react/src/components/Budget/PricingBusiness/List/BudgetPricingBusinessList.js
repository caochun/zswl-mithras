import { Button, Page, Table } from '@zswl/components'
import { useMemo } from 'react'
import { getTableColumns, isFinancialOfficer } from '@/utils'
import ALL_COLUMNS from './Column'
import Store from './store'
import EditModal from './EditModal'
import moment from 'moment'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '时间',
    actions: ({ month, pricingFrequency, id }) => [
      {
        name:
          pricingFrequency === 'QUARTER'
            ? moment(month).format('yyyy年Q季度')
            : moment(month).format('yyyy年MM月'),
        to: `/budget/pricing/business/detail/${id}?month=${moment(month).format('yyyy-MM')}`,
      },
    ],
  },
  '创建人',
  '状态',
  '审批状态',
  '创建时间',
  '生效时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)
const BudgetPricingBusinessList = ({ pathname }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  return (
    <Page>
      <Table
        columnsFilter="pricing_business_1"
        onFilter={(key, val) => saveServer('pricing_business_1', val)}
        store={store.table}
        editable={false}
        scroll={{
          x: 1200,
        }}
        actions={[<Button.Add onClick={store.createModal.open} disabled={!isFinancialOfficer()} />]}
        columns={columns}
      />
      <EditModal store={store.createModal} />
    </Page>
  )
}
export default BudgetPricingBusinessList
