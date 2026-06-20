import { ClientSelect, OrgSelect } from '@/components/Select'

import {
  MatchOptionColumn,
  InputColumn,
  DateColumn,
  AmountColumn,
  FiledFormat,
} from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '核销状态',
    dataIndex: 'writeOffStatus',
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    search: {
      element: <ClientSelect canJump={false} functionCode="clientlist-5"></ClientSelect>,
    },
    render: (val, { clientName }) => <FiledFormat title={clientName} />,
    width: 200,
  },
  InputColumn({ title: '合同编号', dataIndex: 'contractCode', search: true, width: 240 }),
  InputColumn({ title: '期项', dataIndex: 'phase' }),
  {
    title: '现⾦流项⽬',
    dataIndex: 'cashFlowItem',
    matchOption: 'cashFlowItemEnum',
    mode: 'multiple',
  },
  {
    title: '业务部⻔',
    dataIndex: 'bizDeptId',
    render: (val, { bizDept }) => <FiledFormat title={bizDept} />,
    width: 140,
    search: {
      element: <OrgSelect />,
      functionCode: 'kpiProjectAllotSelectOrgs',
    },
  },
  AmountColumn({ title: '应收⾦额（元）', dataIndex: 'planCollectionAmount', width: 160 }),
  AmountColumn({ title: '应付⾦额（元）', dataIndex: 'paymentAmount', width: 160 }),
  AmountColumn({ title: '已付⾦额（元）', dataIndex: 'paidAmount', width: 160 }),
  AmountColumn({ title: '本⾦（元）', dataIndex: 'principal' }),
  AmountColumn({ title: '利息（元）', dataIndex: 'interest' }),
  AmountColumn({ title: '已收⾦额（元）', dataIndex: 'collectionAmount', width: 160 }),
  DateColumn({ title: '应收⽇期', dataIndex: 'planCollectionDate', search: true }),
  DateColumn({ title: '应付⽇期', dataIndex: 'applyPaymentDate', search: true }),
  DateColumn({ title: '最近收款⽇', dataIndex: 'collectionDate' }),
  DateColumn({ title: '最近付款⽇', dataIndex: 'paidInDate' }),
  InputColumn({ title: '现⾦流编号', dataIndex: 'code' }),
  InputColumn({ title: '项⽬名称', dataIndex: 'projName' }),

  { title: '收款方式', dataIndex: 'collectionType' },
  { title: '付款方式', dataIndex: 'paymentMethod' },
  DateColumn({ title: '实付日期', dataIndex: 'paidInDate' }),
  AmountColumn({ title: '付款金额（元）', dataIndex: 'paidInAmount' }),
  InputColumn({ title: '票据号', dataIndex: 'billCode' }),
  AmountColumn({ title: '票据面额（元）', dataIndex: 'billAmount' }),
  DateColumn({ title: '票据到期日', dataIndex: 'billExpireDate' }),
  AmountColumn({ title: '票据买入价', dataIndex: 'billBuyRate', suffix: '%' }),

  MatchOptionColumn({
    title: '核销方式',
    dataIndex: 'writeOffType',
    matchOption: 'writeOffTypeEnum',
  }),
  DateColumn({ title: '操作日期', dataIndex: 'updateTime' }),

  { title: '操作人', dataIndex: 'updateByName' },

  DateColumn({ title: '实收日期', dataIndex: 'collectionDate' }),
  AmountColumn({ title: '实收金额(元)', dataIndex: 'collectionAmount', width: 200 }),
  AmountColumn({ title: '罚息（元）', dataIndex: 'penaltyInterest' }),

  { title: '收/付款', dataIndex: 'type' },
  AmountColumn({ title: '⾦额（元）', dataIndex: 'amount' }),
  DateColumn({ title: '日期', dataIndex: 'date', search: true }),
  InputColumn({ title: '流水ID', dataIndex: 'serialNo', width: 220 }),
  InputColumn({
    title: '融资渠道',
    dataIndex: 'financingRoute',
  }),
  AmountColumn({ title: '融资金额（元）', dataIndex: 'financingAmount' }),
  { title: '业务类型', dataIndex: 'businessType' },
  { title: '交易明细编号', dataIndex: 'bankDetailNo' },
  { title: '融资编号', dataIndex: 'financingCode' },
  { title: '期项', dataIndex: 'phase' },
  AmountColumn({ title: '应付本金（元）', dataIndex: 'principalAmount' }),
  AmountColumn({ title: '应付利息（元）', dataIndex: 'interestAmount' }),
  AmountColumn({ title: '已付金额（元）', dataIndex: 'actualVerifyAmount' }),
  AmountColumn({ title: '已付本金（元）', dataIndex: 'actualVerifyPrincipalAmount' }),
  AmountColumn({ title: '已付利息（元）', dataIndex: 'actualVerifyInterestAmount' }),
  DateColumn({ title: '最近付款日', dataIndex: 'cashFlowDate' }),
]

export default ALL_COLUMNS
