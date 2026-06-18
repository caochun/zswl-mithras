import { founderSelect, clientSelect, orgSelect } from '@/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, DateColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
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
  InputColumn({
    title: '逾期天数(天)',
    dataIndex: 'overdueDuration',
  }),
  AmountColumn({
    title: '逾期金额(元)',
    dataIndex: 'overdueAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '罚息日利率',
    dataIndex: 'interestPenaltyDailyRate',
    initFormat: 1,
    suffix: '%',
  }),
  AmountColumn({
    title: '罚息金额(元)',
    dataIndex: 'interestPenaltyAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '罚息减免金额(元)',
    dataIndex: 'interestPenaltyReduceAmount',
    initFormat: 1,
  }),
  clientSelect(),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '业务模式',
    dataIndex: 'leaseTypeDisplay',
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
]
