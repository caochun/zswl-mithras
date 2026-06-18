import {
  founderSelect,
  clientSelect,
  orgSelect,
  provinceSelect,
} from '@/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  AmountColumn({
    title: '合同金额(元)',
    dataIndex: 'contractAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '剩余本金(元)',
    dataIndex: 'principalBalance',
    initFormat: 1,
  }),
  AmountColumn({
    title: '保证金(元)',
    dataIndex: 'earnest',
    initFormat: 1,
  }),
  AmountColumn({
    title: '风险敞口(元)',
    dataIndex: 'stockRiskExposure',
    initFormat: 1,
  }),
  InputColumn({
    title: '剩余期限(月)',
    dataIndex: 'remainingDurationMonths',
  }),
  // InputColumn({
  //   title: '剩余期限(年)',
  //   dataIndex: 'remainingDurationYears',
  // }),
  AmountColumn({
    title: '拨备金额(元)',
    dataIndex: 'provision',
    initFormat: 1,
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '项目类型',
    dataIndex: 'projectClassifyDisplay',
  }),
  orgSelect({ title: '业务部门' }),
  InputColumn({
    title: '到期日',
    dataIndex: 'deadline',
  }),
  InputColumn({
    title: '风险等级',
    dataIndex: 'assetsClassifyDisplay',
  }),
  AmountColumn({
    title: '计提比例',
    dataIndex: 'provisionRate',
    initFormat: 1,
    suffix: '%',
  }),
]
