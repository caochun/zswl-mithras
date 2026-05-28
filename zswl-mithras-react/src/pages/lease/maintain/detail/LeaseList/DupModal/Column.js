import { history } from '@zswl/admin'

const ALL_COLUMNS = (cb) => [
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 310
  },
  {
    title: '审批状态',
    dataIndex: 'approvalStatus',
    width: 120,
  },
  {
    title: '流程ID',
    dataIndex: 'flowId',
    width: 120,
    render: (value, { id }) => <a onClick={()=>{
      history.push(`/lease/maintain/detail/${id}?type=manage`)
      cb&&cb()
    }}>{value}</a>,
  }
]
export default ALL_COLUMNS
