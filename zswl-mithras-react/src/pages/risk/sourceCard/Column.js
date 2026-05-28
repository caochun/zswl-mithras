import { dateRangeTransform } from '@/utils/transform'
import { rangePresets, rules } from '@/utils'
import { FiledFormat, MatchOptionColumn, TextAreaColumn } from '@/components/Format'
import { FounderSelect } from '@/components'
import { DatePicker, Input } from 'antd'
import moment from 'moment'

const ALL_COLUMNS = [
  // 评分卡名称,适用风控行业分类,状态,更新时间
  {
    title: '评分卡名称',
    dataIndex: 'scorecardName',
    requiredMark: true,
    search: true,
    editable: {
      rules: [rules.required()],
    },
  },
  MatchOptionColumn({
    title: '适用风控行业分类',
    dataIndex: 'suitTrade',
    matchOption: 'riskControlIndustryClassify',
    requiredMark: true,
    editable: true,
    search: true,
  }),
  MatchOptionColumn({
    title: '状态',
    dataIndex: 'status',
    matchOption: 'riskControlAssertEnum',
    requiredMark: true,
    editable: true,
    search: true,
  }),
  MatchOptionColumn({
    title: '选择省内/省外',
    dataIndex: 'provinceSeat',
    matchOption: 'provinceTypeEnum',
    requiredMark: true,
    editable: true,
    search: true,
  }),

  {
    title: '适用年份',
    requiredMark: true,
    dataIndex: 'year',
    editable: (data) => ({
      initialValue: (data && moment(data)) || undefined,
      element: <DatePicker.YearPicker />,
      rules: [rules.required()],
      transform: (val) => val && val.format('YYYY'),
    }),
  },
  TextAreaColumn({
    title: '说明',
    dataIndex: 'content',
    width: 180,
  }),
  {
    title: '创建人',
    dataIndex: 'createByName',
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  {
    title: '创建时间',
    width: 200,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createDateFrom', 'createDateTo'),
    },
  },
  {
    title: '更新时间',
    width: 200,
    dataIndex: 'updateTime',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    search: {
      itemProps: {
        transform: (val) => dateRangeTransform(val, 'updateTimeFrom', 'updateTimeTo'),
      },
    },
  },
]
export default ALL_COLUMNS
