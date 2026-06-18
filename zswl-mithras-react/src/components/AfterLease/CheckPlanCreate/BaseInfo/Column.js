import { Input, DatePicker } from 'antd'
import moment from 'moment'

const { RangePicker } = DatePicker

const disabledDate = (current) => {
  return current && current < moment().endOf('day')
}

const ALL_COLUMNS = [
  {
    title: '计划类型',
    dataIndex: 'planType',
    requiredMark: true,
    matchOption: 'afterLeaseCheckPlanTypeEnum',
    editable: false,
  },
  {
    title: '计划名称',
    dataIndex: 'planName',
    requiredMark: true,
    editable: (record) => {
      return {
        element: <Input />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
  },
  {
    title: '检查填报时间',
    dataIndex: 'checkDate',
    requiredMark: true,
    editable: (record) => {
      return {
        element: <RangePicker allowClear disabledDate={disabledDate} />,
        rules: [{ required: true, message: '请选择' }],
        transform: (val) => {
          const [startDate, endDate] = val || []
          return {
            checkDate: undefined,
            startDate: startDate?.format('yyyy-MM-DD'),
            endDate: endDate?.format('yyyy-MM-DD'),
          }
        },
      }
    },
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    render: (value, record) => {
      const { checkStartDate, checkEndDate } = record
      return checkStartDate ? `${checkStartDate} ~ ${checkEndDate}` : '-'
    },
  },
  {
    title: '检查所属时间-季度',
    dataIndex: 'quarter',
    requiredMark: true,
    editable: (record) => {
      return {
        element: <DatePicker picker="quarter" style={{ width: '100%' }} />,
        rules: [{ required: true, message: '请选择' }],
        transform: (quarter) => {
          return {
            year: quarter && moment(quarter).year(),
            quarter: quarter && moment(quarter).quarter(),
          }
        },
      }
    },
    render: (value, record) => {
      const { year } = record
      const quarter = moment(value).quarter()
      return year ? `${year}年${quarter}季度` : '-'
    },
  },
  {
    title: '检查所属时间-其他',
    dataIndex: 'month',
    requiredMark: true,
    editable: (record) => {
      return {
        element: <DatePicker picker="month" style={{ width: '100%' }} />,
        rules: [{ required: true, message: '请选择' }],
        transform: (month) => {
          return {
            year: month && moment(month).year(),
            month: month && moment(month).month() + 1,
          }
        },
      }
    },
    render: (value, record) => {
      const { year } = record
      const month = moment(value).month() + 1
      return year ? `${year}年${month}月` : '-'
    },
  },
]
export default ALL_COLUMNS
