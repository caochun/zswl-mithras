import { Input } from 'antd'

const ALL_COLUMNS = [
  {
    title: '考核名称',
    dataIndex: 'examineName',
    requiredMark: true,
    actions: ({ examineName, id }) => [
      {
        name: examineName,
        to: `/budgetManagement/assessment/detail/${id}`,
      },
    ],
    editable: <Input />,
    // render: (val) => <FiledFormat title={val}></FiledFormat>,
  },
  { title: '考核年份', dataIndex: 'examineYear' },
  { title: '考核月份', dataIndex: 'examineMonth' },
  { title: '审批状态', dataIndex: 'approvalStatus', matchOption: 'fundReceiptRepayProcessState' },
  { title: '提交人', dataIndex: 'submitUserName' },
  { title: '提交时间', dataIndex: 'submitTime' },
]

export default ALL_COLUMNS
