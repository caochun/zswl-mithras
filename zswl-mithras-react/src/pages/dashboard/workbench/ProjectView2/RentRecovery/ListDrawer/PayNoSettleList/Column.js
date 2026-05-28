import {
  founderSelect,
  orgSelect,
  clientSelect,
  provinceSelect,
} from '@/pages/dashboard/workbench/Column'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '业务模式',
    dataIndex: 'leaseTypeDisplay',
  }),
  InputColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyDisplay',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),

  AmountColumn({
    title: '保证金(元)',
    dataIndex: 'earnestAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '存量风险敞口(元)',
    dataIndex: 'stockRiskExposure',
    initFormat: 1,
  }),
  AmountColumn({
    title: '投放金额(元)',
    dataIndex: 'actualPayAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余金额(元)',
    dataIndex: 'balanceAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余本金(元)',
    dataIndex: 'principalBalanceAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余利息(元)',
    dataIndex: 'interestBalanceAmount',
    initFormat: 1,
  }),
  clientSelect(),
  InputColumn({
    title: '国际行业分类',
    dataIndex: 'industryTypeDisplay',
  }),

  InputColumn({
    title: '承租人',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '担保人',
    dataIndex: 'guarantorNames',
  }),
  orgSelect({ title: '业务部门' }),
  founderSelect(),
  InputColumn({
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  }),
  MatchOptionColumn({
    title: '地区分类',
    dataIndex: 'regionalProjectClassifyCode',
    matchOption: 'projRegionalClassify',
  }),
  MatchOptionColumn({
    title: '是否关联方',
    dataIndex: 'isRelated',
    matchOption: 'yesOrNo',
  }),
  InputColumn({
    title: '投放月份',
    dataIndex: 'actualPayMonth',
  }),
  InputColumn({
    title: '起租日',
    dataIndex: 'actualLeaseDate',
  }),
  InputColumn({
    title: '租赁期限(月)',
    dataIndex: 'leaseDuration',
  }),
  InputColumn({
    title: '剩余期限(月)',
    dataIndex: 'remainingLeaseDuration',
  }),
  AmountColumn({
    title: 'IRR',
    dataIndex: 'irr',
    initFormat: 1,
    suffix: '%',
  }),
]
