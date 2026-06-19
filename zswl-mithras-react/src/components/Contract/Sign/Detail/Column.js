import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'
import { App } from '@zswl/components'

const matchSelect = (optionName, value) => {
  return App.matchOption(optionName, value)?.label
}

// 类型类型  ……
export const bizRender = (val, record) => {
  const {
    bizType,
    factoringType,
    zrType,
    zrTypes,
    leaseType,
    leaseTypes,
    bizTypeCode,
    leaseTypeCode,
  } = record

  const bizTypeVal = bizType?.value ?? bizType ?? bizTypeCode
  const bizName = matchSelect('projEstablishBizType', bizTypeVal)
  const lesseeInfo =
    leaseTypes?.value?.map((v) => matchSelect('leaseType', v)).join('、') ??
    matchSelect('leaseType', leaseType, '') ??
    matchSelect('leaseType', leaseTypeCode, '')
  const factoringName = matchSelect('factoringType', factoringType?.value ?? factoringType, '')
  const zrName =
    zrTypes?.value?.map((v) => matchSelect('zrType', v, '')).join('、') ??
    matchSelect('zrType', zrType?.value ?? zrType, '')
  if (['ZL', 'ZZ'].includes(bizTypeVal)) return lesseeInfo ? `${bizName}-${lesseeInfo}` : bizName
  if (bizTypeVal === 'BL') return factoringName ? `${bizName}-${factoringName}` : bizName
  if (bizTypeVal === 'ZR') return zrName ? `${bizName}-${zrName}` : bizName
}

export const ALL_COLUMNS = [
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '类别',
    dataIndex: 'projCode',
    render: bizRender,
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  }),
  InputColumn({
    title: '客户名称',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptName',
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserName',
  }),
  AmountColumn({
    title: '合同金额(元)',
    dataIndex: 'applyCreditAmount',
  }),
  AmountColumn({
    title: '首期租金(元)',
    dataIndex: 'downPayment',
  }),
  AmountColumn({
    title: '保证金(元)',
    dataIndex: 'earnestMoney',
  }),
  AmountColumn({
    title: '服务费/咨询费(元)',
    dataIndex: 'consultingFee',
  }),
  AmountColumn({
    title: '租赁-手续费(元)',
    dataIndex: 'commission',
  }),
  AmountColumn({
    title: '手续费(元)',
    dataIndex: 'consultingFee',
  }),
  AmountColumn({
    title: '名义价款(元)',
    dataIndex: 'nominalPrice',
  }),
]
