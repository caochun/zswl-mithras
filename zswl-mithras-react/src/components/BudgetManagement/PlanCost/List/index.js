import { observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import { default as ALL_COLUMNS } from '../../PlanColumns'
import { getTableColumns } from '@/utils'

const nameColumns = [
  '状态',
  {
    title: '计划名称',
    actions: ({ budgetPlanName: name, id }) => [
      {
        name,
        to: `/budgetManagement/plan/cost/detail/${id}`,
      },
    ],
  },
  '预算区间',
  '填报区间',
  '预算类型',
  '收集截止时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)

const ProfitBudgetDetail = () => {
  const store = useMemo(() => new Store(), [])

  return (
    <Page>
      <Table columns={columns} store={store.list} editable={false} columnWidth={120} />
    </Page>
  )
}

export default observer(ProfitBudgetDetail)
