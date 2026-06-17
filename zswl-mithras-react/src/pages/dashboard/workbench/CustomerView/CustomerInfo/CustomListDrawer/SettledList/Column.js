import {
  founderSelect,
  orgSelect,
  clientSelect,
  provinceSelect,
} from '@/utils/dashboardColumns'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '客户ID',
    dataIndex: 'clientId',
  }),
  clientSelect(),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '项目ID',
    dataIndex: 'projReviewId',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  }),
  provinceSelect(),
  orgSelect({ title: '所属部门' }),
  founderSelect({ title: '所属主办' }),
  InputColumn({
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  }),
]
