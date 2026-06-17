import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import { Tag } from 'antd'

export const statusRender = (val, record) => {
  const { label, color } = App.matchOption('budgetCollectionStatusEnum', val)
  return <Tag color={color}>{label}</Tag>
}
const ALL_COLUMNS = [
  {
    title: '计划名称',
    dataIndex: 'budgetPlanName',
    width: 200,
  },

  DateColumn({
    title: '预算区间',
    dataIndex: 'budgetPeriod',
    width: 220,
    render: (_, record) => {
      return `${record.budgetDateFrom} - ${record.budgetDateTo}`
    },
  }),
  DateColumn({
    title: '填报区间',
    dataIndex: 'writeDate',
    width: 220,
    render: (_, record) => {
      return `${record.writeDateFrom} - ${record.writeDateTo}`
    },
  }),
  MatchOptionColumn({
    title: '预算类型',
    dataIndex: 'budgetType',
    width: 120,
    matchOption: 'budgetPlanTypeEnum',
  }),
  {
    title: '状态',
    dataIndex: 'budgetStatus',
    width: 120,
    render: statusRender,
  },
  MatchOptionColumn({
    title: '是否收集',
    dataIndex: 'needCollect',
    width: 120,
    matchOption: 'yesOrNo',
  }),
  DateColumn({
    title: '收集截止时间',
    dataIndex: 'collectDateTo',
    width: 200,
  }),
  InputColumn({
    title: '创建人',
    dataIndex: 'createUserId',
    nameKey: 'createUserName',
  }),
  DateColumn({
    title: '创建时间',
    dataIndex: 'createTime',
    search: true,
  }),
  DateColumn({
    title: '更新时间',
    dataIndex: 'updateTime',
  }),
]

export default ALL_COLUMNS
