import { dateRangeTransform } from '@/utils/transform'
import { rangePresets } from '@/utils'
import { AmountColumn, FiledFormat, InputColumn } from '@/components/Format'
import { FounderSelect } from '@/components/Select'

const ALL_COLUMNS = [
  { title: '机构名称', dataIndex: 'organizationName', fixed: 'left', search: true },
  {
    title: '创建人',
    dataIndex: 'createByName',
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  {
    title: '创建日期',
    width: 200,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createDateFrom', 'createDateTo'),
    },
  },
  { title: '更新日期', width: 200, dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
  InputColumn({ title: '时间', dataIndex: 'date', width: 120 }),

  AmountColumn({ title: '每日值', dataIndex: 'value', width: 120, precision: 4, suffix: '%' }),

  // 融资成本
  AmountColumn({ title: '一年期当月均值(%)', dataIndex: 'ONE_YEAR_currentAverage' }),
  AmountColumn({
    title: '一年期当年均值(%)',
    dataIndex: 'ONE_YEAR_annualAverage',
  }),
  AmountColumn({ title: '一年期季度均值(%)', dataIndex: 'ONE_YEAR_currentQuarterAverage' }),
  AmountColumn({
    title: '1-3年期当月均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_currentAverage',
  }),
  AmountColumn({
    title: '1-3年期当年均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_annualAverage',
  }),
  AmountColumn({
    title: '1-3年期季度均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_currentQuarterAverage',
  }),
  AmountColumn({
    title: '3-5年期当月均值(%)',
    dataIndex: 'MORE_THAN_THREE_YEARS_currentAverage',
  }),
  AmountColumn({
    title: '3-5年期当年均值(%)',
    dataIndex: 'MORE_THAN_THREE_YEARS_annualAverage',
  }),
  AmountColumn({
    title: '3-5年期季度均值(%)',
    dataIndex: 'MORE_THAN_THREE_YEARS_currentQuarterAverage',
  }),
  //担保成本
  AmountColumn({
    title: '当期均值(%)',
    dataIndex: 'currentAverage',
    width: 200,
  }),
]
export default ALL_COLUMNS
