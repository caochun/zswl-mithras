import {
  AmountColumn,
  DateColumn,
  InputColumn,
  MatchOptionColumn,
  TextAreaColumn,
} from '@/components/Format'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'

const ALL_COLUMNS = [
  InputColumn({ title: '项目编号', dataIndex: 'projCode' }),
  InputColumn({ title: '项目名称', dataIndex: 'projName' }),
  InputColumn({ title: '承租人', dataIndex: 'clientName' }),
  {
    title: '租赁类型',
    dataIndex: 'leaseTypes',
    render: (val) => {
      const leaseTypes = val ? JSON.parse(val) : []
      return leaseTypes.map((v) => App.matchOption('leaseType', v)?.label)?.join('、')
    },
  },
  InputColumn({ title: '评估主体名称', dataIndex: 'evaluationSubjectName' }),
  InputColumn({ title: '评估主体区域', dataIndex: 'evaluationSubjectAreaName' }),
  AmountColumn({ title: '评估主体营业收入', dataIndex: 'evaluationSubjectOperatingIncome' }),
  MatchOptionColumn({
    title: '地区分类',
    dataIndex: 'regionalProjectClassify',
    matchOption: 'projRegionalClassify',
  }),
  MatchOptionColumn({ title: '行业分类', dataIndex: 'projectClassify' }),
  TextAreaColumn({ title: '资金用途', dataIndex: 'fundsPurpose' }),
  MatchOptionColumn({
    title: '流程状态',
    dataIndex: 'processStatus',
    matchOption: 'commonProcessStatus',
    render: (val) => (val ? App.matchOption('commonProcessStatus', val).label : '未提交'),
    search: true,
  }),
  MatchOptionColumn({
    title: '评级状态',
    dataIndex: 'ratingStatus',
    matchOption: 'customerRatStatus',
    search: true,
  }),

  { title: '客户名称', dataIndex: 'clientName' },
  { title: '客户编号', dataIndex: 'clientCode' },
  DateColumn({ title: '成立日期', dataIndex: 'establishDate' }),
  DateColumn({ title: '失效日期', dataIndex: 'abandonTime' }),
  DateColumn({ title: '生效日期', dataIndex: 'effectTime' }),

  AmountColumn({ title: '注册资本', dataIndex: 'registerCapital' }),
  { title: '行业分类', dataIndex: 'industryTypeName' },
  MatchOptionColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassify',
    matchOption: 'riskControlIndustryClassify',
  }),
  { title: '所在省份', dataIndex: 'province' },
  { title: '所在城市', dataIndex: 'city' },
  { title: '所在区县', dataIndex: 'district' },
  { title: '业务范围', dataIndex: 'bizScope' },

  { title: '历史评级模型名称', dataIndex: 'modelName' },
  {
    title: '历史项目限额',
    dataIndex: 'projQuota',
    render: (val) => {
      const title = val ? val + '万元' : '-'
      return <Tooltip title={title}>{title}</Tooltip>
    },
  },
  DateColumn({ title: '历史评级生效日期', dataIndex: 'effectTime' }),
  DateColumn({ title: '历史评级失效日期', dataIndex: 'abandonTime' }),
  { title: '历史评级模型编号', dataIndex: 'modelCode' },
  { title: '历史评级发起人', dataIndex: 'createByName' },
]
export default ALL_COLUMNS
