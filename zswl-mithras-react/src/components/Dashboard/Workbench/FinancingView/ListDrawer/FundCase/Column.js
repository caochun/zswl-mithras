import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  MatchOptionColumn({
    title: '融资类别',
    dataIndex: 'financingTypeCode',
    matchOption: 'financingTypeEnum',
  }),
  InputColumn({
    title: '融资机构/产品名称',
    dataIndex: 'orgName',
  }),
  InputColumn({
    title: '融资编号',
    dataIndex: 'financingCode',
  }),
  // AmountColumn({
  //   title: '融资余额(元)',
  //   initFormat: 1,
  //   dataIndex: 'balanceAmount',
  //   sorter: {
  //     compare: (a, b) => a.balanceAmount - b.balanceAmount,
  //   },
  // }),
  // AmountColumn({
  //   title: '融资金额(元)',
  //   initFormat: 1,
  //   dataIndex: 'loanAmount',
  //   sorter: {
  //     compare: (a, b) => a.loanAmount - b.loanAmount,
  //   },
  // }),
  AmountColumn({
    title: '利率',
    dataIndex: 'interestRate',
    initFormat: 1,
    suffix: '%',
  }),
  AmountColumn({
    title: '期限(月)',
    dataIndex: 'duration',
    initFormat: 1,
  }),
  InputColumn({
    title: '还款方式',
    dataIndex: 'repayWay',
  }),
  InputColumn({
    title: '质押资产合同编号',
    dataIndex: 'relatedContractCodeList',
  }),
  AmountColumn({
    title: '综合资金成本',
    dataIndex: 'comprehensiveFinancingCost',
    initFormat: 1,
    suffix: '%',
  }),
  InputColumn({
    title: '起息日',
    dataIndex: 'actualLoanDateStr',
  }),
  InputColumn({
    title: '到期日',
    dataIndex: 'actualExpireDateStr',
  }),
]
