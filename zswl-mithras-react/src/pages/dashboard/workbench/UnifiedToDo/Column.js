import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const PRECESS_COLUMNS = [
  {
    title: '流程ID',
    dataIndex: 'processInstanceId',
    width: 100,
    fixed: 'left',
  },
  InputColumn({
    title: '流程类型',
    dataIndex: 'processModelType',
  }),
  InputColumn({
    title: '表单名称',
    dataIndex: 'processName',
  }),
  InputColumn({
    title: '流程到达时间',
    dataIndex: 'taskCreateTime',
  }),
  InputColumn({
    title: '发起人',
    dataIndex: 'startUserName',
  }),
  InputColumn({
    title: '申请部门',
    dataIndex: 'startUserDeptName',
  }),
  InputColumn({
    title: '申请时间',
    dataIndex: 'processStartTime',
  }),
  InputColumn({
    title: '客户名称',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'processStatus',
    matchOption: 'processStatus',
  }),
  InputColumn({
    title: '当前节点',
    dataIndex: 'curTaskNames',
  }),
  InputColumn({
    title: '当前审批人',
    dataIndex: 'curAssigneeNames',
  }),
  InputColumn({
    title: '流程开始时间',
    dataIndex: 'processStartTime',
  }),
  InputColumn({
    title: '流程结束时间',
    dataIndex: 'processEndTime',
  }),
]
