import { founderSelect, clientSelect, orgSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, DateColumn, InputColumn, AmountColumn } from '@/components/Format'

export const All_COLUMNS = [
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  DateColumn({
    title: '到期日',
    dataIndex: 'deadline',
    search: true,
  }),
  AmountColumn({
    title: '剩余期限(月)',
    dataIndex: 'remainingDuration',
    initFormat: 1,
  }),
  AmountColumn({
    title: '已收租金金额(元)',
    dataIndex: 'collectionAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.collectionAmount?.value - b.collectionAmount?.value,
    },
  }),
  AmountColumn({
    title: '剩余租金金额(元)',
    dataIndex: 'remainingAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.remainingAmount?.value - b.remainingAmount?.value,
    },
  }),
  AmountColumn({
    title: '保证金金额(元)',
    dataIndex: 'earnestBalanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.earnestBalanceAmount?.value - b.earnestBalanceAmount?.value,
    },
  }),
  InputColumn({
    title: '最后一期租金计划还款日',
    dataIndex: 'lastRentPlanCollectionDate',
  }),
  clientSelect(),
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
  AmountColumn({
    title: '已收本金(元)',
    dataIndex: 'collectionPrincipalAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.collectionPrincipalAmount?.value - b.collectionPrincipalAmount?.value,
    },
  }),
  AmountColumn({
    title: '已收利息(元)',
    dataIndex: 'collectionInterestAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.collectionInterestAmount?.value - b.collectionInterestAmount?.value,
    },
  }),
  AmountColumn({
    title: '剩余本金(元)',
    dataIndex: 'principalBalanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.principalBalanceAmount?.value - b.principalBalanceAmount?.value,
    },
  }),
  AmountColumn({
    title: '剩余利息(元)',
    dataIndex: 'interestBalanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.interestBalanceAmount?.value - b.interestBalanceAmount?.value,
    },
  }),
]
