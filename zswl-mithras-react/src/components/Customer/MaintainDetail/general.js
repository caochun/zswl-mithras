import { App } from '@zswl/components'
const {
  economyType,
  continuousStatus,
  genderType,
  clientType,
  marriageType,
  relationshipType,
  countryList,
  domesticOrAbroad = [],
} = App.getData().optionsType

const arrToObj = (arr = []) => {
  let obj = {}
  arr.forEach((item) => {
    obj[item.value] = item.label
  })
  return obj
}
//经济类型
const economyTypeList = arrToObj(economyType)
//存续状态
const continuousStatusList = arrToObj(continuousStatus)
//性别
const genderTypeList = arrToObj(genderType)
//客户分类
const clientTypeList = arrToObj(clientType)
const marriageTypeList = arrToObj(marriageType)
const relationshipTypeList = arrToObj(relationshipType)
const countryListObj = arrToObj(countryList)
const domesticOrAbroadObj = arrToObj(domesticOrAbroad)

const CommerceColums = {
  clientId: '客户id',
  clientName: '客户名称',
  clientCode: '客户编号',
  uscCode: '统一社会信用代码',
  tripleCertInOne: '是否三证合一',
  zhongZhengCode: '中征码',
  orgCode: '组织机构代码',
  bizLicenseCode: '营业执照号',
  continuousStatus: '存续状态',
  establishDate: '成立日期',
  approvalDate: '核准日期',
  bizLicenceLongTerm: '营业许可证是否为长期',
  bizLicenseEndDate: '营业许可证到期日',
  bizScope: '业务范围',
  industryType: '国标行业分类',
  industryTypeName: '国标行业分类',
  economyType: '经济类型',
  orgType: '组织机构类型',
  orgScale: '企业规模',
  registerCurrencyType: '注册币种',
  registerCapital: '注册资本',
  realCurrencyType: '实收币种',
  realCapital: '实收资本',
  registerCapitalRate: '注册资本到位率',
  corpRepresent: '法人代表',
  corpGender: '法人性别',
  corpCertType: '法人证件类型',
  corpCertCode: '法人证件号码',
  // listedCompany: '是否上市公司',
  enterpriseNature: '企业性质',
  existsHistory: '存在变更历史',
}
//资料清单类型
const materialList = {
  LEGAL_PERSON_ID_CARD: '法人代表身份证',
  BIZ_LICENSE: '营业执照',
  CORP_RULES_OR_CORRECTIONS: '公司章程及章程修正案',
  INTRODUCTIONS: '企业简介、主要股东和管理层简历',
  FINANCIAL_REPORT: '三年一期财报',
  OTHERS: '其他',
  ID_CARD: '身份证',
  HOUSEHOLD: '户口本',
  MARRIAGE_CERT: '结婚证',
  PERSONAL_CREDIT_REPORT: '个人信用报告',
  NAMED_ASSETS: '名下资产',
}
const shareholderTypeList = {
  LEGAL_PERSON: '法人',
  NORMAL_PERSON: '自然人',
  OTHER: '其他',
}
const addressTypeList = {
  REGISTRY_ADDRESS: '注册地址',
  WORK_ADDRESS: '办公地址',
}
const certTypeList = {
  10: '居民身份证',
  1: '户口簿',
  2: '护照',
  5: '港澳居民来往内地通行证',
  6: '台湾同胞来往内地通行证',
  8: '外国人居留证',
  9: '警官证',
  A: '香港身份证',
  B: '澳门身份证',
  C: '台湾身份证',
  X: '其他证件',
  20: '军人身份证件',
}

const currencyTypeList = {
  CNY: '人民币',
  EUR: '欧元',
  GBP: '英镑',
  JPY: '日元',
  USD: '美元',
  HKD: '港币',
}

const orgScaleTypeList = {
  TINY: '微型',
  SMALL: '小型',
  MIDDLE: '中型',
  BIG: '大型',
}

const orgTypeList = {
  9: '其他组织机构',
  1: '企业',
  A: '个体工商户',
  3: '机关',
  5: '事业单位',
  7: '社会团体',
}

const diffType = {
  ADD: '新增',
  MODIFY: '修改',
  DELETE: '删除',
}
export {
  CommerceColums,
  materialList,
  shareholderTypeList,
  certTypeList,
  currencyTypeList,
  orgScaleTypeList,
  orgTypeList,
  economyTypeList,
  continuousStatusList,
  genderTypeList,
  clientTypeList,
  marriageTypeList,
  relationshipTypeList,
  countryListObj,
  diffType,
  addressTypeList,
  domesticOrAbroadObj,
}
