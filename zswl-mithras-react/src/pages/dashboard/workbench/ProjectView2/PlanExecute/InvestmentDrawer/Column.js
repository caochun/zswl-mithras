import { founderSelect, orgSelect } from '@/utils/dashboardColumns'
import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  orgSelect({
    title: '部门',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '项目类型',
    dataIndex: 'projectClassifyDisplay',
  }),
  InputColumn({
    title: '业务模式',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  AmountColumn({
    title: '融资金额(万元)',
    dataIndex: 'contractAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.contractAmount?.value - b.contractAmount?.value,
    },
  }),
  AmountColumn({
    title: '保证金(万元)',
    dataIndex: 'earnestMoney',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.earnestMoney?.value - b.earnestMoney?.value,
    },
  }),
  AmountColumn({
    title: '手续费(万元)',
    dataIndex: 'commission',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.commission?.value - b.commission?.value,
    },
  }),

  AmountColumn({
    title: 'IRR(%)',
    dataIndex: 'irr',
    initFormat: 1,
  }),
  AmountColumn({
    title: '投放金额(万元)',
    dataIndex: 'actualPayAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.actualPayAmount?.value - b.actualPayAmount?.value,
    },
  }),
  InputColumn({
    title: '投放月份',
    dataIndex: 'actualPayDate',
  }),
  founderSelect(),
  InputColumn({
    title: '省份',
    dataIndex: 'provinceDisplay',
  }),
  InputColumn({
    title: '市',
    dataIndex: 'cityDisplay',
  }),
  InputColumn({
    title: '区',
    dataIndex: 'districtDisplay',
  }),
  InputColumn({
    title: '年限(年)',
    dataIndex: 'contractLimitYear',
  }),
]
