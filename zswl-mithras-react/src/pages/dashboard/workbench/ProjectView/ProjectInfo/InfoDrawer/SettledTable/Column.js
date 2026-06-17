import { founderSelect, clientSelect, orgSelect } from '@/utils/dashboardColumns'
import { MatchOptionColumn, DateColumn, InputColumn, AmountColumn } from '@/components/Format'

export const All_COLUMNS = [
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  AmountColumn({
    title: '投放金额(元)',
    dataIndex: 'creditAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.creditAmount?.value - b.creditAmount?.value,
    },
  }),
  AmountColumn({
    title: '剩余租金金额(元)',
    dataIndex: 'balanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.balanceAmount?.value - b.balanceAmount?.value,
    },
  }),
  InputColumn({
    title: '本期期项',
    dataIndex: 'phase',
  }),
  DateColumn({
    title: '本期应收日期',
    dataIndex: 'planCollectionDate',
    search: true,
  }),
  DateColumn({
    title: '实际还款日期',
    dataIndex: 'actualCollectionDate',
  }),
  MatchOptionColumn({
    title: '是否逾期',
    dataIndex: 'isOverdue',
    matchOption: 'yesOrNo',
  }),
  AmountColumn({
    title: '本期本金(元)',
    dataIndex: 'principalAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.principalAmount?.value - b.principalAmount?.value,
    },
  }),
  AmountColumn({
    title: '本期利息(元)',
    dataIndex: 'interestAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.interestAmount?.value - b.interestAmount?.value,
    },
  }),
  clientSelect(),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '业务模式',
    dataIndex: 'leaseTypeDisplay',
  }),
  InputColumn({
    title: '承租人',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '担保人',
    dataIndex: 'guarantorNames',
  }),
  orgSelect(),
  founderSelect(),
  InputColumn({
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  }),
]
