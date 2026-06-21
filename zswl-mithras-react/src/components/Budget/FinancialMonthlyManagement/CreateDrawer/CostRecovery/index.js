import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from './Column'

const BudgetFinancialMonthlyCostRecovery = ({ store, operationColumns }) => {
  const columns = getTableColumns(ALL_COLUMNS, ALL_COLUMNS, true)
  return (
    <Table
      columnsFilter={'CreateDrawer_CostRecovery_1'}
      onFilter={(key, val) => saveServer('CreateDrawer_CostRecovery_1', val)}
      columns={[...columns, operationColumns('ASSET_SIDE_COST_DK')]}
      store={store.costRecoveryTable}
      editable={false}
      columnWidth={180}
      rowClassName={(record, rowIndex) => {
        return !record?.isEffect ? 'table-row-remove' : ''
      }}
      extra={[
        {
          name: '导出',
          type: 'primary',
          onClick: () =>
            store.exportExcel({
              moduleType: 'COST',
              ...store.costRecoveryTable.getParams(),
            }),
        },
      ]}
    />
  )
}

export default observer(BudgetFinancialMonthlyCostRecovery)
