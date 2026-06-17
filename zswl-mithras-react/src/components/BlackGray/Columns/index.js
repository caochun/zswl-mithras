import {
  AmountColumn,
  DataUploadColumn,
  DateColumn,
  InputColumn,
  MatchOptionColumn,
  TextAreaColumn,
} from '@/components/Format'
import { options } from '@/utils'
import { DatePicker, Tooltip } from 'antd'
import moment from 'moment'
import { App, Select } from '@zswl/components'
import { TimeOutFormat } from '@/components/Format/base'

const { periodUnderObservation } = options

const ALl_COLUMNS = [
  InputColumn({
    title: '企业名称',
    dataIndex: 'enterpriseName',
    width: 260,
    search: true,
  }),
  InputColumn({
    title: '集团名称',
    dataIndex: 'groupName',
    width: 200,
  }),
  InputColumn({
    title: '下属企业在库数',
    dataIndex: 'entCount',
    width: 120,
  }),
  MatchOptionColumn({
    title: '是否报送金控',
    dataIndex: 'reportFlag',
    matchOption: 'yesOrNo',
    width: 120,
  }),
  // 任务编号, 所属机构, 创建时间, 更新时间
  InputColumn({ title: '任务编号', dataIndex: 'taskNum', width: 220 }),
  MatchOptionColumn({
    title: '所属机构',
    dataIndex: 'orgCode',
    matchOption: 'blackGrayOrgEnum',
    search: {
      element: <Select options={'blackGrayOrgEnum'} />,
    },
    width: 140,
  }),
  DateColumn({
    title: '创建时间',
    dataIndex: 'gmtCreate',
    dateFormat: 'yyyy-MM-DD HH:mm',
    width: 180,
  }),
  DateColumn({
    title: '更新时间',
    dataIndex: 'gmtUpdate',
    dateFormat: 'yyyy-MM-DD HH:mm',
    width: 180,
  }),
  InputColumn({
    title: '统一社会信用代码',
    dataIndex: 'unifiedSocialCreditCode',
    width: 200,
    search: true,
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'businessType',
    matchOption: 'blackGrayBusinessTypeEnum',
    search: true,
    width: 100,
  }),
  MatchOptionColumn({
    title: '黑灰标识',
    dataIndex: 'blackGrayType',
    matchOption: 'blackGrayTypeEnum',
    search: true,
    width: 100,
  }),
  MatchOptionColumn({
    title: '所属集团黑灰标识',
    dataIndex: 'groupBlackGrayType',
    matchOption: 'blackGrayTypeEnum',
    width: 130,
  }),
  MatchOptionColumn({
    title: '将集团纳入黑名单',
    dataIndex: 'blacklistStatus',
    formTooltip: '选择是否将集团纳入黑名单，如纳入则其下属公司将一并纳入黑名单',
    matchOption: 'yesOrNo',
  }),

  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'auditStatus',
    matchOption: 'auditStatusEnum',
    render: (val) => App.matchOption('auditStatusEnum', `${val}`)?.label ?? '-',
    width: 120,
  }),
  MatchOptionColumn({
    title: '观察期',
    dataIndex: 'periodUnderObservation',
    matchOption: periodUnderObservation,
    render: (val, { blackGrayType }) => {
      const value = blackGrayType === 'GRAY_LIST' ? '6' : '12'
      return App.matchOption(periodUnderObservation, value).label
    },
  }),

  InputColumn({ title: '所属集团', dataIndex: 'membershipGroup', width: 150 }),
  DateColumn({
    title: '申请时间',
    rename: '申请日期',
    dataIndex: 'applyTime',
    search: true,
    width: 120,
  }),
  MatchOptionColumn({ title: '申请人', dataIndex: 'createByCode', matchOption: 'userList' }),
  MatchOptionColumn({
    title: '报告机构',
    dataIndex: 'applyOrganization',
    matchOption: 'blackGrayOrgEnum',
    width: 150,
  }),
  MatchOptionColumn({
    title: '申请机构',
    dataIndex: 'warehouseOrganization',
    matchOption: 'blackGrayOrgEnum',
    width: 150,
  }),
  InputColumn({ title: '所属部门', dataIndex: 'applyDept', width: 200 }),
  AmountColumn({ title: '业务规模（万元）', dataIndex: 'riskScale', width: 140, initFormat: 1 }),
  DateColumn({
    title: '入库时间',
    dataIndex: 'warehouseTime',
    rename: '入库日期',
    search: true,
    width: 130,
  }),

  DateColumn({
    title: '计划出库时间',
    rename: '计划出库日期',
    dataIndex: 'planOutboundTime',
    search: true,
    width: 140,
  }),
  {
    title: '申请原因',
    dataIndex: 'applyReasonType',
    render: (val, { applyReasonName }) => {
      const title = Array.isArray(applyReasonName) ? applyReasonName?.join('、') : applyReasonName
      return <Tooltip title={title}>{title}</Tooltip>
    },
  },

  DataUploadColumn({ title: '申请原因附件', dataIndex: 'warehouseFileKeys' }),
  TextAreaColumn({ title: '申请原因描述', dataIndex: 'applyReason' }),
  // MatchOptionColumn({
  //   title: '当前处理人',
  //   dataIndex: 'currentOperator',
  //   matchOption: 'userList',
  //   search: true,
  //   width: 120,
  // }),
  MatchOptionColumn({
    title: '入库方式',
    dataIndex: 'source',
    matchOption: 'blackGraySourceEnum',
    width: 120,
  }),

  DateColumn({
    title: '报送截止日',
    dataIndex: 'deadline',
    render: (val, record) => {
      return record.taskType === 'NOT_TIMED' ? '-' : val
    },
  }),

  {
    title: '数据时点',
    dataIndex: 'timePoint',
    dateFormat: 'YYYY-MM',
    search: {
      element: <DatePicker.MonthPicker></DatePicker.MonthPicker>,
      itemProps: {
        transform: (val) => {
          return {
            ['timePoint']: val && moment(val).format(`YYYY-MM`),
          }
        },
      },
    },
  },
  MatchOptionColumn({
    title: '名单来源',
    dataIndex: 'source',
    matchOption: 'blackGraySourceEnum',
    width: 130,
  }),
  {
    title: '是否超时',
    dataIndex: 'overtimeFlag',
    render: (val, record) => {
      return record.taskType === 'NOT_TIMED' ? '-' : <TimeOutFormat value={val ? 1 : 0} />
    },
    width: 100,
  },
  MatchOptionColumn({
    title: '任务类型',
    dataIndex: 'taskType',
    matchOption: 'blackGrayBusinessTaskTypeEnum',
    width: 100,
  }),

  {
    // title: '下级任务进度',
    dataIndex: 'subTaskCount',
    width: 110,
    render: (val) => (
      <Format.ProgressLine
        finishedCount={val?.finishedTaskCount || 0}
        totalCount={val?.totalTaskCount || 0}
      />
    ),
  },
  {
    title: '以下字段校验不通过',
    dataIndex: 'errorFields',
    render: (val) => (val ?? []).join(','),
  },
]

export default ALl_COLUMNS
