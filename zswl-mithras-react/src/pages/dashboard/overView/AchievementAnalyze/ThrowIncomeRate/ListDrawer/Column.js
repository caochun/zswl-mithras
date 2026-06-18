import { founderSelect, orgSelect, clientSelect } from '@/dashboard/DashboardUtilsColumns'
import { InputColumn, AmountColumn, DateColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  DateColumn({
    title: '投放日期',
    dataIndex: 'actualPayDate',
    search: true,
  }),
  clientSelect(),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyDisplay',
  }),
  InputColumn({
    title: '风险策略',
    dataIndex: 'riskStrategy',
  }),
  AmountColumn({
    title: '投放金额(元)',
    dataIndex: 'actualPayAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.actualPayAmount?.value - b.actualPayAmount?.value,
    },
  }),
  InputColumn({
    title: '项目期限(月)',
    dataIndex: 'duration',
  }),
  AmountColumn({
    title: 'IRR(%)',
    dataIndex: 'actualIrr',
    initFormat: 1,
  }),
  AmountColumn({
    title: '合同利率(%)',
    dataIndex: 'interestRate',
    initFormat: 1,
  }),
  AmountColumn({
    title: '咨询费率(%)',
    dataIndex: 'consultingFeeRate',
    initFormat: 1,
  }),
  AmountColumn({
    title: '手续费率(%)',
    dataIndex: 'commissionRate',
    initFormat: 1,
  }),
  AmountColumn({
    title: '保证金金额(元)',
    dataIndex: 'earnest',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.earnest?.value - b.earnest?.value,
    },
  }),
  InputColumn({
    title: '地区',
    dataIndex: 'regionalProjectClassifyDisplay',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  founderSelect({ title: '业务主办' }),
  orgSelect(),
]
