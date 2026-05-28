import { App } from '@zswl/components'

export default function useGetColumns() {
  const options = App.getData().optionsType
  const enterpriseType = (options?.enterpriseType || []).filter((v) => {
    return ![
      'OTHER_SMALL_AND_MICRO',
      'STATE_OWNED_ENTERPRISE',
      'PRIVATE_NON_LISTED',
      'STATE_OWNED_AND_LISTED',
      'PRIVATE_LISTED',
    ].includes(v.value)
  })
  // [

  //   { label: '国有', value: 'STATE_OWNED' },
  //   { label: '其他', value: 'OTHER' },
  //   // { label: '民营上市企业', value: 'PRIVATE_LISTED' },
  //   // { label: '民营非上市企业', value: 'PRIVATE_NON_LISTED' },
  // ]
  const projectClassify = (options?.projectClassify || []).filter((v) => {
    return v.value !== 'CONSTRUCTION_MACHINERY' && v.value !== 'INTRA_GROUP_COLLABORATION'
  })
  //  [
  //   { label: '鼓励介入类', value: 'ENCOURAGEMENT' },
  //   { label: '适度支持类', value: 'MODERATE_SUPPORT' },
  //   { label: '谨慎支持类', value: 'CAUTIOUS' },
  //   // { label: '工程机械类（厂商担保模式）', value: 'CONSTRUCTION_MACHINERY' },
  //   // { label: '工程机械类（银行担保模式）', value: 'CONSTRUCTION_MACHINERY_BANK' },
  // ]
  const creditTerm = options?.creditTerm || []

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
  return {
    enterpriseType,
    creditTerm,
    projectClassify,
    monthType,
    meanType,
  }
}
