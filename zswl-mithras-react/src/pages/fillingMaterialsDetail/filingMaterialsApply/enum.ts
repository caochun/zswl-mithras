const TEMPLATE_LIST = [
  { name: '基础资料', value: 'BASIC_MATERIALS' },
  { name: '内部操作资料', value: 'INNER_OPERATION_MATERIALS' },
  { name: '合同资料', value: 'CONTRACT_MATERIALS' },
  { name: '租赁物资料', value: 'LEASEHOLD_MATERIALS' },
  { name: '抵质押资料', value: 'COLLATERALIZATION_MATERIALS' },
]
const REFERENCE_MATERIALS = 'REFERENCE_MATERIALS'
const OPERATIONAL_REVIEW = 'OPERATIONAL_REVIEW'

const tableEnum = {
  BASIC_INFORMATION: '基础资料',
  INNER_OPERATION_MATERIALS: 'FILING_BUSINESS_INNER_OPERATION',
  CONTRACT_MATERIALS: 'FILING_BUSINESS_PAYMENT',
  LEASEHOLD_MATERIALS: 'FILING_BUSINESS_LEASEHOLD',
  COLLATERALIZATION_MATERIALS: 'FILING_BUSINESS_COLLATERALIZATION',
}

const enumType = [
  { label: '基础资料', value: 'BASIC_INFORMATION' },
  {
    label: '基础信息-承租人',
    value: 'FILING_BUSINESS_LESSEE_CLIENT',
  },
  {
    label: '基础信息-非承租人客户类型法人',
    value: 'FILING_BUSINESS_ENT_CLIENT',
  },
  {
    label: '基础信息-非承租人客户类型自然人',
    value: 'FILING_BUSINESS_IND_CLIENT',
  },
  {
    label: '内部操作资料',
    value: 'FILING_BUSINESS_INNER_OPERATION',
  },
  {
    label: '合同资料',
    value: 'FILING_BUSINESS_PAYMENT',
  },
  {
    label: '租赁物资料',
    value: 'FILING_BUSINESS_LEASEHOLD',
  },
  {
    label: '抵质押资料',
    value: 'FILING_BUSINESS_COLLATERALIZATION',
  },
]
export { TEMPLATE_LIST, REFERENCE_MATERIALS, OPERATIONAL_REVIEW, enumType, tableEnum }
