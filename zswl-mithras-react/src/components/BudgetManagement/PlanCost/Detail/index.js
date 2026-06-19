import { Page, Table } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import { observer } from '@zswl/admin'
import { TableExportAction as TableExport } from '@/components/Actions'

const CostBudgetDetail = ({ params }) => {
  const store = useMemo(() => new Store({ id: params.id }), [params.id])

  return (
    <Page
      extra={[<TableExport table={store.list} otherExcelProps={{ fileName: '成本预算明细' }} />]}
    >
      <Table
        store={store.list}
        columns={store.columns}
        columnWidth={140}
        resizable
        actions={[<div>单位：万元</div>]}
      />
    </Page>
  )
}

export default observer(CostBudgetDetail)
