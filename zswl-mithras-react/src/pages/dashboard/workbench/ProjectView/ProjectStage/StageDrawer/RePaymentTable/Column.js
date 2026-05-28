import { InputColumn, AmountColumn } from '@/components/Format'

export const All_COLUMNS = [
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  AmountColumn({
    title: '已收租金(元)',
    dataIndex: 'totalCollectionRent',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.totalCollectionRent?.value - b.totalCollectionRent?.value,
    },
  }),
  AmountColumn({
    title: '剩余金额(元)',
    dataIndex: 'totalRentBalance',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.totalRentBalance?.value - b.totalRentBalance?.value,
    },
  }),
  AmountColumn({
    title: '存量风险敞口(元)',
    dataIndex: 'stockRiskExposure',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.stockRiskExposure?.value - b.stockRiskExposure?.value,
    },
  }),
]
