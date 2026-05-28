// import { MatchOptionColumn } from '@/components/Format'
import { MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'

const ALL_COLUMNS = [
  { title: '规则编号', dataIndex: 'ruleNumber', width: 160 },
  { title: '规则名称', dataIndex: 'ruleName', width: 200 },
  MatchOptionColumn({
    title: '规则状态',
    dataIndex: 'status',
    matchOption: 'opinionEnableStatus',
  }),
  MatchOptionColumn({
    title: '黑灰标识',
    dataIndex: 'blackGrayType',
    matchOption: 'blackGrayTypeEnum',
  }),
  {
    title: '适用业务',
    dataIndex: 'suitBusiness',
    width: 180,
    render: (val) => {
      const title = val
        ?.map((v) => App.matchOption('blackGrayBusinessTypeEnum', v).label)
        .join('、')
      return <Tooltip title={title}>{title}</Tooltip>
    },
  },
  {
    title: '适用机构',
    dataIndex: 'suitOrg',
    width: 200,
    render: (val) => {
      const title = val?.map((v) => App.matchOption('blackGrayOrgEnum', v).label).join('、')
      return <Tooltip title={title}>{title}</Tooltip>
    },
  },
  { title: '更新时间', dataIndex: 'updateTime', width: 160 },
]
export default ALL_COLUMNS
