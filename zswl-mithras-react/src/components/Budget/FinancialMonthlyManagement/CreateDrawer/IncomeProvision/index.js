import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from './Column'

const Index = ({ store, operationColumns }) => {
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
      '本月计提收入金额(不含税)(元)',
      '本月收入金额(含税)(元)',
      '租赁类型',
      '税率',
      '当前是否逾期',
      '最近一期全额偿还租金期次',
      '最近一期全额偿还租金应收日期',
      '最近一期全额偿还租金应收剩余本金(元)',
      '合同名义利率',
      '是否确认',
    ],
    true
  )

  return (
    <Table
      columnsFilter="CreateDrawer_IncomeProvision_1"
      onFilter={(key, val) => saveServer('CreateDrawer_IncomeProvision_1', val)}
      extra={[
        {
          name: '导出',
          type: 'primary',
          onClick: () =>
            store.exportExcel({
              moduleType: 'RP',
              ...store.incomeProvisionTable.getParams(),
            }),
        },
      ]}
      rowClassName={(record, rowIndex) => {
        return !record?.isEffect ? 'table-row-remove' : ''
      }}
      columns={[...columns, operationColumns('ASSET_SIDE_PR_ACCOUNT')]}
      store={store.incomeProvisionTable}
      editable={false}
      columnWidth={180}
    />
  )
}

export default observer(Index)
