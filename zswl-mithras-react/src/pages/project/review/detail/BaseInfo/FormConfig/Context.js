import Api from '@/pages/project/review/detail/api'

// 当风控行业分类=公用事业类、民生消费类、旅游行业时在“地区分类”后增加需维护字段“区域划分”，下拉选择框，枚举值：浙江地区、一类地区、二类地区，
export const isShowProjRegionalDivision = (detail) => {
  return ['PUBLIC_UTILITIES', 'CIVIL_CONSUMPTION', 'TRAVEL'].includes(
    detail.riskControlIndustryClassify
  )
}

// “行业分类”字段逻辑调整，，当风控行业分类=公用事业类、民生消费类、旅游行业、集团内协同业务时，“行业分类”为非必填项；当风控行业分类=工程机械类或其他分类时，“行业分类”必填 (当前是提交审批时非必填，流程中秘书节点必填，该逻辑可保持）
// 行业分类是否必须
export const isProjectClassifyRequired = (detail) => {
  return !['PUBLIC_UTILITIES', 'CIVIL_CONSUMPTION', 'TRAVEL', 'INTRA_GROUP_COLLABORATION'].includes(
    detail.riskControlIndustryClassify
  )
}

// 若“地区分类”为浙江地区，“区域划分”自动填充展示为浙江地区。
export const onRegionalClassifyChange = (form) => {
  const ftpIndustryCategory = form?.getFieldValue('ftpIndustryCategory')
  const isOther = ['FTP_OTHER_INDUSTRY'].includes(ftpIndustryCategory)
  if (isOther) return
  const isDivision = ['FTP_STATE_OWNED_INDUSTRY'].includes(ftpIndustryCategory)
  const regionalClassifyValue = form?.getFieldValue?.('regionalProjectClassify')
  const isZhejiang = ['ZHEJIANG'].includes(regionalClassifyValue)
  if (!isDivision && isZhejiang) {
    form.setFieldsValue({ regionalDivision: 'PUBLIC_ZJ_AREA' })
    return
  }

  if (isZhejiang) {
    form.setFieldsValue({ regionalDivision: 'ZJ_AREA' })
  }
}

// 评估主体改变
export const onEvaluateMainChange = async (value, form) => {
  if (!value) return
  const res = await Api.postGetAddressByClientId({ clientId: value })
  form.setFieldValue('area', [res.province, res.city, res.district])
}

// [
//   {
//     "label": "公用事业类",
//     "value": "PUBLIC_UTILITIES"
//   },
//   {
//     "label": "民生消费类（含供水供热供电供气、污水处理、公共交通等）",
//     "value": "CIVIL_CONSUMPTION"
//   },
//   {
//     "label": "旅游行业",
//     "value": "TRAVEL"
//   },
//   {
//     "label": "钢铁、不锈钢及有色金属冶炼行业",
//     "value": "STEEL"
//   },
//   {
//     "label": "交通运输物流行业（含冷链仓储物流、普通物流等）",
//     "value": "TRANSPORTATION_LOGISTICS"
//   },
//   {
//     "label": "水上运输业",
//     "value": "WATER_TRANSPORTATION"
//   },
//   {
//     "label": "造纸行业",
//     "value": "PAPER_MAKING"
//   },
//   {
//     "label": "精细化工行业（非长三角地区）",
//     "value": "FINE_CHEMICAL_COMMON"
//   },
//   {
//     "label": "精细化工行业（仅长三角地区）",
//     "value": "FINE_CHEMICAL"
//   },
//   {
//     "label": "建筑工程行业",
//     "value": "CONSTRUCTION"
//   },
//   {
//     "label": "信息产业（5G、IDC、通信服务等）",
//     "value": "INFORMATION_INDUSTRY"
//   },
//   {
//     "label": "新材料、新科技、智能制造等高端装备制造行业（非长三角地区）",
//     "value": "NEW_MATERIALS"
//   },
//   {
//     "label": "新材料、新科技、智能制造等高端装备制造行业（仅长三角地区）",
//     "value": "NEW_MATERIALS_CHANGJIANG_DELTA"
//   },
//   {
//     "label": "工程机械类（厂家担保模式）",
//     "value": "ENGINEERING_MACHINERY"
//   },
//   {
//     "label": "工程机械类（非厂家担保模式）",
//     "value": "ENGINEERING_MACHINERY_NON"
//   },
//   {
//     "label": "新能源",
//     "value": "SHIP_SOLAR"
//   },
//   {
//     "label": "创新业务（取国标行业分类第二级）",
//     "value": "INNOVATION_BUSINESS"
//   },
//   {
//     "label": "集团内协同业务",
//     "value": "INTRA_GROUP_COLLABORATION"
//   }
// ]

// [
//   {
//     "label": "浙江地区",
//     "value": "ZJ_AREA"
//   },
//   {
//     "label": "一类地区",
//     "value": "ONE_AREA"
//   },
//   {
//     "label": "二类地区",
//     "value": "TWO_AREA"
//   }
// ]
