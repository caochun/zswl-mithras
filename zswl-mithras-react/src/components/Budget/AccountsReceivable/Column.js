import moment from 'moment'

const ALL_COLUMNS = [
  {
    title: '计划月份',
    dataIndex: 'recordBillDate',
    width: 120,
    actions: ({ planDate: name, id }) => {
      return [
        { name: moment(name).format('YYYY-MM'), to: `/budget/accountsReceivable/detail/${id}` },
      ]
    },
  },
  {
    title: '报送计划状态',
    dataIndex: 'reportStatus',
    matchOption: 'overduePlanStatueEnum',
    width: 140,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
]

export default ALL_COLUMNS
