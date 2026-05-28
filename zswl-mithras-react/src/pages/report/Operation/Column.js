import { MatchOptionColumn, DateColumn, InputColumn } from '@/components/Format'
import { FounderSelect, OrgSelect } from '@/components/Select'
import { DatePicker } from 'antd'
import { formatQueryDate } from './utils'
import { orgSelect } from '@/pages/dashboard/workbench/Column'

const { RangePicker } = DatePicker

export const COMMON_COLUMNS = [
  MatchOptionColumn({
    title: '流程类型',
    dataIndex: 'processModelTypeList',
    mode: 'multiple',
    matchOption: 'yeWuYunXingFenXiProcessTypeList',
    search: true,
  }),
  orgSelect({
    title: '业务部门',
    dataIndex: 'bizDeptId',
    // mode: 'multiple',
  }),
  DateColumn({
    title: '时间',
    dataIndex: 'processStartDate',
    search: {
      element: <RangePicker allowClear={false} picker={'month'} />,
    },
    itemProps: {
      transform: (value) => formatQueryDate({ dateRange: value }),
    },
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'businessCategory',
    matchOption: 'businessGroupEnum',
    search: true,
  }),
  MatchOptionColumn({
    title: '流程状态',
    dataIndex: 'processStatus',
    matchOption: [
      { label: '审批中', value: 'RUNNING' },
      { label: '审批完成', value: 'FINISH' },
    ],
    search: true,
  }),
  MatchOptionColumn({
    title: '租赁类型',
    dataIndex: 'leaseType',
    matchOption: 'leaseType',
    // matchOption: [
    //   { label: '回租', value: 'hui_zu' },
    //   { label: '直租', value: 'zhi_zu' },
    // ],
    search: true,
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserId',
    search: {
      element: <FounderSelect />,
      functionCode: 'dashboardWorkbenchSelectFounder',
    },
    render: (value, { projSponsorUserName }) => (
      <FiledFormat title={projSponsorUserName}></FiledFormat>
    ),
  }),
  MatchOptionColumn({
    title: '岗位',
    dataIndex: 'job',
    matchOption: [
      { label: '运营经办', value: 'YYJB' },
      { label: '运营复核', value: 'YYFH' },
      { label: '运营负责人', value: 'YYFZR' },
    ],
    search: true,
  }),
]
