import { founderSelect, orgSelect, clientSelect } from '@/utils/dashboardColumns'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '逾期期项',
    dataIndex: 'phase',
  }),
  AmountColumn({
    title: '逾期金额(万元)',
    initFormat: 1,
    dataIndex: 'overdueAmount',
    // sorter: {
    //   compare: (a, b) => a.overdueAmount - b.overdueAmount,
    // },
  }),
  AmountColumn({
    title: '罚息日利率',
    dataIndex: 'penaltyInterestRate',
    initFormat: 1,
    suffix: '%',
  }),
  AmountColumn({
    title: '罚息金额(万元)',
    initFormat: 1,
    dataIndex: 'penaltyInterestAmount',
    // sorter: {
    //   compare: (a, b) => a.penaltyInterestAmount - b.penaltyInterestAmount,
    // },
  }),
  AmountColumn({
    title: '罚息减免金额(万元)',
    initFormat: 1,
    dataIndex: 'penaltyInterestReductionAmount',
    // sorter: {
    //   compare: (a, b) => a.penaltyInterestReductionAmount - b.penaltyInterestReductionAmount,
    // },
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    render: (_, { bizType, bizModel }) => {
      return `${bizType}${bizModel ? `(${bizModel})` : ''}`
    },
  }),
  InputColumn({
    title: '承租人',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '担保人',
    dataIndex: 'guarantorNames',
  }),
  orgSelect({ title: '所属部门' }),
  founderSelect({ title: '所属主办' }),
  InputColumn({
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  }),
]
