import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '../../Column'
import { saveServer } from '@/utils'

const formNameColumns = [
  '合同编号',
  '项目名称',
  '项目主办',
  {
    title: '业务部门',
    dataIndex: 'bizDeptId',
  },
  '审批状态',
  '项目主办（人员分润比）',
  '项目协办（人员分润比）',
  '业务部门（人员分润比）',
]
const nameColumns = [
  '合同编号',
  '审批状态',
  '项目名称',
  {
    title: '合同起始时间',
    rename: '投放日期',
  },
  '业务部门',
  '项目主办',
  '分润比',
]

const KpiProjectAllotUnDealList = ({ store, pathname }) => {
  const columns = getTableColumns(ALL_COLUMNS({ source: 'unDeal', pathname }), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)
  // store.unDealTable.setParams(store.commonParams)

  return (
    <Table
      columnsFilter={'Component_UnDeal_1'}
      onFilter={(key, val) => saveServer('Component_UnDeal_1', val)}
      store={store.unDealTable}
      columnWidth={160}
      editable={false}
      searchbar={{
        items: formColumns,
      }}
      scroll={{
        x: 1500,
      }}
      columns={columns}
      actions={[
        {
          name: '导出',
          type: 'primary',
          onClick: () => store.export('unDeal'),
        },
      ]}
    />
  )
}
export default observer(KpiProjectAllotUnDealList)
