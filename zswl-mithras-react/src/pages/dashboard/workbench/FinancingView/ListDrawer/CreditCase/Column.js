import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '授信编号',
    dataIndex: 'creditCode',
  }),
  InputColumn({
    title: '融资机构',
    dataIndex: 'orgName',
  }),
  InputColumn({
    title: '授信产品',
    dataIndex: 'financingBizTypeDisplay',
  }),
  AmountColumn({
    title: '授信额度(万元)',
    dataIndex: 'creditTotalAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.creditTotalAmount?.value - b.creditTotalAmount?.value,
    },
  }),
  AmountColumn({
    title: '已用额度(万元)',
    dataIndex: 'creditUsedAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.creditUsedAmount?.value - b.creditUsedAmount?.value,
    },
  }),
  MatchOptionColumn({
    title: '是否循环',
    dataIndex: 'isCycle',
    matchOption: 'yesOrNo',
  }),
  DateColumn({
    title: '授信到期日',
    dataIndex: 'deadline',
  }),
]
