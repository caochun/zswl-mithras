import { history, observer } from '@zswl/admin'
import { Button, Page, Table } from '@zswl/components'
import Store from './store'
import { default as ALL_COLUMNS } from '../../PlanColumns'
import { getTableColumns } from '@/utils'
import { useMemo } from 'react'
import EditModal from '../EditModal'

const nameColumns = [
  '状态',
  {
    title: '计划名称',
    actions: ({ budgetPlanName: name, id }) => [
      {
        name,
        onClick: () => history.push(`/budgetManagement/plan/profit/detail/${id}`),
      },
    ],
  },
  '预算区间',
  '填报区间',
  '预算类型',
  '是否收集',
  '收集截止时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)

const ProfitBudget = () => {
  const store = useMemo(() => new Store(), [])

  return (
    <Page>
      <Table
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'action',
            width: 200,
            fixed: 'right',
            actions: (record) => [
              {
                name: '调整',
                onClick: (record) => {
                  const canEdit =
                    record.budgetType === 'MONTH' &&
                    record.isAdjust === 0 &&
                    record.budgetStatus === 'CONFIRM'
                  if (!canEdit) return false
                  return store.editModal.open(record)
                },
                disabled: !(
                  record.budgetType === 'MONTH' &&
                  record.isAdjust === 0 &&
                  record.budgetStatus === 'CONFIRM'
                ),
              },
              { name: '删除', onClick: (record) => store.itemDelete(record) },
            ],
          },
        ]}
        store={store.list}
        editable={false}
        columnWidth={120}
        actions={[
          <Button.Add onClick={() => store.editModal.open()} key="add">
            创建预算
          </Button.Add>,
        ]}
      />
      <EditModal store={store.editModal} />
    </Page>
  )
}

export default observer(ProfitBudget)
