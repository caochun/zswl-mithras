import {
  founderSelect,
  orgSelect,
  provinceSelect,
  clientSelect,
} from '@/utils/domains/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

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
    title: '授信总金额(万元)',
    dataIndex: 'creditAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.creditAmount?.value - b.creditAmount?.value,
    },
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
    title: '客户分类',
    dataIndex: 'clientTypeName',
  }),
  InputColumn({
    title: '国际行业分类',
    dataIndex: 'industryTypeDisplay',
  }),
  InputColumn({
    title: '企业性质',
    dataIndex: 'enterpriseNatureDisplay',
  }),
  provinceSelect(),
  orgSelect({ title: '所属部门' }),
  founderSelect({ title: '所属主办' }),
  InputColumn({
    title: '创建人',
    dataIndex: 'creatorName',
  }),
]
