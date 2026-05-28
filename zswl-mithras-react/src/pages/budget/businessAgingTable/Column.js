import { AmountColumn, DateColumn, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { dateRangeTransform } from '@/utils/transform'
import { rules, formatPercent, amountFormat, rangePresets } from '@/utils'

import { FounderSelect } from '@/components'

export const accountancyOrganizationNameOptions = [
  { label: '浙江浙商融资租赁有限公司', value: '10000396' },
]

const wrapItemProps = {
  inputConfig: {
    min: -Infinity,
  },
}
const itemProps = {
  required: true,
  rules: [{ required: true }],
}
const ALL_COLUMNS = [
  MatchOptionColumn({
    title: '核算组织名称',
    dataIndex: 'accountancyOrganizationNumber',
    matchOption: accountancyOrganizationNameOptions,
    itemProps,
  }),
  // DateColumn({ title: '账龄截止日', dataIndex: 'deadline' }),
  MatchOptionColumn({
    title: '状态',
    dataIndex: 'status',
    matchOption: 'financialAccountAgeRecordStatus',
  }),
  {
    title: '创建用户',
    dataIndex: 'createByName',
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  DateColumn({ title: '创建时间', width: 200, dataIndex: 'createTime' }),
  DateColumn({ title: '更新时间', width: 200, dataIndex: 'updateTime' }),

  AmountColumn({
    title: '期初款项原值',
    wrapItemProps,
    dataIndex: 'originalValueInitial',
    initFormat: 1,
    itemProps,
  }),
  AmountColumn({
    title: '本期增加额',
    dataIndex: 'originalValueIncrease',
    initFormat: 1,
    wrapItemProps,
    itemProps,
  }),
  AmountColumn({
    title: '本期减少额',
    dataIndex: 'originalValueReduce',
    initFormat: 1,
    wrapItemProps,
    itemProps,
  }),
  AmountColumn({
    title: '期末款项原值（余额）',
    dataIndex: 'originalValueFinal',
    initFormat: 1,
    wrapItemProps,
    itemProps,
  }),
  MatchOptionColumn({
    title: '币别',
    dataIndex: 'currency',
    itemProps,
    matchOption: 'currencyEnum',
  }),
  MatchOptionColumn({
    dataIndex: 'accountNumber',
    itemProps,
    title: '科目名称',
    matchOption: 'financialAccountNumberENUM',
  }),
  MatchOptionColumn({
    dataIndex: 'paymentContent',
    itemProps,
    title: '款项内容',
    matchOption: 'financialPaymentContentENUM',
  }),
  MatchOptionColumn({
    title: '苍穹状态',
    rename: '苍穹推送状态',
    dataIndex: 'sendStatus',
    matchOption: 'financialAccountAgeSendStatusStatus',
    itemProps,
  }),

  {
    title: '客商编码（苍穹）',
    dataIndex: 'customerUnitName',
    itemProps,
  },
  DateColumn({
    title: '业务日期',
    dataIndex: 'businessDate',
    itemProps,
  }),
  DateColumn({
    title: '账龄截止日',
    dataIndex: 'deadline',
    itemProps,
    disabled: true,
  }),
  AmountColumn({
    title: '账龄',
    dataIndex: 'businessAge',
    itemProps,
    disabled: true,
    initFormat: 1,
  }),
  {
    title: '合同编号',
    dataIndex: 'contractCode',
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
  },
  DateColumn({
    title: '合同逾期日期',
    dataIndex: 'planCollectionDate',
  }),
]
export default ALL_COLUMNS
