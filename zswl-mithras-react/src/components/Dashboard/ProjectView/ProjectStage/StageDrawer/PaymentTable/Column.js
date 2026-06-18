import { founderSelect, clientSelect, orgSelect } from '@/dashboard/DashboardUtilsColumns'
import {
  MatchOptionColumn,
  InputColumn,
  DateColumn,
  AmountColumn,
  FiledFormat,
} from '@/components/Format'

export const All_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyDisplay',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  DateColumn({
    title: '申请付款日期',
    dataIndex: 'applyPayDate',
    search: true,
  }),
  AmountColumn({
    title: '申请付款金额',
    dataIndex: 'applyPayAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.applyPayAmount?.value - b.applyPayAmount?.value,
    },
  }),
  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'paymentProcessStatusCode',
    matchOption: 'commonProcessStatus',
  }),
  MatchOptionColumn({
    title: '申请状态',
    dataIndex: 'paymentStatusCode',
    matchOption: 'paymentStatusEnum',
  }),
  InputColumn({
    title: '当前阶段停留天数(工作日)',
    dataIndex: 'lengthOfStay',
  }),
  AmountColumn({
    title: '合同金额(元)',
    dataIndex: 'contractAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.contractAmount?.value - b.contractAmount?.value,
    },
  }),
  InputColumn({
    title: '租赁期限(月)',
    dataIndex: 'leaseDuration',
  }),
  AmountColumn({
    title: '租赁利率',
    dataIndex: 'interestRate',
    initFormat: 1,
    suffix: '%',
  }),
  InputColumn({
    title: '国际行业分类',
    dataIndex: 'industryTypeDisplay',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
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
  InputColumn({
    title: '申请时间',
    dataIndex: 'applyTime',
  }),
]
