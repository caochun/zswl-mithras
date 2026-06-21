import { Table } from '@zswl/components'
import { observer, history } from '@zswl/admin'
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

const KpiProjectAllotDealList = ({ store, pathname }) => {
  const columns = getTableColumns(ALL_COLUMNS({ source: 'deal', pathname }), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)
  // store.dealTable.setParams(store.commonParams)

  return (
    <Table
      columnsFilter={'Component_Deal_1'}
      onFilter={(key, val) => saveServer('Component_Deal_1', val)}
      selectable={{
        type: 'radio',
        getCheckboxProps: (record) => {
          return {
            disabled: record.approvalStatus === 'UNDER_APPROVAL',
          }
        },
      }}
      store={store.dealTable}
      columnWidth={180}
      editable={false}
      searchbar={{
        items: formColumns,
      }}
      scroll={{
        x: 1500,
      }}
      columns={[
        ...columns,
        {
          title: '生效月份',
          dataIndex: 'effectMonth',
        },
        {
          title: '操作',
          width: 150,
          actions: (record) => {
            return [
              {
                name: '查看历史',
                onClick: () => history.push(`${pathname}/history/${record.id}`),
              },
            ]
          },
        },
      ]}
      actions={[
        {
          name: '手工调整',
          type: 'primary',
          onClick: () => store.adjust(pathname),
        },
        {
          name: '导出',
          type: 'primary',
          onClick: () => store.export('deal'),
        },
      ]}
    />
  )
}
export default observer(KpiProjectAllotDealList)
