import { hasValue } from '@/utils/base'

export const formulaData = (value) => {
  if (hasValue(value) && String(value).indexOf('=') === 0) {
    return {
      value: value.slice(1),
      configValueType: 'FORMULA',
    }
  }
  return {
    value,
    configValueType: 'VALUE',
  }
}

// 后端维护 kpiProjectWeightTypeEnum
export const AllocateEnum = [
  { label: '业务部门', value: 'BUSINESS_DEPT' },
  { label: '项目主办', value: 'PROJECT_SPONSOR' },
  { label: '项目协办', value: 'PROJECT_COSPONSOR' },
  { label: '跨部门推荐人', value: 'OTHER_DEPT_RECOMMEND' },
]

export const TagColor = {
  BUSINESS_DEPT: '#2db7f5',
  PROJECT_SPONSOR: '#f50',
  PROJECT_COSPONSOR: '#108ee9',
  OTHER_DEPT_RECOMMEND: '#87d068',
}

export const AllocateTypeInfo = {
  BUSINESS_DEPT: {
    allocateLabel: '业务部门',
    allocatePlaceHold: '业务部门',
    ratioLable: '部门池',
  },
  PROJECT_SPONSOR: {
    allocateLabel: '项目主办',
    allocatePlaceHold: '项目经理',
    ratioLable: '主办占比',
  },
  PROJECT_COSPONSOR: {
    allocateLabel: '项目协办',
    allocatePlaceHold: '项目经理',
    ratioLable: '协办占比',
  },
  OTHER_DEPT_RECOMMEND: {
    allocateLabel: '跨部门推荐人',
    allocatePlaceHold: '项目经理',
    ratioLable: '推荐人占比',
  },
}
