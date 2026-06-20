import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '融资编号',
    dataIndex: 'financingCode',
  }),

  InputColumn({
    title: '融资机构/产品名称',
    dataIndex: 'orgName',
  }),
  MatchOptionColumn({
    title: '融资类别',
    dataIndex: 'financingTypeCode',
    matchOption: 'financingTypeEnum',
  }),
  AmountColumn({
    title: '融资金额(元)',
    dataIndex: 'loanAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.loanAmount?.value - b.loanAmount?.value,
    },
  }),

  AmountColumn({
    title: '剩余本金(元)',
    dataIndex: 'remainingPrincipleAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.remainingPrincipleAmount?.value - b.remainingPrincipleAmount?.value,
    },
  }),
  AmountColumn({
    title: '综合资金成本',
    dataIndex: 'comprehensiveInterestRate',
    initFormat: 1,
    suffix: '%',
  }),
  InputColumn({
    title: '质押合同',
    dataIndex: 'relatedContractCodeList',
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
