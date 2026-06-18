import { MatchOptionColumn, DateColumn } from '@/components/Format'
import { DatePicker } from 'antd'
import { formatQueryDate } from '@/dashboard/DashboardUtilsOperation'
import { orgSelect } from '@/dashboard/DashboardUtilsColumns'

const { RangePicker } = DatePicker

export const COMMON_COLUMNS = [
  orgSelect({
    title: '业务部门',
    dataIndex: 'bizDeptIdList',
    mode: 'multiple',
  }),
  DateColumn({
    title: '时间区间',
    dataIndex: 'queryDate',
    search: {
      element: <RangePicker picker={'month'} allowClear={false} />,
    },
    itemProps: {
      transform: (value) => formatQueryDate({ dateRange: value }),
    },
  }),
  MatchOptionColumn({
    title: '业务组类别',
    dataIndex: 'type',
    matchOption: 'businessGroupEnum',
    search: true,
  }),
  MatchOptionColumn({
    title: '项目阶段',
    dataIndex: 'projStage',
    matchOption: 'dashboardProjStageEnum',
    search: true,
    allowClear: false,
  }),
]
