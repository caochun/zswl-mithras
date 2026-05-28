import { DateColumn, MatchOptionColumn } from '@/components/Format'
import { App, Select } from '@zswl/components'

export const COMMON_COLUMNS = ({ listType, handleDetail }) => [
  MatchOptionColumn({
    title: '报表名称',
    dataIndex: 'reportCategoryCode',
    matchOption: 'associationReportCategoryEnum',
    actions: (record) => [
      {
        name: App.matchOption('associationReportCategoryEnum', record.reportCategoryCode).label,
        onClick: () => handleDetail?.(record),
      },
    ],
  }),
  MatchOptionColumn({
    title: '报送状态',
    dataIndex: 'reportStatus',
    matchOption: 'associationReportStatusEnum',
  }),
  {
    title: '是否为重报数据',
    dataIndex: 'isRetry',
    render: (text) => (text === 1 ? '是' : '否'),
  },
  {
    title: '年份',
    dataIndex: 'reportYear',
  },
  {
    title: '报表周期',
    dataIndex: 'reportPeriod',
    render: (text, { reportPeriodCategory }) => {
      if (reportPeriodCategory === 'REALTIME') return '-'
      const isQuarter = reportPeriodCategory === 'QUARTER'
      return isQuarter ? `${text}季度` : `${text}月`
    },
  },
  {
    title: '批次号',
    dataIndex: 'batchNo',
  },
  MatchOptionColumn({
    title: '周期类型',
    dataIndex: 'reportPeriodCategory',
    matchOption: 'associationReportPeriodCategoryEnum',
  }),
  {
    title: '创建方式',
    dataIndex: 'dataSource',
  },
  MatchOptionColumn({
    title: '流程状态',
    dataIndex: 'processStatus',
    matchOption: 'associationProcessStatus',
    rename: '数据流程状态',
  }),
  MatchOptionColumn({
    title: '上报流程状态',
    dataIndex: 'pushProcessStatus',
    matchOption: 'associationProcessStatus',
  }),
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  DateColumn({
    title: '上报日期',
    dataIndex: 'reportTime',
    search: listType === 'finish',
  }),
]
