import { AmountColumn, InputColumn } from '@/components/Format'

const ALL_COLUMNS = () => {
  return [
    {
      title: '最新核算月份',
      dataIndex: 'calculateDate',
    },
    AmountColumn({
      title: '部门池利润(元)',
      dataIndex: 'profitTotal',
    }),
    AmountColumn({
      title: '部门池利润奖金(元)',
      dataIndex: 'bonusTotal',
    }),
    AmountColumn({
      title: '部门池投放奖金(元)',
      dataIndex: 'paymentTotal',
    }),
    AmountColumn({
      title: '部门池合计奖金(元)',
      dataIndex: 'amount',
    }),

    // 详情
    InputColumn({
      title: '考核部门',
      dataIndex: 'deptName',
    }),
    InputColumn({
      title: '主办',
      dataIndex: 'divideTargetName',
    }),
    AmountColumn({
      title: '详情-部门池利润(元)',
      dataIndex: 'profitTotal',
    }),
    AmountColumn({
      title: '详情-部门池投放额(元)',
      dataIndex: 'paymentAwardTotal',
    }),
    AmountColumn({
      title: '详情-部门池利润奖金(元)',
      dataIndex: 'bonusTotal',
    }),
    AmountColumn({
      title: '详情-部门池投放奖金(元)',
      dataIndex: 'paymentTotal',
    }),
    AmountColumn({
      title: '详情-部门池奖金合计(元)',
      dataIndex: 'amount',
    }),
  ]
}

export default ALL_COLUMNS
