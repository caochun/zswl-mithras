import { getDescColumns } from '@/utils'
import ALL_COLUMNS from './Column'

const nameColumns = [
  '担保机构名称',
  '机构编号',
  '是否集团内关联方',
  '统一社会信用码',
  '成立日期',
  '核准日期',
  '营业许可证是否为长期',
  '营业许可证到期日',
  '经济类型',
  '组织机构类型',
  '注册资本（元）',
  '注册资本币种',
  '实收资本（元）',
  '实收资本币种',
  '法人代表',
  '经营范围',
  '备注',
  '资金经理',
  '业务部门',
  '业务分管领导',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

export default columns
