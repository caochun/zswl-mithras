import { FounderSelect, OrgSelect } from '@/components/Select'
import { InputColumn, DateColumn, MatchOptionColumn, FiledFormat } from '@/components/Format'
import { history } from '@zswl/admin'
import { App } from '@zswl/components'

const INIT_FORMAT = 1
const ALL_COLUMNS = ({ onClose }) => [
  InputColumn({
    title: '查询编号',
    dataIndex: 'creditCode',
    width: 140,
    search: true,
    actions: ({ creditCode: name, creditReportId, id }) => {
      return [
        {
          name,
          onClick: () => {
            onClose?.()
            history.push(`/creditManage/search/detail/${creditReportId ?? id}`)
          },
        },
      ]
    },
    fixed: 'left',
  }),
  // InputColumn({ title: '借据编号', dataIndex: 'loanNumber' }),
  InputColumn({
    title: '客户名称',
    dataIndex: 'clientName',
    width: 220,
    search: true,
    render: (val, { clientNameList }) => <FiledFormat value={clientNameList?.join(',') || '-'} />,
    excelRender: (val, { clientNameList }) => clientNameList?.join(',') || '-',
  }),
  InputColumn({
    title: '统一社会信用代码',
    dataIndex: 'cscCode',
    width: 220,
    search: true,
    render: (val, { cscCodeList }) => <FiledFormat value={cscCodeList?.join(',') || '-'} />,
    excelRender: (val, { cscCodeList }) => cscCodeList?.join(',') || '-',
  }),

  InputColumn({ title: '项目名称', dataIndex: 'projName', width: 160, search: true }),
  // InputColumn({ title: '查询原因', dataIndex: 'clientType', width: 160 }),
  MatchOptionColumn({
    title: '查询原因',
    dataIndex: 'selectGoal',
    width: 200,
    search: true,
    matchOption: 'searchGoalEnum',
    render: (val, { selectGoalList }) => (
      <FiledFormat
        value={
          selectGoalList?.map((v) => App.matchOption('searchGoalEnum', v).label).join(',') || '-'
        }
        fieldType="searchGoalEnum"
      />
    ),
  }),
  // InputColumn({
  //   title: '中征码',
  //   dataIndex: 'zhongZhengCode',
  //   width: 200,
  //   render: (val, { zhongZhengCodeList }) => <FiledFormat value={zhongZhengCodeList?.join(',')} />,
  // }),
  InputColumn({
    title: '申请人',
    dataIndex: 'applyUser',
    search: { element: <FounderSelect params={{ job: null }} /> },
    render: (val, value) => value?.applyUserName || '-',
    excelRender: (val, value) => value?.applyUserName || '-',
  }),
  InputColumn({
    title: '申请部门',
    dataIndex: 'applyOrg',
    search: { element: <OrgSelect params={{ type: null }} /> },
    render: (val, value) => value?.applyOrgName || '-',
    excelRender: (val, value) => value?.applyOrgName || '-',
  }),
  MatchOptionColumn({
    title: '申请状态',
    dataIndex: 'applyStatus',
    width: 160,
    matchOption: 'applyStatusEnum',
  }),
  DateColumn({
    title: '申请通过时间',
    dataIndex: 'applyTime',
    width: 180,
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
  }),
  MatchOptionColumn({
    title: '查询状态',
    dataIndex: 'selectStatus',
    width: 160,
    matchOption: 'searchStatusEnum',
  }),
  DateColumn({
    title: '查询完成时间',
    dataIndex: 'selectTime',
    width: 180,
    search: true,
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
  }),
  // InputColumn({ title: '项目编号', dataIndex: 'projCode', width: 160 }),
  // InputColumn({ title: '项目名称', dataIndex: 'projName', width: 200, search: true }),
  // InputColumn({ title: '查询版本', dataIndex: 'selectVersion', width: 180 }),

  // InputColumn({ title: '封装格式', dataIndex: 'reportFormat', width: 160 }),
]

export default ALL_COLUMNS
