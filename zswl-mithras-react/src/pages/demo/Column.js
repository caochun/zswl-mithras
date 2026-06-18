import {
  FiledFormat,
  AmountColumn,
  FounderColumn,
  DateColumn,
  TextAreaColumn,
  InputColumn,
  MatchOptionColumn,
} from '@/components/Format'
import moment from 'moment'
import { BankAccount } from '@/components/Form'
import { FormAmount } from '@/components/Form'
import { CreditOrgSelect } from '@/components'

const ALL_COLUMNS = [
  {
    title: '融资机构',
    dataIndex: 'financingOrgName',
    editable: false,
    search: {
      itemProps: {
        transform: (val) => ({ financingOrgName: undefined, financingOrgId: val }),
      },
      element: <CreditOrgSelect />,
    },
  },
  {
    title: '收付款编号',
    dataIndex: 'receiptRepayCode',
    width: 160,
    editable: false,
  },
  AmountColumn({ title: '融资金额（元）', dataIndex: 'financingAmount', search: true }),
  AmountColumn({ title: '利息总额（元）', dataIndex: 'totalInterest' }),
  // {
  //   title: '增信方式：担保',
  //   dataIndex: 'guaranteeDetail',
  //   editable: false,
  //   span: 2,
  // },
  AmountColumn({ title: '保理手续费（元）', dataIndex: 'factoringFee' }),
  AmountColumn({ title: '开征许可证费（元）', dataIndex: 'licenseFee' }),
  AmountColumn({ title: '保证金金额（元）', dataIndex: 'cashDeposit' }),
  AmountColumn({ title: '其他费用（元）', dataIndex: 'otherFee' }),
  AmountColumn({ title: '总授信额度（元）', dataIndex: 'totalCreditLimit' }),
  AmountColumn({ title: '剩余授信额度（元）', dataIndex: 'remainingCreditLimit' }),
  FounderColumn({ title: '创建人', dataIndex: 'createByName', params: { job: 'moneymanager' } }),
  DateColumn({ title: '创建时间', dataIndex: 'createTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' }),
  DateColumn({ title: '更新时间', dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' }),
  DateColumn({ title: '选择时间', dataIndex: 'date' }),
  TextAreaColumn({ title: '备注', dataIndex: 'remark' }),
  // 客户名称,账户名称,银行账号,开户行,现金流编号,期项
  InputColumn({ title: '客户名称', dataIndex: 'clientName' }),
  InputColumn({ title: '账户名称', dataIndex: 'accountName' }),
  InputColumn({
    title: '银行账号',
    dataIndex: 'accountNum',
    element: <BankAccount.Item itemStyle={{ marginBottom: 0 }} required />,
    render: (val) => BankAccount.Format({ value: val }),
  }),
  InputColumn({ title: '开户行', dataIndex: 'accountAddress' }),
  InputColumn({ title: '现金流编号', dataIndex: 'cashFlowCode', width: 220 }),
  InputColumn({ title: '期项', dataIndex: 'term' }),
  AmountColumn({ title: '本金（元）', dataIndex: 'principal' }),
  AmountColumn({ title: '利息（元）', dataIndex: 'interest' }),
  //   '本月支付金额', '本月收入金额',
  AmountColumn({ title: '本月支付金额', dataIndex: 'paidAmount', editable: true }),
  AmountColumn({ title: '本月收入金额', dataIndex: 'receiptAmount', editable: true }),
  // 实际贷款时间
  InputColumn({ title: '实际贷款时间', dataIndex: 'actualLoanDate' }),

  // 核销状态
  MatchOptionColumn({
    title: '核销状态',
    dataIndex: 'writeOffState',
    matchOption: 'fundReceiptRepayCashFlowState',
    editable: false,
  }),

  AmountColumn({
    title: '本月支付本金（元）',
    dataIndex: 'planRepayPrincipal',
    width: 200,
    render: (val, record) => {
      const isRed = record.isRed?.value ?? record.isRed
      const principleAmount = record.principleAmount?.value ?? record.principleAmount
      const value = isRed && principleAmount
      return <FormAmount.Format value={value} />
    },
  }),
  AmountColumn({
    title: '本月支付利息（元）',
    dataIndex: 'planRepayInterest',
    width: 200,
    render: (val, record) => {
      const isRed = record.isRed?.value ?? record.isRed
      const interestAmount = record.interestAmount?.value ?? record.interestAmount
      const value = isRed && interestAmount
      return <FormAmount.Format value={value} />
    },
  }),

  // 资金经理，所属部门，部门负责人，分管领导
  FounderColumn({
    title: '资金经理',
    dataIndex: 'fundManager',
    search: true,
    params: { job: 'moneymanager' },
    editable: false,
  }),
  { title: '所属部门', dataIndex: 'department', editable: false, disabled: true },
  InputColumn({ title: '部门负责人', dataIndex: 'departmentLeader' }),
  InputColumn({ title: '分管领导', dataIndex: 'chargeLeader' }),
  // 质押编号,项目名称,合同编号,合同金额（元）,合同期限,剩余未还本金（元）
  { title: '质押编号', dataIndex: 'pledgeNo' },
  { title: '项目名称', dataIndex: 'projName' },
  { title: '合同编号', dataIndex: 'contractCode' },
  AmountColumn({ title: '合同金额（元）', dataIndex: 'contractAmount' }),
  {
    title: '合同期限',
    dataIndex: 'contractPeriod',
    render: (val, record) => {
      const contractStartDate = record.contractStartDate?.value ?? record.contractStartDate
      const contractEndDate = record.contractEndDate?.value ?? record.contractEndDate
      const getTime = (time) => {
        if (!time) return ''
        return moment(time).format('YYYY.MM.DD')
      }
      const title = `${getTime(contractStartDate)}~${getTime(contractEndDate)}`
      return <FiledFormat title={title} />
    },
  },
  AmountColumn({ title: '剩余未还本金（元）', dataIndex: 'unpaidPrincipal' }),

  //已还本金（元）,已还利息（元）,一年内到期本金（元）,本月未还金额（元）,收付款状态,审批状态
  AmountColumn({ title: '已还本金（元）', dataIndex: 'repayPrincipal' }),
  AmountColumn({ title: '已还利息（元）', dataIndex: 'repayInterest' }),
  AmountColumn({ title: '一年内到期本金（元）', dataIndex: 'oneYearPrincipal' }),
  AmountColumn({ title: '本月未还金额（元）', dataIndex: 'monthRepayAmount' }),
  MatchOptionColumn({
    title: '本息收付款状态',
    dataIndex: 'receiptRepayState',
    matchOption: 'fundReceiptRepayState',
    search: true,
    element: true,
  }),
  { title: '审批状态', dataIndex: 'processState', matchOption: 'fundReceiptRepayProcessState' },
  MatchOptionColumn({ title: '费用类型', dataIndex: 'expenseType', editable: false }),
  AmountColumn({ title: '金额（元）', dataIndex: 'totalAmount' }),
  AmountColumn({ title: '累计支付金额（元）', dataIndex: 'totalPaidAmount', width: 200 }),
  AmountColumn({
    title: '本月支付金额（元）',
    dataIndex: 'payAmount',
    width: 200,
    editable: true,
  }),
  //    '累计已还本金（元）','累计已还利息（元）','配套项目','质押合同编号','借款日期','到期日期','本月计划还款合计（元）','本月计划还款本金（元）','计划还本日','本月计划还款利息（元）','计划还息日',
  AmountColumn({ title: '累计已还本金（元）', dataIndex: 'paidPrincipal' }),
  AmountColumn({ title: '累计已还利息（元）', dataIndex: 'paidInterest' }),
  {
    title: '配套项目',
    dataIndex: 'projNameList',
    render: (val) => <FiledFormat value={(val?.value ?? val ?? []).join('\n')} />,
  },
  {
    title: '质押合同编号',
    dataIndex: 'pledgeContractCodeList',
    render: (val) => <FiledFormat value={(val?.value ?? val ?? []).join('\n')} />,
  },
  { title: '借款日期', dataIndex: 'borrowingDate' },
  { title: '到期日期', dataIndex: 'expirationDate' },
  DateColumn({ title: '计划还款日', dataIndex: 'repayDate', editable: false }),
  AmountColumn({ title: '本月计划还款合计（元）', dataIndex: 'planedRepayAmount', width: 200 }),
  AmountColumn({ title: '本月计划还款本金（元）', dataIndex: 'planedRepayPrincipal', width: 200 }),
  { title: '计划还本日', dataIndex: 'planedRepayPrincipleDate' },
  AmountColumn({ title: '本月计划还款利息（元）', dataIndex: 'planedRepayInterest', width: 200 }),
  { title: '计划还息日', dataIndex: 'planedRepayInterestDate', width: 200 },
]
export default ALL_COLUMNS
