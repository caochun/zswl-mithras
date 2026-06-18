import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'
import { founderSelect, orgSelect, clientSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'

export const ALL_COLUMNS = [
  clientSelect(),
  MatchOptionColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyCode',
    matchOption: 'riskControlIndustryClassify',
  }),
  MatchOptionColumn({
    title: '资产五级分类',
    dataIndex: 'assetClassifyResultCode',
    matchOption: 'assetClassifyResultEnum',
  }),
  AmountColumn({
    title: '剩余本金(万元)',
    dataIndex: 'principalBalanceAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.principalBalanceAmount?.value - b.principalBalanceAmount?.value,
    },
  }),
  AmountColumn({
    title: '存量风险敞口(万元)',
    dataIndex: 'stockRiskExposure',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.stockRiskExposure?.value - b.stockRiskExposure?.value,
    },
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCodes',
  }),
  DateColumn({
    title: '到期日',
    dataIndex: 'deadline',
    search: true,
  }),
  InputColumn({
    title: '剩余天数(天)',
    dataIndex: 'remainingDuration',
  }),
  InputColumn({
    title: '客户分类',
    dataIndex: 'clientTypeName',
  }),
  InputColumn({
    title: '国际行业分类',
    dataIndex: 'industryTypeDisplay',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projNames',
  }),
  AmountColumn({
    title: '已收租金(万元)',
    dataIndex: 'collectionAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '已收本金(万元)',
    dataIndex: 'collectionPrincipalAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '已收利息(万元)',
    dataIndex: 'collectionInterestAmount',
    initFormat: 1,
  }),
  // AmountColumn({
  //   title: '剩余本金(万元)',
  //   dataIndex: 'interestPrincipalAmount',
  //   initFormat: 1,
  // }),
  AmountColumn({
    title: '剩余利息(万元)',
    dataIndex: 'interestInterestAmount',
    initFormat: 1,
  }),
]
