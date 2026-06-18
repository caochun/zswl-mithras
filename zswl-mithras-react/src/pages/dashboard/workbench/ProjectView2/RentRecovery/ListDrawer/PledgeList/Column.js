import { founderSelect, clientSelect, orgSelect } from '@/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, DateColumn, InputColumn, AmountColumn } from '@/components/Format'
import { BankAccount } from '@/components/Form'

export const ALL_COLUMNS = [
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'businessType',
    matchOption: 'fundFinancingBizTypeEnum',
  }),
  AmountColumn({
    title: '投放金额(元)',
    dataIndex: 'totalPayAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余租金金额(元)',
    dataIndex: 'residualRent',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余本金金额(元)',
    dataIndex: 'remainingPrincipal',
    initFormat: 1,
  }),
  MatchOptionColumn({
    title: '质押/监管情况',
    dataIndex: 'pledgeStatus',
    matchOption: 'dashboardPledgeTypeEnum',
  }),
  InputColumn({
    title: '户名',
    dataIndex: 'accountName',
  }),
  InputColumn({
    title: '开户行',
    dataIndex: 'accountBank',
  }),
  InputColumn({
    title: '账号',
    dataIndex: 'accountNumber',
    render: (val) => BankAccount.Format({ value: val }),
  }),
  InputColumn({
    title: '融资机构/产品名称',
    dataIndex: 'orgName',
  }),
  MatchOptionColumn({
    title: '融资状态',
    dataIndex: 'financingStatus',
    matchOption: 'fundFinancingNewStatusEnum',
  }),
  InputColumn({
    title: '融资编号',
    dataIndex: 'financingCode',
  }),
]
