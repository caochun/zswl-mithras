import { Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import { useMemo } from 'react'
import Store from '../store'
import { statusRender } from '@/components/BudgetManagement/PlanColumns'

const PlacementPlanTab = () => {
  const store = useMemo(() => new Store(), [])

  const columns = [
    {
      title: '状态',
      dataIndex: 'budgetStatus',
      width: 100,
      render: statusRender,
    },
    {
      title: '计划名称',
      dataIndex: 'budgetPlanName',
      width: 200,
      actions: ({ budgetPlanName, id }) => [
        {
          name: budgetPlanName,
          onClick: () => history.push(`/budgetManagement/placementPlan/detail/${id}`),
        },
      ],
    },
    {
      title: '预算区间',
      render: (_, record) => `${record.budgetDateFrom} ~ ${record.budgetDateTo}`,
      width: 220,
    },
    {
      title: '填报区间',
      render: (_, record) => `${record.writeDateFrom} ~ ${record.writeDateTo}`,
      width: 220,
    },
    {
      title: '预算类型',
      dataIndex: 'budgetType',
      matchOption: 'budgetPlanTypeEnum',
      width: 120,
    },
    {
      title: '收集截止时间',
      dataIndex: 'collectDateTo',
      width: 150,
    },
  ]

  return (
    <div>
      {/* <div style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => store.handleAdd()}>
          新建计划
        </Button>
      </div> */}
      <Table columns={columns} store={store.planList} rowKey="id" />
    </div>
  )
}

export default observer(PlacementPlanTab)
