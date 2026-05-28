import { InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '年度',
    dataIndex: 'year',
  }),
  InputColumn({
    title: 'FTP行业分类',
    dataIndex: 'businessType',
  }),
  AmountColumn({
    title: '资产余额(万元)',
    dataIndex: 'assetBalanceTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '投放额目标(万元)',
    dataIndex: 'investmentTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '营业收入目标(万元)',
    dataIndex: 'revenueTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '利润目标（拨备前万元）',
    dataIndex: 'beforeProfitTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '营业收入-咨询服务费收入(万元)',
    dataIndex: 'consultingFeeIncome',
    initFormat: 1,
  }),
  AmountColumn({
    title: '营业收入-利息收入(万元)',
    dataIndex: 'interestIncome',
    initFormat: 1,
  }),
  AmountColumn({
    title: '业务规模(万元)',
    dataIndex: 'assetBalanceTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '经营费用(万元)',
    dataIndex: 'bizFee',
    initFormat: 1,
  }),
  AmountColumn({
    title: '经营费用-业务招待费(万元)',
    dataIndex: 'businessServeFee',
    initFormat: 1,
  }),
  AmountColumn({
    title: '经营费用-差旅费用(万元)',
    dataIndex: 'businessTripFee',
    initFormat: 1,
  }),
  AmountColumn({
    title: '利润目标(万元)',
    dataIndex: 'profitTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '1月投放目标(万元)',
    dataIndex: 'januaryTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '2月投放目标(万元)',
    dataIndex: 'februaryTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '3月投放目标(万元)',
    dataIndex: 'marchTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '4月投放目标(万元)',
    dataIndex: 'aprilTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '5月投放目标(万元)',
    dataIndex: 'mayTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '6月投放目标(万元)',
    dataIndex: 'juneTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '7月投放目标(万元)',
    dataIndex: 'julyTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '8月投放目标(万元)',
    dataIndex: 'augustTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '9月投放目标(万元)',
    dataIndex: 'septemberTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '10月投放目标(万元)',
    dataIndex: 'octoberTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '11月投放目标(万元)',
    dataIndex: 'novemberTarget',
    initFormat: 1,
  }),
  AmountColumn({
    title: '12月投放目标(万元)',
    dataIndex: 'decemberTarget',
    initFormat: 1,
  }),

  InputColumn({
    title: '部门',
    dataIndex: 'dept',
  }),
  InputColumn({
    title: '登陆账户名称',
    dataIndex: 'loginAccountName',
  }),
  InputColumn({
    title: '业务人员名称',
    dataIndex: 'staffName',
  }),
]
