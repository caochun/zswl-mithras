import { dateRangeTransform } from '@/utils/transform'
import { rangePresets } from '@/utils'
import { AmountColumn, DateColumn, FiledFormat } from '@/components/Format'
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
  DateColumn({ title: '时间', dataIndex: 'month', width: 120 }),
  AmountColumn({ title: '一年期LPR(%)', dataIndex: 'oneYearLpr' }),
  AmountColumn({ title: '一年期 LPR 计价值', dataIndex: 'oneYearLprDiff' }),
  AmountColumn({ title: '五年期LPR(%)', dataIndex: 'fiveYearLpr' }),
  AmountColumn({ title: '五年期 LPR 计价值', dataIndex: 'fiveYearLprDiff' }),
  AmountColumn({ title: 'FTP 计价值', dataIndex: 'ftpPricing', width: 120 }),
  AmountColumn({ title: '当前值(%)', dataIndex: 'value', width: 120 }),
  AmountColumn({ title: '波动幅度(%)', dataIndex: 'fluctuationRange', width: 120 }),

  // 融资成本
  AmountColumn({ title: '一年期当月均值(%)', dataIndex: 'ONE_YEAR_currentAverage' }),
  AmountColumn({
    title: '一年期当年均值(%)',
    dataIndex: 'ONE_YEAR_annualAverage',
  }),
  AmountColumn({
    title: '1-3年期当月均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_currentAverage',
  }),
  AmountColumn({
    title: '1-3年期当年均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_annualAverage',
  }),

  AmountColumn({
    title: '3-5年期当月均值(%)',
    dataIndex: 'MORE_THAN_THREE_YEARS_currentAverage',
  }),
  AmountColumn({
    title: '3-5年期当年均值(%)',
    dataIndex: 'MORE_THAN_THREE_YEARS_annualAverage',
  }),
  AmountColumn({ title: '一年期季度均值(%)', dataIndex: 'ONE_YEAR_currentQuarterAverage' }),
  AmountColumn({
    title: '1-3年期当年均值(%)',
    dataIndex: 'ONE_TO_THREE_YEARS_annualAverage',
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
