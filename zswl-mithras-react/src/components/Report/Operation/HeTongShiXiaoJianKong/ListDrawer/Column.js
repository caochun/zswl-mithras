import { InputColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '流程ID',
    dataIndex: 'processInstanceId',
  }),
  InputColumn({
    title: '租赁类型',
    dataIndex: 'leaseType',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'businessCategory',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '业务部门',
    dataIndex: 'bizDeptName',
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'sponsorUserName',
  }),
  InputColumn({
    title: '流程开始时间',
    dataIndex: 'processStartTime',
  }),
  InputColumn({
    title: '流程结束时间',
    dataIndex: 'processEndTime',
  }),

  InputColumn({
    title: '全流程耗时（工作日小时）',
    dataIndex: 'duration',
    align: 'right',
  }),
  InputColumn({
    title: '运营经办耗时（工作日小时）',
    dataIndex: 'durationYYJB',
    align: 'right',
  }),
  InputColumn({
    title: '运营复核耗时（工作日小时）',
    dataIndex: 'durationYYFH',
    align: 'right',
  }),
  InputColumn({
    title: '运营负责人耗时（工作日小时）',
    dataIndex: 'durationYYFZR',
    align: 'right',
  }),
  InputColumn({
    title: '法务经理耗时（工作日小时）',
    dataIndex: 'durationFWJL',
    align: 'right',
  }),
  InputColumn({
    title: '法务负责人耗时（工作日小时）',
    dataIndex: 'durationFWFZR',
    align: 'right',
  }),
  InputColumn({
    title: '财务主管耗时（工作日小时）',
    dataIndex: 'durationCWZG',
    align: 'right',
  }),
]
