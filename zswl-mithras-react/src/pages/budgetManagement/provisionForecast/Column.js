import { Input } from 'antd'

/**
 * 设备预测计划表格列配置
 * 定义表格中各列的显示和编辑配置
 */
const ALL_COLUMNS = [
  {
    title: '计划名称',
    dataIndex: 'budgetPlanName',
    requiredMark: true,
    actions: ({ budgetPlanName, id }) => [
      {
        name: budgetPlanName,
        to: `/budgetManagement/provisionForecast/detail/${id}`,
      },
    ],
    editable: <Input />,
  },
  {
    title: '预测时间',
    dataIndex: 'predictDataBegan',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
]

export default ALL_COLUMNS
