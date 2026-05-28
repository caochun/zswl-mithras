import { App } from '@zswl/components'

const options = App.getData().optionsType
const enterpriseType = (options?.enterpriseType || []).filter((v) => {
  return v.value !== 'PRIVATE_LISTED' && v.value !== 'PRIVATE_NON_LISTED'
})
// [
//   { label: '国有', value: 'STATE_OWNED' },
//   { label: '其他', value: 'OTHER' },
//   // { label: '民营上市企业', value: 'PRIVATE_LISTED' },
//   // { label: '民营非上市企业', value: 'PRIVATE_NON_LISTED' },
// ]
const projectClassify = (options?.projectClassify || []).filter((v) => {
  return v.value !== 'CONSTRUCTION_MACHINERY' && v.value !== 'CONSTRUCTION_MACHINERY_BANK'
})
//  [
//   { label: '鼓励介入类', value: 'ENCOURAGEMENT' },
//   { label: '适度支持类', value: 'MODERATE_SUPPORT' },
//   { label: '谨慎支持类', value: 'CAUTIOUS' },
//   // { label: '工程机械类（厂商担保模式）', value: 'CONSTRUCTION_MACHINERY' },
//   // { label: '工程机械类（银行担保模式）', value: 'CONSTRUCTION_MACHINERY_BANK' },
// ]
const creditTerm = options?.creditTerm || []
// [
//   { label: '1年内(含)', value: 'ONE_YEAR' },
//   { label: '1-3年(含)', value: 'ONE_TO_THREE_YEARS' },
//   { label: '3年以上(含)', value: 'MORE_THAN_THREE_YEARS' },
// ]

const monthType = (options?.monthType || []).filter((v) => {
  return v.value !== 'MEAN_VALUE'
})
const meanType = (options?.monthType || []).filter((v) => {
  return v.value === 'MEAN_VALUE'
})
//  [
//   { label: '孟月', value: 'FIRST_MONTH' },
//   { label: '仲月', value: 'SECOND_MONTH' },
//   { label: '季月', value: 'THIRD_MONTH' },
//   // { label: '均值', value: 'MEAN_VALUE' },
// ]
export default {
  enterpriseType,
  creditTerm,
  projectClassify,
  monthType,
  meanType,
}
