import { founderSelect, orgSelect, clientSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'
import { MatchOptionColumn, InputColumn, DateColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '租后检查计划名称',
    dataIndex: 'checkPlanName',
  }),
  MatchOptionColumn({
    title: '计划类型',
    dataIndex: 'planType',
    matchOption: 'afterLeaseCheckPlanTypeEnum',
  }),
  MatchOptionColumn({
    title: '本次检查形式',
    dataIndex: 'checkWayCode',
    matchOption: 'afterLeaseCheckWayEnum',
  }),
  DateColumn({
    title: '本次租后截止时间',
    dataIndex: 'checkDate',
    search: true,
  }),
  MatchOptionColumn({
    title: '计划状态',
    dataIndex: 'checkPlanStatus',
    matchOption: 'afterLeaseCheckPlanStatusEnum',
  }),
  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'reportProcessStatusCode',
    matchOption: 'afterLeaseCheckPlanProcessStatusEnum',
  }),
  orgSelect({ title: '业务部门' }),
  founderSelect({ title: '客户主办' }),
  InputColumn({
    title: '创建时间',
    dataIndex: 'createTime',
  }),
  InputColumn({
    title: '变更时间',
    dataIndex: 'updateTime',
  }),
]
