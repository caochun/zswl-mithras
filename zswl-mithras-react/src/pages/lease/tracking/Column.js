import {
  AmountColumn,
  DateColumn,
  FiledFormat,
  InputColumn,
  MatchOptionColumn,
  SelectColumn,
  TextAreaColumn,
} from '@/components/Format'
import { Input, InputNumber } from 'antd'
import common from '@/api/groupCredit/common'
import contractApi from '@/pages/contract/list/api'
import trackingApi from '@/api/lease/trackingApi'
import { App } from '@zswl/components'

const taskStatusEnum = [
  { label: '生效', value: true },
  { label: '关闭', value: false },
]
const ALL_COLUMNS = [
  InputColumn({
    title: '任务名称',
    dataIndex: 'taskName',
    requiredMark: true,
    search: true,
    editable: {
      rules: [{ required: true, message: '任务名称不能为空' }],
    },
  }),
  MatchOptionColumn({
    title: '任务类型',
    dataIndex: 'taskType',
    matchOption: 'trackTaskTypeEnum',
    search: true,
    editable: true,
    requiredMark: true,
  }),
  MatchOptionColumn({
    title: '任务状态',
    dataIndex: 'taskStatus',
    matchOption: taskStatusEnum,
    search: true,
  }),
  SelectColumn({
    title: '提出人',
    dataIndex: 'createBy',
    search: true,
    options: trackingApi.getTrackEventQueryProcessor,
    fieldNames: {
      label: 'name',
      value: 'id',
    },
    render: (val, { createByName }) => <FiledFormat value={createByName} />,
  }),
  DateColumn({ title: '计划日期', dataIndex: 'planTime', editable: true }),
  {
    title: '起租后X自然日',
    dataIndex: 'startRentAfterDay',
    editable: {
      element: <InputNumber min={0} precision={0} />,
    },
  },
  SelectColumn({
    title: '处理人',
    dataIndex: 'processorId',
    search: true,
    editable: true,
    requiredMark: true,

    options: trackingApi.getTrackEventQueryProcessor,
    fieldNames: {
      label: 'name',
      value: 'id',
    },
    render: (val, { processor }) => <FiledFormat value={processor} />,
  }),
  InputColumn({ title: '处理人岗位', dataIndex: 'processorDept' }),
  MatchOptionColumn({
    title: '提醒频率',
    dataIndex: 'remindFrequency',
    matchOption: 'trackFrequencyEnum',
    editable: true,
    requiredMark: true,
  }),
  SelectColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
    search: true,
    editable: true,
    width: 260,
    options: async () => {
      const res = await trackingApi.getTrackEventContractCodeList({})
      return res.map((v) => ({ label: v, value: v }))
    },
  }),
  SelectColumn({
    title: '客户名称',
    dataIndex: 'clientId',
    editable: true,
    search: true,
    options: async () => {
      const res = await common.getClientList(
        { pageSize: 5000 },
        { functionCode: 'clientlist-trackevent' }
      )

      return res.list.map((v) => ({ label: v.clientName, value: v.id }))
    },
    render: (val, { clientName }) => <FiledFormat value={clientName} />,
  }),
  AmountColumn({
    title: '剩余可用额度(元)',
    dataIndex: 'remainAvailableQuota',
    formTooltip:
      '剩余可用额度=项目授信金额 - sum合同金额 + if（额度可循环，sum（已核销的本金、首期租金），0）',
  }),

  SelectColumn({
    title: '项目名称',
    dataIndex: 'projName',
    editable: true,
    search: true,
    options: async () => {
      const res = await contractApi.postProjList(
        { pageSize: 1000 },
        'contractreviewquery_trackevent'
      )
      return res.map(({ projName }) => ({ label: projName, value: projName }))
    },
    width: 180,
  }),
  InputColumn({ title: '项目编号', dataIndex: 'projCode', width: 150 }),
  DateColumn({
    title: '创建时间',
    dataIndex: 'createTime',
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
    width: 180,
  }),
  TextAreaColumn({
    title: '任务内容',
    dataIndex: 'taskContent',
    editable: true,
    required: true,
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  }),
  MatchOptionColumn({
    title: '租赁类型',
    dataIndex: 'leaseType',
    matchOption: 'leaseType',
    render: (val) => val?.map((v) => App.matchOption('leaseType', v).label).join(','),
  }),
  MatchOptionColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassify',
    matchOption: 'riskControlIndustryClassify',
  }),
  MatchOptionColumn({
    title: '项目分类',
    dataIndex: 'projItem',
    matchOption: 'projectClassify',
  }),
  MatchOptionColumn({
    title: '项目来源',
    dataIndex: 'projSource',
    matchOption: 'projSourceType',
  }),
  InputColumn({
    title: '资金用途',
    dataIndex: 'fundsPurpose',
  }),
  {
    title: '项目背景',
    dataIndex: 'projBackground',
    span: 2,
    render: (val) => <FiledFormat title={val} hasToolTip={false} />,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    span: 2,
    editable: {
      element: <Input.TextArea />,
    },
  },
  {
    title: '项目主办',
    dataIndex: 'projSponsorUserName',
  },
  {
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  },
  InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptName',
  }),
  InputColumn({
    title: '业务部门负责人',
    dataIndex: 'bizDeptLeaderName',
  }),
  InputColumn({
    title: '业务分管领导',
    dataIndex: 'bizDivisionLeaderName',
  }),
  InputColumn({
    title: '转让方',
    dataIndex: 'assignor',
    render: (val) => <FiledFormat title={val} />,
  }),
  AmountColumn({
    title: '本次申请限额(元)',
    dataIndex: 'remainAvailableQuota',
    formTooltip: '项目授信金额-存量合同金额的发生额或余额（根据项目额度是否可循环判断）',
  }),
  MatchOptionColumn({
    title: '保理类型',
    dataIndex: 'factoringType',
    requiredMark: true,
    matchOption: 'factoringType',
  }),
  MatchOptionColumn({
    title: '转让类型',
    requiredMark: true,
    dataIndex: 'zrType',
    matchOption: 'zrType',
  }),
]
export default ALL_COLUMNS
