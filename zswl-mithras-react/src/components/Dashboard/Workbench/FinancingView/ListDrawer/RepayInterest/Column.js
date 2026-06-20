import { founderSelect, orgSelect, provinceSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'
import { Select, App } from '@zswl/components'

export const ALL_COLUMNS = [
  InputColumn({
    title: '融资编号',
    dataIndex: 'financingCode',
  }),
  InputColumn({
    title: '融资机构/产品名称',
    dataIndex: 'orgName',
  }),
  AmountColumn({
    title: '贷款金额(万元)',
    dataIndex: 'loanAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.loanAmount?.value - b.loanAmount?.value,
    },
  }),
  AmountColumn({
    title: '剩余贷款金额(万元)',
    dataIndex: 'loanBalanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.loanBalanceAmount?.value - b.loanBalanceAmount?.value,
    },
  }),
  AmountColumn({
    title: '本月计划应还金额(元)',
    dataIndex: 'repayTotalAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '本月应还本金(元)',
    dataIndex: 'repayPrincipalAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '本月应还利息(元)',
    dataIndex: 'repayInterestAmount',
    initFormat: 1,
  }),
  InputColumn({
    title: '本月应还日期',
    dataIndex: 'repayDate',
  }),
  InputColumn({
    title: '关联项目名称',
    dataIndex: 'relatedProjName',
  }),
  InputColumn({
    title: '关联合同编号',
    dataIndex: 'relatedContractCode',
  }),
  AmountColumn({
    title: '租金回笼金额(元)',
    dataIndex: 'rentPlanCollectionAmount',
    initFormat: 1,
  }),
  DateColumn({
    title: '租金回笼日期',
    dataIndex: 'rentPlanCollectionDate',
  }),
  AmountColumn({
    title: '本月已还金额(元)',
    dataIndex: 'actualRepayAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '本月未还金额(元)',
    dataIndex: 'repayBalanceAmount',
    initFormat: 1,
  }),
  InputColumn({
    title: '本月支付日期',
    dataIndex: 'actualRepayDate',
  }),
  InputColumn({
    title: '核销状态',
    dataIndex: 'writeOffStateList',
    search: {
      element: <Select mode={'multiple'} options={'fundReceiptRepayCashFlowState'} />,
    },
    render: (val, { writeOffState }) =>
      App.matchOption('fundReceiptRepayCashFlowState', writeOffState)?.label,
  }),
  InputColumn({
    title: '账户性质',
    dataIndex: 'bankAccountTypeDisplay',
  }),
]
