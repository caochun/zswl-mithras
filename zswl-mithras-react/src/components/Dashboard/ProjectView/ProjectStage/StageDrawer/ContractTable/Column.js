import { founderSelect, clientSelect, orgSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const All_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  AmountColumn({
    title: '合同金额(元)',
    dataIndex: 'contractAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.contractAmount?.value - b.contractAmount?.value,
    },
  }),
  MatchOptionColumn({
    title: '签约状态',
    dataIndex: 'contractStatusCode',
    matchOption: 'contractStatus',
  }),
  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'contractProcessStatusCode',
    matchOption: 'contractProcessStatusEnum',
  }),
  InputColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyDisplay',
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
    title: '当前阶段停留天数(工作日)',
    dataIndex: 'lengthOfStay',
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
