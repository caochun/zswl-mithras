import { InputColumn, AmountColumn, MatchOptionColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  // InputColumn({
  //   title: '流程模型类型',
  //   dataIndex: 'processModelType',
  // }),
  InputColumn({
    title: '项目阶段',
    dataIndex: 'projectStage',
  }),
  InputColumn({
    title: '流程ID',
    dataIndex: 'processInstanceId',
  }),
  InputColumn({
    title: '租赁类型',
    dataIndex: 'leaseTypesDisplay',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'businessCategory',
  }),
  InputColumn({
    title: '项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  AmountColumn({
    title: '项目金额(万元)',
    dataIndex: 'projAmount',
    initFormat: 10000 * 10000,
  }),
  InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptName',
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserName',
  }),
  InputColumn({
    title: '流程状态',
    dataIndex: 'processStatusDisplay',
  }),
  InputColumn({
    title: '流程开始时间',
    dataIndex: 'processStartTimeStr',
  }),
  InputColumn({
    title: '流程结束时间',
    dataIndex: 'processEndTimeStr',
  }),
  InputColumn({
    title: '流程结束月份',
    dataIndex: 'processEndTimeMonth',
  }),
  InputColumn({
    title: '投放日期',
    dataIndex: 'minPayDateStr',
  }),
  InputColumn({
    title: '投放月份',
    dataIndex: 'minPayDateMonth',
  }),
]
