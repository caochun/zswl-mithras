import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { Table, Page } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import ChooseMonthModal from './ChooseMonthModal'
import CreateDrawer from './CreateDrawer'
import Store from './Store'
import { AmountColumn, DateColumn, MatchOptionColumn } from '@/components/Format'

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const ALL_COLUMNS = [
    {
      title: '月份',
      dataIndex: 'yearAndMonth',
      actions: (record) => {
        return [
          {
            name: record.yearAndMonth,
            onClick: () => store.openDetail(record),
          },
        ]
      },
    },
    MatchOptionColumn({
      title: '状态',
      dataIndex: 'status',
      matchOption: 'monthlyManagementStatusEnum',
    }),
    AmountColumn({ title: '当期计提收入-实际利率法(含税)', width: 230, dataIndex: 'airCount' }),
    AmountColumn({
      title: '当期计提收入-实际利率法(不含税)',
      width: 230,
      dataIndex: 'airCountExcludeTax',
    }),
    AmountColumn({ title: '当期计提收入-剩余本金法(含税)', width: 230, dataIndex: 'rpCount' }),
    AmountColumn({
      title: '当期计提收入-剩余本金法(不含税)',
      width: 230,
      dataIndex: 'rpCountExcludeTax',
    }),
    AmountColumn({ title: '当期计提成本(含税)', dataIndex: 'costCount' }),
    AmountColumn({ title: '当期计提成本(不含税)', dataIndex: 'costCountExcludeTax' }),
    AmountColumn({ title: '当期计提印花税', dataIndex: 'stampDutyCount' }),
    DateColumn({ title: '确认日期', dataIndex: 'confirmDate' }),
    DateColumn({ title: '关账日期', dataIndex: 'closeDate' }),
  ]
  const columns = getTableColumns(ALL_COLUMNS)
  const { rows } = store.monthlyTable.getSelected()
  const canClose = rows.length === 1 && rows.every((item) => item.status === 'CONFIRMED')
  return (
    <Page>
      <Table
        columnsFilter="budget_financialMonthlyManagement_1"
        onFilter={(key, val) => saveServer('budget_financialMonthlyManagement_1', val)}
        title={() => <div style={{ textAlign: 'right' }}>单位：元</div>}
        actions={[
          {
            name: '创建月结',
            type: 'primary',
            onClick: () => store.chooseMonthModal.open({}),
          },
          {
            name: '关账',
            type: 'primary',
            disabled: !canClose,
            onClick: () => store.close(),
          },
        ]}
        columns={columns}
        store={store.monthlyTable}
        editable={false}
        selectable
        columnWidth={180}
      />
      <ChooseMonthModal store={store} />
      <CreateDrawer store={store} />
    </Page>
  )
}

export default observer(Index)
