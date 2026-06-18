import { Table, Button } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import { useMemo } from 'react'
import Store from '../store'
import { statusRender } from '@/components/BudgetManagement/PlanColumns'

const ProjectReportTab = () => {
  const store = useMemo(() => new Store(), [])

  const columns = [
    {
      title: '状态',
      dataIndex: 'planStatus',
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
          onClick: () => history.push(`/budgetManagement/placementPlan/weekDetail/${id}`),
        },
      ],
    },
    {
      title: '周报区间',
      width: 200,
      render: (_, record) => <span>{`${record.dateFrom} ~ ${record.dateTo}`}</span>,
    },
  ]

  return (
    <div>
      {/* <div style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => store.handleAddReport()}>
          新建周报
        </Button>
      </div> */}
      <Table columns={columns} store={store.reportList} rowKey="id" />
    </div>
  )
}

export default observer(ProjectReportTab)
