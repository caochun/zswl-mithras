import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from './Column'

const BudgetFinancialMonthlyIncomeConfirm = ({ store, operationColumns }) => {
  const columns = getTableColumns(
    ALL_COLUMNS,
    [
      '月份',
      '客户名称',
      '项目名称',
      '合同编号',
      {
        title: '借据编号',
        actions: (record) => [
          { name: record.receiptCode, onClick: () => store.receiptModal.open(record) },
        ],
      },
      '实际起租日期',
      '本月收入金额(不含税)(元)',
      '本月收入金额(含税)(元)',
      '租赁类型',
      '税率',
      '当前是否逾期',
      '是否确认',
    ],
    true
  )
  return (
    <Table
      columnsFilter="CreateDrawer_IncomeConfirm_1"
      onFilter={(key, val) => saveServer('CreateDrawer_IncomeConfirm_1', val)}
      extra={[
        {
          name: '导出',
          type: 'primary',
          onClick: () =>
            store.exportExcel({
              moduleType: 'AIR',
              ...store.incomeConfirmTable.getParams(),
            }),
        },
      ]}
      rowClassName={(record, rowIndex) => {
        return !record?.isEffect ? 'table-row-remove' : ''
      }}
      columns={[...columns, operationColumns('ASSET_SIDE_AIR_ACCOUNT')]}
      store={store.incomeConfirmTable}
      editable={false}
      columnWidth={180}
    />
  )
}

export default observer(BudgetFinancialMonthlyIncomeConfirm)
