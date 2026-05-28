import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn, SelectColumn } from '@/components/Format'
import { App } from '@zswl/components'

const ALL_COLUMNS = [
  InputColumn({ title: '客户编号', dataIndex: 'clientCode', search: true }),
  InputColumn({ title: '客户名称', dataIndex: 'clientName', search: true }),
  InputColumn({ title: '模型名称', dataIndex: 'modelName', search: true, width: 200 }),
  { title: '评级结果', dataIndex: 'score', rename: '系统评级结果' },
  { title: '评级认定结果', dataIndex: 'finalScore', rename: '审查结果' },
  DateColumn({
    title: '发起时间',
    dataIndex: 'createTime',
    width: 200,
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    search: true,
  }),
  InputColumn({ title: '发起机构', dataIndex: 'startOrg' }),
  InputColumn({ title: '发起人', dataIndex: 'createByName' }),
  InputColumn({ title: '评级得分', dataIndex: 'score' }),
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
  SelectColumn({
    title: '评级类型',
    dataIndex: 'ratingType',
    options: [
      { label: '初评', value: '初评' },
      { label: '复评', value: '复评' },
    ],
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
  { title: '项目经理', dataIndex: 'createByName' },
  { title: '所属部门', dataIndex: 'startOrg' },

  { title: '历史评级模型名称', dataIndex: 'modelName' },
  { title: '历史评级结果', dataIndex: 'score', rename: '历史系统评级结果' },
  { title: '历史认定结果', dataIndex: 'finalScore', rename: '历史审查结果' },
  DateColumn({ title: '历史评级生效日期', dataIndex: 'effectTime' }),
  DateColumn({ title: '历史评级失效日期', dataIndex: 'abandonTime' }),
  { title: '历史评级模型编号', dataIndex: 'modelCode' },
  { title: '历史评级发起人', dataIndex: 'createByName' },
]
export default ALL_COLUMNS
