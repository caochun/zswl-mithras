import { DateColumn, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { FounderSelect } from '@/components'
import moment from 'moment'
import { DatePicker } from 'antd'

const ALL_COLUMNS = [
  {
    title: '创建人',
    dataIndex: 'createByName',
    render: (val, record) => <FiledFormat title={record.createByName} />,
    search: {
      element: <FounderSelect params={{ job: 'financialofficer' }} />,
    },
  },
  MatchOptionColumn({ title: '状态', dataIndex: 'ftpStatus', matchOption: 'recordStatus' }),
  DateColumn({ title: '生效时间', dataIndex: 'effectTime', search: true }),
  DateColumn({ title: '创建时间', dataIndex: 'createTime', search: true }),
  {
    title: '时间',
    dataIndex: 'month',
    search: {
      itemProps: {
        transform: (val) => {
          return val && moment(val).format('yyyy-MM-01')
        },
      },

      element: <DatePicker.MonthPicker />,
    },
    render: (val) => moment(val).format('yyyy年MM月'),
  },
  MatchOptionColumn({ title: '审批状态', dataIndex: 'ftpProcessStatus', search: true }),
]
export default ALL_COLUMNS
